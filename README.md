# TinyLinks — learn system design with Java

A small URL shortener built with Java 17, Spring Boot, Spring Data JPA, and file-backed H2. No separate database server is needed for the first version.

## Run it

Install Java 17+ and Maven, then run from this directory:

```bash
mvn spring-boot:run
```

The API listens on `http://localhost:8080`. H2 stores data under `./data`. Set `PORT` and `BASE_URL` to change the listening port and the public URL returned by the create endpoint.

Create a short link:

```bash
curl -i -X POST http://localhost:8080/api/links \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://example.com/articles/system-design"}'
```

Use the returned `shortUrl` to redirect. Get its metadata and click count with:

```bash
curl http://localhost:8080/api/links/CODE123
curl -i http://localhost:8080/CODE123
```

## Request path

```text
POST /api/links ──> LinkController ──> LinkService ──> LinkRepository ──> H2
GET  /{code}     ──> LinkController ──> increment click + find URL ──> HTTP 302
```

The short code is seven random Base62 characters. The database primary key prevents duplicate codes. Destinations must be valid HTTP or HTTPS URLs. The click counter is incremented with one database update statement.

## Learning path

1. Add custom aliases and return `409 Conflict` when an alias already exists.
2. Add expiry timestamps and a cleanup strategy; decide what an expired link returns.
3. Add rate limiting. Think about client identity when a reverse proxy sits in front of the app.
4. Move click analytics to a background queue so the redirect path does not wait on analytics processing.
5. Add a cache for popular codes and define how a changed or expired mapping is invalidated.
6. Replace H2 with PostgreSQL, run multiple app instances, then measure what needs a load balancer or database scaling.

For each step, write down requirements, expected traffic, the current bottleneck, the change, the measured result, and the trade-offs. Keep the design simple until a requirement or measurement justifies extra moving parts.

This is an educational baseline. It has no authentication, rate limiting, expiration, abuse detection, or production database setup. The H2 console is enabled for local inspection and should be disabled before exposing the service publicly.
