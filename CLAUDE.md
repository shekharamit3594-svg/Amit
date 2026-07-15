# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Maven-based Java (JDK 25) API automation training project using the **Karate DSL** framework (`karate-junit5` 1.5.0). JUnit 5 is the test engine. This is the `karateFundamentals` branch; the `main`/`restAssuredFramework` branches hold a separate Rest Assured + TestNG framework.

## Commands

```bash
# Run all Karate runners
mvn clean test

# Run a specific runner class
mvn test -Dtest="sampleRunner.SampleRunner"
mvn test -Dtest="practiceData.gorest.GoRestRunner"
mvn test -Dtest="practiceData.httpMethods.HttpMethodsRunner"
mvn test -Dtest="practiceData.callFeatures.CallFeaturesRunner"

# ── UI Automation ─────────────────────────────────────────────────────────────
# Sequential (CricbuzzRunner) — one scenario at a time
mvn test -Dtest="uiAutomation.cricbuzz.CricbuzzRunner"
# Parallel (CricbuzzParallelRunner) — N browser sessions concurrently
mvn test -Dtest="uiAutomation.cricbuzz.CricbuzzParallelRunner"

# Remote Selenium Grid (default hub: http://selenium-hub:4444)
mvn test -Dtest="uiAutomation.cricbuzz.CricbuzzRunner" -Dwebdriver.remote=true
# Remote with a custom Grid URL
mvn test -Dtest="uiAutomation.cricbuzz.CricbuzzRunner" -Dwebdriver.remote=true -Dgrid.url=http://myhost:4444

# ── Docker (full Selenium Grid stack) ────────────────────────────────────────
docker-compose up --build --abort-on-container-exit

# ── Tag filtering ─────────────────────────────────────────────────────────────
mvn test -Dkarate.options="--tags @JSONParsing"
mvn test -Dkarate.options="--tags @Positive"
mvn test -Dkarate.options="--tags @BasicTypes"

# Run with a specific API env (maps to if-block in karate-config.js)
mvn test -Dkarate.env=e2e

# ── Performance Testing (Karate parallel runner) ──────────────────────────────
mvn test -Dtest="performanceTesting.PerformanceRunner"
mvn test -Dtest="performanceTesting.PerformanceRunner#parallelGetUsers"
mvn test -Dtest="performanceTesting.PerformanceRunner#parallelCreateUsers"
mvn test -Dtest="performanceTesting.PerformanceRunner#parallelUserLifecycle"
mvn test -Dtest="performanceTesting.PerformanceRunner#parallelFullSuite"
mvn test -Dtest="performanceTesting.PerformanceRunner#loadTest"
mvn test -Dtest="performanceTesting.PerformanceRunner#stressTest"
mvn test -Dtest="performanceTesting.PerformanceRunner#volumeTest"
mvn test -Dtest="performanceTesting.PerformanceRunner#concurrencyTest"

# ── Gatling simulations (HTML reports in target/gatling/) ────────────────────
mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.BaselineSimulation
mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.RampUpSimulation
mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.SpikeTestSimulation
mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.SoakTestSimulation
mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.StepUpLoadSimulation
mvn gatling:test   # run ALL Gatling simulations

# ── Allure reporting ──────────────────────────────────────────────────────────
mvn verify         # runs tests + generates allure-report/ via allure:report
```

## Architecture

### Feature file co-location

Feature files live in the **same package directory as their runner**, not under `src/test/resources`. The `pom.xml` registers `src/test/java` as a test resource directory (excluding `*.java`) so Maven copies `.feature`, `.js`, and test data files to the classpath at build time.

