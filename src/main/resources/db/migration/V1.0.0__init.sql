CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

CREATE TABLE IF NOT EXISTS _user
(
    id       uuid DEFAULT public.uuid_generate_v4(),
    login    text UNIQUE ,
    email    text UNIQUE ,
    full_name text,
    password text,
    CONSTRAINT _user_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS role
(
    id uuid DEFAULT public.uuid_generate_v4(),
    role_name int,
    user_id uuid,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES _user (id) ON DELETE CASCADE
)