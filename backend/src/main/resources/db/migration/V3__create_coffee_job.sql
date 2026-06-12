-- ============================
-- coffee_job table
-- ============================

CREATE TABLE coffee_job (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL
        REFERENCES coffee_order(id)
        ON DELETE CASCADE,

    status VARCHAR(32) NOT NULL,
    progress INTEGER NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Status must be a valid coffee job status
ALTER TABLE coffee_job
    ADD CONSTRAINT chk_coffee_job_status
    CHECK (
        status IN (
            'PENDING',
            'IN_PROGRESS',
            'COMPLETED',
            'FAILED'
        )
    );

-- Progress must be between 0 and 100 inclusive
ALTER TABLE coffee_job
    ADD CONSTRAINT chk_coffee_job_progress
    CHECK (
        progress BETWEEN 0 AND 100
    );

-- Index used to efficiently locate jobs by order ID
CREATE INDEX idx_coffee_job_order_id
    ON coffee_job(order_id);
