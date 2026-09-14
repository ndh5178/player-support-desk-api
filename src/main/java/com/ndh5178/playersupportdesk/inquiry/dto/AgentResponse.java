package com.ndh5178.playersupportdesk.inquiry.dto;

import com.ndh5178.playersupportdesk.agent.Agent;

public record AgentResponse(String id, String name, String team) {

    public static AgentResponse from(Agent agent) {
        return new AgentResponse(agent.getId(), agent.getName(), agent.getTeam());
    }
}
