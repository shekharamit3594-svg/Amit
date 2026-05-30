# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Maven-based Java (JDK 25) API automation training project using Rest Assured and TestNG against a live Supabase Banking API backend. Each session's tests live in a date-stamped package under `src/test/java/tests/`.

## Commands

```bash
# Compile and run all tests via Maven defaults (testng.xml is the default suite)
mvn clean test

# Run the explicitly ordered Banking API suite
mvn clean test -Dsurefire.suiteXmlFiles=testng.xml

# Skip the auto-login step by supplying a valid token
mvn clean test -Dsurefire.suiteXmlFiles=testng.xml -DaccessToken="Bearer <token>"

# Run a single test class
mvn test -Dtest="tests.may29th2026.UnderstandingLoggingFilters"
mvn test -Dtest="tests.may29th2026.FormParameters"

# Serve Allure report after a test run (requires Allure CLI)
allure serve allure-results
```

## Architecture

### Package layout

| Location | Purpose |
|---|---|
| `src/main/java/framework/` | Shared utilities (`PathUtils` for Base64 encode/decode) |
| `src/test/java/tests/may18th2026/` | First HTTP methods exploration |
| `src/test/java/tests/may19th2026/` | Continuation of HTTP methods |
| `src/test/java/tests/may20th2025/` | `BaseTest` + first full CRUD end-to-end suite |
| `src/test/java/tests/may25th2026/` | Serialization/deserialization with POJOs + `RequestData` interface |
| `src/test/java/tests/may26th2026/` | Hamcrest matchers + `SpecBuilders` for reusable request/response specs |
| `src/test/java/tests/may27th2026/` | Query parameters: single param, multi-param via `Map`, deserialization into typed POJOs |
| `src/test/java/tests/may28th2026/` | `RequestLoggingFilter` / `ResponseLoggingFilter` writing to `src/test/resources/logs/api-logs.log` |
| `src/test/java/tests/may29th2026/` | Form parameters via `Map<String,Object>` + `getRequestSpecification_URLEncoded`; logging filters revisited in `UnderstandingLoggingFilters` |

### `BaseTest` (authentication backbone)

All test classes that need auth extend `tests.may20th2025.BaseTest`. Its `@BeforeClass` method:
1. Checks `System.getProperty("accessToken")` first (allows CI override via `-DaccessToken`).
2. If absent, calls `POST /auth/v1/token` on Supabase to obtain a JWT and stores it as `Bearer <token>` in the `accessToken` system property.
3. `@BeforeMethod` sets `RestAssured.baseURI` to the Supabase Edge Functions endpoint.

The credential password is stored Base64-encoded and decoded at runtime via `PathUtils.decodeData(...)`. Do not commit decoded credentials.

`BaseTest` also instantiates `faker` (Datafaker) and `specBuilders` (SpecBuilders) as `protected` fields available to every subclass.

### POJO strategy

- `RequestPOJO` uses Lombok `@Getter`/`@Setter`/`@Accessors(chain=true)` for fluent builders and `@JsonProperty` to map camelCase fields to snake_case JSON keys.
- `ResponsePOJO` mirrors the response shape with a nested static `Data` class.
- The `RequestData` marker interface allows `SpecBuilders` to accept any request POJO without coupling to a concrete type.
- POJOs live beside the test package that introduced them; promote to `src/main/java` only if shared across multiple sessions.

### `SpecBuilders`

`tests.may26th2026.SpecBuilders` encapsulates common setup so individual tests call `.spec(specBuilders.getRequestSpecification(request))` and `.spec(specBuilders.getResponseSpecification(statusCode, request))` instead of repeating headers, filters, and matchers inline.

- `getRequestSpecification()` — no body, auth header + Allure filter only.
- `getRequestSpecification(RequestData)` — same plus serialized body.
- `getRequestSpecification_URLEncoded(Map<String,Object>)` — sets `Content-Type: application/x-www-form-urlencoded` and adds all map entries as form params (used in may29th2026).
- `getResponseSpecification(int)` — status code assertion only.
- `getResponseSpecification(int, RequestData)` — full field-by-field assertion against the original `RequestPOJO`.
- `getResponseSpecification_FormParameters(int, Map<String,Object>)` — same field-by-field assertions but reads values from a `Map` instead of a POJO (used in may29th2026).
- `withLogStream(PrintStream)` — fluent setter; once called, every subsequent spec automatically writes request/response bodies to the file (used in may28th2026 and may29th2026).

