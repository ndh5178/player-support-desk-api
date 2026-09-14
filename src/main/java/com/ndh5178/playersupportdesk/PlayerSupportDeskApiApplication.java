package com.ndh5178.playersupportdesk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlayerSupportDeskApiApplication {

    public static void main(String[] args) {
        // 이 패키지 아래의 Controller 등을 등록하고 내장 웹 서버를 시작한다.
        SpringApplication.run(PlayerSupportDeskApiApplication.class, args);
    }
}
