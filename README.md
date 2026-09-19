# Movie Ticket Booking Service
```
Spring Boot 4 / Java 21 service for browsing shows and booking seats, built
around real concurrency correctness rather than just the happy path.
```

## Catalog
```
Theatre → Screen → Seat (created once per screen) and Movie → Show. A Seat
belongs to a Screen, not a Show — availability is derived per show, not
duplicated per showing.
```

## Booking Flow
```
POST /hold → Redis SETNX per seat, 10-min TTL, all-or-nothing
POST /confirm → idempotency check → atomic hold-extend (Lua) → payment
→ short DB transaction → DB UNIQUE(show_id, seat_id) as final guard
```

## Key Design Decisions
```
- Seat status is never stored — derived from Redis holds + confirmed bookings.
- Payment behind a `PaymentGateway` interface (Mock default, Stripe via profile).
- Booking persistence lives in a separate bean (`BookingPersistenceService`)
  to avoid a Spring self-invocation bug that would silently skip `@Transactional`.
- DB unique constraint is the real correctness guarantee; Redis just makes
  the common case fast.
```

## Run
```bash
docker compose up -d
mvn spring-boot:run
```