pub struct ResultValue<T> {
    value: Option<T>,
    error: Option<String>,
}

impl<T> ResultValue<T> {
    pub fn ok(value: T) -> Self {
        Self {
            value: Some(value),
            error: None,
        }
    }

    pub fn err(message: impl Into<String>) -> Self {
        Self {
            value: None,
            error: Some(message.into()),
        }
    }

    pub fn is_ok(&self) -> bool {
        self.value.is_some()
    }

    pub fn is_err(&self) -> bool {
        self.value.is_none()
    }

    pub fn unwrap_or(self, default: T) -> T {
        self.value.unwrap_or(default)
    }

    pub fn map<U, F>(self, mapper: F) -> ResultValue<U>
    where
        F: FnOnce(T) -> U,
    {
        match self.value {
            Some(value) => ResultValue::ok(mapper(value)),
            None => ResultValue::err(self.error.unwrap_or_else(|| "error".to_string())),
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn result_helpers() {
        assert!(ResultValue::ok(42).is_ok());
        assert_eq!(ResultValue::ok(42).unwrap_or(0), 42);
        assert_eq!(ResultValue::<i32>::err("fail").unwrap_or(0), 0);
        assert_eq!(ResultValue::ok(2).map(|value| value * 2).unwrap_or(0), 4);
    }
}
