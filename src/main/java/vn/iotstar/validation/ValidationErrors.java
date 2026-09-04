package vn.iotstar.validation;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ValidationErrors {
    private final Map<String, String> fieldErrors = new LinkedHashMap<>();

    public void add(String field, String message) {
        if (field == null || message == null || message.isBlank()) {
            return;
        }
        fieldErrors.putIfAbsent(field, message);
    }

    public boolean hasErrors() {
        return !fieldErrors.isEmpty();
    }

    public Map<String, String> asMap() {
        return Collections.unmodifiableMap(fieldErrors);
    }
}
