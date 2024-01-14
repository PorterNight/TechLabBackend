--liquibase formatted sql
--changeset renat_gainutdinov:009

CREATE TABLE IF NOT EXISTS unit_courses (
	unit_id BIGINT REFERENCES unit_of_course(id) ON DELETE CASCADE,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    PRIMARY KEY (unit_id, course_id)
);
