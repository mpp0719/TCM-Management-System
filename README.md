# TCM - Traditional Chinese Medicine Management System

A lightweight, local-first patient record system built for a small traditional Chinese medicine clinic. Runs entirely on a single machine — no external database server, no internet dependency, no installation beyond double-clicking an executable.

## Overview

This app was built to replace paper-based patient records with a simple, self-contained digital system for a clinic with a handful of staff and a modest daily patient volume. It handles patient records, visit history, medicine inventory, dispensing, payments, and appointment scheduling — all backed by an embedded database that lives in a single file on disk.

Full Chinese-language support is built in throughout (patient names, complaints, treatment notes, medicine names), since the clinic's records are frequently written in Chinese.

## Features

- **Patient records** — name, Malaysian IC number, gender, address, phone number, with wildcard search across name/IC/phone (works whether or not IC dashes are typed)
- **Visit history** — one patient can have any number of visits over time; each visit records the complaint, treatment plan, and an optional next-appointment date, and can be edited after the fact
- **Medicine inventory** — stock levels are always computed from restock and dispense history (never a manually-edited number, so it can't silently drift from reality); each medicine has a fixed unit of measure and a selling price
- **Restocking** — logs quantity, date, and cost per batch, with full restock history per medicine
- **Dispensing** — medicine given during a visit is deducted from stock immediately; dispensing that would take stock negative is blocked
- **Payments** — one mandatory, immutable payment record per visit; total is the doctor's treatment fee plus an automatically computed medicine cost (quantity dispensed × each medicine's selling price)
- **Appointment calendar** — click-to-select a date range, see which days have upcoming appointments, and who's booked on any given day
- **Automatic backups** — the database is snapshotted to a zip file every 30 minutes and on graceful shutdown, with automatic cleanup of old backups
- **Dark / light theme** — cyberpunk-accented UI, defaults to light mode, toggleable, persists across sessions

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot, Spring Data JPA |
| Database | H2 (embedded, file-based — no server install required) |
| Frontend | HTML, CSS, vanilla JavaScript (no framework) |
| Packaging | `jpackage` — bundles the JRE, produces a standalone `.exe` |

The frontend is served directly by Spring Boot as static resources — there's no separate frontend server, build step, or framework. The whole app is one process.

## Project Structure

```
tcm-management-system/
├── src/main/java/com/tcm_management_system/
│   ├── model/          # JPA entities (Patient, Visit, Medicine, MedicineRestock, VisitMedication, Payment)
│   ├── repository/     # Spring Data JPA repositories
│   ├── service/        # Business logic, validation, DTO mapping
│   ├── controller/      # REST endpoints
│   ├── dto/             # Request/response DTOs — entities are never serialized directly
│   ├── exception/       # Custom exceptions + a global exception handler
│   └── util/             # Shared helpers (e.g. IC number normalization)
├── src/main/resources/
│   ├── static/           # Frontend: HTML, CSS, JS — served at localhost:8080
│   └── application.properties
├── data/                 # H2 database file (created at runtime, not committed)
└── backups/              # Automatic database backups (created at runtime, not committed)
```

## Architecture Notes

- **Database is embedded H2 in file mode**, single-computer only (not server mode) — the JDBC URL points to a local file, no network exposure, no port to secure.
- **All financial figures are computed and stored server-side, never trusted from the client** — medicine stock, payment totals, and medicine cost snapshots are all calculated in the service layer.
- **Payments are immutable** — once recorded, a payment cannot be edited or deleted. Medicine cost and total are snapshotted at the time of payment, so later price changes never retroactively alter a past receipt.
- **IC numbers are stored digits-only** internally and formatted with dashes only for display — this keeps search working regardless of whether a user types dashes.
- **No delete functionality** for patients or medicines — records are meant to persist for a patient's full history with the clinic; only additive/corrective actions (edit, add) are supported.

## Running Locally (Development)

**Prerequisites:** JDK 17+, an IDE with Spring Boot support (developed using IntelliJ IDEA).

1. Clone the repository.
2. Open the project in IntelliJ (or your IDE of choice) as a Maven/Spring Boot project.
3. Run `TcmApplication.java`.
4. Open `http://localhost:8080` in a browser.

The H2 database file will be created automatically under `data/` on first run — no manual setup required.

## API Overview

REST API under `/api`, organized by resource:

- `/api/patients` — create, update, search, get by ID
- `/api/visits` — add/update visits per patient, get visit history, get appointments in a date range
- `/api/medicines` — create, update, get all, restock, dispense, restock history, current stock
- `/api/payments` — create a payment for a visit, get a visit's payment

All endpoints return JSON; errors return a plain-text message with an appropriate HTTP status code (400 for invalid input, 404 for not found).

## Deployment

The app is packaged as a standalone Windows `.exe` using `jpackage`, with the JRE bundled inside — the target machine doesn't need Java installed. Launching the `.exe` starts the Spring Boot server and opens the default browser to `localhost:8080` automatically.

## License

Private project — not currently licensed for public reuse.
