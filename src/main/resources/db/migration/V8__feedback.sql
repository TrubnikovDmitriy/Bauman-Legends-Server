create table public.feedback
(
    user_id bigint not null
        constraint feedback_pk
            primary key
        constraint feedback_users_user_id_fk
            references public.users
            on update cascade on delete restrict,
    pilot_mark int not null,
    final_mark int not null,
    legends_mark int not null,
    site_mark int not null,
    task_mark int not null,
    ghost_mark int not null,
    best_task text,
    worst_task text,
    known_from text,
    message text
);