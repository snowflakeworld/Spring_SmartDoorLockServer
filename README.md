## Spring Smart Door Lock Server

Lightweight Spring Boot backend for a smart door lock mobile app. This project exposes HTTP APIs used by the mobile client to register/login users, query and change user/device state, request door operations, and handle messages.

### Quick facts

- Main class: `spring.restapi.RestapiApplication`
- Controller example: `spring.restapi.controller.DoorController` (routes listed below)
- Config file: `src/main/resources/application.yml`
- Java: 8 (see `pom.xml`)
- Build tool: Maven (wrapper included: `mvnw`, `mvnw.cmd`)

## Prerequisites

- Java 8 JDK installed and JAVA_HOME set.
- MySQL server (or adjust `spring.datasource.url`) and a database configured for the app.
- Redis server if you rely on socket/notification features (defaults shown below).

## Configuration

Edit `src/main/resources/application.yml` to change runtime configuration. Relevant defaults found in the repository:

- server.port: 8081
- spring.datasource.url: `jdbc:mysql://localhost:3306/smartiot?useSSL=false`
- spring.datasource.username/password: `smartiot`/`smartiot`
- redis.host: `localhost`, redis.port: `6379`

Also present under `config`:

- `socket_timeout` and `bluetooth_timeout` (ms)

## Build and run (Windows PowerShell)

From the repository root (Windows):

```powershell
.\mvnw.cmd -DskipTests package
.\mvnw.cmd spring-boot:run
```

Or run the produced jar:

```powershell
.\mvnw.cmd -DskipTests package
java -jar target\SmartHomeServer-0.0.1.jar
```

Run tests:

```powershell
.\mvnw.cmd test
```

## HTTP API (examples)

Base path used by controllers: `/smart_app/` (see `DoorController`). All endpoints are POST and expect JSON bodies.

Common endpoints implemented in `DoorController`:

- `POST /smart_app/login` — user login, accepts `LoginRequestDto` (cid, password)
- `POST /smart_app/check_register` — check if CID is available
- `POST /smart_app/register` — register new user
- `POST /smart_app/search_district` — search district data
- `POST /smart_app/get_business_list` — fetch business list
- `POST /smart_app/change_userinfo` — change user info
- `POST /smart_app/change_password` — change password
- `POST /smart_app/request_admin_auth` — request admin authorization
- `POST /smart_app/user_manage_list` — admin: get user manage list
- `POST /smart_app/user_manage_process` — admin: process user (change role/state)
- `POST /smart_app/check_door_states` — check device/socket/bluetooth/battery
- `POST /smart_app/open_door_request` — request open door (network->bluetooth bridging)
- `POST /smart_app/history` — get door open history
- `POST /smart_app/message_list` — get messages/feedback
- `POST /smart_app/message_send` — send feedback message

Request/response shapes are defined under `src/main/java/spring/restapi/resquest_dto` and `src/main/java/spring/restapi/response_dto`.

## Project structure (key packages)

- `spring.restapi.controller` — REST controllers
- `spring.restapi.service` — service layer (`DoorService`)
- `spring.restapi.repository` — Spring Data JPA repositories
- `spring.restapi.model` — JPA entity models
- `spring.restapi.resquest_dto` and `...response_dto` — DTOs

## Notes

- The project uses Spring Boot 2.2.6 and targets Java 8 (see `pom.xml`).
- Default DB/Redis credentials in `application.yml` are development defaults — change them before running in production.
- If you enable Redis-based features, ensure Redis is reachable at the configured host/port.

## Next steps / Suggestions

- Add a `.env` or externalized configuration to avoid committing secrets.
- Add integration tests that start an in-memory DB (H2) and mock Redis for CI runs.

---

If you'd like, I can also:

- generate an example Postman collection for the listed endpoints,
- add a short `docker-compose.yml` to bring up MySQL + Redis for local testing,
- or create a simple `README` badge section with build status once CI is added.
