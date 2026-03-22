# Intranet — Backend

Spring Boot 3 REST API for the company intranet platform. Handles authentication, employee management, vacation requests, CRM, company hub (news & events), FAQ, and email notifications.

---

## Tech stack

| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.3 |
| Security | Spring Security 6 + Firebase Admin SDK |
| Database | PostgreSQL 16 + Spring Data JPA (Hibernate) |
| Encryption | Jasypt (bank info fields) |
| Email | Resend API via Spring RestClient |
| Build | Maven 3.9 |
| Tests | JUnit 5 + Mockito + Spring MockMvc |

---

## Prerequisites

- Java 21+
- Maven 3.9+
- PostgreSQL 16 (or run via Docker — see root `docker-compose.yml`)
- Firebase project with a service account key

---

## Running locally

### 1. Start PostgreSQL

From the repo root:

```bash
docker compose up -d postgres
```

### 2. Firebase credentials

Download your Firebase service account key and save it as:

```
backend/firebase-service-account.json
```

The app reads this file automatically when `FIREBASE_CREDENTIALS_JSON` is not set.

### 3. Configure environment

Create `backend/.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/intranet
DB_USERNAME=intranet
DB_PASSWORD=intranet
JASYPT_PASSWORD=any-local-secret
RESEND_API_KEY=re_placeholder
MAILERSEND_API_TOKEN=your-mailersend-token
CORS_ORIGINS=http://localhost:3000
```

### 4. Run

```bash
mvn spring-boot:run
```

API available at http://localhost:8080.

> **IntelliJ tip — store env vars in Run Configuration so you don't have to set them every time:**
>
> 1. Open **Run → Edit Configurations**
> 2. Select (or create) your `IntranetApplication` Spring Boot run config
> 3. Click the **Environment variables** field → paste all the key=value pairs from your `.env` file (semicolon-separated: `DB_URL=...;DB_USERNAME=...;JASYPT_PASSWORD=...`)
> 4. Click **OK** — IntelliJ will inject these automatically every time you run or debug the app

Hibernate creates/updates the schema on first start — no migration step needed.

---

## Running tests

```bash
mvn test
```

All tests are unit/slice tests (no database required). MockMvc is used for controller tests, Mockito for service tests.

---

## Project structure

```
src/
├── main/
│   ├── java/com/company/intranet/
│   │   ├── auth/             POST /api/auth/me — resolve Firebase token to employee
│   │   ├── security/         FirebaseTokenFilter, SecurityConfig, @CurrentUser
│   │   ├── config/           AppConfig (RestClient), FirebaseConfig, CorsConfig
│   │   ├── common/           Auditable, ApiResponse<T>, GlobalExceptionHandler
│   │   ├── employee/         GET/POST /api/employees — profiles, bank info, education
│   │   ├── vacation/         GET/POST/PUT /api/vacations — requests & admin review
│   │   ├── crm/              GET/PUT /api/clients, POST/PUT /api/assignments
│   │   ├── hub/              GET/POST/PUT/DELETE /api/news, /api/events, /api/events/{id}/rsvp
│   │   ├── faq/              GET/POST/PUT/DELETE /api/faq
│   │   └── notification/     EmailService (Resend), EmailTemplates, event listeners
│   └── resources/
│       ├── application.yml           Base config (all profiles)
│       ├── application-test.yml      Test environment overrides
│       ├── application-prod.yml      Production overrides
│       └── db/seed/dev-data.sql      Seed data (loaded in test profile)
└── test/
    └── java/com/company/intranet/    One test class per controller/service
```

---

## API overview

All endpoints are prefixed with `/api` and require a valid Firebase ID token in the `Authorization: Bearer <token>` header, except where noted.

