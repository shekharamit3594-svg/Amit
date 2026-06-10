# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Maven-based Java (JDK 25) API automation training project using Rest Assured and TestNG against a live Supabase Banking API backend. The project has two layers: date-stamped session packages under `src/test/java/tests/` (exploration/training history) and a refactored production-style framework (`tests/`, `specbuilders/`, `pojo/`, `listeners/`).

## Commands

```bash
# Run the full suite (testng.xml is wired into Surefire in pom.xml)
mvn clean test

# Run the suite explicitly
mvn clean test -Dsurefire.suiteXmlFiles=testng.xml

# Supply a pre-fetched token to skip auto-login
mvn clean test -DaccessToken="Bearer <token>"

# Run a single test class
mvn test -Dtest="tests.AccountsTest"

# Serve Allure report after a test run (requires Allure CLI)
allure serve allure-results
```

## Architecture

### Package layout

| Location | Purpose |
|---|---|
| `src/main/java/framework/` | `PropertiesUtil` (two-level config), `TestUtil` (in-memory data store), `PathUtils` (Base64 encode/decode + resource path helpers), `GenericExceptions` (Lombok `@StandardException` base), `constants/StatusCodes` (HTTP status enum) |
| `src/test/java/pojo/` | Centralised request/response POJOs, grouped by domain (`account/`, `deposit/`, `withdraw/`, `login/`, etc.) |
| `src/test/java/specbuilders/` | `request/` spec builders (`LoginSpecBuilder`, `AccountSpecBuilder`, `DepositOrWithDrawAmountSpecBuilder`); `response/` spec builders (`AccountResponseSpecBuilder`) |
| `src/test/java/listeners/` | `RetryAnalyser` (3 retries on failure), `AnnotationTransformers` (injects retry + "Regression" group globally) |
| `src/test/java/tests/` | Production-style test classes (`BaseTest`, `AccountsTest`, `CurrencyTest`, `DepositAmountTests`, `WithdrawalAmountTests`, `ListOfTransactionsTests`) |
| `src/test/java/dataProviders/` | `FetchDataFromCSVFile` — TestNG `@DataProvider` that reads `src/test/resources/Test_Data/banking_details.csv` and returns `Iterator<Map<String,String>>`; `ReadingDataFromCSVFile` — standalone exploration scratch class |

### Two-level configuration

`Config.properties` (repo root) declares `TypeOfAPI = Banking`. `PropertiesUtil` reads this first, then loads `src/test/resources/PropertiesFiles/<TypeOfAPI>Config.properties` (`BankingConfig.properties`) for all API-specific settings (base URL, login URL, credentials, endpoint paths). To add a new API target, add a new `<Type>Config.properties` and set `TypeOfAPI` accordingly.

### `TestUtil` — in-memory data store

`framework.TestUtil` is a `Map<String,String>` wrapper (`setData` / `getData`) instantiated in `BaseTest` and passed into spec builders. Use it to share runtime values across tests in the same suite run (e.g. `AccessToken`, `Account Number`). Prefer `testUtil` over `System.setProperty` for framework data; `System.setProperty` is still used for values that must be available before `BaseTest.beforeClass` runs (e.g. `Currency` set by `CurrencyTest`).

### `BaseTest` (authentication backbone)

All production test classes extend `tests.BaseTest`. Its `@BeforeClass`:
1. Instantiates `PropertiesUtil`, `TestUtil`, `LoginSpecBuilder`, `AccountSpecBuilder`, `AccountResponseSpecBuilder`, `DepositOrWithDrawAmountSpecBuilder`, and `Faker`.
2. Sets `RestAssured.baseURI` from `BankingEndPoints.getBaseURL()`.
3. Calls `loginToTheAPI()`, which POSTs to the Supabase auth endpoint and stores the JWT via `testUtil.setData("AccessToken", ...)` — skipped if the token is already present (allows re-use across classes in the same suite).

All `protected` fields (spec builders, `testUtil`, `faker`, `bankingEndPoints`) are available to subclasses.

### `SpecBuilders` — two generations

**Session-based** (`tests.may26th2026.SpecBuilders`) — used by date-stamped test classes; see historical notes below.

**Centralised** (under `specbuilders/`):
- `LoginSpecBuilder.getLoginRequestSpec(RequestPOJO...)` — `Content-Type: JSON` + `apikey` header; body optional.
- `AccountSpecBuilder.getAccountSpecBuilder(RequestPOJO...)` — adds `Authorization: Bearer <token>` from `testUtil`, `apikey` header, `AllureRestAssured` filter; body optional.
- `DepositOrWithDrawAmountSpecBuilder.getDepositOrWithdrawAmountSpecBuilder(RequestPOJO...)` — same headers as `AccountSpecBuilder`; used for deposit, withdrawal, and list-transactions calls.
- `AccountResponseSpecBuilder.getAccountResponseSpec(int, RequestPOJO...)` — asserts status code and `Content-Type: JSON`.

