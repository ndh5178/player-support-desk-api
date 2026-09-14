# 구현 순서와 작업 단위

## 완료: chore/project-setup (PR #1 병합)

목적: JDK 21과 Gradle로 서버를 실행하고 GET /api/health의 JSON 응답을 확인한다.

- Spring Boot 실행 코드와 Gradle Wrapper
- HTTP 200, application/json, status=UP 확인 테스트
- Git 작업 규칙과 PR 템플릿
- 실행 방법과 study/의 첫 HTTP 요청 학습 문서

이 API는 애플리케이션의 HTTP 응답 확인용이다. DB, 인증, Vue 연결은 포함하지 않는다.
검증 결과는 QA_CHECKLIST.md에 기록한다.

## 설계 문서: docs/database-api-design

- [DB 설계](DATABASE_DESIGN.md): 5개 테이블의 컬럼·타입·관계·NULL·저장 규칙
- [API 계약](API_CONTRACT.md): 기존 MSW와 동일한 요청·응답·오류·Vue 연결 방식
- 설계 기록이며 실제 DB·문의 API 구현 완료를 의미하지 않는다.

## 다음 작업

1. PostgreSQL Compose 실행, Flyway 테이블·로컬 시드, JPA 연결을 검증한다.
2. 문의 목록·상세 API와 검색·필터·정렬·페이지 처리를 구현한다.
3. Vue의 실제 API 모드와 개발 프록시를 연결한다. 담당자 조회 등 상세 화면의 추가 요청도 확인한다.
4. 상태·담당자 변경, 메모 저장, 변경 이력과 대시보드 집계를 연결한다.
5. 조회·변경 권한 정책과 프론트엔드 영향 범위를 합의한 뒤 인증을 구현한다.
6. 동시 수정·중복 요청·DB 실패를 재현하고 정책과 해결책을 비교해 검증한다.

각 작업은 독립적으로 검증할 수 있는 작은 PR로 나눈다.
DB 기능부터는 실제 PostgreSQL을 사용하는 테스트를 작성한다.
변경 API에서는 새로고침과 서버 재시작 후 저장 결과를 확인한다.
Vue 기존 MSW 테스트는 유지하며 실제 API 모드에서는 브라우저 MSW를 끈다.
