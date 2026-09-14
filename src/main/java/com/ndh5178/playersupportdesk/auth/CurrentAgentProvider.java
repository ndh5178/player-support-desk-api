package com.ndh5178.playersupportdesk.auth;

import com.ndh5178.playersupportdesk.agent.Agent;
import com.ndh5178.playersupportdesk.agent.AgentRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentAgentProvider {

    private final AgentRepository agentRepository;

    public CurrentAgentProvider(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public Agent getCurrentAgent() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof SupportUserPrincipal principal)) {
            throw new AuthenticationCredentialsNotFoundException("로그인이 필요합니다.");
        }

        return agentRepository.findById(principal.getAgentId())
                .orElseThrow(() -> new IllegalStateException(
                        "로그인한 담당자 정보를 찾을 수 없습니다: " + principal.getAgentId()));
    }
}
