CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA challenge
    AUTHORIZATION postgres;

CREATE TABLE challenge.orders
(
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    status character varying(50) NOT NULL,
    customer_id UUID NOT NULL,
    seller_id UUID NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_orders_customer_id
    ON challenge.orders (customer_id);

CREATE INDEX IF NOT EXISTS idx_orders_seller_id
    ON challenge.orders (seller_id);

CREATE INDEX IF NOT EXISTS idx_orders_created_at_desc
    ON challenge.orders (created_at DESC);

CREATE TABLE challenge.orders_items
(
    id UUID NOT NULL DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    price NUMERIC(10, 2) NOT NULL,
    quantity BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ORDER_ITEM_UK01 UNIQUE (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES challenge.orders(id)
);
