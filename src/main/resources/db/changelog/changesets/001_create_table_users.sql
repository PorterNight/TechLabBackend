--liquibase formatted sql
--changeset renat_gainutdinov:001 

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(125),
	email VARCHAR(75),
	role VARCHAR(75),
	password VARCHAR(255),
	status VARCHAR(75),
	account_balance DECIMAL,
	user_unique_id BIGINT,
    created_at TIMESTAMP WITH TIME ZONE
);