```
src/test/java/
├── karate-config.js                          ← global config, auto-loaded
├── sampleRunner/
│   ├── SampleRunner.java
│   ├── users.feature                         ← JSONPlaceholder smoke tests
│   └── SyntaxesInKarateFramework.feature
├── practiceData/
│   ├── gorest/
│   │   ├── GoRestRunner.java
│   │   ├── GoRestFeature.feature             ← full CRUD with Java interop
│   │   ├── PostOperation.feature
│   │   ├── PutOperationUsingPostId.feature   ← calls PostOperation.feature@PostOperation
│   │   ├── PerformingQueryParameters.feature
│   │   ├── UnderstandingKarateVariables.feature
│   │   ├── DataDrivenExamples.feature        ← 4 Scenario Outline patterns
│   │   ├── DataDrivenUserCreation.feature    ← CSV-driven Examples
│   │   ├── FetchGendersAndStatus.java        ← Java helper for random data
│   │   └── test-data/
│   │       ├── gorest_users.csv
│   │       └── gorest_users_json.json
│   ├── httpMethods/
│   │   ├── HttpMethodsRunner.java
│   │   ├── PatchOperation.feature
│   │   ├── DeleteOperation.feature
│   │   └── PutVsPatch.feature
│   └── callFeatures/
│       ├── CallFeaturesRunner.java
│       ├── CallingWithArgs.feature
│       ├── CallonceSharedAuth.feature
│       ├── AuthSetup.feature                 ← reusable auth token acquisition
│       └── CreateUserHelper.feature          ← reusable user creation helper
├── uiAutomation/
│   └── cricbuzz/
│       ├── CricbuzzRunner.java               ← sequential runner
│       ├── CricbuzzParallelRunner.java       ← Runner.parallel() with 3 browser threads
│       ├── CricbuzzHomePage.feature
│       ├── CricbuzzSearch.feature
│       ├── CricbuzzLiveScores.feature
│       ├── CricbuzzSchedule.feature
│       ├── CricbuzzRankings.feature
│       ├── CricbuzzBrowserControls.feature   ← click, input, scroll, hover
│       ├── CricbuzzBrowserState.feature      ← cookies, localStorage, JS eval
│       ├── CricbuzzDriverModes.feature       ← headless, per-scenario driver override
│       ├── CricbuzzFormAndDialog.feature     ← alerts, confirm dialogs
│       ├── CricbuzzIframe.feature            ← iframe switching
│       └── CricbuzzMultiTab.feature          ← multi-tab / window handles
└── performanceTesting/
    ├── PerformanceRunner.java                ← JUnit5 parallel runner (no Gatling)
    ├── PerformanceMetricsHelper.java         ← thread-safe p50/p95/p99 collector
    └── features/
        ├── pt_HealthCheck.feature            ← PT-1: warm-up / baseline probe
        ├── pt_GetUsers.feature               ← PT-2: read load
        ├── pt_CreateUser.feature             ← PT-3: write load
        ├── pt_UserLifecycle.feature          ← PT-4: CRUD chain
        ├── pt_LoadTest.feature               ← PT-5: SLA assertions + think time
        ├── pt_StressTest.feature             ← PT-6: 50ms think, 15% error threshold
        ├── pt_VolumeTest.feature             ← PT-7: per_page=100, pagination latency
        └── pt_ConcurrencyTest.feature        ← PT-8: race conditions, shared resource

src/test/scala/
└── performanceTesting/
    └── simulations/
        ├── BaselineSimulation.scala          ← PT-SIM-1: atOnceUsers(10)
        ├── RampUpSimulation.scala            ← PT-SIM-2: 0→30 users over 60 s
        ├── SpikeTestSimulation.scala         ← PT-SIM-3: burst to 50 concurrent
        ├── SoakTestSimulation.scala          ← PT-SIM-4: 20 users × 5 min
        └── StepUpLoadSimulation.scala        ← PT-SIM-5: 5→25/s staircase
```

### `karate-config.js` — global configuration

Loaded automatically before every feature. Sets connect/read timeouts (5s/30s), retry policy (3 retries, 10s interval), and exports a `config` object whose fields (`baseUrl`, `bearerToken`, `usersPath`, `contentType`, `driverConfig`, `env`) are available as variables in all feature files.

**Auto-screenshot on failure**: `karate-config.js` configures an `afterScenario` hook that embeds a screenshot into the HTML report when a scenario fails. The screenshot call is wrapped in try/catch so it silently skips on API-only scenarios with no active browser session.

**UI driver config**: `driverConfig` is built once in `karate-config.js` and should be applied in UI feature `Background:` blocks via `* configure driver = driverConfig`. This means local vs remote mode is controlled entirely by `-Dwebdriver.remote=true` without touching feature files.

Add new environments by extending the `if (env == '...')` block. Default env is `dev`.

### Runner classes

Each runner is a plain JUnit 5 class. `@Karate.Test` runners call `Karate.run("FileName.feature").relativeTo(getClass())`. `Runner.parallel(n)` runners use `Runner.path(...)` with a classpath directory or feature path and spawn `n` JUnit threads.

