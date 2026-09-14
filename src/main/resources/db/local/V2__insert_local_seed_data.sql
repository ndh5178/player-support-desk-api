INSERT INTO agents (id, name, team) VALUES
    ('agent-001', '김서윤', 'Player Care'),
    ('agent-002', '박민준', 'Technical Support'),
    ('agent-003', 'Avery Chen', 'Account & Payment'),
    ('agent-004', 'Mina Patel', 'Safety Operations');

INSERT INTO customers (
    id, nickname, email, country_code, country_name, language_code, language_name
) VALUES
    ('customer-001', 'CloudRider', 'cloud.rider@example.com', 'KR', '대한민국', 'ko', '한국어'),
    ('customer-002', 'MapleFox', 'maple.fox@example.com', 'CA', '캐나다', 'en', '영어'),
    ('customer-003', 'LunaByte', 'luna.byte@example.com', 'JP', '일본', 'ja', '일본어'),
    ('customer-004', 'NordicRay', 'nordic.ray@example.com', 'SE', '스웨덴', 'en', '영어'),
    ('customer-005', 'PixelMate', 'pixel.mate@example.com', 'AU', '호주', 'en', '영어'),
    ('customer-006', 'RioSpark', 'rio.spark@example.com', 'BR', '브라질', 'pt-BR', '포르투갈어'),
    ('customer-007', 'AlpineCat', 'alpine.cat@example.com', 'DE', '독일', 'de', '독일어'),
    ('customer-008', 'CobaltWing', 'cobalt.wing@example.com', 'US', '미국', 'en', '영어'),
    ('customer-009', 'SierraSol', 'sierra.sol@example.com', 'MX', '멕시코', 'es', '스페인어'),
    ('customer-010', 'MetroPanda', 'metro.panda@example.com', 'SG', '싱가포르', 'en', '영어'),
    ('customer-011', 'VelvetComet', 'velvet.comet@example.com', 'FR', '프랑스', 'fr', '프랑스어'),
    ('customer-012', 'HarborStone', 'harbor.stone@example.com', 'GB', '영국', 'en', '영어');

