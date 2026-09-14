package com.ndh5178.playersupportdesk.common.error;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

public record ApiErrorResponse(ApiError error) {

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(new ApiError(code, message, null));
    }

    public static ApiErrorResponse of(String code, String message, Map<String, String> details) {
        return new ApiErrorResponse(new ApiError(code, message, details));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ApiError(String code, String message, Map<String, String> details) {
    }
}