| Method | Path | Role | Description |
|--------|------|------|-------------|
| POST | `/api/auth/me` | Any | Resolve token → employee profile |
| GET | `/api/employees` | Admin | List all employees |
| POST | `/api/employees` | Admin | Invite new employee |
| GET | `/api/employees/me` | Any | Own profile + education |
| POST | `/api/employees/me/education` | Any | Add education entry |
| DELETE | `/api/employees/me/education/{id}` | Any | Remove education entry |
| GET | `/api/employees/{id}` | Admin | Employee detail + bank info |
| GET | `/api/vacations/me` | Any | Own vacation requests |
| POST | `/api/vacations` | Any | Submit vacation request |
| DELETE | `/api/vacations/{id}` | Any | Cancel own request |
| GET | `/api/vacations` | Admin | All vacation requests |
| PUT | `/api/vacations/{id}/review` | Admin | Approve / reject |
| GET | `/api/vacations/summary` | Admin | Pending / approved / rejected counts |
| GET | `/api/clients` | Admin | List clients |
| GET | `/api/clients/{id}` | Admin | Client detail |
| PUT | `/api/clients/{id}` | Admin | Update client |
| GET | `/api/placements` | Admin | Placement overview + stats |
| POST | `/api/assignments` | Admin | Create assignment |
| PUT | `/api/assignments/{id}/end` | Admin | End assignment |
| GET | `/api/news` | Any | News list (published only for employees, all for admins) |
| POST | `/api/news` | Admin | Create news post |
| GET | `/api/news/{id}` | Any | News post detail |
| PUT | `/api/news/{id}` | Admin | Update news post |
| DELETE | `/api/news/{id}` | Admin | Delete news post |
| PUT | `/api/news/{id}/publish` | Admin | Publish / unpublish |
| GET | `/api/events` | Any | Upcoming events |
| POST | `/api/events` | Admin | Create event |
| GET | `/api/events/{id}` | Any | Event detail |
| PUT | `/api/events/{id}` | Admin | Update event |
| DELETE | `/api/events/{id}` | Admin | Delete event |
| GET | `/api/events/{id}/rsvp` | Any | RSVP status + counts |
| POST | `/api/events/{id}/rsvp` | Any | Submit RSVP |
| GET | `/api/faq` | Any | FAQ list |
| POST | `/api/faq` | Admin | Create FAQ item |
| PUT | `/api/faq/{id}` | Admin | Update FAQ item |
| DELETE | `/api/faq/{id}` | Admin | Delete FAQ item |

All responses use a standard envelope:

```json
{
  "success": true,
  "data": { ... },
  "message": null
}
```

---

## Environment variables

| Variable | Default | Description |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5433/intranet` | PostgreSQL JDBC URL |
| `DB_USERNAME` | `intranet` | Database username |
| `DB_PASSWORD` | `intranet` | Database password |
| `JASYPT_PASSWORD` | — | Encryption key for bank info — **never change after first deploy** |
| `RESEND_API_KEY` | — | Resend API key (news, events, vacation emails) |
| `RESEND_FROM` | `Intranet <no-reply@yourcompany.com>` | Email sender address for Resend |
| `RESEND_BASE_URL` | `https://intranet.yourcompany.com` | Public app URL used in email links |
| `MAILERSEND_API_TOKEN` | — | MailerSend API token (employee invite emails) |
| `MAILERSEND_FROM_EMAIL` | `no-reply@yourcompany.com` | Sender email for MailerSend |
| `MAILERSEND_FROM_NAME` | `Intranet` | Sender display name for MailerSend |
| `FIREBASE_CREDENTIALS_JSON` | _(falls back to file)_ | Full service account JSON as a string |
| `CORS_ORIGINS` | `http://localhost:3000` | Comma-separated allowed frontend origins |
| `PORT` | `8080` | Server port |
| `SPRING_PROFILES_ACTIVE` | _(default)_ | `prod` in production, `test` in test environment |

---

## Spring profiles

| Profile | When to use | Seed data |
|---|---|---|
| _(none)_ | Local development | No |
| `test` | Railway test environment | Yes — reloaded on every restart |
| `prod` | Railway production | No |