WITH inquiry_seed (
    id, title, content, category, priority, status, customer_id, assignee_id,
    created_hours_ago, sla_hours_from_now
) AS (VALUES
    ('INQ-2026-0001', 'KRAFTON ID 인증 메일이 도착하지 않습니다', '새 PC에서 KRAFTON ID로 로그인하려고 했지만 인증 이메일을 여러 번 요청해도 수신되지 않습니다. 스팸함에도 메일이 없습니다.', 'ACCOUNT', 'HIGH', 'NEW', 'customer-001', NULL, 1.0, 3.0),
    ('INQ-2026-0002', 'G-Coin 상품이 두 번 결제되었습니다', 'G-Coin 결제 완료 화면이 늦게 표시되어 버튼을 다시 눌렀고 카드 명세에 동일한 금액이 두 번 승인되었습니다.', 'PAYMENT', 'URGENT', 'IN_PROGRESS', 'customer-002', 'agent-003', 6.0, -1.0),
    ('INQ-2026-0003', '패치 후 로비 진입 시 검은 화면에서 멈춥니다', '오늘 업데이트 이후 로비에 진입하면 소리만 들리고 화면은 검은색으로 유지됩니다. Steam 파일 무결성 검사는 완료했습니다.', 'GAME_ERROR', 'HIGH', 'IN_PROGRESS', 'customer-003', 'agent-002', 8.0, 2.0),
    ('INQ-2026-0004', '스쿼드 음성 채팅에서 욕설한 플레이어를 신고합니다', '스쿼드 매치에서 같은 플레이어가 팀원에게 지속적으로 욕설했습니다. 경기 시각과 닉네임을 본문에 정리했습니다.', 'REPORT', 'HIGH', 'NEW', 'customer-004', NULL, 2.0, 6.0),
    ('INQ-2026-0005', 'Steam 패치 설치 중 디스크 공간 오류가 발생합니다', '설치 드라이브에 충분한 공간이 있지만 패치가 70%에서 중단되고 디스크 공간 부족 메시지가 표시됩니다.', 'INSTALLATION', 'NORMAL', 'WAITING_CUSTOMER', 'customer-005', 'agent-002', 20.0, 8.0),
    ('INQ-2026-0006', 'KRAFTON ID의 거주 국가를 변경하고 싶습니다', '최근 거주 국가가 바뀌어 프로필의 국가와 기본 언어를 변경하고 싶습니다. 필요한 확인 절차를 알려주세요.', 'ACCOUNT', 'LOW', 'RESOLVED', 'customer-006', 'agent-001', 72.0, -48.0),
    ('INQ-2026-0007', '구매한 무기 스킨이 보관함에 지급되지 않았습니다', '상점에서 무기 스킨을 구매했고 G-Coin 차감도 확인했지만 게임 내 보관함에는 아이템이 표시되지 않습니다.', 'PAYMENT', 'HIGH', 'IN_PROGRESS', 'customer-007', 'agent-003', 12.0, -2.0),
    ('INQ-2026-0008', '경기 시작 직후 매치 서버 연결이 종료됩니다', '로비에서는 문제가 없지만 매치가 시작되면 1분 안에 연결이 종료됩니다. 다른 온라인 서비스는 정상입니다.', 'GAME_ERROR', 'URGENT', 'IN_PROGRESS', 'customer-008', 'agent-002', 4.0, 1.0),
    ('INQ-2026-0009', '부정행위 신고 결과를 확인하고 싶습니다', '일주일 전에 비정상 플레이 사용자를 신고했습니다. 신고가 접수되었는지와 확인이 끝났는지 알고 싶습니다.', 'REPORT', 'LOW', 'WAITING_CUSTOMER', 'customer-009', 'agent-004', 30.0, 10.0),
    ('INQ-2026-0010', 'Steam 업데이트가 계속 처음부터 다시 시작됩니다', '업데이트가 완료된 것처럼 보인 뒤 런처를 다시 열면 같은 파일을 처음부터 다시 내려받습니다.', 'INSTALLATION', 'NORMAL', 'NEW', 'customer-010', NULL, 3.0, 9.0),
    ('INQ-2026-0011', 'KRAFTON ID 비밀번호 재설정 링크가 만료됩니다', '메일을 받은 직후 링크를 열어도 만료된 링크라는 메시지가 표시됩니다. 다른 브라우저에서도 동일합니다.', 'ACCOUNT', 'NORMAL', 'IN_PROGRESS', 'customer-011', 'agent-001', 15.0, 5.0),
    ('INQ-2026-0012', 'G-Coin 환불 요청 상태를 확인하고 싶습니다', '웹사이트에서 환불을 신청한 지 사흘이 지났지만 진행 상태가 바뀌지 않아 확인을 요청합니다.', 'PAYMENT', 'NORMAL', 'WAITING_CUSTOMER', 'customer-012', 'agent-003', 50.0, 12.0),
    ('INQ-2026-0013', 'DirectX 오류와 함께 게임이 강제 종료됩니다', '최신 드라이버 설치 후 게임을 실행하면 충돌 보고서가 표시됩니다. 이전 버전에서는 정상 실행되었습니다.', 'GAME_ERROR', 'NORMAL', 'RESOLVED', 'customer-001', 'agent-002', 96.0, -70.0),
    ('INQ-2026-0014', '부적절한 배틀그라운드 닉네임을 신고합니다', '공개 채널에서 혐오 표현이 포함된 닉네임을 확인했습니다. 사용자 식별 정보와 확인 시각을 전달합니다.', 'REPORT', 'NORMAL', 'RESOLVED', 'customer-002', 'agent-004', 80.0, -50.0),
    ('INQ-2026-0015', 'Steam 설치 경로를 다른 드라이브로 옮기고 싶습니다', '전체 파일을 다시 내려받지 않고 설치 폴더를 다른 드라이브로 이동할 수 있는 방법이 있는지 문의합니다.', 'INSTALLATION', 'LOW', 'RESOLVED', 'customer-003', 'agent-001', 120.0, -90.0),
    ('INQ-2026-0016', 'KRAFTON ID와 Steam 계정 연동을 해제하고 싶습니다', '더 이상 접근할 수 없는 외부 계정이 연결되어 있습니다. 본인 확인 후 연동을 해제하고 싶습니다.', 'ACCOUNT', 'NORMAL', 'WAITING_CUSTOMER', 'customer-004', 'agent-001', 28.0, -4.0),
    ('INQ-2026-0017', 'G-Coin 결제 수단을 등록할 수 없습니다', '카드 정보를 올바르게 입력해도 결제 수단을 확인할 수 없다는 메시지가 표시됩니다.', 'PAYMENT', 'HIGH', 'NEW', 'customer-005', NULL, 5.0, 3.0),
    ('INQ-2026-0018', '스쿼드 음성 채팅이 몇 초마다 끊깁니다', '게임 소리는 정상이지만 팀 음성만 주기적으로 끊깁니다. 입력 장치를 바꿔도 문제가 계속됩니다.', 'GAME_ERROR', 'NORMAL', 'WAITING_CUSTOMER', 'customer-006', 'agent-002', 18.0, 7.0),
    ('INQ-2026-0019', '고의적인 팀킬 플레이어를 신고합니다', '경기 시작부터 같은 팀원을 반복적으로 공격하고 진행을 방해한 사용자의 확인을 요청합니다.', 'REPORT', 'URGENT', 'IN_PROGRESS', 'customer-007', 'agent-004', 7.0, -0.5),
    ('INQ-2026-0020', '노트북에서 배틀그라운드를 실행할 수 있는지 궁금합니다', '공식 사양표에 없는 내장 그래픽 환경에서 실행 가능한지 확인하고 싶습니다.', 'INSTALLATION', 'LOW', 'RESOLVED', 'customer-008', 'agent-001', 140.0, -110.0),
    ('INQ-2026-0021', '보호자 승인 KRAFTON ID의 이메일을 변경하고 싶습니다', '보호자 이메일을 더 이상 사용할 수 없어 새 이메일로 변경하려고 합니다. 필요한 서류가 궁금합니다.', 'ACCOUNT', 'HIGH', 'NEW', 'customer-009', NULL, 2.5, 5.0),
    ('INQ-2026-0022', 'G-Coin 가격이 지역 통화와 다르게 표시됩니다', '프로필 국가와 접속 지역은 동일하지만 상점의 일부 상품만 다른 통화로 표시됩니다.', 'PAYMENT', 'NORMAL', 'IN_PROGRESS', 'customer-010', 'agent-003', 10.0, 6.0),
    ('INQ-2026-0023', '매치메이킹 완료 직후 다시 로비로 돌아갑니다', '경쟁전 매칭이 완료된 직후 연결 오류가 표시되고 로비로 돌아갑니다.', 'GAME_ERROR', 'NORMAL', 'RESOLVED', 'customer-011', 'agent-002', 60.0, -35.0),
    ('INQ-2026-0024', '생존자 패스 미션 보상이 지급되지 않았습니다', '이벤트 조건을 충족했고 완료 알림도 확인했지만 계정에 보상이 지급되지 않았습니다.', 'OTHER', 'HIGH', 'NEW', 'customer-012', NULL, 1.5, 4.0)
)
INSERT INTO inquiries (
    id, title, content, category, priority, status, customer_id, assignee_id,
    created_at, updated_at, sla_due_at
)
SELECT
    id,
    title,
    content,
    category,
    priority,
    status,
    customer_id,
    assignee_id,
    CURRENT_TIMESTAMP - created_hours_ago * INTERVAL '1 hour',
    CASE
        WHEN assignee_id IS NOT NULL
            THEN CURRENT_TIMESTAMP - created_hours_ago * 0.45 * INTERVAL '1 hour'
        ELSE CURRENT_TIMESTAMP - created_hours_ago * INTERVAL '1 hour'
    END,
    CURRENT_TIMESTAMP + sla_hours_from_now * INTERVAL '1 hour'
