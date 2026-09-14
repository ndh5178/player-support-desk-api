# Git 작업 방식

develop 없이 main과 목적별 작업 브랜치를 사용한다.

1. 사용자 변경과 현재 브랜치를 확인한다.
2. 깨끗한 상태에서 main으로 이동하고 `git pull --ff-only origin main`으로 최신화한다.
3. 최신 main에서 `<종류>/<설명>` 브랜치를 새로 만든다.
4. 하나의 확인 가능한 목적을 구현하고 테스트·실행 문서를 함께 갱신한다.
5. `./gradlew clean build`와 `git diff --check`를 실행한다. Windows에서는 `gradlew.bat`을 사용한다.
6. 관련 파일만 명시적으로 스테이징하고 staged diff를 검토한다.
7. 한국어 Conventional Commits 제목과 변경 이유·핵심 내용을 담은 본문으로 커밋한다.
8. 브랜치를 push하고 템플릿을 사용해 Draft PR을 만든다.
9. 사용자가 검토·병합하고 브랜치도 직접 삭제한다. 다음 작업은 다시 최신 main에서 시작한다.

브랜치 종류: feature/, fix/, test/, docs/, chore/.
커밋 타입: feat, fix, test, docs, refactor, chore 등.
한 PR은 하나의 완결된 기능이나 기반 작업, 한 커밋은 설명 가능한 기술적 목적을 담는다.
기능과 직접 관련된 테스트·문서를 파일 종류 때문에 불필요하게 분리하지 않는다.

예시:

```text
chore: Spring Boot 실행 기반과 상태 확인 API 구성

실제 문의 API를 연결하기 전에 Java 서버의 실행과 HTTP 응답을 확인한다.
Gradle Wrapper와 상태 확인 엔드포인트, HTTP 통합 테스트 및 실행 문서를 추가한다.
```

기본 병합은 Rebase and merge로 의미 있는 커밋을 유지한다.
공개 이력 재작성, 강제 push, 사용자 변경 되돌리기는 임의로 하지 않는다.
테스트·빌드 실패를 숨기거나 검증 항목을 삭제해 통과시키지 않는다.
