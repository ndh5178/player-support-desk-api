package com.ndh5178.playersupportdesk.dashboard.dto;

import com.ndh5178.playersupportdesk.inquiry.InquiryPriority;

public record PriorityDistributionResponse(
        InquiryPriority priority,
        long count) {
}
