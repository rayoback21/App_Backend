
CREATE TABLE IF NOT EXISTS users (
    id SERIAL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255),
    email VARCHAR (50) UNIQUE,
    photo_url VARCHAR(255),
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS roles (
    id SERIAL PRIMARY KEY,
    roles VARCHAR(25),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );

