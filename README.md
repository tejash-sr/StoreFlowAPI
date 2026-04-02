# StoreFlow API

> A production-grade Inventory & Order Management REST API  
> Grootan Technologies — Internal Training Exercise

[![CI](https://github.com/tejash-sr/StoreFlowAPI/actions/workflows/ci.yml/badge.svg)](https://github.com/tejash-sr/StoreFlowAPI/actions/workflows/ci.yml)
[![Coverage](https://img.shields.io/badge/coverage-80%25%2B-brightgreen)](#running-tests)
[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)

**Repository:** https://github.com/tejash-sr/StoreFlowAPI

---

## Table of Contents

- [Project Overview](#project-overview)
- [Prerequisites](#prerequisites)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Middleware Architecture](#middleware-architecture)
- [Getting Started (From Scratch)](#getting-started-from-scratch)
- [Running the Application](#running-the-application)
- [Running Tests](#running-tests)
- [API Endpoints Quick Reference](#api-endpoints-quick-reference)
- [Configuration Reference](#configuration-reference)
- [Project Documentation](#project-documentation)
- [Evaluation Rubric](#evaluation-rubric)
- [Submission Checklist](#submission-checklist)

---

## Project Overview

**StoreFlow API** is a comprehensive, fully-tested REST API built as an internal training exercise at Grootan Technologies. The project covers 90%+ of concepts from three flagship courses:

- **Spring Framework 6: Beginner to Guru** — MVC, JPA, Security, WebSocket, Mail, Actuator
- **Spring Boot Unit Testing with JUnit, Mockito & Testcontainers** — TDD, Mockito, Integration Tests
- **PostgreSQL Bootcamp: Complete Beginner to Advanced** — Schema design, Flyway migrations

The system is organized into **8 progressive phases**, each with concrete acceptance criteria and a minimum test count. Test-Driven Development (TDD) is practiced throughout.

### Core Features

- Full CRUD for Products, Categories, and Orders
- JWT Authentication (Access + Refresh Tokens) + Password Reset Flow
- Role-Based Authorization: `USER` and `ADMIN`
- **Middleware Pipeline** — JWT filter, request logging with MDC trace IDs, rate limiting
- File Upload/Download — Product images, User avatars (with Thumbnailator resize)
- On-demand PDF Order Reports (Apache PDFBox) and CSV Order Exports
- Real-time WebSocket Notifications via STOMP (order status changes)
- Transactional Email Notifications — 5 types (mocked in tests with Greenmail)
- Spring Actuator + Custom Micrometer Metrics
- Flyway database migrations (reproducible schema from scratch)
- 80%+ JaCoCo test coverage enforced at build time — **80+ total tests**

---

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| JDK | 21 | [Temurin download](https://adoptium.net/) |
| Docker Desktop | Latest | **Required** — Testcontainers uses Docker to spin up PostgreSQL |
| Maven | 3.9+ | Or use `./mvnw` wrapper (no install needed) |
| Git | Latest | |
| Postman | Latest | Import the collection from `postman/` |

> **Docker must be running** before running any test. Testcontainers will fail immediately if Docker is not active.

---

## Tech Stack

| Category | Technology | Purpose |
|----------|-----------|---------|
| Runtime | Java 21 + Spring Boot 3.x | Core platform |
| Web | Spring MVC | REST controllers |
| Persistence | Spring Data JPA + Hibernate | ORM |
| Database | PostgreSQL 15 | Primary store |
| Schema Mgmt | Flyway | Version-controlled migrations |
| Auth | Spring Security + JJWT + BCrypt | Stateless JWT auth |
| **Middleware** | **OncePerRequestFilter (x3)** | **JWT, Logging, Rate Limiting** |
| Testing | JUnit 5 + Mockito + Testcontainers + MockMvc | Full test pyramid |
| Validation | Jakarta Bean Validation + Custom Validators | Input sanitization |
| File I/O | Spring Multipart + Apache PDFBox + Thumbnailator | Upload, PDF, avatar |
| Email | Spring Mail (JavaMailSender) | Transactional emails |
| Real-time | Spring WebSocket + STOMP + SockJS | Order notifications |
| Metrics | Spring Actuator + Micrometer | Observability |
| Rate Limiting | Bucket4j | Auth endpoint protection |
| Build | Maven + `./mvnw` wrapper | No global Maven needed |
| Coverage | JaCoCo (80% gate) | Enforced at `mvn verify` |

---

## Project Structure

```
storeflow-api/
├── .github/
│   └── workflows/
│       └── ci.yml                     # GitHub Actions CI pipeline
├── docs/
│   ├── PRD.md                         # Product Requirements Document
│   ├── IMPLEMENTATION.md              # Phase-by-phase implementation guide
│   ├── DESIGN.md                      # System architecture & design
│   ├── TESTING_GUIDE.md               # Testing strategy & test checklist
│   └── API_REFERENCE.md               # Complete API endpoint reference
├── postman/
│   └── StoreFlowAPI.postman_collection.json
├── src/
│   ├── main/
│   │   ├── java/com/grootan/storeflow/
│   │   │   ├── StoreFlowApplication.java        # Entry point only
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java           # SecurityFilterChain
│   │   │   │   ├── JwtConfig.java                # JWT secret/expiry props
│   │   │   │   ├── WebSocketConfig.java          # STOMP + SockJS
│   │   │   │   ├── MailConfig.java               # JavaMailSender bean
│   │   │   │   ├── ActuatorSecurityConfig.java   # Actuator endpoint auth
│   │   │   │   └── AppConfig.java                # ObjectMapper, etc.
│   │   │   ├── controllers/
│   │   │   │   ├── HealthController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   └── AdminController.java
│   │   │   ├── services/
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── ProductService.java
│   │   │   │   ├── OrderService.java
│   │   │   │   ├── CategoryService.java
│   │   │   │   ├── EmailService.java
│   │   │   │   ├── FileStorageService.java
│   │   │   │   ├── PdfGenerationService.java
│   │   │   │   ├── CsvExportService.java
│   │   │   │   ├── NotificationService.java      # STOMP publisher
│   │   │   │   └── ScheduledJobService.java      # @Scheduled daily digest
│   │   │   ├── repositories/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── CategoryRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   └── OrderItemRepository.java
│   │   │   ├── entities/
│   │   │   │   ├── User.java
│   │   │   │   ├── Product.java
│   │   │   │   ├── Category.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── OrderItem.java
│   │   │   │   └── ShippingAddress.java          # @Embeddable
│   │   │   ├── dto/
│   │   │   │   ├── request/                      # Validated request DTOs
│   │   │   │   └── response/                     # Response DTOs
│   │   │   ├── middleware/                        # ← ALL FILTERS LIVE HERE
│   │   │   │   ├── JwtAuthenticationFilter.java  # Validates Bearer tokens
│   │   │   │   ├── RequestLoggingFilter.java     # MDC trace ID per request
│   │   │   │   └── RateLimitingFilter.java       # Bucket4j rate limiting
│   │   │   ├── exceptions/
│   │   │   │   ├── AppException.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── InsufficientStockException.java
│   │   │   │   ├── InvalidStatusTransitionException.java
│   │   │   │   ├── AuthenticationFailedException.java
│   │   │   │   └── GlobalExceptionHandler.java   # @ControllerAdvice
│   │   │   ├── validation/
│   │   │   │   ├── ExistsInDatabase.java         # Custom annotation
│   │   │   │   ├── ExistsInDatabaseValidator.java
│   │   │   │   ├── ValidSku.java
│   │   │   │   └── ValidSkuValidator.java
│   │   │   ├── enums/
│   │   │   │   ├── Role.java
│   │   │   │   ├── ProductStatus.java
│   │   │   │   └── OrderStatus.java
│   │   │   ├── utils/
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── PaginationUtil.java
│   │   │   │   └── OrderStatusTransition.java
│   │   │   └── metrics/
│   │   │       └── OrderMetrics.java             # Micrometer counters/gauges
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── db/migration/
│   │       │   ├── V1__create_users.sql
│   │       │   ├── V2__create_categories.sql
│   │       │   ├── V3__create_products.sql
│   │       │   └── V4__create_orders.sql
│   │       └── templates/email/
│   │           ├── welcome.html
│   │           ├── password-reset.html
│   │           ├── order-confirmation.html
│   │           ├── low-stock-alert.html
│   │           └── daily-digest.html
│   └── test/
│       ├── java/com/grootan/storeflow/
│       │   ├── AbstractIntegrationTest.java      # Testcontainers base class
│       │   ├── controllers/
│       │   ├── services/
│       │   ├── repositories/
│       │   ├── middleware/                        # ← Middleware tests
│       │   │   ├── JwtAuthenticationFilterTest.java
│       │   │   ├── RequestLoggingFilterTest.java
│       │   │   └── RateLimitingFilterTest.java
│       │   └── validation/
│       └── resources/
│           └── application-test.yml
├── docker-compose.yml
├── Dockerfile
├── application-example.yml                       # No secrets — safe to commit
├── .gitignore
└── README.md
```

---

## Middleware Architecture

> **Middleware is NOT optional** — it is the backbone of security, observability, and reliability.

Every HTTP request passes through the following filter chain **in order**:

```
HTTP Request
     │
     ▼
┌─────────────────────────────┐  Order 1
│  RequestLoggingFilter       │  Assigns a UUID trace ID via MDC
│  (OncePerRequestFilter)     │  Adds X-Trace-Id to response header
└─────────────┬───────────────┘  Logs method, path, status, duration
              │
              ▼
┌─────────────────────────────┐  Order 2
│  RateLimitingFilter         │  Checks per-IP token bucket (Bucket4j)
│  (OncePerRequestFilter)     │  Active only on /api/auth/** routes
└─────────────┬───────────────┘  Returns HTTP 429 if limit exceeded
              │
              ▼
┌─────────────────────────────┐  Order 3
│  JwtAuthenticationFilter    │  Reads "Authorization: Bearer <token>"
│  (OncePerRequestFilter)     │  Validates JWT signature + expiry
└─────────────┬───────────────┘  Populates Spring SecurityContext
              │
              ▼
┌─────────────────────────────┐
│  Spring Security Filter     │  Enforces @PreAuthorize, role checks
│  (SecurityFilterChain)      │  Returns 401 / 403 as appropriate
└─────────────┬───────────────┘
              │
              ▼
        Controller → Service → Repository
```

| Filter | Class | Registered At | Responsibility |
|--------|-------|--------------|----------------|
| Request Logging | `RequestLoggingFilter` | `@Order(1)` | MDC trace ID, request/response logging |
| Rate Limiting | `RateLimitingFilter` | `@Order(2)` | 5 req/15 min per IP on `/api/auth/**` |
| JWT Auth | `JwtAuthenticationFilter` | Before `UsernamePasswordAuthenticationFilter` | Token validation, SecurityContext population |

---

## Getting Started (From Scratch)

### Step 1 — Clone the Repository

```bash
git clone https://github.com/tejash-sr/StoreFlowAPI.git
cd StoreFlowAPI
```

### Step 2 — Copy Environment Config

```bash
cp application-example.yml src/main/resources/application-local.yml
```

Edit `application-local.yml` — fill in your values (never commit this file):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/storeflow
    username: storeflow_user
    password: your_password_here

jwt:
  secret: your-256-bit-hex-encoded-secret-here
  access-token-expiry: 900        # 15 minutes
  refresh-token-expiry: 604800    # 7 days

storage:
  base-path: ./uploads

spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your@email.com
    password: your-app-password
```

### Step 3 — Start PostgreSQL

```bash
# Start just the database container (recommended for dev)
docker-compose up -d postgres
```

Flyway will automatically apply all migration scripts on first startup.

### Step 4 — Run the App

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## Running the Application

| Command | Description |
|---------|-------------|
| `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` | Run in dev mode |
| `docker-compose up` | Run full stack (app + DB) |
| `docker build -t storeflow-api . && docker run -p 8080:8080 storeflow-api` | Docker only |

App available at: `http://localhost:8080`

---

## Running Tests

| Command | Description |
|---------|-------------|
| `./mvnw test` | Run all tests (requires Docker for Testcontainers) |
| `./mvnw verify` | Run all tests + enforce JaCoCo 80% coverage gate |
| `./mvnw test -Dtest=ProductServiceTest` | Run a specific test class |

Coverage HTML report: `target/site/jacoco/index.html`

### Expected Result

```
# Minimum passing output:
Tests run: ≥80, Failures: 0, Errors: 0, Skipped: 0

JaCoCo Coverage Summary:
  Lines:        ≥80.0%  ✓
  Branches:     ≥80.0%  ✓
  Methods:      ≥80.0%  ✓
  Instructions: ≥80.0%  ✓

BUILD SUCCESS
```

---

## API Endpoints Quick Reference

| Method | Endpoint | Auth | Role |
|--------|----------|------|------|
| GET | `/api/health` | None | Public |
| POST | `/api/auth/signup` | None | Public |
| POST | `/api/auth/login` | None | Public |
| POST | `/api/auth/refresh` | None | Public |
| POST | `/api/auth/forgot-password` | None | Public |
| POST | `/api/auth/reset-password/{token}` | None | Public |
| GET | `/api/auth/me` | Bearer | USER/ADMIN |
| PUT | `/api/auth/me/avatar` | Bearer | USER/ADMIN |
| GET | `/api/products` | None | Public |
| POST | `/api/products` | Bearer | ADMIN |
| GET | `/api/products/{id}` | None | Public |
| PUT | `/api/products/{id}` | Bearer | ADMIN |
| PATCH | `/api/products/{id}/stock` | Bearer | ADMIN |
| DELETE | `/api/products/{id}` | Bearer | ADMIN |
| POST | `/api/products/{id}/image` | Bearer | ADMIN |
| GET | `/api/products/{id}/image` | None | Public |
| POST | `/api/orders` | Bearer | USER |
| GET | `/api/orders` | Bearer | USER/ADMIN |
| GET | `/api/orders/{id}` | Bearer | USER/ADMIN |
| PATCH | `/api/orders/{id}/status` | Bearer | ADMIN |
| GET | `/api/orders/{id}/report` | Bearer | USER/ADMIN |
| GET | `/api/orders/export` | Bearer | ADMIN |
| GET | `/api/admin/products/low-stock` | Bearer | ADMIN |
| GET | `/actuator/health` | None | Public |
| GET | `/actuator/metrics` | Bearer | ADMIN |
| GET | `/actuator/prometheus` | Bearer | ADMIN |

Full reference: [docs/API_REFERENCE.md](docs/API_REFERENCE.md)

---

## Configuration Reference

### application-example.yml (safe to commit — no real values)

```yaml
# Database
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/storeflow}
    username: ${DB_USERNAME:storeflow_user}
    password: ${DB_PASSWORD:changeme}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
    baseline-on-migrate: true
  mail:
    host: ${MAIL_HOST:smtp.gmail.com}
    port: ${MAIL_PORT:587}
    username: ${MAIL_USERNAME:changeme}
    password: ${MAIL_PASSWORD:changeme}
    properties:
      mail.smtp.auth: true
      mail.smtp.starttls.enable: true

# JWT
jwt:
  secret: ${JWT_SECRET:changeme-256-bit-secret}
  access-token-expiry: ${JWT_ACCESS_EXPIRY:900}
  refresh-token-expiry: ${JWT_REFRESH_EXPIRY:604800}

# File Storage
storage:
  base-path: ${STORAGE_PATH:./uploads}
  max-file-size-bytes: 5242880   # 5MB

# Rate Limiting (Bucket4j)
rate-limit:
  auth:
    capacity: 5
    refill-minutes: 15

# Low Stock Alert
inventory:
  low-stock-threshold: ${LOW_STOCK_THRESHOLD:10}

# Server
server:
  port: 8080
  shutdown: graceful
  compression:
    enabled: true
    min-response-size: 1024

# Actuator
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: when-authorized
```

---

## Project Documentation

| Document | Description |
|----------|-------------|
| [docs/PRD.md](docs/PRD.md) | Full Product Requirements — FR, NFR, security, test targets |
| [docs/IMPLEMENTATION.md](docs/IMPLEMENTATION.md) | Phase-by-phase guide with code patterns |
| [docs/DESIGN.md](docs/DESIGN.md) | Architecture, data model, middleware, security flows |
| [docs/TESTING_GUIDE.md](docs/TESTING_GUIDE.md) | Testing strategy, templates, 80+ test checklist |
| [docs/API_REFERENCE.md](docs/API_REFERENCE.md) | Every endpoint — request/response schemas, status codes |

---

## Development Phases Summary

| Phase | Topic | Min Tests |
|-------|-------|-----------|
| 1 | Project setup, health endpoints, middleware scaffold | 6 |
| 2 | Data models, JPA entities, Flyway migrations | 15 |
| 3 | REST CRUD endpoints, service layer, pagination | 20 |
| 4 | JWT authentication, refresh tokens, password reset | 14 |
| 5 | Bean validation, custom validators, error handling | 12 |
| 6 | File upload/download, PDF generation, CSV export | 8 |
| 7 | Advanced queries, cursor pagination, WebSocket | 10 |
| 8 | Email notifications, Actuator, production hardening | **7** |
| **Total** | | **≥ 80** |

---

## Evaluation Rubric

| Criteria | Weight | What Reviewers Check |
|----------|--------|---------------------|
| **Test Quality** | 30% | Meaningful assertions, edge cases, Mockito usage, no false positives |
| **Code Architecture** | 25% | Clean layers, DTO separation, DRY, SOLID principles |
| **API Design** | 20% | RESTful, consistent response envelopes, correct HTTP codes |
| **Feature Completeness** | 15% | All 8 phases working; Flyway migrations apply cleanly |
| **Error Handling** | 10% | @ControllerAdvice covers all types; no unhandled exceptions |

---

## Submission Checklist

- [ ] `./mvnw test` — **0 failures**
- [ ] `./mvnw verify` — JaCoCo **≥ 80%** on all four metrics
- [ ] `target/site/jacoco/index.html` — coverage report generated
- [ ] All 8 phases implemented and working end-to-end
- [ ] All 3 middleware filters implemented and tested
- [ ] `docker-compose up` starts app + DB cleanly from scratch
- [ ] Flyway migrations apply automatically; no manual DB setup needed
- [ ] Postman collection covers all endpoints
- [ ] Clean incremental commit history (one commit per phase minimum)
- [ ] No secrets, no `target/`, no IDE files in git history
- [ ] `application-example.yml` committed (no real values)
- [ ] `README.md` accurately describes setup steps

---

*Grootan Technologies — Internal Training Program | Confidential*  
*Repository: https://github.com/tejash-sr/StoreFlowAPI*
