package com.ndh5178.playersupportdesk.inquiry;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface InquiryRepository extends JpaRepository<Inquiry, String>, JpaSpecificationExecutor<Inquiry> {

    @Override
    @EntityGraph(attributePaths = {"customer", "assignee"})
    Page<Inquiry> findAll(Specification<Inquiry> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"customer", "assignee"})
    Optional<Inquiry> findOneById(String id);
}
