package com.ndh5178.playersupportdesk.common.error;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ApiValidationException extends RuntimeException {

    private final Map<String, String> details;

    public ApiValidationException(String message, Map<String, String> details) {
        super(message);
        this.details = Collections.unmodifiableMap(new LinkedHashMap<>(details));
    }

    public Map<String, String> getDetails() {
        return details;
    }
}
