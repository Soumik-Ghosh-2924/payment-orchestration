
# Payment Processing System

## Overview
This project is a **Payment Processing Microservice** built using **Java + Spring Boot**, following MVC architecture.
It implements a synchronous client-facing API with asynchronous internal gateway processing.

The design strictly follows:
- ACID-compliant relational DB (PostgreSQL)
- Idempotent payment creation
- Explicit payment lifecycle
- Retry with exponential backoff
- Horizontal scalability readiness

---

## Payment Lifecycle

CREATED → PROCESSING → SUCCESS / FAILED

Terminal states:
- SUCCESS
- FAILED

No transitions allowed once a terminal state is reached.

---

## Architecture

Client
 → REST API (Spring Boot Controller)
 → Payment Service (Business Logic)
 → PostgreSQL (ACID, Transactions)
 → Async Gateway Processor
 → Cache (Idempotency + Reads)

---

## Tech Stack

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Spring Cache (In-memory, extendable to Redis)
- REST APIs (Postman-friendly)

---

## APIs

### Create Payment
POST /payments

Request:
{
  "userId": "123",
  "amount": 500,
  "currency": "INR",
  "idempotencyKey": "abc-123"
}

Response:
{
  "paymentId": "pmt_xxx",
  "status": "PROCESSING"
}

### Get Payment
GET /payments/{id}

---

## Idempotency

- Each request must include `idempotencyKey`
- Duplicate requests return same response
- Prevents double charging

---

## Failure Handling

- External gateway assumed unreliable
- Retry with exponential backoff
- Payments move to FAILED or RETRY_PENDING

---

## How to Run

1. Configure PostgreSQL in `application.yml`
2. Run `PaymentApplication.java`
3. Test via Postman

---

## Future Improvements

- Redis cache
- Kafka events
- Circuit breaker (Resilience4j)
- Ledger service