New test classes should use the centralised spec builders under `specbuilders/`.

### `StatusCodes` enum

`framework.constants.StatusCodes` enumerates HTTP codes (`SUCCESS`, `CREATED`, `BAD_REQUEST`, `UNAUTHORIZED`, `PAGE_NOT_FOUND`, `METHOD_NOT_ALLOWED`, `NO_CONTENT`). Use `StatusCodes.SUCCESS.getStatusCode()` instead of magic integers.

### Listeners

`AnnotationTransformers` (registered in `testng.xml`) runs before every test method and:
1. Injects `RetryAnalyser` as the retry analyzer — failed tests are retried up to 3 times before being marked failed.
2. Adds every test to the `"Regression"` group automatically.

### Suite execution order

`testng.xml` runs the following classes in order with `preserve-order="true"`:
1. `CurrencyTest` — fetches all currencies, picks one at random, stores code in `System.setProperty("Currency")`.
2. `AccountsTest` — creates an account using `System.getProperty("Currency")`, stores `account_id` in `System.setProperty("Account Number")`.
3. `DepositAmountTests` — deposits into the account created above.
4. `WithdrawalAmountTests` — withdraws from the same account.
5. `ListOfTransactionsTests` — asserts the transactions list for the account.

Later classes depend on `System.setProperty` values set by earlier ones; running individual classes out of order requires supplying those values manually.

### POJO strategy

All POJOs extend or implement `pojo.request.RequestPOJO` (marker type) to allow spec builders to accept any request type via varargs. Request POJOs use Lombok `@Getter`/`@Setter`/`@Accessors(chain=true)` for fluent building and `@JsonProperty` for snake_case JSON mapping. Response POJOs mirror the exact response shape, often with a nested `Data` class.

### CI / GitHub Actions

Workflow: `.github/workflows/rest-assured-tests.yml`.

Triggers: push/PR to `restAssuredFramework` or `main`; `workflow_dispatch` (manual run with optional token input); schedule every 6 hours. Concurrent runs for the same branch/PR are auto-cancelled on new push.

Steps in order:
1. Compile sources (`mvn compile test-compile`) — fails fast before test execution.
2. Run TestNG suite — token resolved from `workflow_dispatch` input → `ACCESS_TOKEN` secret → BaseTest auto-login.
3. Generate Allure HTML report via Allure CLI 2.34.0 (matches `allure-testng` in pom.xml).
4. Generate `target/test-report.html` (custom report) and write `$GITHUB_STEP_SUMMARY` (inline on the run page) via `.github/scripts/generate_report.js` and `.github/scripts/generate_summary.js`.
5. Upload artifacts: `allure-report-<n>` (14 days), `test-report-<n>` (14 days), `api-logs-<n>` (7 days).

## Target API Endpoints

Base URI: `https://qnajbqxmpbmndnwdqswq.supabase.co/functions/v1`

| Method | Path | Purpose |
|---|---|---|
| POST | `/create-account` | Create a bank account |
| GET | `/view-account?account_id=<id>` | Fetch single account |
| GET | `/list-accounts` | List accounts (supports `currency`, `limit`, `sort_by`, `order`) |
| GET | `/manage-currencies` | List all currencies |
| PATCH | `/patch-account` | Update account fields |
| DELETE | `/delete-account?account_id=<id>` | Remove account |
| POST | `/deposit` | Deposit into an account |
| POST | `/withdraw` | Withdraw from an account |
| GET | `/list-transactions?account_id=<id>&page=<n>&limit=<n>` | List transactions |

Tests create and delete real records; requires a live network connection.

## Coding Conventions

- **Test ordering**: use `@Test(description = "...", priority = N)` when tests form a workflow.
- **Status codes**: always use `StatusCodes.<NAME>.getStatusCode()`, never magic integers.
- **Data sharing**: use `testUtil.setData`/`getData` for framework-owned values; `System.setProperty`/`getProperty` for values that cross `@BeforeClass` boundaries between test classes.
- **Assertions**: prefer inline Hamcrest matchers in `.then()`; use `Assert.*` for post-extraction checks.
- **Commit style**: short sentence summaries, e.g. `Discussed about Hamcrest Matchers and Spec Builders`.
