<a href="https://fireflysoftware.io/openplatform/">
<picture>
  <source media="(max-width: 640px) and (prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/firefly-oss/.github/main/profile/assets/banner-narrow-dark.svg">
  <source media="(max-width: 640px)" srcset="https://raw.githubusercontent.com/firefly-oss/.github/main/profile/assets/banner-narrow-light.svg">
  <source media="(prefers-color-scheme: dark)" srcset="https://raw.githubusercontent.com/firefly-oss/.github/main/profile/assets/banner-dark.svg">
  <img src="https://raw.githubusercontent.com/firefly-oss/.github/main/profile/assets/banner-light.svg" alt="Firefly OpenPlatform - Domain Customer - People" width="100%">
</picture>
</a>

# Firefly OpenPlatform - Domain Customer - People

Developed by the Firefly Software Foundation. [dev@fireflysoftware.io](mailto:dev@fireflysoftware.io)

[Website](https://fireflysoftware.io/) · [OpenPlatform](https://fireflysoftware.io/openplatform/) · [Module catalogue](https://fireflysoftware.io/openplatform/modules/)

Domain layer microservice for managing people and business entities within the Firefly platform. This service acts as the orchestration layer for customer (natural person) and business (legal entity) lifecycle operations, including registration, contact information management, party relationships, compliance data, and status transitions.

Repository: [https://github.com/firefly-oss/domain-customer-people](https://github.com/firefly-oss/domain-customer-people)

---

## Overview

`domain-customer-people` provides a reactive REST API that coordinates complex, multi-step entity management workflows. It sits between upstream consumers and the `core-common-customer-mgmt` core service, applying domain orchestration through the FireflyFramework Saga Engine and CQRS patterns.

### Key Features

- **Customer lifecycle management** -- register, update, and query natural persons with full contact, compliance, and relationship data.
- **Business lifecycle management** -- register, update, and query legal entities (organizations) with identical contact and compliance capabilities.
- **Contact information CRUD** -- addresses, emails, phones, identity documents, and preferred channel selection, all with saga-backed consistency.
- **Party relationship management** -- register, update, and remove inter-party relationships.
- **Status management** -- dormant, reactivate, deceased/defunct, closure request/confirm, lock/unlock transitions with audit reasons.
- **Compliance data** -- PEP (Politically Exposed Person) registration, economic activity links, and consent tracking.
- **Saga-orchestrated transactions** -- every write operation is executed through the FireflyFramework `SagaEngine` with compensating steps for rollback on failure.
- **CQRS architecture** -- commands dispatched via `CommandBus`, queries via `QueryBus`, with configurable timeouts and caching.
- **Event-driven architecture** -- Kafka-based event publishing for domain events via the FireflyFramework EDA module.
- **Reactive, non-blocking** -- built on Spring WebFlux with virtual threads enabled.
- **SDK generation** -- auto-generated Java client SDK from the OpenAPI specification using `openapi-generator-maven-plugin`.

---

## Architecture

### Module Structure

| Module | Purpose |
|--------|---------|
| `domain-customer-people-core` | Business logic: services, commands, handlers, saga workflows, queries, and constants. Organized by subdomain (customer, business, contact, party, compliance, status). |
| `domain-customer-people-interfaces` | Interface adapters bridging the web layer to core domain logic; depends on core. |
| `domain-customer-people-infra` | Infrastructure: `ClientFactory` for external API clients, `CustomerMgmtProperties` configuration, validators. |
| `domain-customer-people-web` | Deployable Spring Boot application: REST controllers, application config, actuator, and OpenAPI setup. |
| `domain-customer-people-sdk` | Auto-generated Java client SDK from the OpenAPI spec (WebClient-based, reactive). |

### Dependency Flow

```
web --> interfaces --> core --> infra
sdk (generated from openapi.yml)
```

### Core Subdomains

| Subdomain | Description |
|-----------|-------------|
| `customer` | Natural person registration, update, query (via `RegisterCustomerSaga`, `UpdateCustomerSaga`) |
| `business` | Legal entity registration, update, query (via `RegisterCustomerSaga`, `UpdateBusinessSaga`) |
| `contact` | Addresses, emails, phones, identity documents, preferred channel (12+ individual sagas) |
| `party` | Party creation, relationships, providers, group memberships |
| `compliance` | PEP records, economic activity links, consents |
| `status` | Status transitions (active, inactive, suspended, pending, closed) via `UpdateStatusSaga` |

### Technology Stack

| Technology | Purpose |
|------------|---------|
| Java 25 | Language runtime |
| Spring Boot (WebFlux) | Reactive web framework |
| Project Reactor | Reactive streams |
| Virtual Threads | Enabled via `spring.threads.virtual.enabled: true` |
| [FireflyFramework BOM 26.01.01](https://github.com/fireflyframework/) | Parent POM, dependency management |
| [FireflyFramework Transactional Saga Engine](https://github.com/fireflyframework/) | Distributed saga orchestration (`@Saga`, `@SagaStep`, `@StepEvent`, `SagaEngine`) |
| [FireflyFramework CQRS](https://github.com/fireflyframework/) | Command/Query bus pattern (`CommandBus`, `QueryBus`) |
| [FireflyFramework EDA](https://github.com/fireflyframework/) | Event-driven architecture with Kafka publishing |
| [FireflyFramework Utils](https://github.com/fireflyframework/) | Common utilities |
| [FireflyFramework Domain](https://github.com/fireflyframework/) | Domain abstractions |
| [FireflyFramework Validators](https://github.com/fireflyframework/) | Validation utilities |
| [FireflyFramework Web](https://github.com/fireflyframework/) | Common web configurations |
| core-common-customer-mgmt-sdk | SDK client for the Customer Management core platform |
| SpringDoc OpenAPI (WebFlux UI) | API documentation |
| Micrometer + Prometheus | Metrics and monitoring |
| OpenAPI Generator | SDK code generation (WebClient library, reactive) |
| MapStruct | Object mapping |
| Lombok | Boilerplate reduction |
| OkHttp3 | HTTP client (SDK layer) |

---

## Setup

### Prerequisites

- **Java 25** (or later)
- **Apache Maven 3.9+**
- Access to the FireflyFramework Maven repository for `org.fireflyframework` dependencies
- Access to the `core-common-customer-mgmt-sdk` artifact
- **Apache Kafka** (for event publishing -- `localhost:9092` by default)

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_ADDRESS` | `localhost` | Address the server binds to |
| `SERVER_PORT` | `8080` | HTTP port |

### Application Configuration

Key settings from `application.yaml`:

```yaml
spring:
  application:
    name: domain-customer-people
  threads:
    virtual:
      enabled: true

firefly:
  cqrs:
    enabled: true
    command:
      timeout: 30s
      metrics-enabled: true
      tracing-enabled: true
    query:
      timeout: 15s
      caching-enabled: true
      cache-ttl: 15m
  saga.performance.enabled: true
  eda:
    enabled: true
    default-publisher-type: KAFKA
    publishers:
      kafka:
        default:
          default-topic: domain-layer
          bootstrap-servers: localhost:9092
  stepevents:
    enabled: true

api-configuration:
  common-platform.customer-mgmt:
    base-path: http://localhost:8082
```

### Spring Profiles

| Profile | Logging | Swagger | Notes |
|---------|---------|---------|-------|
| `default` | INFO | Enabled | Standard development |
| `dev` | DEBUG (io.fireflysoftware, R2DBC, Flyway) | Enabled | Verbose debugging |
| `testing` | DEBUG (io.fireflysoftware) | Enabled | Test environments |
| `prod` | WARN (root), INFO (io.fireflysoftware) | Disabled | Production |

### Build

```bash
# Full build (includes SDK generation)
mvn clean install

# Skip tests
mvn clean install -DskipTests
```

### Run

```bash
# Run via Spring Boot Maven plugin
mvn -pl domain-customer-people-web spring-boot:run

# Run with a specific profile
mvn -pl domain-customer-people-web spring-boot:run -Dspring-boot.run.profiles=dev

# Or run the packaged JAR
java -jar domain-customer-people-web/target/domain-customer-people.jar
```

---

## API Endpoints

All endpoints return reactive `Mono<ResponseEntity>` responses. The service exposes three controller groups under `/api/v1`.

### Customers (`/api/v1/customers`)

| Method | Path | Summary |
|--------|------|---------|
| `POST` | `/api/v1/customers` | Register a customer (natural person) |
| `PUT` | `/api/v1/customers` | Update an existing customer |
| `GET` | `/api/v1/customers/{customerId}` | Get customer information |
| `POST` | `/api/v1/customers/{partyId}/addresses` | Add customer address |
| `PUT` | `/api/v1/customers/{partyId}/addresses/{addressId}` | Update customer address |
| `DELETE` | `/api/v1/customers/{partyId}/addresses/{addressId}` | Remove customer address |
| `POST` | `/api/v1/customers/{partyId}/emails` | Add customer email |
| `PUT` | `/api/v1/customers/{partyId}/emails/{emailId}` | Update customer email |
| `DELETE` | `/api/v1/customers/{partyId}/emails/{emailId}` | Remove customer email |
| `POST` | `/api/v1/customers/{partyId}/phones` | Add customer phone |
| `PUT` | `/api/v1/customers/{partyId}/phones/{phoneId}` | Update customer phone |
| `DELETE` | `/api/v1/customers/{partyId}/phones/{phoneId}` | Remove customer phone |
| `POST` | `/api/v1/customers/{partyId}/id-documents` | Add identity document / tax ID |
| `DELETE` | `/api/v1/customers/{partyId}/id-documents/{taxId}` | Remove identity document / tax ID |
| `POST` | `/api/v1/customers/{partyId}/preferred-channel` | Set preferred contact channel |
| `POST` | `/api/v1/customers/{partyId}/dormant` | Mark customer dormant |
| `POST` | `/api/v1/customers/{partyId}/reactivate` | Reactivate customer |
| `POST` | `/api/v1/customers/{partyId}/deceased` | Mark customer deceased |
| `POST` | `/api/v1/customers/{partyId}/closure-request` | Request customer closure |
| `POST` | `/api/v1/customers/{partyId}/confirm-closure` | Confirm customer closure |
| `POST` | `/api/v1/customers/{partyId}/lock` | Lock customer profile |
| `POST` | `/api/v1/customers/{partyId}/unlock` | Unlock customer profile |

### Businesses (`/api/v1/businesses`)

| Method | Path | Summary |
|--------|------|---------|
| `POST` | `/api/v1/businesses` | Register a business (legal entity) |
| `PUT` | `/api/v1/businesses` | Update an existing business |
| `GET` | `/api/v1/businesses/{businessId}` | Get business information |
| `POST` | `/api/v1/businesses/{partyId}/addresses` | Add business address |
| `PUT` | `/api/v1/businesses/{partyId}/addresses/{addressId}` | Update business address |
| `DELETE` | `/api/v1/businesses/{partyId}/addresses/{addressId}` | Remove business address |
| `POST` | `/api/v1/businesses/{partyId}/emails` | Add business email |
| `PUT` | `/api/v1/businesses/{partyId}/emails/{emailId}` | Update business email |
| `DELETE` | `/api/v1/businesses/{partyId}/emails/{emailId}` | Remove business email |
| `POST` | `/api/v1/businesses/{partyId}/phones` | Add business phone |
| `PUT` | `/api/v1/businesses/{partyId}/phones/{phoneId}` | Update business phone |
| `DELETE` | `/api/v1/businesses/{partyId}/phones/{phoneId}` | Remove business phone |
| `POST` | `/api/v1/businesses/{partyId}/dormant` | Mark business dormant |
| `POST` | `/api/v1/businesses/{partyId}/reactivate` | Reactivate business |
| `POST` | `/api/v1/businesses/{partyId}/defunct` | Mark business defunct |
| `POST` | `/api/v1/businesses/{partyId}/closure-request` | Request business closure |
| `POST` | `/api/v1/businesses/{partyId}/confirm-closure` | Confirm business closure |
| `POST` | `/api/v1/businesses/{partyId}/lock` | Lock business profile |
| `POST` | `/api/v1/businesses/{partyId}/unlock` | Unlock business profile |

### Relationships (`/api/v1/party-relationships`)

| Method | Path | Summary |
|--------|------|---------|
| `POST` | `/api/v1/party-relationships` | Register a party relationship |
| `PUT` | `/api/v1/party-relationships/{relationId}` | Update a relationship |
| `DELETE` | `/api/v1/party-relationships/{relationId}` | Remove a relationship |

### Idempotency

The API supports idempotent requests via the `X-Idempotency-Key` header. When provided, identical requests with the same key will only be processed once.

---

## Development Guidelines

### Project Conventions

- **Reactive-only**: all service methods return `Mono` or `Flux`. Do not use blocking calls.
- **Saga pattern**: every write operation is orchestrated via the FireflyFramework `SagaEngine`. Sagas define `@SagaStep` methods with compensating actions and `@StepEvent` annotations for event emission.
- **CQRS**: writes go through `CommandBus`, reads through `QueryBus`. Commands have configurable 30s timeouts; queries have 15s timeouts with caching (15m TTL).
- **Command/Record pattern**: commands are modeled as Java records (e.g., `RegisterCustomerCommand`, `RegisterBusinessCommand`).
- **Layer separation**: controllers delegate to service interfaces; implementations use `SagaEngine` to orchestrate workflows.
- **Lombok + MapStruct**: use `@Data`, `@RequiredArgsConstructor`, `@Builder` for models; MapStruct for DTO mapping.
- **Virtual threads**: enabled by default for improved concurrency.

### Package Structure

```
io.fireflysoftware.domain.people
  +-- core
  |     +-- customer/             # Natural person services, commands, handlers, queries, sagas
  |     +-- business/             # Legal entity services, commands, handlers, sagas
  |     +-- contact/              # Address, email, phone, ID doc, preferred channel
  |     +-- party/                # Party, relationships, providers, group memberships
  |     +-- compliance/           # PEP, economic activities, consents
  |     +-- status/               # Status transitions (dormant, active, closed, etc.)
  |     +-- utils/constants/      # GlobalConstants, StatusTypeEnum
  +-- infra
  |     +-- ClientFactory.java          # Creates API clients for customer-mgmt SDK
  |     +-- CustomerMgmtProperties.java # Config properties (api-configuration.common-platform.customer-mgmt)
  +-- web
        +-- controller/
        |     +-- CustomersController.java
        |     +-- BusinessesController.java
        |     +-- RelationshipsController.java
        +-- CustomerDomainPeopleApplication.java
```

### Registration Saga Workflow

The `RegisterCustomerSaga` orchestrates up to 13 steps for customer/business registration, each with compensating actions:

1. **Register Party** -- creates the base party record
2. **Register Natural Person** (customers) or **Register Legal Entity** (businesses)
3. **Register Status Entry** -- initial status history
4. **Register PEP** -- politically exposed person data (natural persons only)
5. **Register Identity Documents** -- ID documents (supports multiple via `ExpandEach`)
6. **Register Addresses** -- postal/physical addresses
7. **Register Emails** -- email contacts
8. **Register Phones** -- phone contacts
9. **Register Economic Activity Links** -- NACE/industry codes
10. **Register Consents** -- data processing consents (natural persons only)
11. **Register Party Providers** -- external data providers
12. **Register Party Relationships** -- inter-party links
13. **Register Party Group Memberships** -- group affiliations

### Running Tests

```bash
mvn test
```

Unit tests exist for core services: `CustomerServiceImplTest`, `ContactServiceImplTest`, `PartyServiceImplTest`, `StatusServiceImplTest`, and `CustomersControllerTest`.

---

## Monitoring

### Actuator Endpoints

Exposed via Spring Boot Actuator (`management.endpoints.web.exposure.include`):

| Endpoint | Purpose |
|----------|---------|
| `/actuator/health` | Application health with liveness and readiness probes |
| `/actuator/info` | Build information |
| `/actuator/prometheus` | Prometheus-compatible metrics |

Health check details are always shown (`show-details: always`). Redis health is disabled by default.

### Metrics

Prometheus metrics are exported via `micrometer-registry-prometheus`. CQRS command/query metrics and tracing are enabled by default through the FireflyFramework configuration:

- Command metrics: `firefly.cqrs.command.metrics-enabled: true`
- Distributed tracing: `firefly.cqrs.command.tracing-enabled: true`
- Saga performance: `firefly.saga.performance.enabled: true`

### API Documentation

Interactive API documentation is available (disabled in `prod` profile):

- **Swagger UI**: `/swagger-ui.html` (tags sorted alphabetically, operations sorted by method)
- **OpenAPI JSON**: `/v3/api-docs`
- **Scanned packages**: `io.fireflysoftware.domain.people.web.controller`
- **Paths matched**: `/api/**`
