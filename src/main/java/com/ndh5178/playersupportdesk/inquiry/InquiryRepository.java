package com.ndh5178.playersupportdesk.inquiry;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface InquiryRepository extends JpaRepository<Inquiry, String>, JpaSpecificationExecutor<Inquiry> {

    @Override
    @EntityGraph(attributePaths = {"customer", "assignee"})
    Page<Inquiry> findAll(Specification<Inquiry> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "assignee"})
    Optional<Inquiry> findOneById(String id);

    long countByStatus(InquiryStatus status);

    long countByStatusNotAndSlaDueAtBefore(InquiryStatus status, Instant now);

    @Query("""
            select inquiry.priority as priority, count(inquiry) as inquiryCount
            from Inquiry inquiry
            group by inquiry.priority
            """)
    List<InquiryPriorityCount> countGroupedByPriority();

    @EntityGraph(attributePaths = {"customer", "assignee"})
    List<Inquiry> findTop5ByOrderByCreatedAtDescIdDesc();
}
