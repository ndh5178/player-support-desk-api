package com.ndh5178.playersupportdesk.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SupportUserDetailsService implements UserDetailsService {

    private final SupportAccountRepository accountRepository;

    public SupportUserDetailsService(SupportAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return accountRepository.findByUsername(username)
                .map(SupportUserPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "지원 담당자 계정을 찾을 수 없습니다."));
    }
}
