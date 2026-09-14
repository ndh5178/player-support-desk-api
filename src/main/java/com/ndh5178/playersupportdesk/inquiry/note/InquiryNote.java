package com.ndh5178.playersupportdesk.inquiry.note;

import java.time.Instant;

import com.ndh5178.playersupportdesk.agent.Agent;
import com.ndh5178.playersupportdesk.inquiry.Inquiry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inquiry_notes")
public class InquiryNote {

    @Id
    @Column(columnDefinition = "text")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Agent author;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected InquiryNote() {
    }

    public String getId() {
        return id;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public Agent getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
