alter table results drop constraint results_tasks_task_id_fk;

alter table results
    add constraint results_tasks_task_id_fk
        foreign key (task_id) references tasks
            on update cascade on delete restrict;