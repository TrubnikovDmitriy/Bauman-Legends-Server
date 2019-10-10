create table public.ghosts
(
    ghost_id serial not null
        constraint ghosts_pk
            primary key,
    history text not null,
    keyword text not null
);

create unique index ghosts_keyword_uindex
    on public.ghosts (keyword);

create table public.open_ghosts
(
    team_id bigint not null
        constraint open_ghosts_teams_team_id_fk
            references teams
            on update cascade on delete cascade,
    ghost_id bigint not null
        constraint open_ghosts_ghosts_ghost_id_fk
            references ghosts
            on update cascade on delete cascade
);

create unique index open_ghosts_team_id_ghost_id_uindex
    on public.open_ghosts (team_id, ghost_id);

