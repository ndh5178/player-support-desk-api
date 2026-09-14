package com.ndh5178.playersupportdesk.dashboard.dto;

import java.util.List;

import com.ndh5178.playersupportdesk.inquiry.dto.InquiryResponse;

public record DashboardResponse(
        long totalCount,
        long newCount,
        long inProgressCount,
        long slaOverdueCount,
        List<InquiryResponse> recentInquiries,
        List<PriorityDistributionResponse> priorityDistribution) {
}
