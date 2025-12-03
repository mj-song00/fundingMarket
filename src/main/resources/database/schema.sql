-- 데이터베이스 초기화
DROP DATABASE IF EXISTS market;
CREATE DATABASE market;
USE market;

-- User 테이블
CREATE TABLE users
(
    id         VARCHAR(36)              NOT NULL,
    created_at DATETIME(6),
    updated_at DATETIME(6),
    deleted_at DATETIME(6),
    email      VARCHAR(255),
    nick_name  VARCHAR(255),
    password   VARCHAR(255),
    user_role  ENUM ('USER', 'CREATOR') NOT NULL,
    PRIMARY KEY (id)
) engine = InnoDB;

-- Creator 테이블
CREATE TABLE creator
(
    id           VARCHAR(36)              NOT NULL,
    created_at   DATETIME(6),
    updated_at   DATETIME(6),
    deleted_at   DATETIME(6),
    email        VARCHAR(255),
    nick_name    VARCHAR(255),
    password     VARCHAR(255),
    user_role    ENUM ('USER', 'CREATOR') NOT NULL,
    introduce    VARCHAR(255),
    bank_account VARCHAR(255),
    bank         VARCHAR(255),
    is_active    BOOLEAN                  NOT NULL DEFAULT TRUE,
    PRIMARY KEY (id)
) engine = InnoDB;

-- Order 테이블
CREATE TABLE orders
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      VARCHAR(36)  NOT NULL,
    order_id     VARCHAR(255) NOT NULL,             -- 토스 주문 식별자
    payment_key  VARCHAR(255) NOT NULL,             -- 토스 결제 키
    order_name   VARCHAR(255) NOT NULL,             -- 결제한 프로젝트명
    total_amount INT          NOT NULL,             -- 총 결제 금액
    method       VARCHAR(50)  NOT NULL,             -- 결제 수단
    status       VARCHAR(50),                       -- APPROVE, DONE, CANCELED
    approved_at  DATETIME,                          -- 승인 일자
    address      VARCHAR(255),                      -- 배송 주소
    phone_number VARCHAR(50)  NOT NULL,             -- 전화번호
    created_at   DATETIME(6),
    updated_at   DATETIME(6),
    CONSTRAINT fk_orders_user
        FOREIGN KEY (user_id) REFERENCES users (id) -- User 테이블 참조

) engine = InnoDB;

-- Payment 테이블
CREATE TABLE payment
(
    payment_key  VARCHAR(255) NOT NULL, -- PK
    user_id      VARCHAR(36),           -- 결제한 유저 (UUID)
    order_name   VARCHAR(255),          -- 결제 이름
    method       VARCHAR(50),           -- 결제 수단
    price        INT,                   -- 결제 가격
    status       VARCHAR(50),           -- 결제 처리 상태
    requested_at VARCHAR(50),           -- 결제일 (문자열로 저장)
    approved_at  DATETIME,              -- 결제 요청일
    order_id     VARCHAR(255),          -- 주문 식별자
    created_at   DATETIME(6),
    updated_at   DATETIME(6),
    PRIMARY KEY (payment_key),
    CONSTRAINT fk_payment_user
        FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE = InnoDB;

-- Project 테이블
CREATE TABLE project
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    title                  VARCHAR(255) NOT NULL,
    category               VARCHAR(50),           -- Enum 저장
    contents               LONGTEXT,              -- @Lob
    funding_amount         BIGINT       NOT NULL, -- 펀딩 목표 금액
    collected_amount       INT,                   -- 현재 모금액
    funding_schedule       VARCHAR(255) NOT NULL, -- 펀딩 일정
    status                 VARCHAR(50)  NOT NULL, -- Enum FundingStatusㅇ
    expected_delivery_date VARCHAR(255),          -- 배송 예상일
    deleted_at             DATETIME,              -- 삭제 일자
    creator_id             VARCHAR(36),           -- Creator FK
    end_date               VARCHAR(255),
    created_at             DATETIME(6),
    updated_at             DATETIME(6),
    CONSTRAINT fk_project_creator
        FOREIGN KEY (creator_id) REFERENCES creator (id)
) ENGINE = InnoDB;