Tag filtering: `.tags("@SomeTag")` or `--tags @SomeTag` via `-Dkarate.options`.

**`CricbuzzParallelRunner`** uses `Runner.parallel(3)` — each thread gets an independent browser session. Thread count should match the `SE_NODE_MAX_SESSIONS` on the Grid chrome-node (default: 3 in `docker-compose.yml`).

### Performance testing: two approaches

| | `PerformanceRunner` (JUnit parallel) | Gatling simulations (Scala) |
|---|---|---|
| Dependencies | Just `karate-junit5` | Gatling, Scala, `karate-gatling` |
| Reports | Karate HTML (pass/fail) | HTML with latency percentile graphs |
| Concurrency model | Thread count | Arrival rate (ramp/spike/soak/step) |
| Use when | Quick parallel sanity check | SLA validation, capacity planning |

**`PerformanceMetricsHelper`** (Java, thread-safe): used inside feature files via `Java.type('performanceTesting.PerformanceMetricsHelper')` to collect `p50/p90/p95/p99` response times across parallel threads. Call `metrics.reset()` from the runner before the test, `metrics.startRequest()` before each HTTP step, and `metrics.recordSuccess(start)` / `recordError(start)` after.

**`concurrencyTest`** runs in three sequential phases (`@Setup` → `@ConcurrentRead,@ConcurrentWrite` → `@ConsistencyCheck,@Cleanup`) to safely test race conditions on a shared resource.

### Docker / Selenium Grid

`docker-compose.yml` brings up: `selenium-hub` (Grid 4), `chrome-node` (3 sessions), `firefox-node` (2 sessions), and the `karate-tests` Maven runner. Tests run headlessly inside the container against the hub.

- **noVNC live preview**: `http://localhost:7900` (Chrome), `http://localhost:7901` (Firefox) while tests run.
- The `karate-tests` service passes `-Dwebdriver.remote=true`; `karate-config.js` routes `driverConfig.webDriverUrl` to `http://selenium-hub:4444/wd/hub`.

For API-only tests inside Docker (no Grid needed):
```bash
docker-compose run --rm karate-tests mvn -o test -Dtest="practiceData.gorest.GoRestRunner"
```

### Calling feature files (`call` / `callonce`)

```gherkin
# call — runs every time (per-scenario setup)
* def result = call read('CreateUserHelper.feature') { name: 'Alice', email: 'alice@test.com' }
* def userId = result.userId

# callonce — result cached for the feature's lifetime (auth tokens)
* def auth = callonce read('classpath:practiceData/callFeatures/AuthSetup.feature')
* header Authorization = 'Bearer ' + auth.bearerToken
```

The last `def` in a called feature is its return value.

### Data-driven testing

**Scenario Outline with inline Examples:**
```gherkin
Scenario Outline: <title>
  * param status = '<status>'
  Examples:
    | status   |
    | active   |
    | inactive |
```

**CSV-driven Examples** (read at runtime):
```gherkin
Examples:
  | read('../test-data/gorest_users.csv') |
```

**JSON file-based Examples:**
```gherkin
Examples:
  | read('../test-data/gorest_users_json.json') |
```

CSV files go in `test-data/` alongside the feature.

### Java interop in feature files

```gherkin
* def gender = Java.type('practiceData.gorest.FetchGendersAndStatus')
* payload.gender = gender.getGender()
```

`FetchGendersAndStatus` provides `getGender()` / `getStatus()` returning random values via `ThreadLocalRandom`.

### Karate match syntax

- `#string`, `#number`, `#boolean`, `#array`, `#object` — fuzzy type matchers
- `#notnull`, `#null` — null checks
- `#[10]` — assert array length
- `match each response contains { ... }` — assert every element
- `#regex '^[A-Z]'` — regex match

### Coding conventions

- Scenario-level tags go on the `Scenario:` line; filter with `--tags @Name` or `.tags("@Name")` in the runner.
- Shared setup belongs in `Background:` within a feature or in `karate-config.js` for cross-feature values.
- Inline JS functions in `Background:` (e.g. random email generators) are idiomatic for dynamic test data without a Java helper.
- `PutOperationUsingPostId.feature` demonstrates chaining: calling another feature's tagged scenario (`@PostOperation`) to get a resource ID before operating on it.
- **Commit style**: short sentence summaries, e.g. `Discussed about Karate Fundamentals and Concepts`.
