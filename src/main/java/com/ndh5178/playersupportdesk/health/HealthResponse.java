package com.ndh5178.playersupportdesk.health;

// 응답으로 전달할 데이터 구조다. Spring이 status 값을 JSON으로 변환한다.
public record HealthResponse(String status) {
}
