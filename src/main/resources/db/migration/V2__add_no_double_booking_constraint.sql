ALTER TABLE booking_seats
    ADD CONSTRAINT uq_show_seat UNIQUE (show_id, seat_id);