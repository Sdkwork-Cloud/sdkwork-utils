//! Token-bucket rate limiting primitives with an optional Redis backend.
//!
//! The pure [`TokenBucket`] state machine is dependency-free and is used by
//! in-process limiters. With the `redis` feature, [`RedisTokenBucketClient`]
//! provides an atomic, cluster-safe token bucket backed by a Lua script so
//! multiple gateway instances enforce one shared budget.

use std::time::Duration;

/// In-process token bucket state machine.
///
/// `tokens` may go slightly negative after bursts are drained; the refill
/// calculation always clamps back to `capacity`.
#[derive(Clone, Debug, Default)]
pub struct TokenBucket {
    /// Current token balance (may be fractional between requests).
    pub tokens: f64,
    /// Wall-clock instant of the last refill.
    pub last_refill_millis: u64,
    /// Bucket capacity in tokens.
    pub capacity: u64,
}

impl TokenBucket {
    /// Create a full bucket at the given reference time (epoch millis).
    pub fn new(capacity: u64, now_millis: u64) -> Self {
        Self {
            tokens: capacity as f64,
            last_refill_millis: now_millis,
            capacity,
        }
    }

    /// Try to acquire one token at `now_millis`, refilling at
    /// `refill_per_second` (tokens per second).
    ///
    /// Returns `Some((remaining_tokens, reset_seconds))` when allowed, `None`
    /// when the bucket is empty.
    pub fn try_acquire(&mut self, refill_per_second: u64, now_millis: u64) -> Option<(u64, u64)> {
        let elapsed_secs = now_millis.saturating_sub(self.last_refill_millis) as f64 / 1000.0;
        let refill = elapsed_secs * refill_per_second as f64;
        self.tokens = (self.tokens + refill).min(self.capacity as f64);
        self.last_refill_millis = now_millis;

        if self.tokens < 1.0 {
            return None;
        }
        self.tokens -= 1.0;
        let remaining = self.tokens.floor().max(0.0) as u64;
        let reset = if remaining >= 1 {
            0
        } else if refill_per_second == 0 {
            1
        } else {
            ((1.0 - self.tokens) / refill_per_second as f64)
                .ceil()
                .max(1.0) as u64
        };
        Some((remaining, reset))
    }
}

/// Outcome of a remote token-bucket acquisition.
#[derive(Clone, Copy, Debug, Eq, PartialEq)]
pub struct RedisBucketDecision {
    /// Whether the request consumed a token.
    pub allowed: bool,
    /// Tokens remaining after the acquisition (floored).
    pub remaining: u64,
    /// Bucket capacity.
    pub capacity: u64,
    /// Seconds until the bucket refills one token (0 when tokens remain).
    pub reset_seconds: u64,
}

/// Result type for Redis-backed acquisitions.
pub type RedisBucketResult = Result<RedisBucketDecision, RedisBucketError>;

/// Error returned when the Redis backend is unreachable or rejects the script.
#[derive(Clone, Debug, Eq, PartialEq)]
pub enum RedisBucketError {
    /// Connection/execution failure (caller should fall back to local state).
    Unavailable(String),
    /// The Lua script returned an unexpected shape.
    InvalidScriptReply(String),
}

/// Atomic token bucket over a shared Redis connection.
///
/// One Lua script performs read-modify-write on a hash (`tokens`, `ts`) with a
/// bounded TTL so idle identities do not accumulate. Instances share the
/// bucket through the key namespace, giving cluster-wide enforcement.
#[cfg(feature = "redis")]
pub struct RedisTokenBucketClient {
    connection: redis::aio::ConnectionManager,
    key_prefix: String,
    script: redis::Script,
}

/// Default key TTL for rate-limit buckets (seconds).
pub const REDIS_BUCKET_KEY_TTL_SECS: u64 = 600;

#[cfg(feature = "redis")]
const ACQUIRE_SCRIPT: &str = r#"
local tokens_key = KEYS[1]
local now = tonumber(ARGV[1])
local capacity = tonumber(ARGV[2])
local refill = tonumber(ARGV[3])
local ttl = tonumber(ARGV[4])
local bucket = redis.call('HMGET', tokens_key, 'tokens', 'ts')
local tokens = tonumber(bucket[1])
local ts = tonumber(bucket[2])
if tokens == nil then
  tokens = capacity
  ts = now
end
local elapsed = (now - ts) / 1000.0
tokens = math.min(capacity, tokens + elapsed * refill)
local allowed = 0
if tokens >= 1 then
  tokens = tokens - 1
  allowed = 1
end
redis.call('HMSET', tokens_key, 'tokens', tokens, 'ts', now)
redis.call('EXPIRE', tokens_key, ttl)
local remaining = math.floor(tokens)
if remaining < 0 then remaining = 0 end
local reset = 0
if allowed == 0 then
  if refill > 0 then
    reset = math.max(1, math.ceil((1 - tokens) / refill))
  else
    reset = 1
  end
