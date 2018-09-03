create table adventure (
    "id" bigint identity primary key,
    "author" varchar(255),
    "published_date" date,
    "title" varchar(255),
    "version" varchar(255)
);
