package utils

import (
	"bytes"
	"encoding/json"
	"strings"
)

func isObject(value any) (map[string]any, bool) {
	object, ok := value.(map[string]any)
	return object, ok
}

func Pick(source map[string]any, keys []string) map[string]any {
	result := make(map[string]any, len(keys))
	for _, key := range keys {
		if value, ok := source[key]; ok {
			result[key] = value
		}
	}
	return result
}

func Omit(source map[string]any, keys []string) map[string]any {
	blocked := make(map[string]struct{}, len(keys))
	for _, key := range keys {
		blocked[key] = struct{}{}
	}
	result := make(map[string]any, len(source))
	for key, value := range source {
		if _, skip := blocked[key]; !skip {
			result[key] = value
		}
	}
	return result
}

func GetPath(source any, path string) (any, bool) {
	current := source
	for _, segment := range splitPath(path) {
		object, ok := isObject(current)
		if !ok {
			return nil, false
		}
		value, exists := object[segment]
		if !exists {
			return nil, false
		}
		current = value
	}
	return current, true
}

func HasPath(source any, path string) bool {
	value, ok := GetPath(source, path)
	return ok && value != nil
}

func ShallowMerge(base any, overlay any) any {
	baseObject, baseOk := isObject(base)
	overlayObject, overlayOk := isObject(overlay)
	if !baseOk || !overlayOk {
		return overlay
	}

	result := cloneMap(baseObject)
	for key, overlayValue := range overlayObject {
		result[key] = overlayValue
	}
	return result
}

func SetPath(source map[string]any, path string, value any) map[string]any {
	segments := splitPath(path)
	if len(segments) == 0 {
		return source
	}

	root := cloneMap(source)
	current := root
	for _, segment := range segments[:len(segments)-1] {
		next, ok := current[segment].(map[string]any)
		if !ok {
			next = map[string]any{}
			current[segment] = next
		} else {
			next = cloneMap(next)
			current[segment] = next
		}
		current = next
	}
	current[segments[len(segments)-1]] = value
	return root
}

func CompactMap(source map[string]any) map[string]any {
	result := make(map[string]any, len(source))
	for key, value := range source {
		if value != nil {
			result[key] = value
		}
	}
	return result
}

func Keys(source map[string]any) []string {
	raw, err := json.Marshal(source)
	if err != nil {
		return nil
	}
	keys, err := KeysFromJSON(raw)
	if err != nil {
		return nil
	}
	return keys
}

func Values(source map[string]any) []any {
	keys := Keys(source)
	values := make([]any, 0, len(keys))
	for _, key := range keys {
		values = append(values, source[key])
	}
	return values
}

func KeysFromJSON(data []byte) ([]string, error) {
	_, order, err := decodeOrderedObject(data)
	return order, err
}

func ValuesFromJSON(data []byte) ([]any, error) {
	values, order, err := decodeOrderedObject(data)
	if err != nil {
		return nil, err
	}
	result := make([]any, 0, len(order))
	for _, key := range order {
		result = append(result, values[key])
	}
	return result, nil
}

func decodeOrderedObject(data []byte) (map[string]any, []string, error) {
	decoder := json.NewDecoder(bytes.NewReader(data))
	token, err := decoder.Token()
	if err != nil {
		return nil, nil, err
	}
	if token != json.Delim('{') {
		return nil, nil, err
	}

	values := make(map[string]any)
	order := make([]string, 0)
	for decoder.More() {
		keyToken, err := decoder.Token()
		if err != nil {
			return nil, nil, err
		}
		key, ok := keyToken.(string)
		if !ok {
			return nil, nil, err
		}
		var value any
		if err := decoder.Decode(&value); err != nil {
			return nil, nil, err
		}
		order = append(order, key)
		values[key] = value
	}
	return values, order, nil
}

func DeepMerge(base any, overlay any) any {
	baseObject, baseOk := isObject(base)
	overlayObject, overlayOk := isObject(overlay)
	if !baseOk || !overlayOk {
		return overlay
	}

	result := cloneMap(baseObject)
	for key, overlayValue := range overlayObject {
		if existing, ok := result[key]; ok {
			result[key] = DeepMerge(existing, overlayValue)
		} else {
			result[key] = overlayValue
		}
	}
	return result
}

func splitPath(path string) []string {
	parts := make([]string, 0)
	for _, segment := range strings.Split(path, ".") {
		if segment != "" {
			parts = append(parts, segment)
		}
	}
	return parts
}

func cloneMap(source map[string]any) map[string]any {
	result := make(map[string]any, len(source))
	for key, value := range source {
		result[key] = value
	}
	return result
}
