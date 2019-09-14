--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_with_oids = false;



CREATE TYPE public.task_status AS ENUM (
    'running',
    'success',
    'fail',
    'skip'
);
ALTER TYPE public.task_status OWNER TO trubnikov;


CREATE TABLE public.results (
    result_id integer NOT NULL,
    team_id integer NOT NULL,
    task_id integer NOT NULL,
    start_time bigint NOT NULL,
    finish_time bigint,
    status public.task_status DEFAULT 'running'::public.task_status NOT NULL
);
ALTER TABLE public.results OWNER TO trubnikov;



CREATE SEQUENCE public.results_result_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;
ALTER TABLE public.results_result_id_seq OWNER TO trubnikov;



ALTER SEQUENCE public.results_result_id_seq OWNED BY public.results.result_id;

ALTER TABLE ONLY public.results ALTER COLUMN result_id SET DEFAULT nextval('public.results_result_id_seq'::regclass);

ALTER TABLE ONLY public.results
    ADD CONSTRAINT results_pk PRIMARY KEY (result_id);

ALTER TABLE ONLY public.results
    ADD CONSTRAINT results_tasks_task_id_fk FOREIGN KEY (task_id) REFERENCES public.tasks(task_id) ON UPDATE RESTRICT ON DELETE RESTRICT;

ALTER TABLE ONLY public.results
    ADD CONSTRAINT results_teams_team_id_fk FOREIGN KEY (team_id) REFERENCES public.teams(team_id) ON UPDATE RESTRICT ON DELETE RESTRICT;

