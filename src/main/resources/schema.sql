CREATE TABLE IF NOT EXISTS event
(
    id        bigserial
        primary key,
    doc_id    varchar(255),
    timestamp varchar(255),
    user_id   varchar(255)
);

alter table event
    owner to postgres;

create index idx_docid
    on event (doc_id);

create index idx_userid
    on event (user_id);

create index idx_timestamp
    on event (timestamp);

