create schema if not exists order_service;

create table if not exists order_service.customer_delivery_info
(
    id               serial primary key,
    address          varchar not null check (length(trim(address)) > 0),
    customer_user_id bigint  not null check (customer_user_id > 0)
);

create table if not exists order_service.orders
(
    id                        serial primary key,
    customer_user_id          bigint    not null,
    customer_delivery_info_id bigint references order_service.customer_delivery_info (id),
    payment_id                bigint check (payment_id > 0),
    total_price               numeric   not null,
    order_status              varchar   not null check (length(trim(order_status)) > 0),
    payment_status            varchar   not null check (length(trim(payment_status)) > 0),
    provider                  varchar   not null check (length(trim(provider)) > 0),
    created_at                timestamp not null,
    updated_at                timestamp,
    error_message             varchar,
    constraint uk_order_id_delivery_info_id unique (id, customer_delivery_info_id)
);

create table if not exists order_service.orders_history
(
    id                 serial primary key,
    order_id           bigint    not null references order_service.orders (id),
    order_history_step varchar   not null,
    event_id           uuid,
    created_at         timestamp not null,
    executed_at        timestamp,
    details            varchar,
    performed_by       bigint,
    constraint uk_order_id_order_history_step unique (order_id, order_history_step)
);

create table if not exists order_service.order_products
(
    id          serial primary key,
    order_id    bigint  not null references order_service.orders (id),
    product_id  bigint  not null,
    name        varchar check (length(trim(name)) > 0),
    quantity    integer not null,
    fixed_price numeric not null,
    constraint uk_order_product_id_product_id unique (order_id, product_id)
);

create index order_customer_user_id on order_service.orders (customer_user_id);
create index order_history_order_id on order_service.orders_history (order_id);

alter sequence order_service.customer_delivery_info_id_seq restart with 1 increment 50;
alter sequence order_service.order_products_id_seq restart with 1 increment 50;
alter sequence order_service.orders_history_id_seq restart with 1 increment 50;
alter sequence order_service.orders_id_seq restart with 1 increment 50;
