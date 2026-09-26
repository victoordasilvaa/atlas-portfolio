-- Initial migration; PostgreSQL. Applied once by the database container.
CREATE SEQUENCE project_seq START WITH 1 INCREMENT BY 1;
CREATE TABLE member_snapshot (
 id BIGINT PRIMARY KEY, name VARCHAR(160) NOT NULL, role VARCHAR(80) NOT NULL,
 version INTEGER NOT NULL DEFAULT 0, date_last_update TIMESTAMP NOT NULL, user_last_update VARCHAR(150) NOT NULL
);
CREATE TABLE project (
 id BIGINT DEFAULT nextval('project_seq') PRIMARY KEY, name VARCHAR(160) NOT NULL,
 description VARCHAR(4000) NOT NULL, start_date DATE NOT NULL, expected_end_date DATE NOT NULL,
 actual_end_date DATE, budget NUMERIC(18,2) NOT NULL CHECK (budget >= 0),
 status VARCHAR(30) NOT NULL CHECK(status IN ('EM_ANALISE','ANALISE_REALIZADA','ANALISE_APROVADA','INICIADO','PLANEJADO','EM_ANDAMENTO','ENCERRADO','CANCELADO')),
 manager_id BIGINT NOT NULL REFERENCES member_snapshot(id),
 version INTEGER NOT NULL DEFAULT 0, date_last_update TIMESTAMP NOT NULL,user_last_update VARCHAR(150) NOT NULL,
 CHECK(expected_end_date >= start_date), CHECK(actual_end_date IS NULL OR actual_end_date >= start_date),
 CHECK(status <> 'ENCERRADO' OR actual_end_date IS NOT NULL)
);
CREATE TABLE project_member(project_id BIGINT NOT NULL REFERENCES project(id) ON DELETE CASCADE,member_id BIGINT NOT NULL REFERENCES member_snapshot(id),PRIMARY KEY(project_id,member_id));
CREATE INDEX project_member_member_idx ON project_member(member_id);
CREATE INDEX project_status_id_idx ON project(status,id DESC);
CREATE INDEX project_manager_idx ON project(manager_id);
-- One transactional lock coordinates aggregate membership limits across application instances.
CREATE TABLE portfolio_guard(id INTEGER PRIMARY KEY CHECK(id=1));
INSERT INTO portfolio_guard VALUES(1);
-- Deliberately no FK: preserve audit after authorized project deletion.
CREATE TABLE project_event(id BIGSERIAL PRIMARY KEY,project_id BIGINT NOT NULL,action VARCHAR(100) NOT NULL,actor VARCHAR(150) NOT NULL,occurred_at TIMESTAMP WITH TIME ZONE NOT NULL);
CREATE INDEX project_event_project_idx ON project_event(project_id,occurred_at);
