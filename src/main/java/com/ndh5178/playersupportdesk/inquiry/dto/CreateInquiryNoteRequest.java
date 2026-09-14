package com.ndh5178.playersupportdesk.inquiry.dto;

import java.util.Map;

import com.ndh5178.playersupportdesk.common.error.ApiValidationException;
import tools.jackson.databind.JsonNode;

public record CreateInquiryNoteRequest(String content) {

    public static CreateInquiryNoteRequest from(JsonNode body) {
        String content = "";

        if (body != null && body.isObject()) {
            JsonNode contentNode = body.get("content");
            if (contentNode != null && contentNode.isTextual()) {
                content = contentNode.textValue().trim();
            }
        }

        if (content.isEmpty() || content.length() > 1000) {
            throw new ApiValidationException(
                    "메모 내용을 확인해 주세요.",
                    Map.of("content", "공백이 아닌 1자 이상 1,000자 이하의 내용이 필요합니다."));
        }

        return new CreateInquiryNoteRequest(content);
    }
}
