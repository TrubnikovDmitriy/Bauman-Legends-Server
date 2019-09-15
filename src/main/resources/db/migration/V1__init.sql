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
-- Name: auth; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.auth (
    team_id integer NOT NULL,
    login text NOT NULL,
    pass text DEFAULT "left"(md5((random())::text), 8) NOT NULL,
    type text DEFAULT 'PLAYER'::text NOT NULL,
    salt text NOT NULL
);


ALTER TABLE public.auth OWNER TO trubnikov;

--
-- Name: current_tasks; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.current_tasks (
    id integer NOT NULL,
    task_id integer NOT NULL,
    team_id integer NOT NULL,
    start_time integer NOT NULL,
    success boolean,
    type text NOT NULL,
    finish_time integer
);


ALTER TABLE public.current_tasks OWNER TO trubnikov;

--
-- Name: current_task_id_seq; Type: SEQUENCE; Schema: public; Owner: trubnikov
--

CREATE SEQUENCE public.current_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.current_task_id_seq OWNER TO trubnikov;

--
-- Name: current_task_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: trubnikov
--

ALTER SEQUENCE public.current_task_id_seq OWNED BY public.current_tasks.id;


--
-- Name: old_tasks; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.old_tasks (
    id integer NOT NULL,
    content text NOT NULL,
    answers text DEFAULT "left"(md5((random())::text), 15) NOT NULL,
    points integer NOT NULL,
    duration integer,
    type text NOT NULL,
    extra_id integer
);


ALTER TABLE public.old_tasks OWNER TO trubnikov;

--
-- Name: old_teams; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.old_teams (
    id integer NOT NULL,
    name text NOT NULL,
    score integer DEFAULT 0 NOT NULL,
    leader_name text NOT NULL,
    pilot_tasks_arr integer[] DEFAULT '{}'::integer[],
    final_tasks_arr integer[] DEFAULT '{}'::integer[],
    start_time integer,
    finish_time integer,
    fails_count integer DEFAULT 0 NOT NULL,
    started boolean DEFAULT false NOT NULL,
    finished boolean DEFAULT false NOT NULL
);


ALTER TABLE public.old_teams OWNER TO trubnikov;

--
-- Name: players; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.players (
    first_name text NOT NULL,
    second_name text NOT NULL,
    id integer NOT NULL,
    team_id integer NOT NULL
);


ALTER TABLE public.players OWNER TO trubnikov;

--
-- Name: players_id_seq; Type: SEQUENCE; Schema: public; Owner: trubnikov
--

CREATE SEQUENCE public.players_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.players_id_seq OWNER TO trubnikov;

--
-- Name: players_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: trubnikov
--

ALTER SEQUENCE public.players_id_seq OWNED BY public.players.id;


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
-- Name: tasks_id_seq; Type: SEQUENCE; Schema: public; Owner: trubnikov
--

CREATE SEQUENCE public.tasks_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.tasks_id_seq OWNER TO trubnikov;

--
-- Name: tasks_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: trubnikov
--

ALTER SEQUENCE public.tasks_id_seq OWNED BY public.old_tasks.id;


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
-- Name: teams_id_seq; Type: SEQUENCE; Schema: public; Owner: trubnikov
--

CREATE SEQUENCE public.teams_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.teams_id_seq OWNER TO trubnikov;

--
-- Name: teams_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: trubnikov
--

ALTER SEQUENCE public.teams_id_seq OWNED BY public.old_teams.id;


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
-- Name: tooltips; Type: TABLE; Schema: public; Owner: trubnikov
--

CREATE TABLE public.tooltips (
    extra_id integer NOT NULL,
    content text NOT NULL,
    tooltip text NOT NULL
);


ALTER TABLE public.tooltips OWNER TO trubnikov;

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
-- Name: current_tasks id; Type: DEFAULT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.current_tasks ALTER COLUMN id SET DEFAULT nextval('public.current_task_id_seq'::regclass);


