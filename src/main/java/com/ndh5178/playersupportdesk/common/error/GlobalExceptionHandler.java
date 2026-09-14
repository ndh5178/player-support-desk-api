package com.ndh5178.playersupportdesk.common.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(ApiValidationException exception) {
        ApiErrorResponse body = ApiErrorResponse.of(
                "VALIDATION_ERROR",
                exception.getMessage(),
                exception.getDetails());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InquiryNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleInquiryNotFound(InquiryNotFoundException exception) {
        ApiErrorResponse body = ApiErrorResponse.of("INQUIRY_NOT_FOUND", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception exception) {
        log.error("예상하지 못한 API 오류가 발생했습니다.", exception);
        ApiErrorResponse body = ApiErrorResponse.of(
                "INTERNAL_SERVER_ERROR",
                "요청을 처리하는 중 오류가 발생했습니다.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
