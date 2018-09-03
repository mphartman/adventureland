create table "script" (
    "script" clob,
    "adventure_id" bigint not null,
    primary key ("adventure_id")
);

alter table "script"
    add constraint FKkv69ykje7l8m7i0milusg4oh7
    foreign key ("adventure_id") references "adventure";
