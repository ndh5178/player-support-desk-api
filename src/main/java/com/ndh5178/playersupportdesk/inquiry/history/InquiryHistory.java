package com.ndh5178.playersupportdesk.inquiry.history;

import java.time.Instant;
import java.util.UUID;

import com.ndh5178.playersupportdesk.inquiry.Inquiry;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inquiry_histories")
public class InquiryHistory {

    @Id
    @Column(columnDefinition = "text")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InquiryHistoryType type;

    @Column(name = "actor_name", nullable = false, columnDefinition = "text")
    private String actorName;

    @Column(nullable = false, columnDefinition = "text")
    private String description;

    @Column(name = "previous_value", columnDefinition = "text")
    private String previousValue;

    @Column(name = "next_value", columnDefinition = "text")
    private String nextValue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected InquiryHistory() {
    }

    private InquiryHistory(
            String id,
            Inquiry inquiry,
            InquiryHistoryType type,
            String actorName,
            String description,
            String previousValue,
            String nextValue,
            Instant createdAt) {
        this.id = id;
        this.inquiry = inquiry;
        this.type = type;
        this.actorName = actorName;
        this.description = description;
        this.previousValue = previousValue;
        this.nextValue = nextValue;
        this.createdAt = createdAt;
    }

    public static InquiryHistory create(
            Inquiry inquiry,
            InquiryHistoryType type,
            String actorName,
            String description,
            String previousValue,
            String nextValue,
            Instant createdAt) {
        return new InquiryHistory(
                "history-" + UUID.randomUUID(),
                inquiry,
                type,
                actorName,
                description,
                previousValue,
                nextValue,
                createdAt);
    }

    public String getId() {
        return id;
    }

    public Inquiry getInquiry() {
        return inquiry;
    }

    public InquiryHistoryType getType() {
        return type;
    }

    public String getActorName() {
        return actorName;
    }

    public String getDescription() {
        return description;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public String getNextValue() {
        return nextValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
