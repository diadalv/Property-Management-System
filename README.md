# Hotel PMS — Property Management System

[![CI](https://github.com/diadalv/Property-Management-System/actions/workflows/ci.yml/badge.svg)](https://github.com/diadalv/Property-Management-System/actions/workflows/ci.yml)

A hotel **Property Management System** built with **Java** and **Spring Boot**, backed by an **Oracle Database**.
It covers the core daily operations of a hotel: reservations, check-in and check-out, guest folios (charges and payments), housekeeping, and the night audit.

I worked for several years in hotel operations, using **OPERA PMS** at the front desk. This project is my way of building, from the developer's side, the kind of system I used every day — with the same business rules a real front office depends on.

---

## Features

**Reservations**
- Create reservations with automatic availability check (no overbooking)
- Check-in and check-out with business rules:
  - check-in only for booked reservations and rooms that are not dirty
  - check-out only when the folio balance is zero
- Daily lists: arrivals, departures and in-house guests

**Folio (guest account)**
- View all charges, payments, totals and balance
- Post charges (only for checked-in guests)
- Record payments (deposits before arrival, or during the stay)
- Charges are **append-only**, like in a real PMS: a posted charge is never deleted

**Housekeeping**
- Mark rooms as clean, dirty or inspected
- A room can only be inspected when it is clean
- Check-out automatically marks the room as dirty
- Housekeeping status is saved in the database, so it survives restarts

**Night audit**
- Posts the room charge for every in-house guest for a given business date
- Cannot run twice for the same date, so no guest is charged twice

**API & errors**
- REST API with JSON
- Clear error responses: `400` invalid data, `404` not found, `409` business rule conflict

**Security**
- Login with HTTP Basic and role-based access: `MANAGER`, `RECEPTION`, `HOUSEKEEPING`
- `401` without login, `403` when the role is not allowed
- Passwords are hashed with BCrypt

---

## Tech stack

| Area | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4 (Spring Web, Spring Data JPA) |
| Persistence | Hibernate (JPA) |
| Security | Spring Security (HTTP Basic, role-based access, BCrypt) |
| Database | Oracle AI Database 26ai Free, running in Docker |
| Build | Maven |
| Testing | JUnit 5, Mockito, MockMvc, Testcontainers |
| Version control | Git & GitHub (ticket-based workflow with pull requests) |
| CI | GitHub Actions (tests run on every pull request) |
| Containers | Docker, Docker Compose |

---

## Architecture

```
┌─────────────┐   HTTP / JSON    ┌──────────────────────────────────────────────┐   JDBC   ┌──────────────┐
│   Client    │ ───────────────► │              Spring Boot (port 8080)          │ ───────► │   Oracle DB  │
│ (browser,   │ ◄─────────────── │                                              │ ◄─────── │  (Docker,    │
│  other app) │                  │  Controllers → Hotel (service) → Repository  │          │  port 1521)  │
└─────────────┘                  └──────────────────────────────────────────────┘          └──────────────┘
```

Inside the application, each layer has one responsibility:

```
Controller          receives HTTP requests, converts JSON ↔ objects (DTOs), delegates to the Hotel
   │
   ▼
Hotel (service)     coordinates the use cases: find → apply business rule → save
   │
   ▼
Domain model        Reservation, Room, Folio, Charge, Payment — all business rules live here
   │
   ▼
Repository          ReservationRepository and RoomRepository interfaces, each with two implementations:
                     • Jpa...Repository      → Oracle (used by the application)
                     • InMemory...Repository → in memory (used by unit tests)
   │
   ▼
Persistence         JPA entities + mapper (domain ↔ entity) → Hibernate → Oracle
```

---

## Design decisions

- **Business rules live in the domain model, not in the controllers.**
  Controllers only translate HTTP to method calls. Rules such as "no check-out with an open balance" are in `Reservation`, so they apply everywhere and are covered by unit tests.

- **DTOs for the API.**
  The API never exposes internal classes. Request and response records (`CreateReservationRequest`, `ReservationResponse`, `FolioResponse`...) define the API contract, so the internal model can change without breaking clients.

- **Repository interfaces with two implementations.**
  The `Hotel` depends only on the `ReservationRepository` and `RoomRepository` interfaces. Switching from in-memory storage to Oracle was a one-line change in the configuration, and the in-memory versions are kept to keep unit tests fast and independent of a database.

- **Separate JPA entities and a mapper.**
  Domain classes stay free of persistence concerns (no-argument constructors, non-final fields, annotations). The trade-off is more code: the mapper converts between domain objects and entities, and only inserts new charges when saving.

- **A `folios` table from the start.**
  The application currently uses one folio per reservation, but the schema already supports several folio windows per reservation, as in OPERA (for example, room charges to the company and extras to the guest).

- **Action endpoints for state changes.**
  Check-in, check-out and housekeeping actions are `POST` endpoints such as `POST /reservations/{id}/check-in`, instead of letting the client set a status directly. The client requests the action, and the domain decides whether it is allowed.

---

## Testing

The tests follow the test pyramid: many fast unit tests, fewer API tests, and a small number of integration tests with a real database.

| Level | What it checks | Tools | Example |
|---|---|---|---|
| Unit tests | Business rules in the domain model and the `Hotel` service | JUnit 5, in-memory repositories | No overbooking, no check-out with an open balance |
| API tests | URLs, HTTP status codes, JSON responses and error handling | `@WebMvcTest`, MockMvc, Mockito | `POST /reservations` returns `409` when the room is already booked |
| Integration tests | Persistence with a real Oracle database: schema, entities, mappers | Testcontainers, `@SpringBootTest` | Room status is saved in Oracle and loaded again |

Integration tests start an Oracle container automatically, so Docker must be running. All tests run on every pull request with GitHub Actions.

---

## Security

Access is based on the employee's role, following the principle of least privilege:

| Endpoint | MANAGER | RECEPTION | HOUSEKEEPING |
|---|:-:|:-:|:-:|
| `/rooms/**` | ✅ | ✅ | ✅ |
| `/reservations/**` | ✅ | ✅ | ❌ |
| `POST /night-audit` | ✅ | ❌ | ❌ |

Users for local development (in memory, for demonstration only):

| Username | Password | Role |
|---|---|---|
| `manager` | `manager123` | MANAGER |
| `reception` | `reception123` | RECEPTION |
| `housekeeping` | `housekeeping123` | HOUSEKEEPING |

In a production system, users would be stored in a database or an identity provider, and the API would run over HTTPS.

---

## Database schema

```
ROOMS ──< RESERVATIONS ──< FOLIOS ──< CHARGES
                                  └──< PAYMENTS
```

| Table | Purpose |
|---|---|
| `rooms` | room number, floor, type, housekeeping status |
| `reservations` | guest details, room, dates, status, rate per night |
| `folios` | guest accounts per reservation (window number) |
| `charges` | room nights, extras, corrections |
| `payments` | cash, card or bank transfer |

The balance is **not stored**: it is always calculated from charges and payments, so it can never get out of sync.
The schema is created automatically on startup from `src/main/resources/schema.sql`.

---

## Getting started

### Option 1: Run everything with Docker Compose (recommended)

The only requirement is **Docker**.

```bash
docker compose up --build
```

This starts two containers: the Oracle database and the application. The application waits until the database is healthy before it starts. The first start takes a few minutes, while the database is initialized.

The API is available at `http://localhost:8080`. On the first start, sample rooms and reservations are created.

To stop:

```bash
docker compose down       # stops the containers, keeps the data
docker compose down -v    # also deletes the database volume
```

### Option 2: Run the application locally (for development)

Prerequisites: **Java 25** and **Docker**. Maven does not need to be installed: the project includes the Maven Wrapper (`mvnw` / `mvnw.cmd`).

1. Start only the database:

```bash
docker compose up -d oracle
docker compose ps          # wait until the status is "healthy"
```

2. Run the application:

```bash
./mvnw spring-boot:run        # macOS / Linux
.\mvnw.cmd spring-boot:run    # Windows
```

> The database connection is configured in `application.properties` with local defaults. It can be overridden with environment variables (`SPRING_DATASOURCE_URL`, `DB_USERNAME`, `DB_PASSWORD`). Docker Compose uses this to point the application to the `oracle` container.

### Run the tests

```bash
./mvnw test        # macOS / Linux
.\mvnw.cmd test    # Windows
```

> Docker must be running: the integration tests start their own Oracle container with Testcontainers.

---

## API overview

### Rooms
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/rooms` | All rooms |
| `GET` | `/rooms/{number}` | One room |
| `POST` | `/rooms/{number}/clean` | Mark room as clean |
| `POST` | `/rooms/{number}/dirty` | Mark room as dirty |
| `POST` | `/rooms/{number}/inspect` | Mark room as inspected (only if clean) |

### Reservations
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/reservations` | All reservations |
| `POST` | `/reservations` | Create a reservation |
| `GET` | `/reservations/{id}` | One reservation |
| `POST` | `/reservations/{id}/check-in` | Check in |
| `POST` | `/reservations/{id}/check-out` | Check out (balance must be zero) |
| `GET` | `/reservations/arrivals?date=YYYY-MM-DD` | Arrivals (default: today) |
| `GET` | `/reservations/departures?date=YYYY-MM-DD` | Departures (default: today) |
| `GET` | `/reservations/in-house` | Guests currently in house |

### Folio
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/reservations/{id}/folio` | Charges, payments, totals and balance |
| `POST` | `/reservations/{id}/charges` | Post a charge |
| `POST` | `/reservations/{id}/payments` | Record a payment |

### Night audit
| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/night-audit?date=YYYY-MM-DD` | Post room charges for the given business date |

### Examples

Create a reservation:

```bash
curl -X POST http://localhost:8080/reservations \
  -u reception:reception123 \
  -H "Content-Type: application/json" \
  -d '{
        "firstName": "Elena",
        "lastName": "Ivanova",
        "email": null,
        "phone": null,
        "roomNumber": "101",
        "arrivalDate": "2026-12-01",
        "departureDate": "2026-12-04",
        "ratePerNight": 85.00
      }'
```

Post a charge:

```bash
curl -X POST http://localhost:8080/reservations/{id}/charges \
  -u reception:reception123 \
  -H "Content-Type: application/json" \
  -d '{ "type": "EXTRA", "description": "Minibar", "amount": 12.50 }'
```

Error example (room already booked for those dates):

```json
HTTP 409 Conflict
{ "message": "Room 204 is not available for these dates" }
```

---

## How I work on this project

The project is developed like in a team:
- every change starts as a **ticket** (`PMS-1`, `PMS-2`, ...)
- each ticket has its own **branch** (`feature/PMS-12-post-reservation`)
- changes are merged through **pull requests** with a description of what changed, why, and how it was tested
- commits follow the format `PMS-12: Add POST /reservations with 400/409 error handling`
- every pull request runs the tests automatically with **GitHub Actions**
- `main` is protected: changes go through pull requests, and merging is blocked until CI passes

---

## Author

**Diana Koropetska** | Entry Level Software Engineer

- GitHub: [diadalv](https://github.com/diadalv)
- LinkedIn: [diadalv](https://www.linkedin.com/in/diadalv/)
