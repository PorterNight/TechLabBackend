--liquibase formatted sql
--changeset renat_gainutdinov:004 

CREATE TABLE IF NOT EXISTS orders (
    order_id BIGSERIAL NOT NULL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    total_amount DECIMAL,
    status VARCHAR(75),
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE IF NOT EXISTS order_details (
    order_detail_id BIGSERIAL NOT NULL PRIMARY KEY,
    order_id BIGINT REFERENCES orders(order_id) ON DELETE CASCADE,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    quantity INT,
    price DECIMAL
);

CREATE TABLE IF NOT EXISTS course_prices (
    price_id BIGSERIAL NOT NULL PRIMARY KEY,
    course_id BIGINT REFERENCES courses(id) ON DELETE CASCADE,
    price DECIMAL,
    promo_start_date TIMESTAMP WITH TIME ZONE,
    promo_end_date TIMESTAMP WITH TIME ZONE,
    promo_active BOOLEAN
);