# Secure Microservices Architecture

A fullstack microservices architecture demonstrating end-to-end security using Vite + React, Spring Boot, Spring Cloud, Keycloak, and MongoDB.

Authentication and authorization are enforced at every layer: the frontend authenticates via Keycloak, the API Gateway validates JWT tokens, and individual microservices enforce role-based access control.

## Overview

This project is built with the following technologies:

- **Frontend:** Vite + React
- **Microservices:** Spring Boot
- **Service Infrastructure:** Spring Cloud Config Server, Eureka, API Gateway
- **Identity Provider:** Keycloak (OAuth2 / OpenID Connect)
- **Database:** H2
- **Token Format:** JWT with role-based authorization

---

## Architecture

```
Vite + React (Port 5173)
        |
        | Login / Token Request
        v
     Keycloak (8080)
        |
        | JWT Access Token
        v
   API Gateway (8090)        <-- Token validation
        |
        | Authorized Request
        v
   Hotel Microservice (8082) <-- Role enforcement
        |
        v
        H2
```

---

## Services and Ports

| Service            | Port | URL                        |
|--------------------|------|----------------------------|
| Config Server      | 8888 | http://localhost:8888      |
| Eureka Server      | 8761 | http://localhost:8761      |
| API Gateway        | 8090 | http://localhost:8090      |
| Hotel Microservice | 8082 | http://localhost:8082      |
| Keycloak           | 8080 | http://localhost:8080      |
| Vite Frontend      | 5173 | http://localhost:5173      |

---

## Prerequisites

- Java 17+
- Node.js 18+
- Docker
- Maven

---

## Startup Order

Services must be started in the following order to ensure proper registration and configuration loading:

1. Config Server (8888)
2. Eureka Server (8761)
3. Keycloak (8080)
4. Hotel Microservice (8082)
5. API Gateway (8090)
6. Vite Frontend (5173)

The Config Server must be fully running before any other Spring service starts, as they fetch their configuration from it on startup. Eureka must be available before the Gateway and microservices attempt to register.

---

## Running Keycloak

Start Keycloak in development mode using Docker:

```bash
docker run -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  -v keycloak_data:/opt/keycloak/data \
  quay.io/keycloak/keycloak:24.0.3 start-dev
```

The `-v keycloak_data` flag mounts a named volume so that realm configuration, users, and clients persist across container restarts.

Access the admin console at `http://localhost:8080` with the following default credentials:

```
Username: admin
Password: admin
```

---

## Keycloak Setup

### 1. Create a Realm

Create a new realm to isolate this project's users, clients, and roles from the master realm. Example name: `microservices-realm`.

### 2. Create a Client for the Frontend

| Setting             | Value                      |
|---------------------|----------------------------|
| Client Type         | OpenID Connect             |
| Access Type         | Public                     |
| Valid Redirect URIs | `http://localhost:5173/*`  |

A public client is appropriate here because the frontend is a browser-based application and cannot securely store a client secret.

### 3. Create a Client for the Backend (Recommended)

| Setting          | Value         |
|------------------|---------------|
| Client Type      | Confidential  |
| Service Accounts | Enabled       |

A confidential backend client allows machine-to-machine token validation and can be used to configure the Gateway or resource servers as Keycloak resource servers.

## License

This project is intended for educational and portfolio purposes.
