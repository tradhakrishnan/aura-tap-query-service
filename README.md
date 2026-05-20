# AURA TAP Query Service

Read-only REST API for querying TAP control data — hotels, locations, and user EIDs — backed by MongoDB.

---

## Overview

| Property | Value |
|---|---|
| Service Name | `aura-tap-query-service` |
| Port | **8081** |
| Protocol | HTTP REST (JSON) |
| Database | MongoDB `aura_db` |
| Stack | Spring Boot 3.3.0 · Java 21 · Spring Data MongoDB · Lombok |
| Role | Read-only TAP data access layer used by agents and client services |

---

## Architecture

```
┌──────────────────────────────────────┐
│         aura-tap-query-service       │
│                :8081                 │
│                                      │
│  Controllers                         │
│  ├── HotelQueryController            │
│  │   └── GET /api/hotels/**          │
│  ├── LocationQueryController         │
│  │   └── GET /api/locations/**       │
│  └── UserEidQueryController          │
│      └── GET /api/users/**           │
│                                      │
│  Services → Repositories             │
│  └── Spring Data MongoDB             │
└─────────────────┬────────────────────┘
                  │
                  ▼
        ┌─────────────────┐
        │   MongoDB       │
        │   aura_db       │
        │                 │
        │ control-hotels  │
        │ control-locations│
        │ user-eids       │
        └─────────────────┘

Consumers:
  aura-agent-service   :8090  (LangGraph agents query this service)
  aura-acrs-client     :8085  (verify EID before updating)
  aura-vds-client      :8086  (validate MINT location membership)
```

---

## Data Models

### ControlHotel (`control-hotels` collection)

| Field | Type | Description |
|---|---|---|
| `id` | String | Hotel code (e.g. `PARBA`) |
| `locationName` | String | Full hotel name |
| `crsSystem` | String | Source system: `MARSHA`, `ACRS` |
| `status` | String | `Active` / `Inactive` |
| `createdBy` | String | Creator EID |
| `createdOn` | Date | Creation timestamp |
| `updatedBy` | String | Last updater EID |
| `updatedOn` | Date | Last update timestamp |

### ControlLocation (`control-locations` collection)

| Field | Type | Description |
|---|---|---|
| `id` | String | Location code (e.g. `HTDV7N`) |
| `locationName` | String | Full location name |
| `app` | String | Owner system: `MARSHA`, `MINT`, `ACRS` |
| `status` | String | `Active` / `Inactive` |
| `supervisorEids` | List\<String\> | EIDs with supervisor access |
| `controlledHotels` | List\<String\> | Hotel codes under this location |
| `createdBy` | String | Creator EID |
| `createdOn` | Date | Creation timestamp |
| `updatedBy` | String | Last updater EID |
| `updatedOn` | Date | Last update timestamp |

### UserEid (`user-eids` collection)

| Field | Type | Description |
|---|---|---|
| `id` | String | MongoDB document ID |
| `eid` | String | Employee ID (e.g. `NMKOS046`) |
| `app` | String | System: `MARSHA`, `MINT`, `ACRS` |
| `status` | String | `Active` / `Inactive` |
| `locations` | List\<String\> | Location codes assigned |
| `assignments` | List\<String\> | Role/persona assignments |
| `createdOn` | Date | Creation timestamp |
| `updatedOn` | Date | Last update timestamp |

### ToolResponse Wrapper

All endpoints return:

```json
{
  "success": true,
  "data": { ... },
  "message": "OK",
  "count": 1
}
```

---

## API Reference

### Hotels — `GET /api/hotels/**`

#### Get hotel by ID
```
GET /api/hotels/{id}
```
**Example:**
```bash
curl http://localhost:8081/api/hotels/PARBA
```
**Response:**
```json
{
  "success": true,
  "data": {
    "id": "PARBA",
    "locationName": "Paris Marriott Opera Ambassador Hotel",
    "crsSystem": "MARSHA",
    "status": "Active",
    "createdBy": "ADMIN",
    "createdOn": "2024-01-15T10:00:00.000Z",
    "updatedBy": "ADMIN",
    "updatedOn": "2024-01-15T10:00:00.000Z"
  },
  "message": "OK",
  "count": 1
}
```

