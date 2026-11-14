CREATE TABLE films (
                       id BIGSERIAL PRIMARY KEY,
                       film_id BIGINT UNIQUE NOT NULL,
                       film_name VARCHAR(255) NOT NULL,
                       year INTEGER,
                       rating DOUBLE PRECISION,
                       description TEXT
);