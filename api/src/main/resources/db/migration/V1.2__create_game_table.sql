create table "game" (
    "id" bigint identity primary key,
    "current_state" blob,
    "player" varchar(255),
    "start_time" timestamp,
    "status" integer,
    "adventure_id" bigint not null
);

alter table "game"
    add constraint FKo9yy5m9fo5vq7osydi1lh7w31
    foreign key ("adventure_id") references "adventure";

