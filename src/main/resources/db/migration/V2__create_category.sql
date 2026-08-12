CREATE TABLE category
(
    ID      BIGINT PRIMARY KEY,
    NAME    VARCHAR(120) not null,
    USER_ID BIGINT      not null,

    CONSTRAINT fk_user_category
        FOREIGN KEY (USER_ID)
            REFERENCES users (id)
)