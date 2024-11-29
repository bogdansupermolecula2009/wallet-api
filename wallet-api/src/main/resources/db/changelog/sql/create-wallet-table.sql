CREATE TABLE wallet (
    wallet_id UUID PRIMARY KEY,
    balance DECIMAL(19,2) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);
