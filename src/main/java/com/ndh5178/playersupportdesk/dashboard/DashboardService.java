package com.ndh5178.playersupportdesk.dashboard;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.ndh5178.playersupportdesk.dashboard.dto.DashboardResponse;
import com.ndh5178.playersupportdesk.dashboard.dto.PriorityDistributionResponse;
import com.ndh5178.playersupportdesk.inquiry.InquiryPriority;
import com.ndh5178.playersupportdesk.inquiry.InquiryPriorityCount;
import com.ndh5178.playersupportdesk.inquiry.InquiryRepository;
import com.ndh5178.playersupportdesk.inquiry.InquiryService;
import com.ndh5178.playersupportdesk.inquiry.InquiryStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final InquiryRepository inquiryRepository;
    private final InquiryService inquiryService;

    public DashboardService(
            InquiryRepository inquiryRepository,
            InquiryService inquiryService) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryService = inquiryService;
    }

    public DashboardResponse getDashboard() {
        Map<InquiryPriority, Long> priorityCounts = new EnumMap<>(InquiryPriority.class);
        for (InquiryPriorityCount result : inquiryRepository.countGroupedByPriority()) {
            priorityCounts.put(result.getPriority(), result.getInquiryCount());
        }

        List<PriorityDistributionResponse> priorityDistribution = List.of(
                createPriorityResponse(InquiryPriority.URGENT, priorityCounts),
                createPriorityResponse(InquiryPriority.HIGH, priorityCounts),
                createPriorityResponse(InquiryPriority.NORMAL, priorityCounts),
                createPriorityResponse(InquiryPriority.LOW, priorityCounts));

        return new DashboardResponse(
                inquiryRepository.count(),
                inquiryRepository.countByStatus(InquiryStatus.NEW),
                inquiryRepository.countByStatus(InquiryStatus.IN_PROGRESS),
                inquiryRepository.countByStatusNotAndSlaDueAtBefore(
                        InquiryStatus.RESOLVED,
                        Instant.now()),
                inquiryService.getRecentInquiries(),
                priorityDistribution);
    }

    private PriorityDistributionResponse createPriorityResponse(
            InquiryPriority priority,
            Map<InquiryPriority, Long> priorityCounts) {
        return new PriorityDistributionResponse(
                priority,
                priorityCounts.getOrDefault(priority, 0L));
    }
}
