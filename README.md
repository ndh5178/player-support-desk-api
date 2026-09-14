# Player Support Desk API

게임 고객 문의 운영 화면과 연결할 Java 백엔드 학습 프로젝트다.
요청이 서버에서 검증·조회·저장되어 응답하는 과정을 작은 기능 단위로 구현한다.

프론트엔드: [player-support-desk](https://github.com/ndh5178/player-support-desk)

## 현재 구현

- Java 21, Spring Boot 4.1.1, Gradle Wrapper 9.7.1
- PostgreSQL 17.11, Spring Data JPA, Flyway
- 고객·담당자·문의·운영 메모·변경 이력 테이블
- 기존 Mock을 옮긴 로컬 초기 데이터
- 문의 목록·상세 및 담당자 조회 API
- 문의 상태·담당자 변경과 변경 이력 저장 API
- 운영 메모 등록과 메모 이력 저장 API
- 문의 상태·SLA·우선순위와 최근 문의를 계산하는 대시보드 API
- GET /api/health: HTTP 200과 `{"status":"UP"}` 응답
- 실제 내장 서버를 사용하는 HTTP 통합 테스트

현재 인증은 구현 전이다. 별도 Vue 프로젝트는 실제 API 모드에서 조회·변경·대시보드 API를 같은 요청 계약으로 호출한다.
health 성공은 문의 조회나 전체 서비스 정상 동작을 보장하지 않는다.

## Windows PowerShell 실행

JDK 21이 필요하다. JAVA_HOME은 JDK 설치 폴더를 가리켜야 한다.
설치와 환경 변수 변경 후에는 터미널 또는 IDE를 다시 실행한다.
Gradle은 저장소의 Wrapper를 사용하므로 별도 설치하지 않는다.
첫 실행에는 Gradle과 의존성을 다운로드할 인터넷 연결이 필요하다.

처음 한 번 로컬 설정 파일을 만든다. `.env`는 Git에 포함되지 않는다.

```powershell
Copy-Item .env.example .env
```

`.env`의 `DB_PASSWORD`를 로컬에서 사용할 값으로 바꾼다.
같은 파일을 Docker Compose와 Spring Boot가 함께 읽는다.
`APP_PROFILE=local`이면 공통 스키마와 로컬 가상 데이터가 함께 적용된다.

PostgreSQL을 먼저 실행한다.

```powershell
docker compose up -d database
```

이 명령은 PostgreSQL 17.11 컨테이너와 데이터 볼륨을 만든다.
컨테이너를 중지해도 볼륨의 데이터는 유지된다.

```powershell
cd C:\Users\ehdgu\Desktop\정글\player-support-desk-api
java -version
javac -version
echo $env:JAVA_HOME
.\gradlew.bat bootRun
```

다른 터미널에서 응답을 확인한다.

```powershell
curl.exe -i http://localhost:8080/api/health
```

기대 결과: HTTP 200, Content-Type application/json, 본문 `{"status":"UP"}`.
브라우저에서 같은 주소를 열어도 JSON을 확인할 수 있다.
서버 종료는 실행 터미널에서 Ctrl+C를 누른다.
8080 포트가 사용 중이면 `--args="--server.port=8081"`을 bootRun 뒤에 붙이고 확인 주소도 바꾼다.

조회 API는 새 터미널에서 확인한다.

```powershell
curl.exe "http://localhost:8080/api/inquiries"
curl.exe "http://localhost:8080/api/inquiries?status=NEW&sort=newest&page=1&limit=10"
curl.exe "http://localhost:8080/api/inquiries/INQ-2026-0001"
curl.exe "http://localhost:8080/api/agents"
curl.exe "http://localhost:8080/api/dashboard"
```

잘못된 목록 조건과 없는 문의의 오류 응답도 확인할 수 있다.

```powershell
curl.exe -i "http://localhost:8080/api/inquiries?page=0"
curl.exe -i "http://localhost:8080/api/inquiries/INQ-NOT-FOUND"
```

변경 API는 다음 요청으로 확인한다. 로컬 인증 전에는 `agent-001`이 작업한 것으로 기록된다.

```powershell
curl.exe -X PATCH "http://localhost:8080/api/inquiries/INQ-2026-0001" -H "Content-Type: application/json" --data-raw '{"status":"IN_PROGRESS"}'
curl.exe -X PATCH "http://localhost:8080/api/inquiries/INQ-2026-0001" -H "Content-Type: application/json" --data-raw '{"assigneeId":"agent-002"}'
curl.exe -X POST "http://localhost:8080/api/inquiries/INQ-2026-0001/notes" -H "Content-Type: application/json" --data-raw '{"content":"인증 메일 발송 로그를 확인했습니다."}'
```

변경 후 상세 조회에서 현재 상태·담당자·메모·이력을 확인한다. 같은 상태나 담당자를 다시 보내면 새 이력과 `updatedAt`을 만들지 않는다.

## 테스트와 배포 파일 생성

```powershell
.\gradlew.bat clean build
java -jar build\libs\player-support-desk-api-0.0.1-SNAPSHOT.jar
```

두 번째 명령은 생성한 실행 JAR의 로컬 실행이다. bootRun과 동시에 같은 포트로 실행하지 않는다.
테스트 보고서는 `build/reports/tests/test/index.html`에 생성된다.
macOS/Linux에서는 `./gradlew`를 사용한다.

## 구조와 문서

- `src/main/java/com/ndh5178/playersupportdesk/`: 실행 진입점과 기능별 코드
- `health/HealthController.java`: 요청 경로와 처리 함수 연결
- `health/HealthResponse.java`: JSON 응답 데이터 구조
- `src/main/resources/application.yml`: 앱 이름과 로컬 서버 주소·포트
- `compose.yaml`: 로컬 PostgreSQL 컨테이너와 데이터 볼륨
- `src/main/resources/db/migration/`: 모든 환경에서 사용하는 DB 스키마
- `src/main/resources/db/local/`: local 프로필에서만 넣는 가상 초기 데이터
- `src/test/`: 자동화 테스트
- [작업 순서](docs/IMPLEMENTATION_PLAN.md)
- [DB 컬럼·관계 설계](docs/DATABASE_DESIGN.md)
- [API 계약·Vue 연결 설계](docs/API_CONTRACT.md)
- [기술 결정](docs/DECISIONS.md)
- [Git 규칙](docs/GIT_WORKFLOW.md)
- [검증 기록](docs/QA_CHECKLIST.md)
- `study/`: Git에서 제외한 개인 학습 문서

Vue 실제 API 모드의 전체 흐름 검증과 자동 E2E 테스트는 후속 작업에서 진행한다.
공개 배포와 인증·권한 검증은 아직 진행하지 않았다.

DB만 중지하려면 `docker compose stop database`, 다시 시작하려면
`docker compose start database`를 사용한다. 데이터 볼륨 삭제는 초기화가 필요한 경우에만 별도로 진행한다.
