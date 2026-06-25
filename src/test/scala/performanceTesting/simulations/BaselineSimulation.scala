package performanceTesting.simulations

import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._
import scala.concurrent.duration._

/**
 * PT-SIM-1 : Baseline Simulation
 * ─────────────────────────────────────────────────────────────────────────────
 * Purpose
 *   Establish a zero-load performance baseline before any load tuning begins.
 *   Run this FIRST on every new environment to capture raw "single-user"
 *   response times — the theoretical floor below which you can never go.
 *
 * Injection pattern: atOnceUsers(n)
 *   Injects ALL n virtual users simultaneously at t=0.
 *   No ramp, no warm-up — the server sees the full concurrency immediately.
 *   Best for: finding the absolute capacity ceiling, stress-testing cold starts.
 *   NOT for: realistic traffic modeling (use RampUpSimulation for that).
 *
 * Run command
 *   mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.BaselineSimulation
 *
 * Report
 *   target/gatling/baselinesimulation-<timestamp>/index.html
 * ─────────────────────────────────────────────────────────────────────────────
 */
class BaselineSimulation extends Simulation {

  // ── Protocol ──────────────────────────────────────────────────────────────
  // karateProtocol() is the bridge between Gatling's HTTP lifecycle hooks and
  // Karate's own HTTP engine.  The path → Nil mapping tells Gatling how to
  // group and label requests in its HTML report.
  // "Nil" = capture all HTTP methods (GET, POST, PATCH, DELETE) on this path.
  val protocol = karateProtocol(
    "/public/v2/users" -> Nil
  )

  // ── Scenario ──────────────────────────────────────────────────────────────
  // A Gatling "scenario" wraps one or more karateFeature() calls.
  // Each virtual user executes the scenario once per injection.
  // karateFeature() resolves to the classpath (src/test/java is a test resource).
  val healthCheck = scenario("HealthCheck — PT-1 Warm-Up Baseline")
    .exec(karateFeature("classpath:performanceTesting/features/pt_HealthCheck.feature@HealthCheck"))

  // ── Load model ────────────────────────────────────────────────────────────
  // atOnceUsers(10) → 10 virtual users fire at t=0 simultaneously.
  setUp(
    healthCheck.inject(atOnceUsers(10))
  ).protocols(protocol)
   .assertions(
     // Max observed response time across all 10 users must be < 5 seconds
     global.responseTime.max.lt(5000),
     // At least 95 % of requests must return a 2xx status
     global.successfulRequests.percent.gt(95.0)
   )
}
