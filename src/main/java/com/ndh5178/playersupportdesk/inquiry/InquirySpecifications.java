package com.ndh5178.playersupportdesk.inquiry;

import java.util.Locale;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public final class InquirySpecifications {

    private InquirySpecifications() {
    }

    public static Specification<Inquiry> from(InquiryListQuery query) {
        Specification<Inquiry> specification = Specification.unrestricted();

        if (!query.search().isEmpty()) {
            specification = specification.and(containsSearch(query.search()));
        }
        if (query.status() != null) {
            specification = specification.and(hasStatus(query.status()));
        }
        if (query.priority() != null) {
            specification = specification.and(hasPriority(query.priority()));
        }
        if (query.category() != null) {
            specification = specification.and(hasCategory(query.category()));
        }

        return specification;
    }

    private static Specification<Inquiry> containsSearch(String search) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            String escapedSearch = escapeLikePattern(search.toLowerCase(Locale.KOREAN));
            String pattern = "%" + escapedSearch + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("id")), pattern, '\\'),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), pattern, '\\'),
                    criteriaBuilder.like(
                            criteriaBuilder.lower(root.join("customer", JoinType.INNER).get("nickname")),
                            pattern,
                            '\\'));
        };
    }

    private static Specification<Inquiry> hasStatus(InquiryStatus status) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), status);
    }

    private static Specification<Inquiry> hasPriority(InquiryPriority priority) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("priority"), priority);
    }

    private static Specification<Inquiry> hasCategory(InquiryCategory category) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"), category);
    }

    private static String escapeLikePattern(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
