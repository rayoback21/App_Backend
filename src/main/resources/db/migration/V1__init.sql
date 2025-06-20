
CREATE TABLE IF NOT EXISTS admins (
    id SERIAL,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL,
    PRIMARY KEY (id)
    );


CREATE TABLE IF NOT EXISTS aspirants (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    nui VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS racing (
    id SERIAL PRIMARY KEY,
    career VARCHAR (350) NOT NULL,
    aspirants_id INT,
    admins_id INT,
    FOREIGN KEY (aspirants_id) REFERENCES aspirants(id),
    FOREIGN KEY (admins_id) REFERENCES admins(id)
    );


CREATE TABLE IF NOT EXISTS questions (
    id SERIAL PRIMARY KEY,
    text VARCHAR(500) NOT NULL,
    software_q VARCHAR(450) NOT NULL,
    design_q VARCHAR(450) NOT NULL,
    gastronomy_q VARCHAR(450) NOT NULL,
    marketing_q VARCHAR(450) NOT NULL,
    tourism_q VARCHAR(450) NOT NULL,
    talent_q VARCHAR(450) NOT NULL,
    nursing_q VARCHAR(450) NOT NULL,
    electricity_q VARCHAR(450) NOT NULL,
    accounting_q VARCHAR(450) NOT NULL,
    networks_q VARCHAR(450) NOT NULL,
    aspirants_id INT,
    admins_id INT,
    FOREIGN KEY (aspirants_id) REFERENCES aspirants(id),
    FOREIGN KEY (admins_id) REFERENCES admins(id)
    );




CREATE TABLE IF NOT EXISTS answer (
    id SERIAL,
    text_a VARCHAR(500) NOT NULL,
    software_a VARCHAR (450) NOT NULL,
    design_a VARCHAR(450) NOT NULL,
    gastronomy_a VARCHAR(450) NOT NULL,
    marketing_a VARCHAR(450) NOT NULL,
    tourism_a VARCHAR(450) NOT NULL,
    talent_a VARCHAR(450) NOT NULL,
    nursing_a VARCHAR(450) NOT NULL,
    electricity_a VARCHAR(450) NOT NULL,
    accounting_a VARCHAR(450) NOT NULL,
    networks_a VARCHAR(450) NOT NULL,
    aspirant_id INT,
    questions_id INT,
    selected_option CHAR(1),
    FOREIGN KEY (aspirant_id) REFERENCES aspirants(id),
    FOREIGN KEY (questions_id) REFERENCES questions(id),
    PRIMARY KEY (id)
    );


CREATE TABLE IF NOT EXISTS results (
    id SERIAL,
    aspirant_id INT, -- ¡Añadido UNIQUE aquí!
    score INT,
    exam_date TIMESTAMP,
    FOREIGN KEY (aspirant_id) REFERENCES aspirants(id),
    PRIMARY KEY (id)
    );