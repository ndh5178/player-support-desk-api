package com.ndh5178.playersupportdesk.inquiry;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ndh5178.playersupportdesk.agent.Agent;
import com.ndh5178.playersupportdesk.agent.AgentRepository;
import com.ndh5178.playersupportdesk.common.error.ApiValidationException;
import com.ndh5178.playersupportdesk.common.error.InquiryNotFoundException;
import com.ndh5178.playersupportdesk.inquiry.dto.CreateInquiryNoteRequest;
import com.ndh5178.playersupportdesk.inquiry.dto.InquiryListResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.InquiryNoteResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.InquiryResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.PaginationResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.UpdateInquiryRequest;
import com.ndh5178.playersupportdesk.inquiry.history.InquiryHistory;
import com.ndh5178.playersupportdesk.inquiry.history.InquiryHistoryRepository;
import com.ndh5178.playersupportdesk.inquiry.history.InquiryHistoryType;
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

    private static final String CURRENT_AGENT_ID = "agent-001";

    private final InquiryRepository inquiryRepository;
    private final AgentRepository agentRepository;
    private final InquiryHistoryRepository historyRepository;
    private final InquiryNoteRepository noteRepository;

    public InquiryService(
            InquiryRepository inquiryRepository,
            AgentRepository agentRepository,
            InquiryHistoryRepository historyRepository,
            InquiryNoteRepository noteRepository) {
        this.inquiryRepository = inquiryRepository;
        this.agentRepository = agentRepository;
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
        Inquiry inquiry = findInquiry(inquiryId);
        return createInquiryResponse(inquiry);
    }

    @Transactional
    public InquiryResponse updateInquiry(String inquiryId, UpdateInquiryRequest request) {
        Inquiry inquiry = findInquiry(inquiryId);
        Agent nextAssignee = resolveAssignee(request);

        boolean statusChanged = request.hasStatus() && inquiry.getStatus() != request.status();
        boolean assigneeChanged = request.hasAssigneeId()
                && !Objects.equals(agentId(inquiry.getAssignee()), agentId(nextAssignee));

        if (!statusChanged && !assigneeChanged) {
            return createInquiryResponse(inquiry);
        }

        Agent currentAgent = findCurrentAgent();
        Instant changedAt = Instant.now();

        if (statusChanged) {
            InquiryStatus previousStatus = inquiry.getStatus();
            inquiry.changeStatus(request.status());
            historyRepository.save(InquiryHistory.create(
                    inquiry,
                    InquiryHistoryType.STATUS_CHANGED,
                    currentAgent.getName(),
                    "문의 상태를 %s에서 %s(으)로 변경했습니다."
                            .formatted(previousStatus, request.status()),
                    previousStatus.name(),
                    request.status().name(),
                    changedAt));
        }

        if (assigneeChanged) {
            Agent previousAssignee = inquiry.getAssignee();
            inquiry.changeAssignee(nextAssignee);
            historyRepository.save(InquiryHistory.create(
                    inquiry,
                    InquiryHistoryType.ASSIGNEE_CHANGED,
                    currentAgent.getName(),
                    createAssigneeHistoryDescription(nextAssignee),
                    agentId(previousAssignee),
                    agentId(nextAssignee),
                    changedAt));
        }

        inquiry.updateTimestamp(changedAt);
        return createInquiryResponse(inquiry);
    }

    @Transactional
    public InquiryNoteResponse addInquiryNote(String inquiryId, CreateInquiryNoteRequest request) {
        Inquiry inquiry = findInquiry(inquiryId);
        Agent currentAgent = findCurrentAgent();
        Instant createdAt = Instant.now();

        InquiryNote note = InquiryNote.create(
                inquiry,
                currentAgent,
                request.content(),
                createdAt);
        noteRepository.save(note);
        historyRepository.save(InquiryHistory.create(
                inquiry,
                InquiryHistoryType.NOTE_ADDED,
                currentAgent.getName(),
                "내부 메모를 추가했습니다.",
                null,
                null,
                createdAt));
        inquiry.updateTimestamp(createdAt);

        return InquiryNoteResponse.from(note);
    }

    private Inquiry findInquiry(String inquiryId) {
        return inquiryRepository.findOneById(inquiryId)
                .orElseThrow(() -> new InquiryNotFoundException(inquiryId));
    }

    private InquiryResponse createInquiryResponse(Inquiry inquiry) {
        List<InquiryHistory> histories = historyRepository
                .findAllByInquiryIdOrderByCreatedAtAscIdAsc(inquiry.getId());
        List<InquiryNote> notes = noteRepository
                .findAllByInquiryIdOrderByCreatedAtAscIdAsc(inquiry.getId());

        return InquiryResponse.from(inquiry, histories, notes);
    }

    private Agent resolveAssignee(UpdateInquiryRequest request) {
        if (!request.hasAssigneeId() || request.assigneeId() == null) {
            return null;
        }

        return agentRepository.findById(request.assigneeId())
                .orElseThrow(() -> new ApiValidationException(
                        "수정할 문의 정보를 확인해 주세요.",
                        Map.of("assigneeId", "존재하는 담당자 ID 또는 null이어야 합니다.")));
    }

    private Agent findCurrentAgent() {
        return agentRepository.findById(CURRENT_AGENT_ID)
                .orElseThrow(() -> new IllegalStateException(
                        "로컬 작업 담당자를 찾을 수 없습니다: " + CURRENT_AGENT_ID));
    }

    private String agentId(Agent agent) {
        return agent == null ? null : agent.getId();
    }

    private String createAssigneeHistoryDescription(Agent assignee) {
        return assignee == null
                ? "담당자 배정을 해제했습니다."
                : assignee.getName() + " 담당자로 배정했습니다.";
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
