use rand::{distributions::Alphanumeric, Rng};
use uuid::Uuid;

pub fn uuid() -> String {
    Uuid::new_v4().to_string()
}

pub fn random_string(length: usize) -> String {
    rand::thread_rng()
        .sample_iter(&Alphanumeric)
        .take(length)
        .map(char::from)
        .collect()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn id_helpers() {
        assert_eq!(random_string(8).len(), 8);
        assert_eq!(uuid().len(), 36);
    }
}
