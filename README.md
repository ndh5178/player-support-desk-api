# Player Support Desk API

게임 고객 문의 운영 화면과 연결할 Java 백엔드 학습 프로젝트다.
요청이 서버에서 검증·조회·저장되어 응답하는 과정을 작은 기능 단위로 구현한다.

프론트엔드: [player-support-desk](https://github.com/ndh5178/player-support-desk)

## 현재 구현

- Java 21, Spring Boot 4.1.1, Gradle Wrapper 9.7.1
- GET /api/health: HTTP 200과 `{"status":"UP"}` 응답
- 실제 내장 서버를 사용하는 HTTP 통합 테스트

현재 DB·인증·문의 API·Vue 실제 서버 연결은 구현 전이다.
health 성공은 DB 상태나 전체 서비스 정상 동작을 보장하지 않는다.

## Windows PowerShell 실행

JDK 21이 필요하다. JAVA_HOME은 JDK 설치 폴더를 가리켜야 한다.
설치와 환경 변수 변경 후에는 터미널 또는 IDE를 다시 실행한다.
Gradle은 저장소의 Wrapper를 사용하므로 별도 설치하지 않는다.
첫 실행에는 Gradle과 의존성을 다운로드할 인터넷 연결이 필요하다.

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
- `src/test/`: 자동화 테스트
- [작업 순서](docs/IMPLEMENTATION_PLAN.md)
- [DB 컬럼·관계 설계](docs/DATABASE_DESIGN.md)
- [API 계약·Vue 연결 설계](docs/API_CONTRACT.md)
- [기술 결정](docs/DECISIONS.md)
- [Git 규칙](docs/GIT_WORKFLOW.md)
- [검증 기록](docs/QA_CHECKLIST.md)
- `study/`: Git에서 제외한 개인 학습 문서

PostgreSQL·JPA·Flyway·Docker Compose는 다음 DB 연결 작업에서 추가한다.
공개 배포와 인증·권한 검증은 아직 진행하지 않았다.
