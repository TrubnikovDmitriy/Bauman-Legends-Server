alter table ghosts drop column history;


create table witness_decision
(
    team_id bigint not null
        constraint witness_decision_teams_team_id_fk
            references teams,
    decision boolean
);

create unique index witness_decision_team_id_uindex
    on witness_decision (team_id);

alter table witness_decision
    add constraint witness_decision_pk
        primary key (team_id);

