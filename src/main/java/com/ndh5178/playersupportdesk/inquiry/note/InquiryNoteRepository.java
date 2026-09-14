package com.ndh5178.playersupportdesk.inquiry.note;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryNoteRepository extends JpaRepository<InquiryNote, String> {

    List<InquiryNote> findAllByInquiryIdOrderByCreatedAtAscIdAsc(String inquiryId);
}
