# URL Shortener Service

A Spring Boot URL shortener service that converts long URLs into
compact, shareable short URLs and redirects requests back to the
original URL.

## Features

-   Create short URLs through `POST /api/v1/urls`
-   Redirect through `GET /{shortCode}`
-   Duplicate long-URL detection
-   Base62 and Sequential short-code generation strategies
-   Strategy + Factory design
-   MySQL persistence using JPA
-   Redis cache-aside lookup
-   Redis TTL-based caching
-   Optional URL expiration
-   Expired URL cleanup
-   Database-backed ID generation
-   Request validation
-   Global exception handling
-   Configurable public/base URL
-   Unit, controller, cache, and integration tests

## Architecture

``` text
Client
  |
  +-- POST /api/v1/urls --> UrlController --> UrlCreationService
  |                                             |
  |                                             +--> MySQL
  |                                             +--> DatabaseIdGenerator
  |                                             +--> ShortCodeGeneratorFactory
  |                                             +--> Redis
  |
  +-- GET /{shortCode} --> RedirectController --> UrlRedirectService
                                                |
                                                +--> Redis
                                                |     |
                                                |     +-- HIT --> UrlMapping
                                                |     +-- MISS --> MySQL --> Redis
                                                |
                                                +--> expiration validation
                                                |
                                                +--> HTTP 302 redirect
```

## Storage responsibilities

### MySQL

MySQL is the persistent source of truth. It stores URL mappings and the
database-backed ID sequence.

### Redis

Redis is the fast lookup cache. It stores URL mappings by short code and
uses TTLs to avoid retaining cached mappings indefinitely.

## Creating a short URL

Request:

``` http
POST /api/v1/urls
Content-Type: application/json
```

Example:

``` json
{
  "longUrl": "https://www.netflix.com",
  "generationStrategy": "BASE62"
}
```

Example response:

``` json
{
  "shortCode": "5",
  "shortUrl": "http://localhost:8080/5"
}
```

The creation flow is:

1.  Check whether the long URL already exists.
2.  Return the existing mapping if it does.
3.  Otherwise obtain a unique ID from the database-backed ID generator.
4.  Generate the short code.
5.  Save the mapping in MySQL.
6.  Cache the mapping in Redis.
7.  Return the short URL.

> Current API behavior exposes `generationStrategy` in the request. The
> actual strategy implementation is isolated behind the generator and
> factory abstractions.

## Redirecting

Request:

``` http
GET /5
```

The service first checks Redis. On a cache miss it loads the mapping
from MySQL and repopulates Redis.

For an active mapping:

``` http
HTTP/1.1 302 Found
Location: https://www.netflix.com
```

Postman may automatically follow this redirect and display HTML returned
by the destination website. Disable automatic redirect following if you
want to inspect the original `302` response.

## URL expiration

`expiresAt` is optional.

Example:

``` json
{
  "longUrl": "https://www.netflix.com",
  "generationStrategy": "BASE62",
  "expiresAt": "2026-12-31T23:59:59"
}
```

If `expiresAt` is `null` or omitted, the URL does not expire.

For expiring mappings:

-   Redirect resolution validates expiration.
-   Expired mappings are removed from persistent storage.
-   Redis TTL is calculated from the remaining URL lifetime.

For non-expiring mappings, Redis uses the configured cache TTL.

## ID generation

The service intentionally separates persistent ID allocation from
short-code generation.

The `IdGenerator` abstraction is backed by a database-maintained
sequence rather than an in-memory counter.

This prevents collisions with IDs already persisted in MySQL and
provides a concurrency-safe foundation for multiple application
instances.

The flow is:

``` text
Database-backed ID
        |
        v
ShortCodeGeneratorFactory
        |
   +----+----+
   |         |
 Base62   Sequential
   |         |
   +----+----+
        |
        v
   shortCode
```

## Configuration

Example local configuration:

``` properties
server.port=8080

app.base-url=http://localhost:8080

spring.data.redis.host=localhost
spring.data.redis.port=6379

url.cache.ttl=86400
```

`app.base-url` controls the public URL returned by the creation API.

Local:

``` properties
app.base-url=http://localhost:8080
```

Production example:

``` properties
app.base-url=https://short.example.com
```

The application code remains unchanged; only the environment
configuration changes.

## Prerequisites

-   Java version configured by the project
-   MySQL
-   Redis-compatible server
-   Git
-   Maven, or the included Maven Wrapper