CREATE TABLE file
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    image_url          VARCHAR(255) NOT NULL,
    is_thumbnail       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at         DATETIME(6),
    updated_at         DATETIME(6),
    project_id         BIGINT,
    CONSTRAINT fk_file_project
        FOREIGN KEY (project_id) REFERENCES project (id)
) ENGINE = InnoDB;

-- Reward 테이블
CREATE TABLE reward
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    price       BIGINT,       -- 후원 가격
    description VARCHAR(255), -- 리워드 설명
    quantity    INT,          -- 제작 수량
    title       VARCHAR(255), -- 리워드 제목
    deleted_at  DATETIME,     -- 삭제 일자
    project_id  BIGINT,       -- Project FK
    created_at  DATETIME(6),
    updated_at  DATETIME(6),
    CONSTRAINT fk_reward_project
        FOREIGN KEY (project_id) REFERENCES project (id)
) ENGINE = InnoDB;

-- Sponsorship 테이블
CREATE TABLE sponsorship
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    amount        INT NOT NULL,                        -- 후원 금액 (Entity: int, NULL 방지)
    sponsored_at  VARCHAR(255),                      -- 후원 시점 (Entity: String)
    quantity      INT NOT NULL DEFAULT 1,            -- 선택한 리워드 갯수 (Entity: int)
    is_canceled   BOOLEAN NOT NULL DEFAULT FALSE,    -- 취소 여부 (Entity: isCanceled)
    order_id      VARCHAR(255) NOT NULL,             -- 토스 요청 시 사용한 주문 식별자 (Entity: orderId)
    payment_key   VARCHAR(255) NOT NULL,             -- 토스 결제 키 (Entity: paymentKey)
    order_name    VARCHAR(255) NOT NULL,             -- 선택한 리워드명 (Entity: orderName)
    method        VARCHAR(255) NOT NULL,             -- 결제 수단 (Entity: method)
    status        VARCHAR(255),                      -- 결제 상태 (Entity: status)
    approved_at   DATETIME(6),                       -- 승인 일자 (Entity: LocalDateTime)

    user_id       VARCHAR(36) NOT NULL,              -- 후원자 (User FK)
    project_id    BIGINT NOT NULL,                   -- 후원한 프로젝트 (Project FK)
    reward_id     BIGINT NOT NULL,                   -- 선택한 리워드 (Reward FK)

    created_at    DATETIME(6),                       -- Timestamped 상속 필드
    updated_at    DATETIME(6),                       -- Timestamped 상속 필드

    CONSTRAINT fk_sponsorship_user
        FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_sponsorship_project
        FOREIGN KEY (project_id) REFERENCES project (id),
    CONSTRAINT fk_sponsorship_reward
        FOREIGN KEY (reward_id) REFERENCES reward (id)
) ENGINE = InnoDB;

