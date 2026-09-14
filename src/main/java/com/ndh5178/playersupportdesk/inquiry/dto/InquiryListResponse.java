package com.ndh5178.playersupportdesk.inquiry.dto;

import java.util.List;

public record InquiryListResponse(List<InquiryResponse> data, PaginationResponse pagination) {
}