#### List hotels (filtered)
```
GET /api/hotels?status=Active&crsSystem=MARSHA&page=0&size=20
```
**Query params:**

| Param | Required | Description |
|---|---|---|
| `status` | No | Filter by status: `Active` / `Inactive` |
| `crsSystem` | No | Filter by CRS: `MARSHA` / `ACRS` |
| `page` | No | Page number (default `0`) |
| `size` | No | Page size (default `20`) |

**Example:**
```bash
curl "http://localhost:8081/api/hotels?status=Active&crsSystem=MARSHA&page=0&size=5"
```

#### Search hotels by name
```
GET /api/hotels/search?q=Marriott+Atlanta&page=0&size=20
```
**Example:**
```bash
curl "http://localhost:8081/api/hotels/search?q=Paris"
```

#### Batch get hotels by IDs
```
POST /api/hotels/batch
Content-Type: application/json

["PARBA", "NYCES", "CHIGS"]
```
**Example:**
```bash
curl -X POST http://localhost:8081/api/hotels/batch \
  -H "Content-Type: application/json" \
  -d '["PARBA", "NYCES", "CHIGS"]'
```

#### Get hotels by CRS system
```
GET /api/hotels/by-crs/{crsSystem}?page=0&size=20
```
**Example:**
```bash
curl "http://localhost:8081/api/hotels/by-crs/MARSHA?size=10"
```

#### Get hotels by status
```
GET /api/hotels/by-status/{status}?page=0&size=20
```
**Example:**
```bash
curl "http://localhost:8081/api/hotels/by-status/Active"
```

#### Count hotels
```
GET /api/hotels/count?status=Active&crsSystem=MARSHA
```
**Example:**
```bash
curl "http://localhost:8081/api/hotels/count?status=Active"
```
**Response:**
```json
{
  "success": true,
  "data": { "count": 142 },
  "message": "OK",
  "count": 1
}
```

---

### Locations — `GET /api/locations/**`

#### Get location by ID
```
GET /api/locations/{id}
```
**Example:**
```bash
curl http://localhost:8081/api/locations/HTDV7N
```
**Response:**
```json
{
  "success": true,
  "data": {
    "id": "HTDV7N",
    "locationName": "Revenue Management - Europe",
    "app": "MARSHA",
    "status": "Active",
    "supervisorEids": ["NMKOS046", "JBROW012"],
    "controlledHotels": ["PARBA", "LONBA", "AMSHA"],
    "createdBy": "ADMIN",
    "createdOn": "2024-01-15T10:00:00.000Z",
    "updatedBy": "NMKOS046",
    "updatedOn": "2024-03-20T14:30:00.000Z"
  },
  "message": "OK",
  "count": 1
}
```

#### List locations (filtered)
```
GET /api/locations?app=MARSHA&status=Active&page=0&size=20
```
**Query params:**

| Param | Required | Description |
|---|---|---|
| `app` | No | Filter by app: `MARSHA`, `MINT`, `ACRS` |
| `status` | No | Filter by status |
| `page` | No | Page number (default `0`) |
| `size` | No | Page size (default `20`) |

#### Search locations by name
```
GET /api/locations/search?q=Revenue+Management
```

#### Get locations by hotel
```
GET /api/locations/by-hotel/{hotelCode}
```
**Example:**
```bash
curl http://localhost:8081/api/locations/by-hotel/PARBA
```

#### Get locations by supervisor EID
```
GET /api/locations/by-supervisor/{eid}
```
**Example:**
```bash
curl http://localhost:8081/api/locations/by-supervisor/NMKOS046
```

#### Get locations by app
```
GET /api/locations/by-app/{app}?status=Active
```
**Example:**
```bash
curl "http://localhost:8081/api/locations/by-app/MINT?status=Active"
```

