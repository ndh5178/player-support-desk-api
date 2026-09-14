package com.ndh5178.playersupportdesk.inquiry.dto;

import java.time.Instant;

import com.ndh5178.playersupportdesk.inquiry.note.InquiryNote;

public record InquiryNoteResponse(
        String id,
        String content,
        AgentResponse author,
        Instant createdAt) {

    public static InquiryNoteResponse from(InquiryNote note) {
        return new InquiryNoteResponse(
                note.getId(),
                note.getContent(),
                AgentResponse.from(note.getAuthor()),
                note.getCreatedAt());
    }
}
