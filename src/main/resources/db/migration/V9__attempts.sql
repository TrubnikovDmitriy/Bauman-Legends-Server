alter table tasks
    add max_attempts int
        default null;

create table public.attempts
(
    team_id bigint not null,
    task_id bigint not null,
    user_id bigint not null,
    answer  text   not null,
    time    bigint not null
);

create index attempts_team_id_task_id_index on public.attempts (team_id, task_id);

comment on table public.attempts is 'Попытки ответов на задания';
alter table public.attempts owner to trubnikov;
