drop index users_login_uindex;

create unique index users_login_uindex ON public.users USING btree (LOWER(login));
