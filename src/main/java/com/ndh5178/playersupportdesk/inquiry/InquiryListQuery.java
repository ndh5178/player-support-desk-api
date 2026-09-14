package com.ndh5178.playersupportdesk.inquiry;

public record InquiryListQuery(
        String search,
        InquiryStatus status,
        InquiryPriority priority,
        InquiryCategory category,
        InquirySort sort,
        int page,
        int limit) {
}
