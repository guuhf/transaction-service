CREATE table users
(
    ID       BIGINT PRIMARY KEY,
    NAME     VARCHAR(255) not null,
    EMAIL    VARCHAR(255) not null unique,
    PASSWORD VARCHAR(255) not null
)