--
-- Name: old_tasks id; Type: DEFAULT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.old_tasks ALTER COLUMN id SET DEFAULT nextval('public.tasks_id_seq'::regclass);


--
-- Name: old_teams id; Type: DEFAULT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.old_teams ALTER COLUMN id SET DEFAULT nextval('public.teams_id_seq'::regclass);


--
-- Name: players id; Type: DEFAULT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.players ALTER COLUMN id SET DEFAULT nextval('public.players_id_seq'::regclass);


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
-- Name: auth auth_team_id_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.auth
    ADD CONSTRAINT auth_team_id_pk PRIMARY KEY (team_id);


--
-- Name: current_tasks current_task_pkey; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.current_tasks
    ADD CONSTRAINT current_task_pkey PRIMARY KEY (id);


--
-- Name: players players_id_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.players
    ADD CONSTRAINT players_id_pk PRIMARY KEY (id);


--
-- Name: results results_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.results
    ADD CONSTRAINT results_pk PRIMARY KEY (team_id, task_id);


--
-- Name: old_tasks tasks_id_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.old_tasks
    ADD CONSTRAINT tasks_id_pk PRIMARY KEY (id);


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
-- Name: old_teams teams_pkey; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.old_teams
    ADD CONSTRAINT teams_pkey PRIMARY KEY (id);


--
-- Name: tooltips tooltips_extra_id_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.tooltips
    ADD CONSTRAINT tooltips_extra_id_pk PRIMARY KEY (extra_id);


--
-- Name: users users_pk; Type: CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pk PRIMARY KEY (user_id);


--
-- Name: auth_login_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX auth_login_uindex ON public.auth USING btree (login);


--
-- Name: tasks_task_id_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX tasks_task_id_uindex ON public.tasks USING btree (task_id);


--
-- Name: tasks_task_name_uindex; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX tasks_task_name_uindex ON public.tasks USING btree (task_name);


--
-- Name: team_and_task_ids_unique; Type: INDEX; Schema: public; Owner: trubnikov
--

CREATE UNIQUE INDEX team_and_task_ids_unique ON public.current_tasks USING btree (team_id, task_id);


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
-- Name: auth auth_teams_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.auth
    ADD CONSTRAINT auth_teams_id_fk FOREIGN KEY (team_id) REFERENCES public.old_teams(id) ON DELETE CASCADE;


--
-- Name: current_tasks current_task_tasks_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.current_tasks
    ADD CONSTRAINT current_task_tasks_id_fk FOREIGN KEY (task_id) REFERENCES public.old_tasks(id);


--
-- Name: current_tasks current_task_teams_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.current_tasks
    ADD CONSTRAINT current_task_teams_id_fk FOREIGN KEY (team_id) REFERENCES public.old_teams(id);


--
-- Name: players players_teams_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.players
    ADD CONSTRAINT players_teams_id_fk FOREIGN KEY (team_id) REFERENCES public.old_teams(id) ON UPDATE CASCADE ON DELETE CASCADE;


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
-- Name: old_tasks tasks_tasks_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.old_tasks
    ADD CONSTRAINT tasks_tasks_id_fk FOREIGN KEY (extra_id) REFERENCES public.old_tasks(id);


--
-- Name: teams teams_users_user_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.teams
    ADD CONSTRAINT teams_users_user_id_fk FOREIGN KEY (leader_id) REFERENCES public.users(user_id) ON UPDATE CASCADE ON DELETE RESTRICT;


--
-- Name: tooltips tooltips_tasks_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.tooltips
    ADD CONSTRAINT tooltips_tasks_id_fk FOREIGN KEY (extra_id) REFERENCES public.old_tasks(id);


--
-- Name: users users_teams_team_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: trubnikov
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_teams_team_id_fk FOREIGN KEY (team_id) REFERENCES public.teams(team_id) ON UPDATE CASCADE ON DELETE SET NULL;


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

