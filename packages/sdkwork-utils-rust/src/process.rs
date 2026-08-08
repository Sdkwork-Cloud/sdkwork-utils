//! Bounded subprocess execution: hard timeout and output size caps so
//! discovery/probe commands cannot hang forever or exhaust memory.

use std::io::Read;
use std::process::{Command, Stdio};
use std::sync::mpsc;
use std::thread;
use std::time::{Duration, Instant};

/// Default subprocess timeout for bounded commands.
pub const BOUNDED_COMMAND_TIMEOUT: Duration = Duration::from_secs(10);

/// Default maximum combined output bytes captured per stream.
pub const BOUNDED_COMMAND_MAX_OUTPUT_BYTES: usize = 1_048_576;

/// Result of a bounded command execution.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct BoundedCommandOutput {
    pub status: Option<i32>,
    pub success: bool,
    pub stdout: String,
    pub stderr: String,
    pub timed_out: bool,
}

/// Errors that are not represented by an exit status (spawn failure, timeout).
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum BoundedCommandError {
    Spawn(String),
    Timeout {
        program: String,
        timeout_seconds: u64,
    },
}

/// Runs `command` with a hard timeout and per-stream output caps.
///
/// - The process is killed when `timeout` elapses (`timed_out = true`).
/// - Each captured stream is truncated to `max_output_bytes`.
/// - Streams are drained on worker threads so a chatty child cannot deadlock
///   the parent while it polls for completion.
pub fn run_bounded(
    command: &mut Command,
    timeout: Duration,
    max_output_bytes: usize,
) -> Result<BoundedCommandOutput, BoundedCommandError> {
    let program = command.get_program().to_string_lossy().into_owned();
    let mut child = command
        .stdout(Stdio::piped())
        .stderr(Stdio::piped())
        .spawn()
        .map_err(|cause| BoundedCommandError::Spawn(format!("{program}: {cause}")))?;

    let stdout_pipe = child
        .stdout
        .take()
        .ok_or_else(|| BoundedCommandError::Spawn("stdout pipe unavailable".into()))?;
    let stderr_pipe = child
        .stderr
        .take()
        .ok_or_else(|| BoundedCommandError::Spawn("stderr pipe unavailable".into()))?;

    let (stdout_tx, stdout_rx) = mpsc::channel();
    let (stderr_tx, stderr_rx) = mpsc::channel();
    thread::spawn(move || {
        let _ = stdout_tx.send(read_bounded(stdout_pipe, max_output_bytes));
    });
    thread::spawn(move || {
        let _ = stderr_tx.send(read_bounded(stderr_pipe, max_output_bytes));
    });

    let deadline = Instant::now() + timeout;
    let status = loop {
        match child.try_wait() {
            Ok(Some(status)) => break status,
            Ok(None) => {}
            Err(cause) => {
                let _ = child.kill();
                return Err(BoundedCommandError::Spawn(cause.to_string()));
            }
        }
        if Instant::now() >= deadline {
            let _ = child.kill();
            let _ = child.wait();
            return Ok(BoundedCommandOutput {
                status: None,
                success: false,
                stdout: String::new(),
                stderr: String::new(),
                timed_out: true,
            });
        }
        thread::sleep(Duration::from_millis(50));
    };

    let stdout = stdout_rx.recv().unwrap_or_default().trim_end().to_string();
    let stderr = stderr_rx.recv().unwrap_or_default().trim_end().to_string();

    Ok(BoundedCommandOutput {
        status: status.code(),
        success: status.success(),
        stdout,
        stderr,
        timed_out: false,
    })
}

/// Convenience wrapper with the default timeout/output caps.
pub fn run_bounded_default(
    command: &mut Command,
) -> Result<BoundedCommandOutput, BoundedCommandError> {
    run_bounded(
        command,
        BOUNDED_COMMAND_TIMEOUT,
        BOUNDED_COMMAND_MAX_OUTPUT_BYTES,
    )
}

fn read_bounded(mut reader: impl Read, max_bytes: usize) -> String {
    let mut bytes = Vec::with_capacity(max_bytes.min(8192));
    let mut buffer = [0u8; 8192];
    let mut remaining = max_bytes;
    loop {
        let read = match reader.read(&mut buffer) {
            Ok(0) => break,
            Ok(read) => read,
            Err(_) => break,
        };
        if read >= remaining {
            bytes.extend_from_slice(&buffer[..remaining]);
            break;
        }
        bytes.extend_from_slice(&buffer[..read]);
        remaining -= read;
    }
    String::from_utf8_lossy(&bytes).into_owned()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[cfg(not(windows))]
    #[test]
    fn bounded_command_captures_output_and_status() {
        let mut command = Command::new("sh");
        command.args(["-c", "echo hello; echo err 1>&2; exit 3"]);
        let output = run_bounded_default(&mut command).unwrap();
        assert_eq!(output.status, Some(3));
        assert!(!output.success);
        assert_eq!(output.stdout, "hello");
        assert_eq!(output.stderr, "err");
        assert!(!output.timed_out);
    }

    #[cfg(not(windows))]
    #[test]
    fn bounded_command_kills_on_timeout() {
        let mut command = Command::new("sh");
        command.args(["-c", "sleep 5"]);
        let output = run_bounded(&mut command, Duration::from_millis(200), 1024).unwrap();
        assert!(output.timed_out);
        assert!(!output.success);
    }

    #[cfg(not(windows))]
    #[test]
    fn bounded_command_truncates_oversized_output() {
        let mut command = Command::new("sh");
        command.args(["-c", "yes x | head -c 4096"]);
        let output = run_bounded(&mut command, Duration::from_secs(5), 64).unwrap();
        assert!(output.stdout.len() <= 64);
        assert!(output.stdout.contains('x'));
    }

    #[cfg(windows)]
    #[test]
    fn bounded_command_captures_output_and_status_on_windows() {
        let mut command = Command::new("cmd");
        command.args(["/c", "echo hello & echo err 1>&2 & exit /b 3"]);
        let output = run_bounded_default(&mut command).unwrap();
        assert_eq!(output.status, Some(3));
        assert!(!output.success);
        assert!(output.stdout.contains("hello"));
        assert!(output.stderr.contains("err"));
        assert!(!output.timed_out);
    }

    #[cfg(windows)]
    #[test]
    fn bounded_command_kills_on_timeout_on_windows() {
        let mut command = Command::new("ping");
        command.args(["-n", "30", "127.0.0.1"]);
        let output = run_bounded(&mut command, Duration::from_millis(300), 1024).unwrap();
        assert!(output.timed_out);
        assert!(!output.success);
    }
}
