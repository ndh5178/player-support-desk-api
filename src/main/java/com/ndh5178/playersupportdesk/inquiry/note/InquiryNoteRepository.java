package com.ndh5178.playersupportdesk.inquiry.note;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryNoteRepository extends JpaRepository<InquiryNote, String> {

    @EntityGraph(attributePaths = "author")
    List<InquiryNote> findAllByInquiryIdOrderByCreatedAtAscIdAsc(String inquiryId);

    @EntityGraph(attributePaths = "author")
    List<InquiryNote> findAllByInquiryIdInOrderByCreatedAtAscIdAsc(List<String> inquiryIds);
}
