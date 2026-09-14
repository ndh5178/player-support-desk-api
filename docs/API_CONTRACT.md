# API 계약과 Vue 연결 설계

상태: 문의 목록·상세·담당자 조회와 문의 변경·메모 등록을 구현했다. 대시보드는 구현 전이다.

## 확인한 근거

프론트엔드 커밋 04bbf398c45f813244cee653380b80f47676ef00의 다음 파일을 기준으로 한다.

- src/services/api.ts: getDashboard, getAgents, getInquiries, getInquiry, updateInquiry, addInquiryNote
- src/types/inquiry.ts, src/types/api.ts: 요청·응답 타입
- src/mocks/handlers.ts: 검증, 조회, 변경, 집계 규칙
- src/mocks/data.ts: 기존 가상 데이터
- src/stores/inquiry.ts: 응답 이후 상태 반영
- tests/integration/mock-api.spec.ts: API 계약 테스트

## 공통

- 기본 경로: /api. GET은 200, PATCH는 200, 메모 POST는 201.
- 요청 본문과 성공 응답은 JSON이다. API 필드는 camelCase를 유지한다.
- ID는 문자열, 시각은 ISO-8601 문자열이다.
- 목록·담당자 응답 외에는 data로 감싸지 않는다.
- 존재하지 않는 문의는 404 INQUIRY_NOT_FOUND다.
- 잘못된 입력은 400 VALIDATION_ERROR, 파싱 불가능한 JSON은 400 INVALID_JSON이다.

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "목록 조회 조건을 확인해 주세요.",
    "details": { "page": "1 이상의 정수여야 합니다." }
  }
}
```

details는 선택 필드이며 없으면 생략할 수 있다.
Vue의 requestJson은 실패 응답을 ApiError로 변환한다.
서버 구현 시 예상하지 못한 DB·서버 실패도 동일한 외형의 500 오류로 변환할 계획이다.
그때 사용할 코드·메시지는 새 서버 정책으로 확정하며 SQL이나 비밀값을 응답하지 않는다.
현재 Spring 기본 오류 응답이 이 계약을 만족한다고 가정하지 않는다.

## 공통 응답 데이터 구조

아래는 실제 응답 값이 아닌 필드와 타입의 명세다.

```typescript
interface Agent {
  id: string
  name: string
  team: string
}
interface Customer {
  id: string
  nickname: string
  email: string
  countryCode: string
  countryName: string
  languageCode: string
  languageName: string
}
interface InquiryNote {
  id: string
  content: string
  author: Agent
  createdAt: string
}
interface InquiryHistory {
  id: string
  type: 'CREATED' | 'STATUS_CHANGED' | 'ASSIGNEE_CHANGED' | 'NOTE_ADDED'
  actorName: string
  description: string
  createdAt: string
  previousValue?: string | null
  nextValue?: string | null
}
interface Inquiry {
  id: string
  title: string
  content: string
  category: 'ACCOUNT' | 'PAYMENT' | 'GAME_ERROR' | 'REPORT' | 'INSTALLATION' | 'OTHER'
  priority: 'URGENT' | 'HIGH' | 'NORMAL' | 'LOW'
  status: 'NEW' | 'IN_PROGRESS' | 'WAITING_CUSTOMER' | 'RESOLVED'
  customer: Customer
  assignee: Agent | null
  createdAt: string
  updatedAt: string
  slaDueAt: string
  history: InquiryHistory[]
  notes: InquiryNote[]
}
```

## GET /api/inquiries

구현됨. InquiryController → InquiryService → InquiryRepository 흐름으로 처리한다.

| Query | 생략 시 기본값 | 규칙 |
| --- | --- | --- |
| search | 빈 문자열 | 앞뒤 공백 제거, ID·제목·고객 닉네임 중 부분 일치 |
| status | 필터 없음 | Inquiry.status의 허용 코드 |
| priority | 필터 없음 | Inquiry.priority의 허용 코드 |
| category | 필터 없음 | Inquiry.category의 허용 코드 |
| sort | newest | newest 또는 oldest |
| page | 1 | 1 이상의 정수 |
| limit | 10 | 1~50 정수 |

검색은 현재 Mock에서 ko-KR 기준 소문자로 바꾼 뒤 비교한다.
SQL 구현은 동일한 검색 의도를 유지하고 %, _ 같은 문자를 와일드카드로 오해하지 않도록 검증한다.
모든 필터는 AND로 결합한다. 정렬 기준은 createdAt이며 newest는 내림차순이다.
Query가 중복되면 기존 URLSearchParams.get과 같이 첫 값을 사용한다.
알 수 없는 Query 이름은 기존 Mock처럼 무시한다. 빈 상태·정렬 값은 유효한 코드가 아니므로 400이다.
page와 limit은 숫자 문자열을 검증하고 정수 오버플로를 막는다.
현재 Mock의 범위보다 더 좁은 서버 페이지 상한을 도입한다면 계약 변경으로 먼저 설명한다.

예시 요청:

```http
GET /api/inquiries?status=NEW&sort=newest&page=1&limit=10
```

응답: `{ data: Inquiry[], pagination: { page, limit, total, totalPages } }`.
total은 필터 후 전체 개수, totalPages는 ceil(total / limit)이다.
문의가 없으면 data=[], total=0, totalPages=0이다.
마지막 페이지보다 큰 page를 요청하면 해당 page를 유지하고 빈 data를 반환한다.
목록도 기존과 동일한 전체 Inquiry 객체를 반환하며 notes/history 필드를 임의로 제거하지 않는다.

## GET /api/inquiries/{id}

구현됨. 고객·담당자와 문의 ID에 연결된 메모·이력을 조합한다.

예: GET /api/inquiries/INQ-2026-0001.
성공 본문은 Inquiry 객체 하나다. notes와 history를 포함한다.
별도 메모 조회 API는 현재 없다. 없는 문의의 메시지는 '요청한 문의를 찾을 수 없습니다.'다.

## GET /api/agents

구현됨. 담당자 ID 오름차순으로 반환한다.

성공 본문: `{ data: Agent[] }`.
담당자 선택 UI가 사용하므로 상세 화면 연결 단계에 함께 구현한다.

## PATCH /api/inquiries/{id}

구현됨. `InquiryController.updateInquiry`가 요청 필드의 존재 여부와 값을 검증하고 `InquiryService.updateInquiry`가 변경과 이력 저장을 한 트랜잭션으로 처리한다.

```json
{ "status": "IN_PROGRESS", "assigneeId": "agent-001" }
```

- status와 assigneeId 중 하나 이상을 보내야 한다.
- status는 허용 코드만 가능하며 null은 허용하지 않는다.
- assigneeId 생략은 기존 배정 유지, null은 배정 해제, 문자열은 해당 담당자로 배정이다.
- 존재하지 않는 담당자 ID는 400 VALIDATION_ERROR다.
- 두 값을 함께 보내면 둘 다 검증한 뒤 함께 변경한다.
- 같은 상태·같은 담당자라면 해당 이력을 추가하지 않는다.
- 실제 변경이 하나도 없으면 updatedAt도 유지하고 기존 Inquiry를 200으로 반환한다.
- 실제 변경 시 상태·담당자별 이력과 updatedAt을 저장한 뒤 전체 Inquiry를 반환한다.
- 기존 Mock은 추가로 전달된 알 수 없는 필드를 무시하며, 알려진 필드가 하나도 없으면 실패한다.

입력 예시의 NEW → IN_PROGRESS 변경 흐름:

```text
Vue 관리 폼 이벤트
→ Store의 변경 작업
→ updateInquiry(id, {status: 'IN_PROGRESS'})
→ PATCH /api/inquiries/INQ-2026-0001
→ Controller: 경로 ID와 요청 본문 검증
→ Service: 문의 조회와 상태 변경 판단
→ Repository / DB: status 변경 + 이력 추가 + updated_at 갱신
→ 트랜잭션 커밋
→ Inquiry 응답
→ Pinia의 상세·이미 조회한 목록 항목 갱신
→ Vue 화면 반영
```

트랜잭션 실패 시 부분 저장하지 않고 오류를 반환한다.

## POST /api/inquiries/{id}/notes

구현됨. `InquiryController.addInquiryNote`가 내용을 검증하고 `InquiryService.addInquiryNote`가 메모, `NOTE_ADDED` 이력과 문의 수정 시각을 한 트랜잭션으로 저장한다.

```json
{ "content": "인증 메일 발송 로그를 확인했습니다." }
```

content는 문자열이며 trim 후 1~1,000자여야 한다.
신규 InquiryNote를 반환하며 상태 코드는 201이다.
서버는 메모, NOTE_ADDED 이력, 문의 updatedAt을 한 트랜잭션으로 저장한다.
현재 Mock의 수행자는 agent-001이다. 로컬 인증 전에는 서버가 이 고정 담당자를 선택한다.
실제 인증·권한 보호를 제공하는 것으로 설명하지 않는다.
현재 Pinia는 반환된 메모를 추가하고 createdAt으로 updatedAt과 임시 표시 이력을 구성한다.
재조회 시 서버에 저장된 이력이 기준이 된다. 메모 응답을 전체 Inquiry로 바꾸지 않는다.
같은 내용의 POST를 두 번 보내면 현재 계약에서는 메모 두 개가 생성된다.
중복 방지는 이후 동시성·중복 요청 정책에서 다룬다.

## GET /api/dashboard

| 필드 | 타입 | 계산 규칙 |
| --- | --- | --- |
| totalCount | number | 전체 문의 개수 |
| newCount | number | status=NEW |
| inProgressCount | number | status=IN_PROGRESS |
| slaOverdueCount | number | status가 RESOLVED가 아니고 slaDueAt < 현재 시각 |
| recentInquiries | Inquiry[] | createdAt 최신순 최대 5개 |
| priorityDistribution | {priority, count}[] | URGENT, HIGH, NORMAL, LOW 순서, 0건도 포함 |

data로 감싸지 않는다. 집계는 저장된 문의 원본을 기준으로 하고 UI 표시 비율은 프론트엔드에서 계산한다.

## Vue 연결과 역할

현재 Vue는 같은 origin의 /api로 fetch한다. 실제 연결 단계에서 Vite의 /api 프록시를
http://127.0.0.1:8080으로 구성할 계획이다. Vue는 5173, Spring은 8080, DB는 5432를 사용한다.
DB 포트는 로컬에만 노출하고 접속 비밀값은 Git에서 제외한다.

- MSW 모드: VITE_ENABLE_MOCKS=true.
- 실제 API 모드: VITE_ENABLE_MOCKS=false. 현재 main.ts가 이 조건에서 worker.start를 생략한다.
- 모드 변경 후 개발 서버를 재시작하고 브라우저도 새로고침해 기존 Worker가 가로채지 않는지 확인한다.
- 프록시 방식에서는 브라우저가 같은 origin으로 요청하므로 개발용 CORS 추가가 필요하지 않다.
- 브라우저가 8080에 직접 요청하도록 바꾸면 허용 origin 설정을 별도로 검토한다.

프론트엔드는 URL 정규화·검색 debounce·로딩·오류·요청 취소·Pinia·Props/Emit을 유지한다.
서버는 입력 재검증·검색·필터·정렬·페이지·저장·이력·집계를 담당한다.
잘못된 Vue URL을 기본값으로 정규화하는 것과, API 직접 호출의 잘못된 값을 400으로 거절하는 것은 별개다.
Pinia는 브라우저 상태이며 DB나 다른 사용자의 화면과 자동 동기화되지 않는다.

## 구현 시 검증할 항목

- 기본 목록, 복합 필터, 검색 문자·대소문자, 정렬, 페이지 경계와 빈 결과
- 잘못된 Query, 중복 Query, 없는 문의, 없는 담당자
- assigneeId 생략/null 구분, 동일 값 변경 시 이력·시각 유지
- 메모 공백·길이·이모지 경계, 저장 실패 시 전체 롤백
- 상태 변경 후 목록·대시보드와 새로고침·서버 재시작 후 DB 유지
- MSW 모드 회귀와 실제 API 모드의 가로채기 비활성화

조회 단계에서는 /inquiries에 직접 접속해 검증한다.
대시보드·변경 API를 구현하기 전에는 실제 API 모드에서 앱 전체가 완성됐다고 표시하지 않는다.
