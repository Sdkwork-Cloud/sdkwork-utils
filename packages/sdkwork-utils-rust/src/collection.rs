use std::collections::HashMap;
use std::hash::Hash;

pub fn unique<T>(items: Vec<T>) -> Vec<T>
where
    T: Eq + Hash + Clone,
{
    let mut seen = std::collections::HashSet::new();
    items
        .into_iter()
        .filter(|item| seen.insert(item.clone()))
        .collect()
}

pub fn chunk<T: Clone>(items: &[T], size: usize) -> Vec<Vec<T>> {
    if size == 0 {
        return Vec::new();
    }
    items.chunks(size).map(|chunk| chunk.to_vec()).collect()
}

pub fn group_by<T, K, F>(items: Vec<T>, key_fn: F) -> HashMap<K, Vec<T>>
where
    K: Eq + Hash,
    F: Fn(&T) -> K,
{
    let mut groups: HashMap<K, Vec<T>> = HashMap::new();
    for item in items {
        groups.entry(key_fn(&item)).or_default().push(item);
    }
    groups
}

pub fn flatten<T>(items: Vec<Vec<T>>) -> Vec<T> {
    items.into_iter().flatten().collect()
}

pub fn compact<T>(items: Vec<Option<T>>) -> Vec<T> {
    items.into_iter().flatten().collect()
}

pub fn first<T: Clone>(items: &[T]) -> Option<T> {
    items.first().cloned()
}

pub fn last<T: Clone>(items: &[T]) -> Option<T> {
    items.last().cloned()
}

pub fn sort_by<T, K, F>(items: Vec<T>, key_fn: F) -> Vec<T>
where
    T: Clone,
    K: Ord,
    F: Fn(&T) -> K,
{
    let mut sorted = items;
    sorted.sort_by(|left, right| key_fn(left).cmp(&key_fn(right)));
    sorted
}

pub fn key_by<T, K, F>(items: Vec<T>, key_fn: F) -> HashMap<K, T>
where
    K: Eq + Hash,
    F: Fn(&T) -> K,
{
    items
        .into_iter()
        .map(|item| (key_fn(&item), item))
        .collect()
}

pub fn filter<T, F>(items: Vec<T>, predicate: F) -> Vec<T>
where
    F: Fn(&T) -> bool,
{
    items.into_iter().filter(|item| predicate(item)).collect()
}

pub fn find<T, F>(items: &[T], predicate: F) -> Option<T>
where
    T: Clone,
    F: Fn(&T) -> bool,
{
    items.iter().find(|item| predicate(item)).cloned()
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn collection_helpers() {
        assert_eq!(unique(vec![1, 2, 2, 3]), vec![1, 2, 3]);
        assert_eq!(
            chunk(&[1, 2, 3, 4, 5], 2),
            vec![vec![1, 2], vec![3, 4], vec![5]]
        );
        assert_eq!(flatten(vec![vec![1, 2], vec![3]]), vec![1, 2, 3]);
    }
}
