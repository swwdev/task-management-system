--liquibase formatted sql

--changeset danila:1
create table users
(
    id       bigserial primary key,
    email    varchar(48)  not null unique,
    password varchar(128) not null
);

--changeset danila:2
create table task
(
    id          bigserial primary key,
    header      varchar(48) not null,
    description text        not null,
    status      smallint    not null,
    priority    smallint    not null,
    author      bigint references users (id)
        on delete cascade   not null
);

--changeset danila:3
create table task_assignee
(
    task_id     bigint references task (id)
        on delete cascade,
    assignee_id bigint references users (id)
        on delete cascade,
    constraint pk_task_assignee primary key (task_id, assignee_id)
)