-- Spring Batch 테이블
CREATE TABLE BATCH_JOB_INSTANCE
(
    job_instance_id BIGINT       NOT NULL PRIMARY KEY,
    version         BIGINT,
    job_name        VARCHAR(100) NOT NULL,
    job_key         VARCHAR(32)  NOT NULL,
    UNIQUE KEY uk_job_name_key (job_name, job_key)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION
(
    job_execution_id BIGINT      NOT NULL PRIMARY KEY,
    version          BIGINT,
    job_instance_id  BIGINT      NOT NULL,
    create_time      DATETIME(6) NOT NULL,
    start_time       DATETIME(6),
    end_time         DATETIME(6),
    status           VARCHAR(10),
    exit_code        VARCHAR(2500),
    exit_message     VARCHAR(2500),
    last_updated     DATETIME(6),
    FOREIGN KEY (job_instance_id) REFERENCES BATCH_JOB_INSTANCE (job_instance_id)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION_PARAMS
(
    job_execution_id BIGINT       NOT NULL,
    parameter_name   VARCHAR(100) NOT NULL,
    parameter_type   VARCHAR(100) NOT NULL,
    parameter_value  VARCHAR(2500),
    identifying      CHAR(1)      NOT NULL,
    FOREIGN KEY (job_execution_id) REFERENCES BATCH_JOB_EXECUTION (job_execution_id)
) ENGINE = InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION
(
    step_execution_id  BIGINT       NOT NULL PRIMARY KEY,
    version            BIGINT       NOT NULL,
    step_name          VARCHAR(100) NOT NULL,
    job_execution_id   BIGINT       NOT NULL,
    create_time        DATETIME(6)  NOT NULL,
    start_time         DATETIME(6),
    end_time           DATETIME(6),
    status             VARCHAR(10),
    commit_count       BIGINT,
    read_count         BIGINT,
    filter_count       BIGINT,
    write_count        BIGINT,
    read_skip_count    BIGINT,
    write_skip_count   BIGINT,
    process_skip_count BIGINT,
    rollback_count     BIGINT,
    exit_code          VARCHAR(2500),
    exit_message       VARCHAR(2500),
    last_updated       DATETIME(6),
    FOREIGN KEY (job_execution_id) REFERENCES BATCH_JOB_EXECUTION (job_execution_id)
) ENGINE = InnoDB;

CREATE TABLE BATCH_STEP_EXECUTION_CONTEXT
(
    step_execution_id  BIGINT        NOT NULL PRIMARY KEY,
    short_context      VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    FOREIGN KEY (step_execution_id) REFERENCES BATCH_JOB_EXECUTION (job_execution_id)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_EXECUTION_CONTEXT
(
    job_execution_id   BIGINT        NOT NULL PRIMARY KEY,
    short_context      VARCHAR(2500) NOT NULL,
    serialized_context TEXT,
    FOREIGN KEY (job_execution_id) REFERENCES BATCH_JOB_EXECUTION (job_execution_id)
) ENGINE = InnoDB;

-- 커스텀 배치 테이블
CREATE TABLE BATCH_LOCK
(
    id        BIGINT       NOT NULL AUTO_INCREMENT,
    job_name  VARCHAR(255) NOT NULL,
    locked_at DATETIME(6)  NOT NULL,
    locked_by VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_job_name (job_name)
) ENGINE = InnoDB;

CREATE TABLE BATCH_JOB_REQUEST
(
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    job_name     VARCHAR(255) NOT NULL,
    job_param    VARCHAR(255),
    requested_at DATETIME(6)  NOT NULL,
    PRIMARY KEY (id)
) ENGINE = InnoDB;

-- 시퀀스 테이블
CREATE TABLE BATCH_STEP_EXECUTION_SEQ
(
    id         BIGINT  NOT NULL,
    unique_key CHAR(1) NOT NULL,
    UNIQUE KEY uk_batch_step_seq (unique_key)
) ENGINE = InnoDB;

INSERT INTO BATCH_STEP_EXECUTION_SEQ (id, unique_key)
VALUES (0, '0')
ON DUPLICATE KEY UPDATE id = id;

CREATE TABLE BATCH_JOB_EXECUTION_SEQ
(
    id         BIGINT  NOT NULL,
    unique_key CHAR(1) NOT NULL,
    UNIQUE KEY uk_batch_job_exec_seq (unique_key)
) ENGINE = InnoDB;

INSERT INTO BATCH_JOB_EXECUTION_SEQ (id, unique_key)
VALUES (0, '0')
ON DUPLICATE KEY UPDATE id = id;

CREATE TABLE BATCH_JOB_SEQ
(
    id         BIGINT  NOT NULL,
    unique_key CHAR(1) NOT NULL,
    UNIQUE KEY uk_batch_job_seq (unique_key)
) ENGINE = InnoDB;

INSERT INTO BATCH_JOB_SEQ (id, unique_key)
VALUES (0, '0')
ON DUPLICATE KEY UPDATE id = id;