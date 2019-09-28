--
-- PostgreSQL database dump
--

-- Dumped from database version 10.10 (Ubuntu 10.10-0ubuntu0.18.04.1)
-- Dumped by pg_dump version 10.10 (Ubuntu 10.10-0ubuntu0.18.04.1)

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


ALTER DATABASE legends_of_bmstu OWNER TO trubnikov;

--
-- Name: task_status; Type: TYPE; Schema: public; Owner: trubnikov
--

CREATE TYPE public.task_status AS ENUM (
    'running',
    'success',
    'fail',
    'skip'
);


ALTER TYPE public.task_status OWNER TO trubnikov;

--
-- Name: task_type; Type: TYPE; Schema: public; Owner: trubnikov
--

CREATE TYPE public.task_type AS ENUM (
    'photo',
    'logic',
    'main',
    'draft'
);


ALTER TYPE public.task_type OWNER TO trubnikov;

--
-- Name: user_role; Type: TYPE; Schema: public; Owner: trubnikov
--

CREATE TYPE public.user_role AS ENUM (
    'admin',
    'moderator',
    'player',
    'tester',
    'captain'
);


ALTER TYPE public.user_role OWNER TO trubnikov;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: results; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.results (
    team_id bigint NOT NULL,
    task_id bigint NOT NULL,
    start_time bigint NOT NULL,
    finish_time bigint,
    status public.task_status DEFAULT 'running'::public.task_status NOT NULL,
    answer text
);


ALTER TABLE public.results OWNER TO trubnikov;

--
-- Name: tasks; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.tasks (
    task_id bigint NOT NULL,
    html text NOT NULL,
    img_path text,
    task_type public.task_type NOT NULL,
    duration bigint,
    points integer NOT NULL,
    answers text[] NOT NULL,
    skip_possible boolean DEFAULT false NOT NULL,
    task_name text NOT NULL,
    capacity integer NOT NULL
);


ALTER TABLE public.tasks OWNER TO trubnikov;

--
-- Name: tasks_task_id_seq; Type: SEQUENCE; Schema: public; Owner: trubnikov
--

CREATE SEQUENCE public.tasks_task_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tasks_task_id_seq OWNER TO trubnikov;

--
-- Name: tasks_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: trubnikov
--

ALTER SEQUENCE public.tasks_task_id_seq OWNED BY public.tasks.task_id;


--
-- Name: teams; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.teams (
    team_id bigint NOT NULL,
    team_name text NOT NULL,
    leader_id integer NOT NULL,
    score integer DEFAULT 0 NOT NULL,
    invite_code text NOT NULL
);


ALTER TABLE public.teams OWNER TO trubnikov;

--
-- Name: teams_team_id_seq; Type: SEQUENCE; Schema: public; Owner: trubnikov
--

CREATE SEQUENCE public.teams_team_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.teams_team_id_seq OWNER TO trubnikov;

--
-- Name: teams_team_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: trubnikov
--

ALTER SEQUENCE public.teams_team_id_seq OWNED BY public.teams.team_id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.users (
    user_id bigint NOT NULL,
    login text NOT NULL,
    password bytea NOT NULL,
    salt bytea NOT NULL,
    team_id integer,
    role public.user_role DEFAULT 'player'::public.user_role NOT NULL,
    first_name text NOT NULL,
    last_name text NOT NULL,
    vk text,
    study_group text NOT NULL
);


ALTER TABLE public.users OWNER TO trubnikov;

--
-- Name: users_user_id_seq; Type: SEQUENCE; Schema: public; Owner: trubnikov
--

CREATE SEQUENCE public.users_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_user_id_seq OWNER TO trubnikov;

--
-- Name: users_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: trubnikov
--

ALTER SEQUENCE public.users_user_id_seq OWNED BY public.users.user_id;


--
-- Name: tasks task_id; Type: DEFAULT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.tasks ALTER COLUMN task_id SET DEFAULT nextval('public.tasks_task_id_seq'::regclass);


--
-- Name: teams team_id; Type: DEFAULT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.teams ALTER COLUMN team_id SET DEFAULT nextval('public.teams_team_id_seq'::regclass);


--
-- Name: users user_id; Type: DEFAULT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.users ALTER COLUMN user_id SET DEFAULT nextval('public.users_user_id_seq'::regclass);


--
-- Name: results results_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.results
    ADD CONSTRAINT results_pk PRIMARY KEY (team_id, task_id);


--
-- Name: tasks tasks_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.tasks
    ADD CONSTRAINT tasks_pk PRIMARY KEY (task_id);


--
-- Name: teams teams_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT teams_pk PRIMARY KEY (team_id);


--
-- Name: users users_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (user_id);


--
-- Name: tasks_task_id_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX tasks_task_id_uindex ON public.tasks USING btree (task_id);


--
-- Name: tasks_task_name_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX tasks_task_name_uindex ON public.tasks USING btree (task_name);


--
-- Name: teams_leader_id_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX teams_leader_id_uindex ON public.teams USING btree (leader_id);


--
-- Name: teams_team_name_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX teams_team_name_uindex ON public.teams USING btree (team_name);


--
-- Name: users_login_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX users_login_uindex ON public.users USING btree (login);


--
-- Name: users_team_id_index; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE INDEX users_team_id_index ON public.users USING btree (team_id);


--
-- Name: results results_tasks_task_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.results
    ADD CONSTRAINT results_tasks_task_id_fk FOREIGN KEY (task_id) REFERENCES public.tasks(task_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: results results_teams_team_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.results
    ADD CONSTRAINT results_teams_team_id_fk FOREIGN KEY (team_id) REFERENCES public.teams(team_id) ON UPDATE RESTRICT ON DELETE RESTRICT;


--
-- Name: teams teams_users_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT teams_users_user_id_fk FOREIGN KEY (leader_id) REFERENCES public.users(user_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: users users_teams_team_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_teams_team_id_fk FOREIGN KEY (team_id) REFERENCES public.teams(team_id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- PostgreSQL database dump complete
--

