--liquibase formatted sql
--changeset renat_gainutdinov:003 

CREATE TABLE IF NOT EXISTS user_course (
	user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, course_id)	
);