FROM inquiry_seed;

INSERT INTO inquiry_histories (
    id, inquiry_id, type, actor_name, description, previous_value, next_value, created_at
)
SELECT
    id || '-history-created',
    id,
    'CREATED',
    '고객',
    '고객 문의가 접수되었습니다.',
    NULL,
    NULL,
    created_at
FROM inquiries;

INSERT INTO inquiry_histories (
    id, inquiry_id, type, actor_name, description, previous_value, next_value, created_at
)
SELECT
    inquiry.id || '-history-assignee',
    inquiry.id,
    'ASSIGNEE_CHANGED',
    '자동 배정',
    agent.name || ' 담당자에게 배정되었습니다.',
    NULL,
    agent.id,
    inquiry.created_at + (CURRENT_TIMESTAMP - inquiry.created_at) * 0.15
FROM inquiries inquiry
JOIN agents agent ON agent.id = inquiry.assignee_id;

INSERT INTO inquiry_histories (
    id, inquiry_id, type, actor_name, description, previous_value, next_value, created_at
)
SELECT
    inquiry.id || '-history-status',
    inquiry.id,
    'STATUS_CHANGED',
    COALESCE(agent.name, '운영 담당자'),
    '문의 상태가 ' || inquiry.status || '(으)로 변경되었습니다.',
    'NEW',
    inquiry.status,
    inquiry.created_at + (CURRENT_TIMESTAMP - inquiry.created_at) * 0.30
