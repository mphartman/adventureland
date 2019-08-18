create TABLE adventure (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    author varchar(50) NOT NULL,
    published_date date NOT NULL,
    title varchar(100) NOT NULL,
    version varchar(50) NOT NULL
);

create TABLE game (
    id bigint AUTO_INCREMENT PRIMARY KEY,
    current_state blob,
    player varchar(50),
    start_time timestamp,
    status integer,
    adventure_id bigint REFERENCES adventure
);

create TABLE script (
    adventure_id bigint PRIMARY KEY REFERENCES adventure,
    script text
);

create TABLE turn (
    id bigint AUTO_INCREMENT primary key,
    command varchar(50),
    output varchar(1024),
    `timestamp` timestamp,
    game_id bigint REFERENCES game
);