New test classes should prefer `SpecBuilders` over hand-rolling request/response specs.

### File logging pattern (may28th2026 / may29th2026)

Log file path: `src/test/resources/logs/api-logs.log` (append mode; directory created at runtime via `logDir.mkdirs()`).

Two approaches for writing Rest Assured output to the log file:

1. **Inline** — add `.filter(new RequestLoggingFilter(LogDetail.BODY, logStream))` directly in the `given()` chain for per-test control.
2. **Centralised** — call `specBuilders.withLogStream(logStream)` once in `@BeforeClass`; all subsequent spec-built requests log automatically. This is the approach used in `UnderstandingLoggingFilters`.

`@BeforeClass(dependsOnMethods = "generateJWTToken")` ensures the log file is set up after auth completes. Always flush/close the `PrintStream` in `@AfterClass(alwaysRun = true)`.

### Form parameters pattern (may29th2026)

`FormParameters` demonstrates sending request data as `application/x-www-form-urlencoded` instead of JSON:

- Build a `Map<String, Object>` with all field values.
- Pass it to `specBuilders.getRequestSpecification_URLEncoded(map)` which calls `addFormParams()` on the `RequestSpecBuilder`.
- Use `specBuilders.getResponseSpecification_FormParameters(statusCode, map)` for assertions.
- **Note:** the `/create-account` Supabase Edge Function processes JSON and form data through different code paths; its form-data path uppercases string values before validation, which can cause `currency_not_found` errors. Use currency codes (e.g. `INR`) rather than full names when sending form params.

### Allure integration

`SpecBuilders.getRequestSpecification()` adds `.filter(new AllureRestAssured())` automatically. Run `allure serve allure-results` after tests to view the report.

### CI / GitHub Actions

Workflow file: `.github/workflows/rest-assured-tests.yml`
Default branch: `restAssuredFundamentals` (GitHub runs scheduled workflows from the default branch only).

Triggers:
- **push** to `restAssuredFundamentals`
- **schedule** — cron `*/5 * * * *` (every 5 minutes; GitHub enforces a minimum of ~5 min and may delay runs under high load)

The workflow runs the full TestNG suite, generates a self-contained HTML report via `.github/scripts/generate_report.js`, and uploads it as a downloadable artifact (`test-report-<run_number>`). API logs from `src/test/resources/logs/` are uploaded as a separate artifact (`api-logs-<run_number>`).

## Target API Endpoints

Base URI: `https://qnajbqxmpbmndnwdqswq.supabase.co/functions/v1`

| Method | Path | Purpose |
|---|---|---|
| POST | `/create-account` | Create a bank account |
| GET | `/view-account?account_id=<id>` | Fetch single account details |
| GET | `/list-accounts` | List accounts (supports `currency`, `limit`, `sort_by`, `order` query params) |
| GET | `/manage-currencies?include_inactive=true` | List all currencies |
| PATCH | `/patch-account` | Update account fields (e.g. name, email) |
| DELETE | `/delete-account?account_id=<id>` | Remove account |

Tests create and delete real records in the remote Supabase database; run on a network connection and expect side effects.

## Coding Conventions

- **Class names**: descriptive scenario names (`UnderstandingHamcrestMatchers`) or role names (`BaseTest`, `SpecBuilders`).
- **Test ordering**: use `@Test(description = "...", priority = N)` when tests form a workflow (create → view → patch → delete).
- **Query parameters**: pass a single param with `.queryParam(key, value)`; pass multiple with `.queryParams(Map<String, Object>)`.
- **Assertions**: prefer inline Hamcrest matchers in the `.then()` chain; fall back to `Assert.*` for post-extraction checks.
- **Logging**: use `IO.println(...)` (Java 25 preview) for console output; use `.log().ifValidationFails()` in Rest Assured for failure-only logging.
- **Commit style**: short sentence summaries, e.g. `Discussed about Hamcrest Matchers and Spec Builders`.
