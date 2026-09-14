package com.ndh5178.playersupportdesk.inquiry;

import java.time.Instant;

import com.ndh5178.playersupportdesk.agent.Agent;
import com.ndh5178.playersupportdesk.customer.Customer;
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
@Table(name = "inquiries")
public class Inquiry {

    @Id
    @Column(columnDefinition = "text")
    private String id;

    @Column(nullable = false, columnDefinition = "text")
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InquiryCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InquiryPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InquiryStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private Agent assignee;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "sla_due_at", nullable = false)
    private Instant slaDueAt;

    protected Inquiry() {
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public InquiryCategory getCategory() {
        return category;
    }

    public InquiryPriority getPriority() {
        return priority;
    }

    public InquiryStatus getStatus() {
        return status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Agent getAssignee() {
        return assignee;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getSlaDueAt() {
        return slaDueAt;
    }

    public void changeStatus(InquiryStatus status) {
        this.status = status;
    }

    public void changeAssignee(Agent assignee) {
        this.assignee = assignee;
    }

    public void updateTimestamp(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