On Windows, Memurai can be used as the Redis-compatible server.

Verify Redis/Memurai:

``` powershell
memurai-cli ping
```

Expected:

``` text
PONG
```

## Database setup

The application uses MySQL for persistent URL mappings and ID
allocation.

The database contains:

``` text
url_mapping
id_sequence
```

For a fresh database, initialize the ID sequence with the next available
ID.

Example:

``` sql
CREATE TABLE id_sequence (
    id BIGINT PRIMARY KEY,
    next_id BIGINT NOT NULL
);
```

For an empty `url_mapping` table:

``` sql
INSERT INTO id_sequence (id, next_id)
VALUES (1, 1);
```

If existing URL mappings already use IDs `1` and `2`, initialize the
sequence with:

``` sql
INSERT INTO id_sequence (id, next_id)
VALUES (1, 3);
```

Initialize the sequence only once.

## Running locally

Clone the repository:

``` bash
git clone <repository-url>
cd url-shortener-service
```

Windows:

``` powershell
.\mvnw.cmd spring-boot:run
```

Linux/macOS:

``` bash
./mvnw spring-boot:run
```

The application runs locally on:

``` text
http://localhost:8080
```

## End-to-end example

Create:

``` http
POST http://localhost:8080/api/v1/urls
Content-Type: application/json
```

``` json
{
  "longUrl": "https://github.com",
  "generationStrategy": "BASE62"
}
```

Possible response:

``` json
{
  "shortCode": "1",
  "shortUrl": "http://localhost:8080/1"
}
```

Then:

``` http
GET http://localhost:8080/1
```

returns:

``` http
302 Found
Location: https://github.com
```

## Redis verification

Open the CLI:

``` powershell
memurai-cli
```

List keys:

``` redis
KEYS *
```

Inspect a key\'s remaining TTL:

``` redis
TTL 1
```

A positive TTL indicates that the cached mapping is still present with
an expiration.

For development, the cache can be cleared with:

``` redis
FLUSHDB
```

## Testing

Run the complete test suite:

``` powershell
.\mvnw.cmd clean test
```

Run the complete Maven build:

``` powershell
.\mvnw.cmd clean install
```

The test suite covers URL creation, duplicate handling, short-code
generation, ID generation, redirects, Redis cache hit/miss behavior,
cache expiration, URL expiration, controller validation, exception
handling, and integration flows.

## Project structure

``` text
src/
├── main/
│   ├── java/com/lld/urlshortnerservice/
│   │   ├── cache/
│   │   ├── common/
│   │   │   └── idgenerator/
│   │   ├── exception/
│   │   ├── url/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── mapper/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── strategy/
│   │   └── UrlShortnerServiceApplication.java
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/lld/urlshortnerservice/
```

## Design patterns

### Strategy Pattern

`ShortCodeGenerator` represents the short-code generation abstraction.
Base62 and Sequential implementations provide alternative strategies.

### Factory Pattern

`ShortCodeGeneratorFactory` selects the requested short-code generator.

### Repository / Adapter {#repository--adapter}

Business services depend on repository abstractions rather than directly
coupling themselves to JPA implementation details.

### Cache-aside

Redirects check Redis first, fall back to MySQL on a miss, then
repopulate Redis.

### Database-backed ID allocation

Persistent ID allocation avoids the collision problem of an in-memory
counter.

## Error handling

The application uses request validation and a global exception handler
to return structured JSON error responses.

Examples of handled failures include:

-   missing request body
-   invalid or blank long URL
-   missing generation strategy
-   invalid expiration data
-   missing short code
-   expired URL
-   persistence/cache failures

## Production scope

This repository represents the completed core URL-shortener design.

The following are intentionally outside the current core scope:

-   deployment infrastructure
-   CI/CD
-   container orchestration
-   metrics and distributed tracing
-   rate limiting
-   authentication/authorization
-   load testing
-   high-availability database/cache deployment
-   production security hardening
-   advanced scaling and operational configuration

These can be addressed separately as a production-readiness phase.

## Current status

The core URL-shortener implementation is complete on the `main` branch.

The implemented core includes:

-   URL creation
-   URL redirection
-   duplicate URL detection
-   Base62 and Sequential strategies
-   MySQL persistence
-   Redis caching
-   Redis TTL
-   URL expiration
-   database-backed ID generation
-   validation
-   global exception handling
-   configurable base URL
-   unit/controller/cache/integration tests
