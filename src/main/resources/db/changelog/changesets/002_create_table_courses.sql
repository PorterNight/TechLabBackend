--liquibase formatted sql
--changeset renat_gainutdinov:002 

CREATE TABLE IF NOT EXISTS courses (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(255),
	type VARCHAR(75),
	description VARCHAR(1024),
    created_at TIMESTAMP WITH TIME ZONE
);
