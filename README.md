# TCM Clinic Patient Record System

A lightweight, local-first patient record system built for a small traditional Chinese medicine clinic. Runs entirely on a single machine — no external database server, no internet dependency, no installation beyond double-clicking an executable.

## Overview

This app replaces paper-based patient records with a simple, self-contained digital system for a clinic with a handful of staff and a modest daily patient volume. It handles patient records, visit history, medicine inventory, dispensing, payments, appointment scheduling, and financial export — all backed by an embedded database that lives in a single file on disk.

Full Chinese-language support is built in throughout (patient names, complaints, treatment notes, medicine names), since the clinic's records are frequently written in Chinese.

## Features

- **Patient records** — name, Malaysian IC number, gender, address, phone number, with wildcard search across name/IC/phone (works whether or not IC dashes are typed). Patients with an upcoming appointment or a recent visit surface near the top of search results automatically.
- **Visit history** — a patient can have any number of visits over time; each records the complaint, treatment plan, and an optional next-appointment date, and can be edited — **until a payment is recorded against it**, after which the visit is locked.
- **Medicine inventory** — stock levels are always computed from restock and dispense history (never a manually-edited number, so it can't silently drift from reality); each medicine has a fixed unit of measure (locked after creation) and an editable selling price.
- **Restocking** — logs quantity, date, and cost per batch, with full restock history per medicine.
- **Dispensing** — medicine given during a visit is deducted from stock immediately; dispensing that would take stock negative is blocked, and dispensing is **locked once a visit has been paid**, so a payment's medicine-cost figure can never drift out of sync afterward.
- **Payments** — one payment record per visit; the total is the doctor's treatment fee plus an automatically computed medicine cost (quantity dispensed × each medicine's selling price at the time of payment). Payments can be corrected after the fact (date, method, treatment fee) to fix data-entry mistakes — the medicine-cost figure itself is never editable, since dispensing is already locked by that point.
- **Appointment calendar** — click-to-select a date range, see which days have upcoming appointments, and who's booked on any given day, in a two-column calendar/detail layout.
- **Transaction export** — download all payments within a chosen date range as a formatted `.xlsx` file (patient name, IC, visit date, payment method, treatment fee, medicine cost, total).
- **Automatic backups** — the database is snapshotted to a timestamped zip file every 30 minutes and on graceful shutdown, with automatic cleanup keeping only the most recent 48 backups (~24 hours of history).
- **Dark / light theme** — cyberpunk-accented UI, defaults to light mode, toggleable, persists across sessions.
- **One-click launch** — the packaged `.exe` starts the server and opens the default browser automatically; no manual navigation required.

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot, Spring Data JPA |
| Database | H2 (embedded, file-based — no server install required) |
| Excel export | Apache POI |
| Frontend | HTML, CSS, vanilla JavaScript (no framework) |
| Packaging | `jpackage` — bundles the JRE, produces a standalone Windows `.exe` |

The frontend is served directly by Spring Boot as static resources — there's no separate frontend server, build step, or framework. The whole app is one process.

## Project Structure

```
TCM-Management-System/
├── src/main/java/com/tcm_management_system/
│   ├── model/          # JPA entities — Patient, Visit, Medicine, MedicineRestock,
│   │                   #   VisitMedication, Payment
│   ├── repository/     # Spring Data JPA repositories
│   ├── service/        # Business logic, validation, DTO mapping, backups
│   ├── controller/     # REST endpoints
│   ├── dto/             # Request/response DTOs — entities are never serialized directly
│   ├── exception/       # Custom exceptions + a global exception handler
│   ├── util/             # Shared helpers (e.g. IC number normalization)
│   ├── BrowserLauncher.java   # Opens the default browser once the server is ready
│   └── TcmApplication.java    # Main class
├── src/main/resources/
│   ├── static/                 # Frontend: HTML, CSS, JS — served at localhost:8080
│   ├── application.properties       # Production defaults (safe for shipping)
│   └── application-dev.properties   # Dev-only overrides (SQL logging, H2 console)
├── data/                 # H2 database file (created at runtime, not committed)
└── backups/               # Automatic database backups (created at runtime, not committed)
```

## Architecture Notes

- **Database is embedded H2 in file mode**, single-computer only (not server mode) — the JDBC URL points to a local file, no network exposure, no port to secure.
- **All financial figures are computed and stored server-side, never trusted from the client** — medicine stock, payment totals, and medicine cost snapshots are all calculated in the service layer.
- **Payments snapshot medicine cost at creation time and never recalculate it** — even if a medicine's selling price changes later, past payments stay exactly as they were when recorded. Dispensing is locked once a visit is paid specifically to guarantee this can never drift.
- **IC numbers are stored digits-only** internally and formatted with dashes only for display — this keeps search working regardless of whether a user types dashes.
- **No delete functionality** for patients or medicines — records are meant to persist for a patient's full history with the clinic; only additive/corrective actions (edit, add) are supported.
- **Config is split by Spring profile** — `application.properties` holds safe, locked-down defaults (no SQL logging, H2 console disabled); `application-dev.properties` (activated with `--spring.profiles.active=dev`) re-enables both for development convenience.

## Running Locally (Development)

**Prerequisites:** JDK 17+ (this project was built and packaged against a newer JDK — see `pom.xml` for the exact target), an IDE with Spring Boot support (developed using IntelliJ IDEA).

1. Clone the repository.
2. Open the project in IntelliJ as a Maven/Spring Boot project.
3. Run `TcmApplication.java` — optionally with the `dev` profile active (`--spring.profiles.active=dev`) for SQL logging and the H2 web console.
4. The default browser opens automatically to `http://localhost:8080`. If it doesn't, open it manually.

The H2 database file is created automatically under `data/` on first run — no manual setup required.

## API Overview

REST API under `/api`, organized by resource:

- `/api/patients` — create, update, search, get by ID
- `/api/visits` — add/update visits per patient (locked once paid), get visit history, get appointments in a date range
- `/api/medicines` — create, update (name/price only — unit is locked), get all, restock, dispense (locked once the visit is paid), restock history, current stock
- `/api/payments` — create, update, get by visit, export all payments in a date range as `.xlsx`

All endpoints return JSON (except the Excel export, which returns a binary file); errors return a plain-text message with an appropriate HTTP status code (400 for invalid input, 404 for not found).

## Deployment

Packaged as a standalone Windows `.exe` via `jpackage`, with the JRE bundled inside — the target machine doesn't need Java installed. The launcher is built with a capped JVM heap (`-Xmx256m`, suitable for older/lower-spec clinic machines) and a visible console window, which doubles as the app's "on/off" indicator — closing it shuts the app down gracefully (triggering a final backup).

**Release checklist:**
1. `mvn clean package`
2. Copy the resulting jar into a clean `input/` folder
3. Run `jpackage` with `--main-class org.springframework.boot.loader.launch.JarLauncher` (note the `.launch.` package — this is Spring Boot 3.2+'s loader path)
4. Test the packaged `.exe` directly (not through the IDE)
5. **Delete `data/` and `backups/` from the tested output folder before zipping** — otherwise the shipped copy inherits the developer's test database and its file permissions won't transfer cleanly to another machine
6. Zip the entire output folder (not just the `.exe`) and distribute

## License

MIT — see [LICENSE](./LICENSE) for details.
