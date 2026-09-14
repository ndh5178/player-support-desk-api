package com.ndh5178.playersupportdesk.health;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthApiTest {

    @LocalServerPort
    private int port;

    @Test
    void returnsUpAsJsonOverHttp() throws Exception {
        // 임의의 포트에 실제 서버를 띄워 HTTP 상태와 JSON 직렬화를 함께 검증한다.
        URI uri = URI.create("http://127.0.0.1:" + port + "/api/health");
        HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.headers().firstValue("Content-Type").orElse(""))
                    .startsWith("application/json");
            ObjectMapper mapper = new ObjectMapper();
            assertThat(mapper.readTree(response.body()))
                    .isEqualTo(mapper.readTree("{\"status\":\"UP\"}"));
        }
    }
}