FROM inquiries inquiry
LEFT JOIN agents agent ON agent.id = inquiry.assignee_id
WHERE inquiry.status <> 'NEW';

WITH note_seed (inquiry_id, content) AS (VALUES
    ('INQ-2026-0002', '결제 승인 번호 두 건을 확인하고 있습니다.'),
    ('INQ-2026-0003', '그래픽 설정 초기화 절차를 안내했습니다.'),
    ('INQ-2026-0005', '설치 로그와 드라이브 정보를 요청했습니다.'),
    ('INQ-2026-0006', '국가 변경 정책과 본인 확인 절차를 안내했습니다.'),
    ('INQ-2026-0007', '상품 지급 내역과 거래 로그를 대조하고 있습니다.'),
    ('INQ-2026-0008', '네트워크 진단 파일을 분석 중입니다.'),
    ('INQ-2026-0009', '개별 제재 결과는 공유하기 어렵다는 정책을 안내했습니다.'),
    ('INQ-2026-0011', '이전 재설정 요청을 만료시키고 새 링크를 발급했습니다.'),
    ('INQ-2026-0012', '결제 수단에 따른 환불 처리 기간을 안내했습니다.'),
    ('INQ-2026-0013', '안정 버전 드라이버 설치 후 정상 실행을 확인했습니다.'),
    ('INQ-2026-0014', '운영 정책에 따라 닉네임 변경 조치를 완료했습니다.'),
    ('INQ-2026-0015', '런처의 설치 경로 변경 절차를 안내했습니다.'),
    ('INQ-2026-0016', '연동 해제에 필요한 본인 확인 항목을 요청했습니다.'),
    ('INQ-2026-0018', '음성 장치 설정 화면과 진단 파일을 요청했습니다.'),
    ('INQ-2026-0019', '경기 기록과 반복 신고 이력을 확인하고 있습니다.'),
    ('INQ-2026-0020', '지원 사양과 예상 성능 범위를 안내했습니다.'),
    ('INQ-2026-0022', '계정의 결제 지역 설정을 확인하고 있습니다.'),
    ('INQ-2026-0023', '손상된 캐시를 정리한 뒤 정상 매칭을 확인했습니다.')
)
INSERT INTO inquiry_notes (id, inquiry_id, author_id, content, created_at)
SELECT
    inquiry.id || '-note-001',
    inquiry.id,
    inquiry.assignee_id,
    note_seed.content,
    inquiry.created_at + (CURRENT_TIMESTAMP - inquiry.created_at) * 0.55
FROM note_seed
JOIN inquiries inquiry ON inquiry.id = note_seed.inquiry_id;
