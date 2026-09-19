CREATE TABLE movies
(
    id               VARCHAR(255) PRIMARY KEY,
    title            VARCHAR(255),
    duration_minutes INT,
    language         VARCHAR(255),
    genre            VARCHAR(255)
);

CREATE TABLE theatres
(
    id      VARCHAR(255) PRIMARY KEY,
    name    VARCHAR(255),
    city    VARCHAR(255),
    address VARCHAR(255)
);

CREATE TABLE screens
(
    id            VARCHAR(255) PRIMARY KEY,
    theatre_id    VARCHAR(255) REFERENCES theatres (id),
    screen_number VARCHAR(255),
    screen_type   VARCHAR(255)
);

CREATE TABLE seats
(
    id          VARCHAR(255) PRIMARY KEY,
    screen_id   VARCHAR(255) REFERENCES screens (id),
    seat_number VARCHAR(255),
    seat_type   VARCHAR(255),
    UNIQUE (screen_id, seat_number)
);

CREATE TABLE shows
(
    id         VARCHAR(255) PRIMARY KEY,
    movie_id   VARCHAR(255) REFERENCES movies (id),
    screen_id  VARCHAR(255) REFERENCES screens (id),
    start_time TIMESTAMP,
    end_time   TIMESTAMP,
    base_price NUMERIC
);

CREATE TABLE bookings
(
    id                VARCHAR(255) PRIMARY KEY,
    show_id           VARCHAR(255),
    user_id           VARCHAR(255),
    status            VARCHAR(50),
    payment_status    VARCHAR(50),
    payment_reference VARCHAR(255),
    total_amount      NUMERIC,
    created_at        TIMESTAMP,
    idempotency_key   VARCHAR(255) UNIQUE
);

CREATE TABLE booking_seats
(
    id         BIGSERIAL PRIMARY KEY,
    booking_id VARCHAR(255) REFERENCES bookings (id),
    show_id    VARCHAR(255),
    seat_id    VARCHAR(255)
);