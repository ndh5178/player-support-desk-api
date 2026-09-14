# 기술 결정

## Java 21, Spring Boot 4.1.1, Gradle 9.7.1

Java 21을 학습 기준으로 고정한다. 공식 Spring Initializr에서 생성 가능한
안정 버전 4.1.1과 생성기가 제공하는 Gradle 9.7.1 Wrapper를 사용한다.
Spring Boot가 관리하는 의존성 버전을 사용하며 개별 Spring 라이브러리 버전을 따로 지정하지 않는다.
Groovy build.gradle을 사용하고 전역 Gradle 설치를 요구하지 않는다.

- https://docs.spring.io/spring-boot/system-requirements.html
- https://docs.gradle.org/current/userguide/compatibility.html
- https://start.spring.io/

## 첫 작업은 HTTP 응답까지

DB 연결이 없는 상태에서도 실행과 요청 전달을 이해하도록 Web MVC만 추가한다.
JPA, PostgreSQL 드라이버, Flyway는 다음 DB 작업에서 함께 추가한다.
HealthController에 업무 처리나 저장이 없으므로 불필요한 Service·Repository를 만들지 않는다.
GET /api/health는 항상 {"status":"UP"}를 응답하며 DB readiness를 의미하지 않는다.

## 응답 DTO와 학습

HealthResponse record는 JSON 응답 구조를 명시한다.
Lombok이나 매핑 라이브러리를 도입하지 않는다.
코드의 목적은 주석으로 짧게, 실행 순서와 문법은 study/에서 설명한다.
study/는 기존 프론트엔드의 개인 학습 폴더와 같이 Git에서 제외한다.

## 로컬 실행 범위

서버는 127.0.0.1:8080에 바인딩한다. 현재 인증이 없는 로컬 학습용이다.
이번 API는 Vue 기존 API와 별개의 실행 확인 엔드포인트다.
Vue를 연결할 때 기존 /api 계약을 유지하고 Vite 개발 프록시와 MSW 모드 선택을 구성한다.
추후 컨테이너에서 앱을 실행할 때는 컨테이너 내부 바인딩과 호스트 포트 노출을 별도로 설정한다.

## HTTP 통합 테스트

SpringBootTest RANDOM_PORT로 실제 내장 서버를 시작한다.
Java HttpClient로 상태 코드, Content-Type, JSON 내용을 검증한다.
테스트 포트는 임의로 배정해 개발용 8080 포트와 충돌하지 않게 한다.
HTTP 요청에는 5초 제한을 두며 테스트 종료 시 클라이언트를 닫는다.
