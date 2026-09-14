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

## 문의 조회 API — 검증 대기

- [ ] 기본 목록 10건과 pagination 확인
- [ ] 검색·상태·우선순위·유형 필터 조합 확인
- [ ] 최신순·오래된순과 페이지 경계 확인
- [ ] 잘못된 Query의 400 오류 형식 확인
- [ ] 상세의 고객·담당자·메모·이력 확인
- [ ] 없는 문의의 404 오류 형식 확인
- [ ] 담당자 4명 조회 확인

사용자가 직접 검증하기로 하여 구현 시점에는 컴파일·테스트·HTTP 요청을 실행하지 않았다.

## 문의 변경·메모 API — 검증 대기

- [ ] 상태 변경 후 문의, `updatedAt`, `STATUS_CHANGED` 이력 확인
- [ ] 담당자 배정·변경·해제와 `ASSIGNEE_CHANGED` 이력 확인
- [ ] 상태와 담당자를 한 요청에서 함께 변경
- [ ] 같은 값 재요청 시 이력과 `updatedAt` 유지
- [ ] 존재하지 않는 담당자와 허용하지 않는 상태의 400 오류 확인
- [ ] 필드 생략과 `assigneeId: null`의 차이 확인
- [ ] 운영 메모 공백 제거, 등록과 `NOTE_ADDED` 이력 확인
- [ ] 빈 메모·1,000자 초과·잘못된 JSON의 400 오류 확인
- [ ] 변경 후 새로고침과 서버 재시작에도 PostgreSQL 값 유지
- [ ] Vue 실제 API 모드에서 상태·담당자 변경과 메모 등록 확인

사용자가 직접 검증하기로 하여 구현 시점에는 컴파일·테스트·HTTP 요청을 실행하지 않았다.
