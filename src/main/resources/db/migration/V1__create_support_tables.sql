CREATE TABLE customers (
    id text PRIMARY KEY,
    nickname text NOT NULL,
    email text NOT NULL,
    country_code varchar(2) NOT NULL,
    country_name text NOT NULL,
    language_code varchar(35) NOT NULL,
    language_name text NOT NULL
);

CREATE TABLE agents (
    id text PRIMARY KEY,
    name text NOT NULL,
    team text NOT NULL
);

CREATE TABLE inquiries (
    id text PRIMARY KEY,
    title text NOT NULL,
    content text NOT NULL,
    category varchar(30) NOT NULL,
    priority varchar(20) NOT NULL,
    status varchar(30) NOT NULL,
    customer_id text NOT NULL REFERENCES customers (id) ON DELETE RESTRICT,
    assignee_id text REFERENCES agents (id) ON DELETE RESTRICT,
    created_at timestamptz NOT NULL,
    updated_at timestamptz NOT NULL,
    sla_due_at timestamptz NOT NULL,
    CONSTRAINT inquiries_category_check
        CHECK (category IN ('ACCOUNT', 'PAYMENT', 'GAME_ERROR', 'REPORT', 'INSTALLATION', 'OTHER')),
    CONSTRAINT inquiries_priority_check
        CHECK (priority IN ('URGENT', 'HIGH', 'NORMAL', 'LOW')),
    CONSTRAINT inquiries_status_check
        CHECK (status IN ('NEW', 'IN_PROGRESS', 'WAITING_CUSTOMER', 'RESOLVED'))
);

CREATE TABLE inquiry_notes (
    id text PRIMARY KEY,
    inquiry_id text NOT NULL REFERENCES inquiries (id) ON DELETE RESTRICT,
    author_id text NOT NULL REFERENCES agents (id) ON DELETE RESTRICT,
    content text NOT NULL,
    created_at timestamptz NOT NULL
);

CREATE TABLE inquiry_histories (
    id text PRIMARY KEY,
    inquiry_id text NOT NULL REFERENCES inquiries (id) ON DELETE RESTRICT,
    type varchar(30) NOT NULL,
    actor_name text NOT NULL,
    description text NOT NULL,
    previous_value text,
    next_value text,
    created_at timestamptz NOT NULL,
    CONSTRAINT inquiry_histories_type_check
        CHECK (type IN ('CREATED', 'STATUS_CHANGED', 'ASSIGNEE_CHANGED', 'NOTE_ADDED'))
);

CREATE INDEX inquiries_created_at_id_idx
    ON inquiries (created_at DESC, id DESC);

CREATE INDEX inquiry_notes_inquiry_created_at_id_idx
    ON inquiry_notes (inquiry_id, created_at, id);

CREATE INDEX inquiry_histories_inquiry_created_at_id_idx
    ON inquiry_histories (inquiry_id, created_at, id);
