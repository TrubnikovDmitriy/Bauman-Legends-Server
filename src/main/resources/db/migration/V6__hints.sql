create table public.hints
(
    hint_id serial not null
        constraint hints_pk
            primary key,
    task_id bigint not null
        constraint hints_tasks_task_id_fk
            references tasks
            on update cascade on delete cascade,
    cost int not null,
    html text not null
);

create index hints_task_id_index
    on public.hints (task_id);

create table public.open_hints
(
    team_id bigint not null
        constraint open_hints_teams_team_id_fk
            references teams
            on update cascade on delete cascade,
    hint_id bigint not null
        constraint open_hints_hints_hint_id_fk
            references hints
            on update cascade on delete cascade
);

create unique index open_hints_uindex
    on public.open_hints (hint_id, team_id);

