create schema if not exists order_service;

create table if not exists order_service.orders(
    id serial primary key,
    customer_user_id bigint not null,
    payment_id bigint check (payment_id > 0),
    total_price numeric not null,
    order_status varchar not null check (length(trim(order_status)) > 0),
    provider varchar not null check (length(trim(provider)) > 0),
    created_at timestamp not null,
    updated_at timestamp,
    error_message varchar
);

create table if not exists order_service.orders_history(
    id serial primary key,
    order_id bigint not null references order_service.orders(id),
    order_status varchar not null,
    event_id uuid,
    created_at timestamp not null,
    executed_at timestamp,
    details varchar,
    performed_by bigint,
    constraint uk_order_id_order_status unique (order_id, order_status)
);

create table if not exists order_service.order_products(
    id serial primary key,
    order_id bigint not null references order_service.orders(id),
    product_id bigint not null,
    quantity integer not null,
    fixed_price numeric not null,
    constraint uk_order_product_id_product_id unique (order_id, product_id)
);

create index order_customer_user_id on order_service.orders(customer_user_id);
create index order_history_order_id on order_service.orders_history(order_id);