create table facts
(
    task_id bigint not null
        constraint facts_pk
            primary key
        constraint facts_tasks_task_id_fk
            references tasks,
    fact text not null
);

comment on table facts is 'Факт о задании';