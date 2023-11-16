--liquibase formatted sql
--changeset renat_gainutdinov:102 

ALTER SEQUENCE courses_id_seq RESTART WITH 1;

INSERT INTO courses (name, type, description, created_at) VALUES
 ('Beginning C++ Programming', 'programming', 'Learn to program with one of the most powerful programming languages that exists today, C++', NOW()),
 ('Python for Data Science, AI & Development', 'programming', 'Describe Python Basics including Data Types, Expressions, Variables, and Data Structures', NOW()),
 ('Crash Course on Python', 'programming', 'This course is a fairly comprehensive course on Python, teaching you most of the language’s features', NOW()),
 ('Java Course - Mastering the Fundamentals', 'programming', 'Embark on your programming journey with our comprehensive Free Java Course for Beginners. Master the fundamentals of Java and gain the skills needed for advanced Java development. This easy-to-follow course is designed with beginners in mind, offering a structured learning path to specialize in Java programming. With no prerequisites, this course empowers you to learn Java at your own pace and take the first step toward a promising career in tech', NOW());