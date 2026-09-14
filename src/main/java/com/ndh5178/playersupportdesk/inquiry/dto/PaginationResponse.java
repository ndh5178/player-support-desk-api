package com.ndh5178.playersupportdesk.inquiry.dto;

public record PaginationResponse(int page, int limit, long total, int totalPages) {
}
