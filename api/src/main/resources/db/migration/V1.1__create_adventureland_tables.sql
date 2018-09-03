CREATE TABLE adventure (
    id bigserial PRIMARY KEY,
    author varchar(50),
    published_date date,
    title varchar(100),
    version varchar(50)
);

CREATE TABLE game (
    id bigserial PRIMARY KEY,
    current_state bytea,
    player varchar(50),
    start_time timestamp,
    status integer,
    adventure_id bigint REFERENCES adventure
);

CREATE TABLE script (
    adventure_id bigint PRIMARY KEY REFERENCES adventure,
    script text
);

CREATE TABLE turn (
    id bigserial primary key,
    command varchar(50),
    output varchar(1024),
    "timestamp" timestamp,
    game_id bigint REFERENCES game
);
