CREATE TABLE IF NOT EXISTS rooms
(
    room_number         VARCHAR2(10) PRIMARY KEY,
    floor               NUMBER(3)    NOT NULL,
    room_type           VARCHAR2(20) NOT NULL,
    housekeeping_status VARCHAR2(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations
(
    reservation_id     VARCHAR2(36)  PRIMARY KEY,
    guest_first_name   VARCHAR2(100) NOT NULL,
    guest_last_name    VARCHAR2(100) NOT NULL,
    guest_email        VARCHAR2(200),
    guest_phone        VARCHAR2(30),
    room_number        VARCHAR2(10)  NOT NULL REFERENCES rooms (room_number),
    arrival_date       DATE          NOT NULL,
    departure_date     DATE          NOT NULL,
    reservation_status VARCHAR2(20)  NOT NULL,
    rate_per_night     NUMBER(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS folios
(
    id             NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reservation_id VARCHAR2(36) NOT NULL REFERENCES reservations (reservation_id),
    window_number  NUMBER(2)    NOT NULL,
    UNIQUE (reservation_id, window_number)
);

CREATE TABLE IF NOT EXISTS charges
(
    id          NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    folio_id    NUMBER        NOT NULL REFERENCES folios (id),
    charge_date DATE          NOT NULL,
    charge_type VARCHAR2(20)  NOT NULL,
    description VARCHAR2(200) NOT NULL,
    amount      NUMBER(10, 2) NOT NULL
);

CREATE TABLE IF NOT EXISTS payments
(
    id             NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    folio_id       NUMBER        NOT NULL REFERENCES folios (id),
    payment_date   DATE          NOT NULL,
    payment_method VARCHAR2(20)  NOT NULL,
    amount         NUMBER(10, 2) NOT NULL
);