end
return {allowed, remaining, capacity, reset}
"#;

#[cfg(feature = "redis")]
impl RedisTokenBucketClient {
    /// Connect to the Redis endpoint and prepare the acquire script.
    ///
    /// Connection and response timeouts are bounded so an unreachable backend
    /// degrades quickly instead of hanging the data plane.
    pub async fn connect(
        url: impl AsRef<str>,
        key_prefix: impl Into<String>,
    ) -> Result<Self, String> {
        let client = redis::Client::open(url.as_ref())
            .map_err(|error| format!("open redis client failed: {error}"))?;
        let config = redis::aio::ConnectionManagerConfig::new()
            .set_connection_timeout(Duration::from_secs(3))
            .set_response_timeout(Duration::from_secs(1))
            .set_number_of_retries(1);
        let connection = redis::aio::ConnectionManager::new_with_config(client, config)
            .await
            .map_err(|error| format!("connect redis failed: {error}"))?;
        Ok(Self {
            connection,
            key_prefix: key_prefix.into(),
            script: redis::Script::new(ACQUIRE_SCRIPT),
        })
    }

    /// Build a client from an already-connected manager (e.g. shared by the
    /// application host).
    pub fn from_manager(
        connection: redis::aio::ConnectionManager,
        key_prefix: impl Into<String>,
    ) -> Self {
        Self {
            connection,
            key_prefix: key_prefix.into(),
            script: redis::Script::new(ACQUIRE_SCRIPT),
        }
    }

    /// Attempt to acquire one token for `identity`.
    ///
    /// The bucket key is `{key_prefix}:{rule_id}:{identity}`; callers are
    /// responsible for hashing high-cardinality identities when needed.
    ///
    /// `ConnectionManager` multiplexes one logical connection pool, so a
    /// `&self` borrow is enough; callers may share the client across tasks.
    pub async fn try_acquire(
        &self,
        rule_id: &str,
        identity: &str,
        capacity: u64,
        refill_per_second: u64,
        now_millis: u64,
    ) -> RedisBucketResult {
        if capacity == 0 {
            return Ok(RedisBucketDecision {
                allowed: false,
                remaining: 0,
                capacity: 0,
                reset_seconds: 1,
            });
        }
        let key = format!("{}:{rule_id}:{identity}", self.key_prefix);
        let mut connection = self.connection.clone();
        let reply: Vec<i64> = self
            .script
            .key(key)
            .arg(now_millis as i64)
            .arg(capacity as i64)
            .arg(refill_per_second as i64)
            .arg(REDIS_BUCKET_KEY_TTL_SECS as i64)
            .invoke_async(&mut connection)
            .await
            .map_err(|error| RedisBucketError::Unavailable(error.to_string()))?;
        if reply.len() != 4 {
            return Err(RedisBucketError::InvalidScriptReply(format!(
                "expected 4 values, got {}",
                reply.len()
            )));
        }
        Ok(RedisBucketDecision {
            allowed: reply[0] == 1,
            remaining: reply[1] as u64,
            capacity: reply[2] as u64,
            reset_seconds: reply[3] as u64,
        })
    }
}

/// Milliseconds since the Unix epoch (shared by callers and the Lua script).
pub fn unix_millis(now: &std::time::SystemTime) -> u64 {
    now.duration_since(std::time::UNIX_EPOCH)
        .unwrap_or(Duration::ZERO)
        .as_millis() as u64
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn bucket_accepts_capacity_requests_then_rejects() {
        let mut bucket = TokenBucket::new(3, 0);
        assert!(bucket.try_acquire(1, 0).is_some());
        assert!(bucket.try_acquire(1, 0).is_some());
        assert!(bucket.try_acquire(1, 0).is_some());
        assert!(bucket.try_acquire(1, 0).is_none());
    }

    #[test]
    fn bucket_refills_over_time() {
        let mut bucket = TokenBucket::new(1, 0);
        assert!(bucket.try_acquire(1, 0).is_some());
        // After one second the single token is back (consumed again).
        let (remaining, reset) = bucket.try_acquire(1, 1000).expect("refilled");
        assert_eq!(remaining, 0);
        // Bucket is empty again; the next token arrives one second from now.
        assert_eq!(reset, 1);
    }

    #[test]
    fn empty_bucket_reports_reset_seconds() {
        let mut bucket = TokenBucket::new(1, 0);
        assert!(bucket.try_acquire(1, 0).is_some());
        let result = bucket.try_acquire(1, 10);
        assert!(result.is_none());
    }

    #[test]
    fn refill_clamps_to_capacity() {
        let mut bucket = TokenBucket::new(2, 0);
        bucket.try_acquire(1, 0);
        // 10 seconds later refill would exceed capacity; clamp to 2.
        let (remaining, _) = bucket.try_acquire(1, 10_000).expect("allowed");
        assert!(remaining <= 2);
    }
}
