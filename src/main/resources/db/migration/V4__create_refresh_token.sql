CREATE TABLE refresh_token
(
    ID              BIGINT PRIMARY KEY,
    TOKEN           VARCHAR(1000) not null unique,
    EXPIRATION_DATE TIMESTAMP     not null,
    USER_ID         BIGINT        not null unique,

    CONSTRAINT fk_user_refresh_token
        FOREIGN KEY (USER_ID)
            REFERENCES users (id)
)