--liquibase formatted sql
--changeset renat_gainutdinov:008

CREATE TABLE unit_of_course
(
    id         BIGSERIAL NOT NULL PRIMARY KEY,
    title      VARCHAR(256),
    body       VARCHAR(1024),
    created_at TIMESTAMP WITH TIME ZONE
);