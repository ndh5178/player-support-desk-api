# DB 설계

상태: Flyway 테이블·로컬 시드와 JPA 모델을 구현했다. 실행 검증은 아직 하지 않았다.
기존 Vue의 Inquiry, Customer, Agent, InquiryNote, InquiryHistory를 기준으로 한다.
응답 계약은 [API_CONTRACT.md](API_CONTRACT.md)를 함께 읽는다.

## 기본 결정

- PostgreSQL에 고객·담당자·문의·운영 메모·변경 이력을 각각 저장한다.
- PK는 각 행의 고유 ID, FK는 다른 테이블의 ID를 참조하는 컬럼이다.
- 기존 문자열 ID를 그대로 보존하도록 ID 타입은 text를 사용한다.
- 아래 표에서 NULL 허용이 아니면 NOT NULL이다. PK는 고유하고 NULL을 허용하지 않는다.
- DB 컬럼은 snake_case, API JSON은 기존 camelCase를 유지한다.
- 시각은 timestamptz로 저장하고 Java Instant를 통해 ISO-8601 문자열로 응답한다.
- 제목·본문·이메일·닉네임의 최대 길이와 유일성을 임의로 추가하지 않는다.
- 문의 등록·삭제 API, 로그인·권한, 동시성 버전 컬럼은 현재 범위에 포함하지 않는다.

## inquiries — 문의

| 컬럼 | 타입 | NULL 허용 | 의미 |
| --- | --- | --- | --- |
| id | text, PK | 아니오 | INQ-2026-0001 등 문의 ID |
| title | text | 아니오 | 문의 제목 |
| content | text | 아니오 | 문의 본문 |
| category | varchar(30) | 아니오 | 문의 유형 코드 |
| priority | varchar(20) | 아니오 | 우선순위 코드 |
| status | varchar(30) | 아니오 | 현재 상태 코드 |
| customer_id | text, FK → customers.id | 아니오 | 문의한 고객 |
| assignee_id | text, FK → agents.id | 예 | 담당자, NULL은 미배정 |
| created_at | timestamptz | 아니오 | 접수 시각 |
| updated_at | timestamptz | 아니오 | 마지막 실제 변경 시각 |
| sla_due_at | timestamptz | 아니오 | 응답 목표 시각 |

허용 코드는 문자열로 저장하고 CHECK 제약으로 제한할 계획이다.

- status: NEW, IN_PROGRESS, WAITING_CUSTOMER, RESOLVED
- priority: URGENT, HIGH, NORMAL, LOW
- category: ACCOUNT, PAYMENT, GAME_ERROR, REPORT, INSTALLATION, OTHER

Java enum은 문자열 이름으로 매핑한다. 순서 번호로 저장하지 않는다.
응답 목표 시각은 접수 시각과 다르다. 지연 여부는 현재 시각과 상태로 계산하므로 별도 boolean을 저장하지 않는다.

## customers — 플레이어

| 컬럼 | 타입 | 의미 |
| --- | --- | --- |
| id | text, PK | customer-001 등 |
| nickname | text | CloudRider 등 |
| email | text | 연락 이메일 |
| country_code | varchar(2) | KR 등 |
| country_name | text | 대한민국 등 |
| language_code | varchar(35) | ko, pt-BR 등 |
| language_name | text | 한국어 등 |

모두 필수값이다. 이메일·닉네임의 중복을 금지한다는 기존 정책은 없다.
언어 코드는 실제 Mock의 pt-BR을 수용해야 하므로 두 글자로 제한하지 않는다.
국가·언어 이름은 기존 응답을 보존하려고 함께 저장한다.
별도 국가·언어 테이블은 관리 기능이 필요해질 때 검토한다.
고객 프로필 변경 기능은 없으며, 문의 접수 당시 프로필 스냅샷 보존 정책은 후속 설계 대상이다.

## agents — 운영 담당자

| 컬럼 | 타입 | 의미 |
| --- | --- | --- |
| id | text, PK | agent-001 등 |
| name | text | 담당자 이름 |
| team | text | 소속 팀 |

모두 필수값이다. 현재 Agent 타입에는 로그인 정보가 없다.
인증 단계에서 계정과 담당자의 연결, 권한, 수행자 식별 정책을 별도로 설계한다.

## inquiry_notes — 운영 메모

| 컬럼 | 타입 | NULL 허용 | 의미 |
| --- | --- | --- | --- |
| id | text, PK | 아니오 | 메모 ID |
| inquiry_id | text, FK → inquiries.id | 아니오 | 대상 문의 |
| author_id | text, FK → agents.id | 아니오 | 작성 담당자 |
| content | text | 아니오 | 앞뒤 공백 제거 후 1~1,000자 |
| created_at | timestamptz | 아니오 | 등록 시각 |

메모 하나마다 행 하나를 추가한다. 수정·삭제 기능은 현재 없다.
서버에서 작성자를 결정하며 요청에는 content만 받는다.
신규 ID는 note- 접두사와 UUID 문자열을 결합하고 기존 시드 ID는 보존한다.
현재 API의 길이는 JavaScript 문자열 length(UTF-16 코드 단위) 기준이다.
Java에서도 같은 기준으로 검증하고 이모지·공백 경계 사례를 테스트한다.
PostgreSQL 문자 수와 JavaScript length가 같다고 가정해 DB 제약만으로 대체하지 않는다.

