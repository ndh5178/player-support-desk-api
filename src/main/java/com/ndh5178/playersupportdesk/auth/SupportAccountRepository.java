package com.ndh5178.playersupportdesk.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportAccountRepository extends JpaRepository<SupportAccount, String> {

    @EntityGraph(attributePaths = "agent")
    Optional<SupportAccount> findByUsername(String username);
}
