package com.ndh5178.playersupportdesk.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ndh5178.playersupportdesk.auth.SupportUserPrincipal;
import com.ndh5178.playersupportdesk.inquiry.dto.AgentResponse;

public record AuthSessionResponse(
        boolean authenticated,
        @JsonInclude(JsonInclude.Include.NON_NULL) AgentResponse agent,
        String csrfToken) {

    public static AuthSessionResponse anonymous(String csrfToken) {
        return new AuthSessionResponse(false, null, csrfToken);
    }

    public static AuthSessionResponse authenticated(
            SupportUserPrincipal principal,
            String csrfToken) {
        AgentResponse agent = new AgentResponse(
                principal.getAgentId(),
                principal.getName(),
                principal.getTeam());
        return new AuthSessionResponse(true, agent, csrfToken);
    }
}
