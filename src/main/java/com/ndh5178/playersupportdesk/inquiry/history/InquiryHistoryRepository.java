package com.ndh5178.playersupportdesk.inquiry.history;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryHistoryRepository extends JpaRepository<InquiryHistory, String> {

    List<InquiryHistory> findAllByInquiryIdOrderByCreatedAtAscIdAsc(String inquiryId);

    List<InquiryHistory> findAllByInquiryIdInOrderByCreatedAtAscIdAsc(List<String> inquiryIds);
}
