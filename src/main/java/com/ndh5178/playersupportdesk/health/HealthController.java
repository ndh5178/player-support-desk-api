package com.ndh5178.playersupportdesk.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public HealthResponse getHealth() {
        // HTTP 응답 가능 여부만 확인한다. DB 상태를 검사하는 API는 아니다.
        return new HealthResponse("UP");
    }
}
