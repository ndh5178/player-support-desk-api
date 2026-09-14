package com.ndh5178.playersupportdesk.auth;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

import com.ndh5178.playersupportdesk.agent.Agent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class SupportUserPrincipal implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String agentId;
    private final String name;
    private final String team;
    private final String username;
    private final String password;
    private final boolean enabled;

    private SupportUserPrincipal(
            String agentId,
            String name,
            String team,
            String username,
            String password,
            boolean enabled) {
        this.agentId = agentId;
        this.name = name;
        this.team = team;
        this.username = username;
        this.password = password;
        this.enabled = enabled;
    }

    public static SupportUserPrincipal from(SupportAccount account) {
        Agent agent = account.getAgent();
        return new SupportUserPrincipal(
                agent.getId(),
                agent.getName(),
                agent.getTeam(),
                account.getUsername(),
                account.getPasswordHash(),
                account.isEnabled());
    }

    public String getAgentId() {
        return agentId;
    }

    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_AGENT"));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
