package com.ndh5178.playersupportdesk.inquiry;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ndh5178.playersupportdesk.common.error.InquiryNotFoundException;
import com.ndh5178.playersupportdesk.inquiry.dto.InquiryListResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.InquiryResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.PaginationResponse;
import com.ndh5178.playersupportdesk.inquiry.history.InquiryHistory;
import com.ndh5178.playersupportdesk.inquiry.history.InquiryHistoryRepository;
import com.ndh5178.playersupportdesk.inquiry.note.InquiryNote;
import com.ndh5178.playersupportdesk.inquiry.note.InquiryNoteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryHistoryRepository historyRepository;
    private final InquiryNoteRepository noteRepository;

    public InquiryService(
            InquiryRepository inquiryRepository,
            InquiryHistoryRepository historyRepository,
            InquiryNoteRepository noteRepository) {
        this.inquiryRepository = inquiryRepository;
        this.historyRepository = historyRepository;
        this.noteRepository = noteRepository;
    }

    public InquiryListResponse getInquiries(InquiryListQuery query) {
        Pageable pageable = PageRequest.of(query.page() - 1, query.limit(), createSort(query.sort()));
        Page<Inquiry> inquiryPage = inquiryRepository.findAll(
                InquirySpecifications.from(query),
                pageable);

        List<Inquiry> inquiries = inquiryPage.getContent();
        List<String> inquiryIds = inquiries.stream().map(Inquiry::getId).toList();
        Map<String, List<InquiryHistory>> histories = loadHistories(inquiryIds);
        Map<String, List<InquiryNote>> notes = loadNotes(inquiryIds);

        List<InquiryResponse> data = inquiries.stream()
                .map(inquiry -> InquiryResponse.from(
                        inquiry,
                        histories.getOrDefault(inquiry.getId(), List.of()),
                        notes.getOrDefault(inquiry.getId(), List.of())))
                .toList();

        PaginationResponse pagination = new PaginationResponse(
                query.page(),
                query.limit(),
                inquiryPage.getTotalElements(),
                inquiryPage.getTotalPages());

        return new InquiryListResponse(data, pagination);
    }

    public InquiryResponse getInquiry(String inquiryId) {
        Inquiry inquiry = inquiryRepository.findOneById(inquiryId)
                .orElseThrow(() -> new InquiryNotFoundException(inquiryId));
        List<InquiryHistory> histories = historyRepository
                .findAllByInquiryIdOrderByCreatedAtAscIdAsc(inquiryId);
        List<InquiryNote> notes = noteRepository
                .findAllByInquiryIdOrderByCreatedAtAscIdAsc(inquiryId);

        return InquiryResponse.from(inquiry, histories, notes);
    }

    private Sort createSort(InquirySort sort) {
        Sort.Direction direction = sort == InquirySort.NEWEST
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return Sort.by(direction, "createdAt").and(Sort.by(direction, "id"));
    }

    private Map<String, List<InquiryHistory>> loadHistories(List<String> inquiryIds) {
        if (inquiryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return historyRepository.findAllByInquiryIdInOrderByCreatedAtAscIdAsc(inquiryIds).stream()
                .collect(Collectors.groupingBy(history -> history.getInquiry().getId()));
    }

    private Map<String, List<InquiryNote>> loadNotes(List<String> inquiryIds) {
        if (inquiryIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return noteRepository.findAllByInquiryIdInOrderByCreatedAtAscIdAsc(inquiryIds).stream()
                .collect(Collectors.groupingBy(note -> note.getInquiry().getId()));
    }
}
