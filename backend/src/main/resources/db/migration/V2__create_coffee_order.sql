-- ============================
-- coffee_order table
-- ============================

CREATE TABLE coffee_order (
    id UUID PRIMARY KEY,

    type VARCHAR(32) NOT NULL,
    status VARCHAR(32) NOT NULL,

    created_at TIMESTAMP NOT NULL
);

-- Status must be a valid coffee order status
ALTER TABLE coffee_order
    ADD CONSTRAINT chk_coffee_order_status
    CHECK (
        status IN (
            'PENDING',
            'ACCEPTED',
            'REJECTED',
            'INVALID',
            'FAILED'
        )
    );

-- Index used by scheduled cleanup
CREATE INDEX idx_coffee_order_created_at
    ON coffee_order(created_at);
