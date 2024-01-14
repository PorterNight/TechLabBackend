--liquibase formatted sql
--changeset renat_gainutdinov:007

ALTER TABLE courses ADD group_learning varchar(256);
ALTER TABLE courses ADD self_placed_learning varchar(256);
ALTER TABLE courses ADD unit_of_lessons varchar(256);

