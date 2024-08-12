--liqudbase formatted sql

--changeset danila:1
create table valid_refresh_token
(
    id           bigserial primary key,
    token        text        not null,
    email        varchar(48) not null,
    finger_print varchar(496) not null,
    constraint unique_refresh_token unique (email, finger_print)
);