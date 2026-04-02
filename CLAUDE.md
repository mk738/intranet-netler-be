# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Run the app locally (requires PostgreSQL on localhost:5433)
mvn spring-boot:run

# Run all tests (no database required — pure unit/mock tests)
mvn test

# Run a single test class
mvn test -Dtest=BoardServiceTest

# Run a single test method
mvn test -Dtest=BoardServiceTest#someMethod

# Build
mvn clean package
```

Local PostgreSQL defaults: `jdbc:postgresql://localhost:5433/intranet`, user/pass `intranet`.

## Architecture

Spring Boot 3.3 / Java 21 monolith organized strictly by domain. Each domain package under `com.company.intranet` contains its entity, repository, service, controller, and DTOs — no shared layers across domains.

### Domains

| Package | Responsibility |
|---------|---------------|
| `employee` | Employee accounts, profiles, bank info (Jasypt-encrypted), education, contracts, CVs, avatars, benefits |
| `vacation` | Vacation requests with PENDING → APPROVED/REJECTED workflow |
| `crm` | Client companies and employee-to-client assignments |
| `hub` | News posts and internal events with RSVP |
| `board` | Kanban boards — boards, columns, cards, comments, file attachments |
| `faq` | FAQ entries with categories and ordering |
| `candidate` | Recruitment candidate tracking |
| `skill` | Skill catalog, referenced by employees via M2M |
| `auth` | Firebase UID → Employee lookup with email fallback |
| `security` | Spring Security config, Firebase filter, role/permission model |
| `common` | `Auditable` base entity, `ApiResponse<T>` envelope, exceptions, error codes |

### Auth & Security

Firebase ID tokens are validated by `FirebaseTokenFilter` on every `/api/**` request. The filter resolves the Firebase UID to an `Employee`, loads their role + permissions, and sets the `SecurityContext`. If a user's Firebase UID changes (e.g. provider switch), the filter re-links via email.

Three roles: `SUPERADMIN`, `ADMIN`, `EMPLOYEE`. Fine-grained permissions are defined in `Permission.java` and mapped per role in `RolePermissions.java`. Controllers use `@PreAuthorize` and `@CurrentUser` (custom parameter annotation to inject the authenticated employee).

### Schema & Migrations

`ddl-auto: update` — **Hibernate manages the schema**, not Flyway. Flyway is explicitly disabled (`flyway.enabled: false`). The `db/migration/` SQL scripts are historical records and run once only on environments where Flyway was previously active; they do not run on new deployments.

**Important:** avoid introducing Hibernate entity changes that would require destructive DDL (column drops, renames) — `ddl-auto: update` only adds, never removes.

Dev seed data lives in `src/main/resources/db/seed/dev-data.sql`. Run manually to reset a local database. When adding new tables referenced by board cards or other seeded entities, add the corresponding `DELETE FROM` in FK-safe order at the top of that file.

### Key Patterns

**`Auditable`** — all entities extend this abstract class, which provides `createdAt`, `updatedAt`, `createdBy`, `updatedBy` auto-populated by Spring Data auditing.

**`ApiResponse<T>`** — every controller returns this envelope:
```json
{ "success": true,  "data": {...}, "message": null }
{ "success": false, "data": null,  "message": "Reason" }
```
Use `ApiResponse.success(data)` and `ApiResponse.error(message)`.

**DTOs are Java records** — immutable, constructor-only, no setters. Mappers live as `@Component` classes (not MapStruct).

**Exceptions** — throw `BadRequestException` or `ResourceNotFoundException` (extend `AppException`). `GlobalExceptionHandler` translates them to the error envelope with an `ErrorCode` enum value.

**Email** — sent via `MailerSendService` using RestClient. Events are published on the domain service and consumed asynchronously by `EmailEventListener`. Do not call the mailer directly from controllers.

**Encryption** — `BankInfo.accountNumber` and `clearingNumber` are Jasypt-encrypted at rest. The password comes from the `JASYPT_PASSWORD` env var. Never change this after first deployment without re-encrypting existing data.

### Testing

Tests are pure unit tests — `@ExtendWith(MockitoExtension.class)` for services, `@WebMvcTest` for controllers. No database, no TestContainers. Each test class mocks its own dependencies with `@Mock` / `@InjectMocks`.

### Deployment

Docker + Railway. The `Dockerfile` builds a layered JAR image. `railway.toml` configures the deploy. Healthcheck: `GET /actuator/health`.
