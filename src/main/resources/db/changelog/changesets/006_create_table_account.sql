--liquibase formatted sql
--changeset renat_gainutdinov:006

CREATE TABLE IF NOT EXISTS account (
    id BIGSERIAL REFERENCES users(id) ON DELETE CASCADE,
    balance DECIMAL,
	funded_amount DECIMAL,
    currency_type VARCHAR(75),
    updated_at TIMESTAMP WITH TIME ZONE
);
