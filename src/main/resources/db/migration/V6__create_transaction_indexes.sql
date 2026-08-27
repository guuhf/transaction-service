CREATE INDEX idx_transactions_user_date
    ON transactions (USER_ID, DATE);

CREATE INDEX idx_transactions_user_category
    ON transactions (USER_ID, CATEGORY_ID);

CREATE INDEX idx_transactions_user_due_date
    ON transactions (USER_ID, DUE_DATE);

CREATE INDEX idx_transactions_user_status_date
    ON transactions (USER_ID, TRANSACTION_STATUS, DATE);