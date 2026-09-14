package com.ndh5178.playersupportdesk.agent;

import com.ndh5178.playersupportdesk.agent.dto.AgentListResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.AgentResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AgentService {

    private final AgentRepository agentRepository;

    public AgentService(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    public AgentListResponse getAgents() {
        return new AgentListResponse(
                agentRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                        .map(AgentResponse::from)
                        .toList());
    }
}
