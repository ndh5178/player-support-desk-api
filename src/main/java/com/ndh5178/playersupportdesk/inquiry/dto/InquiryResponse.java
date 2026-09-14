package com.ndh5178.playersupportdesk.inquiry.dto;

import java.time.Instant;
import java.util.List;

import com.ndh5178.playersupportdesk.inquiry.Inquiry;
import com.ndh5178.playersupportdesk.inquiry.InquiryCategory;
import com.ndh5178.playersupportdesk.inquiry.InquiryPriority;
import com.ndh5178.playersupportdesk.inquiry.InquiryStatus;
import com.ndh5178.playersupportdesk.inquiry.history.InquiryHistory;
import com.ndh5178.playersupportdesk.inquiry.note.InquiryNote;

public record InquiryResponse(
        String id,
        String title,
        String content,
        InquiryCategory category,
        InquiryPriority priority,
        InquiryStatus status,
        CustomerResponse customer,
        AgentResponse assignee,
        Instant createdAt,
        Instant updatedAt,
        Instant slaDueAt,
        List<InquiryHistoryResponse> history,
        List<InquiryNoteResponse> notes) {

    public static InquiryResponse from(
            Inquiry inquiry,
            List<InquiryHistory> histories,
            List<InquiryNote> notes) {
        AgentResponse assignee = inquiry.getAssignee() == null
                ? null
                : AgentResponse.from(inquiry.getAssignee());

        return new InquiryResponse(
                inquiry.getId(),
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getCategory(),
                inquiry.getPriority(),
                inquiry.getStatus(),
                CustomerResponse.from(inquiry.getCustomer()),
                assignee,
                inquiry.getCreatedAt(),
                inquiry.getUpdatedAt(),
                inquiry.getSlaDueAt(),
                histories.stream().map(InquiryHistoryResponse::from).toList(),
                notes.stream().map(InquiryNoteResponse::from).toList());
    }
}