#### Count locations
```
GET /api/locations/count?app=MARSHA&status=Active
```

---

### Users (EIDs) — `GET /api/users/**`

#### Get user by EID (all apps)
```
GET /api/users/{eid}
```
**Example:**
```bash
curl http://localhost:8081/api/users/NMKOS046
```
**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "65a1b2c3d4e5f6789abcdef0",
      "eid": "NMKOS046",
      "app": "MARSHA",
      "status": "Active",
      "locations": ["HTDV7N", "HTDV8M"],
      "assignments": ["Revenue Manager", "Rate Analyst"],
      "createdOn": "2024-01-15T10:00:00.000Z",
      "updatedOn": "2024-04-10T09:00:00.000Z"
    }
  ],
  "message": "OK",
  "count": 1
}
```

#### Get user by EID and app
```
GET /api/users/{eid}/{app}
```
**Example:**
```bash
curl http://localhost:8081/api/users/NMKOS046/MARSHA
```

#### List users (filtered)
```
GET /api/users?app=MARSHA&status=Active&page=0&size=20
```

#### Get users by location
```
GET /api/users/by-location/{locationCode}
```
**Example:**
```bash
curl http://localhost:8081/api/users/by-location/HTDV7N
```

#### Get users by assignment/persona
```
GET /api/users/by-assignment?persona=Revenue+Manager
```
**Example:**
```bash
curl "http://localhost:8081/api/users/by-assignment?persona=Revenue%20Manager"
```

#### Get users by app
```
GET /api/users/by-app/{app}?status=Active
```

#### Count users
```
GET /api/users/count?app=MARSHA&status=Active
```

---

### Health Check
```
GET /actuator/health
```
**Response:**
```json
{
  "status": "UP",
  "components": {
    "mongo": { "status": "UP" },
    "diskSpace": { "status": "UP" }
  }
}
```

---

## Error Responses

| HTTP Status | Scenario |
|---|---|
| `200 OK` | Success (even for empty results — check `success` field) |
| `404 Not Found` | Resource not found |
| `500 Internal Server Error` | Database or application error |

**Error body example:**
```json
{
  "success": false,
  "data": null,
  "message": "Hotel 'XXXXX' not found",
  "count": 0
}
```

---

## Setup & Run

### Prerequisites
- Java 21 LTS (`/Library/Java/JavaVirtualMachines/openjdk-21.0.2`)
- Maven 3.9+
- MongoDB running on `localhost:27017`

### Configuration (`src/main/resources/application.properties`)
```properties
spring.application.name=aura-tap-query-service
server.port=8081
spring.data.mongodb.uri=mongodb://localhost:27017/aura_db
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
logging.level.com.marriott.aura=DEBUG
```

### Run from terminal
```bash
cd aura-tap-query-service
mvn spring-boot:run
```

### Run in IntelliJ / PyCharm
1. Open `aura-tap-query-service` as a Maven project
2. Set Project SDK to Java 21 (not Java 26-EA — Lombok incompatible)
3. Run `AuraTapQueryApplication.java`

### Verify running
```bash
curl http://localhost:8081/actuator/health
```

---

## Non-Functional Requirements

| Requirement | Target |
|---|---|
| Availability | 99.9% uptime during demo window |
| Response time | < 200ms for single-record lookups |
| Throughput | Supports all 8 agents querying concurrently |
| Read-only | No write operations — all mutations go to `aura-tap-updater-service` |
| CORS | `*` allowed (internal service mesh) |
| Authentication | None (internal service, not internet-facing) |

---

## Dependencies

| Service | Direction | Purpose |
|---|---|---|
| MongoDB `aura_db` | Downstream | Data store |
| `aura-agent-service` :8090 | Consumer | Agents query hotels/locations/users |
| `aura-acrs-client` :8085 | Consumer | EID validation before updates |
| `aura-vds-client` :8086 | Consumer | MINT location membership validation |
