# 검증 기록

## Spring Boot 기본 구성 — 2026-09-14

환경: Windows x64, Temurin JDK 21.0.12.1, Spring Boot 4.1.1, Gradle 9.7.1.

- [x] `gradlew.bat clean build --no-daemon` 성공
- [x] HealthApiTest 1건 성공, 실패 0건, 생략 0건
- [x] 실제 임의 포트 서버에서 HTTP 200, application/json, 정확한 JSON 응답 검증
- [x] `gradlew.bat bootRun --no-daemon` 실행 후 localhost:8080/api/health 응답 확인
- [x] 생성한 실행 JAR로 서버 시작 후 같은 주소에서 HTTP 200과 `{"status":"UP"}` 확인
- [x] 확인용 개발 서버와 JAR 프로세스 종료
- [x] `git diff --check` 통과
- [x] study/가 Git에서 제외되는지 확인
- [x] 사용자 JAVA_HOME을 설치된 JDK 21 폴더로 설정

테스트 과정에서 Spring 테스트 의존성의 Mockito 동적 agent 로딩 경고가 출력됐다.
현재 JDK 21에서 테스트는 통과했으며 이후 JDK 변경 시 agent 설정을 검토한다.
DB·인증·Vue 연결과 macOS/Linux 실행은 이번 검증 대상이 아니다.
UI를 변경하지 않아 프론트엔드 테스트는 재실행하지 않았다.

## DB·API 설계 문서 — 2026-09-14

- [x] 기존 Vue 타입·API 호출·MSW 핸들러와 문서의 필드·경로·검증 규칙 대조
- [x] Markdown 내부 문서 링크와 코드 블록 닫힘 확인
- [x] `gradlew.bat clean build --offline --no-daemon` 성공, 기존 HTTP 테스트 1건 통과
- [x] `git diff --check` 통과

문서만 변경했다. DB 스키마 실행, 문의 API와 Vue 실제 연결은 검증 전이다.
