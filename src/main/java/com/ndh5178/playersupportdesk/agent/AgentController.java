package com.ndh5178.playersupportdesk.agent;

import com.ndh5178.playersupportdesk.agent.dto.AgentListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @GetMapping
    public AgentListResponse getAgents() {
        return agentService.getAgents();
    }
}
