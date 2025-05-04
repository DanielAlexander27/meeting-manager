
CREATE TABLE IF NOT EXISTS app_user(
    id IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS finance_movement(
    id IDENTITY PRIMARY KEY,
                                               user_id INT,
                                               foreign key (user_id) references app_user(id),
    title VARCHAR(600) NOT NULL,
    description VARCHAR(600) NOT NULL,
    quantity NUMERIC(10, 2) NOT NULL,
    expense_type VARCHAR(40),
    movement_type VARCHAR(10),
    created_at DATE,
    CHECK(movement_type IN ('CASH', 'BANK'))
    );