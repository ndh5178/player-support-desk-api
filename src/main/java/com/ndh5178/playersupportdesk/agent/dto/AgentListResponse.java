package com.ndh5178.playersupportdesk.agent.dto;

import java.util.List;

import com.ndh5178.playersupportdesk.inquiry.dto.AgentResponse;

public record AgentListResponse(List<AgentResponse> data) {
}
