package com.ndh5178.playersupportdesk.inquiry.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ndh5178.playersupportdesk.inquiry.history.InquiryHistory;
import com.ndh5178.playersupportdesk.inquiry.history.InquiryHistoryType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InquiryHistoryResponse(
        String id,
        InquiryHistoryType type,
        String actorName,
        String description,
        Instant createdAt,
        String previousValue,
        String nextValue) {

    public static InquiryHistoryResponse from(InquiryHistory history) {
        return new InquiryHistoryResponse(
                history.getId(),
                history.getType(),
                history.getActorName(),
                history.getDescription(),
                history.getCreatedAt(),
                history.getPreviousValue(),
                history.getNextValue());
    }
}
