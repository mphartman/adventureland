create table turn (
    "id" bigint identity primary key,
    "command" varchar(255),
    "output" clob,
    "timestamp" timestamp,
    "game_id" bigint not null
);

alter table "turn"
    add constraint FKfnda1g6jd92jpiakpu2689pgf
    foreign key ("game_id") references "game";
