-- These run automatically via Hibernate ddl-auto=update for the demo.
-- In real production, replace ddl-auto with Flyway/Liquibase migrations and
-- apply this constraint explicitly - Hibernate's auto-DDL will NOT create it.

-- The actual correctness guarantee against double-booking, independent of the
-- Redis hold layer's behavior under any timing race:
ALTER TABLE booking_seats
    ADD CONSTRAINT uq_show_seat UNIQUE (show_id, seat_id);

-- If you outgrow point-in-time seat booking and move to time-window based
-- booking (as in the Restaurant Reservation problem), the equivalent guarantee
-- for interval overlap instead of point equality is:
--
-- CREATE EXTENSION IF NOT EXISTS btree_gist;
-- ALTER TABLE reservations
--     ADD CONSTRAINT no_overlapping_reservations
--     EXCLUDE USING gist (
--         table_id WITH =,
--         tsrange(start_time, end_time) WITH &&
--     ) WHERE (status = 'CONFIRMED');
