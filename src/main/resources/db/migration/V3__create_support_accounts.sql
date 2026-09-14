CREATE TABLE support_accounts (
    agent_id text PRIMARY KEY REFERENCES agents (id) ON DELETE RESTRICT,
    username varchar(100) NOT NULL UNIQUE,
    password_hash varchar(100) NOT NULL,
    enabled boolean NOT NULL DEFAULT true
);
