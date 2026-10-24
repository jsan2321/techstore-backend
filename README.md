# TechStore Backend API

[![Java](https://img.shields.io/badge/Java-21-orange.svg?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen.svg?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg?logo=postgresql)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-red.svg?logo=flyway)](https://flywaydb.org/)
[![Redis](https://img.shields.io/badge/Redis-Alpine-red.svg?logo=redis)](https://redis.io/)
[![Frontend Companion](https://img.shields.io/badge/Frontend-Angular%2021-red.svg?logo=angular)](https://github.com/jsan2321/techstore-frontend)

> The REST API for the **TechStore** e-commerce platform. Engineered with **Hexagonal Architecture (Ports & Adapters)**, **Domain-Driven Design (DDD)**, and **Spring Boot 3.4**. Designed to demonstrate production-grade reliability, concurrency control, and secure payment lifecycles.

🌐 **Client Repository**: [TechStore Frontend (Angular 21)](https://github.com/jsan2321/techstore-frontend)

---

## 📌 Table of Contents

- [Architectural Overview](#-architectural-overview)
- [Key Engineering Highlights](#-key-engineering-highlights)
- [Tech Stack](#-tech-stack)
- [System Architecture Diagram](#-system-architecture-diagram)
- [Prerequisites](#-prerequisites)
- [Environment Configuration](#-environment-configuration)
- [Quick Start](#-quick-start)
- [API Documentation & Health](#-api-documentation--health)
- [Pre-Seeded Accounts](#-pre-seeded-accounts)
- [Testing & Quality](#-testing--quality)
- [Security Model](#-security-model)

---

## 🏛 Architectural Overview

The backend is built as a **Modular Monolith** adhering strictly to **Hexagonal Architecture (Ports & Adapters)**. The application is divided into distinct bounded contexts (`user`, `product`, `cart`, `order`). Core business entities and domain rules are completely isolated from Spring frameworks, JPA, and third-party APIs.

```
com.ecoapi.techstore.<context>/
├── domain/                    # Pure Java domain models & business rules (Framework-free)
│   ├── model/                 # Rich aggregates & entities (Order, User, Product, Cart)
│   ├── valueobjects/          # Immutable value types (Money, Email, ProductId, Quantity)
│   └── exception/             # Domain business exceptions
│
├── application/               # Application orchestration layer
│   ├── port/
│   │   ├── in/                # Inbound Port Interfaces (Use cases invoked by adapters)
│   │   └── out/               # Outbound Port Interfaces (Repository & service contracts)
│   └── service/               # Use Case implementations & domain event coordination
│
└── infrastructure/            # Framework & external tool adapters
    ├── adapter/
    │   ├── input/rest/        # Spring REST Controllers, Request/Response DTOs, Validation
    │   └── output/
    │       ├── persistence/   # Spring Data JPA repositories, database entities & mappers
    │       ├── payment/       # PayPal REST API integration adapter
    │       ├── storage/       # AWS S3 / MinIO cloud storage adapter
    │       ├── email/         # Spring Mail / MailHog adapter
    │       └── events/        # Spring ApplicationEventPublisher adapters
    └── config/                # Spring @Configuration beans & security policies
```

---

## 🚀 Key Engineering Highlights

### 1. Concurrency & Inventory Protection
* **Optimistic Locking (`@Version`)**: The `ProductEntity` utilizes JPA `@Version` along with database-level `CHECK (stock >= 0)` constraints in Flyway (`V2__add_product_version_and_stock_constraint.sql`). This prevents race conditions and overselling when multiple customers attempt to purchase the last unit simultaneously.
* **Resilient Transaction Isolation**: Payment capture services catch external gateway errors and reliably persist order audit status (`FAILED`) without rolling back database failure logs.

### 2. Enterprise Authentication & Security
* **JWT Access + HttpOnly Refresh Cookies**: Access tokens are kept in memory while refresh tokens are stored in `HttpOnly`, `SameSite=Lax` cookies, protecting against XSS token theft.
* **CSRF Protection**: State-changing endpoints require dual-token validation (`/api/v1/csrf` + `X-XSRF-TOKEN` header).
* **Dedicated Admin Authentication**: Isolated login endpoint (`/api/v1/admin/auth/login`) enforcing strict `ROLE_ADMIN` checks, returning `403 Forbidden` for standard users.

---

## 🛠 Tech Stack

| Component | Technology |
| :--- | :--- |
| **Language & Runtime** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.4 (MVC, Security, Data JPA, Validation, Mail) |
| **Database** | PostgreSQL 16 (with Flyway database migrations) |
| **Cache & Rate Limiting**| Redis Alpine (`spring-boot-starter-data-redis`) |
| **Token Security** | JJWT (`0.12.6`), BCrypt |
| **Object Storage** | AWS SDK for Java v2 (S3-compatible storage or MinIO) |
| **Payments** | PayPal Sandbox REST API |
| **Local SMTP** | MailHog |
| **Monitoring & Docs** | Springdoc OpenAPI (Swagger UI), Sentry, Actuator |

---

## 📐 System Architecture Diagram

```
                 [ Angular 21 Client ]
                           │
                           │ HTTPS / REST (Port 8080)
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    TechStore Backend                        │
│                                                             │
│  [ Primary Adapters ]                                       │
│    ├── REST Controllers (Admin & Public APIs)              │
│    └── Security Filters (JWT HttpOnly, CSRF Protection)     │
│                                                             │
│  [ Hexagonal Ports & Application Use Cases ]                │
│    ├── User & Authentication Context                        │
│    ├── Product, Brand & Category Catalog                    │
│    ├── Shopping Cart Context                                │
│    └── Order & PayPal Payment Context                       │
│                                                             │
│  [ Secondary Adapters ]                                     │
│    ├── PostgreSQL Repositories (Spring Data JPA + Flyway)   │
│    ├── Redis Cache & Rate Limiting                          │
│    ├── PayPal Sandbox Payment Gateway Adapter               │
│    ├── AWS S3 / MinIO Object Storage Adapter                │
│    └── Spring Mail / MailHog Notification Adapter           │
└──────────────────────────┬──────────────────────────────────┘
                           │
    ┌──────────────────────┼──────────────────────┐
    ▼                      ▼                      ▼
┌─────────────┐     ┌─────────────┐        ┌─────────────┐
│ PostgreSQL  │     │ Redis Cache │        │ PayPal API  │
│  (Port 5432)│     │ (Port 6379) │        │  (Sandbox)  │
└─────────────┘     └─────────────┘        └─────────────┘
```

---

## ⚡ Prerequisites

* [JDK 21](https://adoptium.net/)
* [Docker Desktop](https://www.docker.com/products/docker-desktop/)

---

## ⚙️ Environment Configuration

The backend reads environment variables from `.env` in the project root:

```bash
# Generate .env from example template
cp .env.example .env
```

*Critical Variables:*

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `DB_NAME` / `DB_PORT` | PostgreSQL database name & port | `techstore` / `5432` |
| `ADMIN_ENABLED` | Seeds administrator account on boot | `true` |
| `ADMIN_EMAIL` | Default administrator username | `admin@goodshopping.local` |
| `ADMIN_PASSWORD` | Default administrator password | `Admin123!` |
| `PAYPAL_CLIENT_ID` | PayPal Sandbox Client ID | *(Your sandbox client ID)* |
| `PAYPAL_SECRET` | PayPal Sandbox Secret | *(Your sandbox secret)* |
| `REDIS_HOST` / `PORT`| Redis connection | `localhost` / `6379` |

---

## 🏃 Quick Start

### 1. Launch Support Services (Postgres, Redis, MailHog)

```bash
docker compose up -d
```

### 2. Run the Spring Boot API

*On Windows (PowerShell):*
```powershell
.\mvnw.cmd spring-boot:run
```

*On macOS / Linux:*
```bash
./mvnw spring-boot:run
```

The API starts on **`http://localhost:8080`** with the prefix **`/api/v1`**.

---

## 📖 API Documentation & Health

* **Swagger UI (Interactive API Docs)**: [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
* **OpenAPI Schema (JSON)**: [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)
* **Actuator Health Probes**: [`http://localhost:8080/actuator/health`](http://localhost:8080/actuator/health)
* **MailHog Local Email Inbox**: [`http://localhost:8025`](http://localhost:8025)

---

## 🔑 Pre-Seeded Accounts

When `ADMIN_ENABLED=true`, `AdminUserInitializer` automatically ensures the admin user exists:

| Role | Email | Password | Allowed Access |
| :--- | :--- | :--- | :--- |
| **Admin** | `admin@goodshopping.local` | `Admin123!` | Full admin management + Swagger UI + customer shop |
| **Customer**| *(Register via `/api/v1/auth/register`)* | *(Configured by user)* | Storefront browsing, cart, checkout |

---

## 🧪 Testing & Quality

Run the automated test suite:

```bash
# Windows
.\mvnw.cmd test

# macOS / Linux
./mvnw test
```

Package the production executable JAR:

```bash
./mvnw clean package -DskipTests
```

Containerize with Docker:

```bash
docker build -t techstore-backend .
```

---

## 🔒 Security Model

* All private credentials (`.env`, JWT keys, AWS secrets) are excluded from version control via `.gitignore`.
* Tokens in production should be rotated and supplied via platform secret managers (e.g. AWS Secrets Manager, Render Environment Secrets).
