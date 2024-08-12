create table comment
(
    id      bigserial primary key,
    "text"  text          not null,
    author  bigint references users (id)
        on delete cascade not null,
    task_id bigint references task (id)
        on delete cascade not null
)