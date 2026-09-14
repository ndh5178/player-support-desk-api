package com.ndh5178.playersupportdesk.inquiry;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ndh5178.playersupportdesk.common.error.ApiValidationException;
import jakarta.servlet.http.HttpServletRequest;

public final class InquiryListQueryParser {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 50;

    private InquiryListQueryParser() {
    }

    public static InquiryListQuery parse(HttpServletRequest request) {
        Map<String, String> details = new LinkedHashMap<>();

        String search = parseSearch(request.getParameter("search"));
        InquiryStatus status = parseEnum(
                request.getParameter("status"), InquiryStatus.class,
                "status", "지원하지 않는 문의 상태입니다.", details);
        InquiryPriority priority = parseEnum(
                request.getParameter("priority"), InquiryPriority.class,
                "priority", "지원하지 않는 우선순위입니다.", details);
        InquiryCategory category = parseEnum(
                request.getParameter("category"), InquiryCategory.class,
                "category", "지원하지 않는 문의 유형입니다.", details);
        InquirySort sort = parseSort(request.getParameter("sort"), details);
        int page = parsePositiveInteger(
                request.getParameter("page"), DEFAULT_PAGE,
                "page", "1 이상의 정수여야 합니다.", details);
        int limit = parsePositiveInteger(
                request.getParameter("limit"), DEFAULT_LIMIT,
                "limit", "1 이상 50 이하의 정수여야 합니다.", details);

        if (limit > MAX_LIMIT) {
            details.put("limit", "1 이상 50 이하의 정수여야 합니다.");
        }

        if (!details.isEmpty()) {
            throw new ApiValidationException("목록 조회 조건을 확인해 주세요.", details);
        }

        return new InquiryListQuery(search, status, priority, category, sort, page, limit);
    }

    private static String parseSearch(String value) {
        return value == null ? "" : value.trim();
    }

    private static InquirySort parseSort(String value, Map<String, String> details) {
        if (value == null) {
            return InquirySort.NEWEST;
        }

        return switch (value) {
            case "newest" -> InquirySort.NEWEST;
            case "oldest" -> InquirySort.OLDEST;
            default -> {
                details.put("sort", "newest 또는 oldest만 사용할 수 있습니다.");
                yield InquirySort.NEWEST;
            }
        };
    }

    private static int parsePositiveInteger(
            String value,
            int defaultValue,
            String field,
            String message,
            Map<String, String> details) {
        if (value == null) {
            return defaultValue;
        }

        if (!value.matches("[0-9]+")) {
            details.put(field, message);
            return defaultValue;
        }

        try {
            int parsed = Integer.parseInt(value);
            if (parsed > 0) {
                return parsed;
            }
        } catch (NumberFormatException ignored) {
            // 너무 큰 숫자도 다른 잘못된 정수와 같은 필드 오류로 응답한다.
        }

        details.put(field, message);
        return defaultValue;
    }

    private static <E extends Enum<E>> E parseEnum(
            String value,
            Class<E> enumType,
            String field,
            String message,
            Map<String, String> details) {
        if (value == null) {
            return null;
        }

        try {
            return Enum.valueOf(enumType, value);
        } catch (IllegalArgumentException exception) {
            details.put(field, message);
            return null;
        }
    }
}
