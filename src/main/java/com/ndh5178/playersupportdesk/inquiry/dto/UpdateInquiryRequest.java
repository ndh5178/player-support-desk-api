package com.ndh5178.playersupportdesk.inquiry.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ndh5178.playersupportdesk.common.error.ApiValidationException;
import com.ndh5178.playersupportdesk.inquiry.InquiryStatus;
import tools.jackson.databind.JsonNode;

public record UpdateInquiryRequest(
        boolean hasStatus,
        InquiryStatus status,
        boolean hasAssigneeId,
        String assigneeId) {

    public static UpdateInquiryRequest from(JsonNode body) {
        if (body == null || !body.isObject()) {
            throw new ApiValidationException(
                    "수정할 문의 정보를 확인해 주세요.",
                    Map.of());
        }

        boolean hasStatus = body.has("status");
        boolean hasAssigneeId = body.has("assigneeId");
        Map<String, String> details = new LinkedHashMap<>();

        if (!hasStatus && !hasAssigneeId) {
            details.put("body", "status 또는 assigneeId 중 하나 이상이 필요합니다.");
        }

        InquiryStatus status = null;
        if (hasStatus) {
            JsonNode statusNode = body.get("status");
            if (!statusNode.isTextual()) {
                details.put("status", "지원하지 않는 문의 상태입니다.");
            } else {
                try {
                    status = InquiryStatus.valueOf(statusNode.textValue());
                } catch (IllegalArgumentException exception) {
                    details.put("status", "지원하지 않는 문의 상태입니다.");
                }
            }
        }

        String assigneeId = null;
        if (hasAssigneeId) {
            JsonNode assigneeNode = body.get("assigneeId");
            if (assigneeNode.isTextual()) {
                assigneeId = assigneeNode.textValue();
            } else if (!assigneeNode.isNull()) {
                details.put("assigneeId", "존재하는 담당자 ID 또는 null이어야 합니다.");
            }
        }

        if (!details.isEmpty()) {
            throw new ApiValidationException("수정할 문의 정보를 확인해 주세요.", details);
        }

        return new UpdateInquiryRequest(hasStatus, status, hasAssigneeId, assigneeId);
    }
}
