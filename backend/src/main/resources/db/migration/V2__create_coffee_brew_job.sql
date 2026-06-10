-- ============================
-- coffee_brew_job table
-- ============================

CREATE TABLE coffee_brew_job (
    id UUID PRIMARY KEY,

    status VARCHAR(32) NOT NULL,
    progress INTEGER NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Status must be a valid coffee brew job status.
ALTER TABLE coffee_brew_job
    ADD CONSTRAINT chk_coffee_brew_job_status
    CHECK (
        status IN (
            'PENDING',
            'IN_PROGRESS',
            'COMPLETED',
            'FAILED'
        )
    );

-- Progress must be between 0 and 100 inclusive.
ALTER TABLE coffee_brew_job
    ADD CONSTRAINT chk_coffee_brew_job_progress
    CHECK (
        progress BETWEEN 0 AND 100
    );
