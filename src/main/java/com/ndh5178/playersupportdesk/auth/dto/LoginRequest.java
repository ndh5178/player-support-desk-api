package com.ndh5178.playersupportdesk.auth.dto;

import java.util.LinkedHashMap;
import java.util.Map;

import com.ndh5178.playersupportdesk.common.error.ApiValidationException;

public record LoginRequest(String username, String password) {

    public LoginRequest validated() {
        Map<String, String> details = new LinkedHashMap<>();

        if (username == null || username.isBlank()) {
            details.put("username", "아이디를 입력해 주세요.");
        }
        if (password == null || password.isBlank()) {
            details.put("password", "비밀번호를 입력해 주세요.");
        }

        if (!details.isEmpty()) {
            throw new ApiValidationException("로그인 정보를 확인해 주세요.", details);
        }

        return new LoginRequest(username.trim(), password);
    }
}
