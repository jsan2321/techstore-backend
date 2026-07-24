# TechStore Backend

TechStore Backend is the REST API for the TechStore e-commerce application. It provides product catalog management, customer accounts, shopping carts, orders, image storage, email flows, PayPal Sandbox payments, and administration endpoints.

## Features

- Product, brand, and category catalog with public browsing and administrative management.
- Customer registration, email confirmation, password reset, password changes, and Google Sign-In.
- JWT access and refresh-token authentication with role-based administration endpoints.
- Per-user shopping carts and order placement with shipping addresses.
- PayPal Sandbox payment initiation and capture.
- Product-image uploads backed by Amazon S3-compatible storage.

## Architecture

The code is organized using hexagonal architecture (ports and adapters):

- `domain`: business models, value objects, events, and rules.
- `application`: use cases and inbound/outbound port contracts.
- `infrastructure`: REST controllers, JPA repositories, security, email, storage, payment, and framework configuration.

The application root package is `com.ecoapi.techstore`. See [Hexagonal.md](Hexagonal.md) for the project architecture notes.

## Technology

- Java 21 and Spring Boot 3.4
- Spring Web, Validation, Security, Data JPA, and Mail
- PostgreSQL 16
- JWT, BCrypt, and Google Identity Services token verification
- AWS S3-compatible object storage
- PayPal Sandbox
- OpenAPI / Swagger UI

## Prerequisites

- JDK 21
- Docker Desktop with Docker Compose
- An S3-compatible storage account or local compatible service for image uploads
- Google OAuth client ID and PayPal Sandbox credentials if those features are enabled

## Configuration

Copy the example environment file and set values appropriate for your local setup:

```powershell
Copy-Item .env.example .env
```

On macOS/Linux:

```bash
cp .env.example .env
```

`.env` is ignored by Git. It contains database, mail, JWT, admin, storage, Google, and PayPal settings. Do not commit it. The tracked `.env.example` documents every required variable.

## Run locally

Start PostgreSQL and MailHog:

```bash
docker compose up -d
```

Then launch the API:

```powershell
.\mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

The API listens on `http://localhost:8081` and serves routes below `/api/v1`.

MailHog receives local email at SMTP port `1025`; its inbox is available at `http://localhost:8025`.

## API and authentication

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI document: `http://localhost:8081/v3/api-docs`
- API base path: `http://localhost:8081/api/v1`

Authentication endpoints issue a JWT access token and a refresh token. Send the access token as `Authorization: Bearer <token>` for authenticated routes. Administrative operations require an account with the `ADMIN` role. Google Sign-In sends the browser-issued Google ID token to the API, where the token is verified before a local session is created.

## Integrations

- **PostgreSQL** stores users, products, carts, orders, and token records.
- **S3 storage** stores product images; configure `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_REGION`, and `AWS_S3_BUCKET_NAME`.
- **SMTP/MailHog** delivers confirmation and password-reset messages.
- **PayPal Sandbox** is configured with `PAYPAL_CLIENT_ID` and `PAYPAL_SECRET`.

## Tests and packaging

Run the test suite:

```powershell
.\mvnw.cmd test
```

Package the application:

```powershell
.\mvnw.cmd package
```

The Dockerfile expects the packaged JAR in `target/`:

```bash
docker build -t techstore-backend .
```

## Related repository

The browser client is maintained separately in the `techstore-frontend` repository. Its local API URL must point to this service's `/api/v1` base path.

## Security notes

- Keep `.env` and all private keys, passwords, and secrets outside source control.
- Google browser client IDs are public identifiers; Google client secrets must remain server-side.
- Rotate a credential immediately if it is ever committed or shared unintentionally.