## inquiry_histories — 처리 이력

| 컬럼 | 타입 | NULL 허용 | 의미 |
| --- | --- | --- | --- |
| id | text, PK | 아니오 | 이력 ID |
| inquiry_id | text, FK → inquiries.id | 아니오 | 대상 문의 |
| type | varchar(30) | 아니오 | 이력 종류 |
| actor_name | text | 아니오 | 작업 당시 수행자 이름 |
| description | text | 아니오 | 표시할 설명 |
| previous_value | text | 예 | 변경 전 상태 코드 또는 담당자 ID |
| next_value | text | 예 | 변경 후 상태 코드 또는 담당자 ID |
| created_at | timestamptz | 아니오 | 발생 시각 |

type은 CREATED, STATUS_CHANGED, ASSIGNEE_CHANGED, NOTE_ADDED로 제한한다.
상태 변경은 NEW → IN_PROGRESS, 담당자 변경은 agent-001 → agent-002처럼 기록한다.
배정·해제에서는 전후 값 중 하나가 NULL일 수 있다. CREATED와 NOTE_ADDED에는 전후 값이 없다.
previous_value와 next_value는 유형에 따라 의미가 달라서 담당자 FK로 만들지 않는다.
actor_name은 당시 표시 이름의 스냅샷이며 인증된 사용자 ID를 대신하지 않는다.
신규 ID는 history- 접두사와 UUID 문자열을 결합한다.

## 관계와 저장 규칙

- 고객 한 명은 여러 문의를 작성할 수 있다.
- 담당자 한 명은 여러 문의를 맡고 여러 메모를 작성할 수 있다.
- 문의 하나에는 여러 메모와 이력이 연결된다.
- 존재하지 않는 고객·담당자·문의를 참조하지 못하도록 FK를 설정한다.
- 삭제 기능이 없으므로 자동 연쇄 삭제를 사용하지 않는다. 참조 중인 행 삭제는 막는다.
- 상태·담당자가 실제로 바뀔 때만 이력과 updated_at을 갱신한다.
- 문의 변경 + 이력 추가는 하나의 트랜잭션에서 처리한다.
- 메모 추가 + NOTE_ADDED 이력 + 문의 updated_at 갱신도 하나의 트랜잭션에서 처리한다.

## 조회와 응답 조합

GET /api/inquiries/INQ-2026-0001 요청을 받으면:

1. inquiries에서 id로 문의를 찾는다.
2. customer_id와 assignee_id로 고객·담당자를 찾는다. 미배정이면 assignee=null이다.
3. inquiry_id로 메모와 이력을 조회한다.
4. 메모의 author_id로 작성자 정보를 조합한다.
5. 응답 DTO에 customer, assignee, notes, history를 담아 기존 Inquiry JSON으로 반환한다.

위 순서는 데이터 관계를 설명한다. 반드시 SQL을 항목마다 따로 실행하라는 의미가 아니다.
목록에서 문의마다 반복 조회가 발생하는지 확인하고 필요한 관계를 묶어서 가져온다.
두 개의 일대다 컬렉션을 동시에 조인한 상태로 페이지를 자르면 행이 늘어날 수 있으므로,
문의 페이지를 먼저 정하고 그 문의 ID들의 메모·이력을 모아 조회하는 방식을 우선 검토한다.

초기 인덱스 후보는 inquiries(created_at, id), notes(inquiry_id, created_at, id),
histories(inquiry_id, created_at, id)다. 최종 인덱스는 구현 쿼리와 실행 계획으로 검증한다.
필터 조합마다 인덱스를 미리 만들거나 부분 문자열 검색에 일반 인덱스가 항상 유효하다고 가정하지 않는다.
동일 생성 시각의 정렬 순서는 구현 전 API 정책으로 명시하고 회귀 테스트한다.

## 집계·시드·실행 계획

대시보드용 별도 테이블은 만들지 않는다. inquiries에서 상태·우선순위별 개수 등을 계산한다.
총 문의 수, 페이지 수, 지연 여부와 비율을 문의 컬럼에 중복 저장하지 않는다.

Flyway로 스키마를 관리하고 JPA는 스키마를 검증하도록 구성했다.
Mock 초기 문의 24건, 고객, 담당자, 메모·이력을 로컬 시드로 변환한다.
시드는 로컬 전용으로 구분하고 재시작 때 변경 데이터를 덮어쓰지 않게 한다.
기존 브라우저 localStorage의 사용자 변경은 자동 이전하지 않는다.
상대 시각 기반 초기 데이터는 시드 생성 시 절대 시각으로 고정한다.
Compose의 DB 데이터는 볼륨에 보존하고 재시작 후 유지 여부를 검증한다.

공통 스키마는 `db/migration`, 가상 시드는 `db/local`에 두어 local 프로필에서만 시드가 실행된다.
동시성은 현재 보장하지 않는다. 이후 재현 실험에서 버전 검증·조건부 갱신·중복 요청 정책을 비교한다.
