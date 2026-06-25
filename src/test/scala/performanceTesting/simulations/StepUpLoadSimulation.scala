package performanceTesting.simulations

import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._
import scala.concurrent.duration._

/**
 * PT-SIM-5 : Step-Up Load Simulation  (Staircase Pattern)
 * ─────────────────────────────────────────────────────────────────────────────
 * Purpose
 *   Systematically find the system's breaking point by increasing load in
 *   discrete, flat "steps" and measuring KPIs at each level.
 *
 * Why step-up instead of a simple ramp?
 *   rampUsers() produces a continuous slope — latency data from each instant
 *   is mixed with data from neighbouring load levels, making it hard to say
 *   "at exactly 20 users/sec the p95 crossed 2 seconds."
 *
 *   incrementUsersPerSec() creates PLATEAUS — each step holds a fixed load
 *   long enough to collect statistically stable samples before moving to the
 *   next level.  The result is a clear step graph where you can pinpoint the
 *   exact load level at which SLAs break.
 *
 * Traffic shape (5 steps × 5 users/sec, 30 s each + 5 s ramp between steps)
 *   ┌────────────────────────────────────────────────────────────────────────┐
 *   │     5/s       10/s      15/s      20/s      25/s                      │
 *   │  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐  ┌──────┐                   │
 *   │  │ 30s  │  │ 30s  │  │ 30s  │  │ 30s  │  │ 30s  │                   │
 *   │  └──┬───┘  └──┬───┘  └──┬───┘  └──┬───┘  └──┬───┘                   │
 *   │     │ 5s      │ 5s      │ 5s      │ 5s      │ (ramps between steps)  │
 *   └────────────────────────────────────────────────────────────────────────┘
 *   Wall-clock total: ~2.9 minutes
 *
 * How to read the Gatling report
 *   Look at "Response Time Percentiles over Time":
 *     ● At which step does p95 first cross your SLA threshold?
 *     ● At which step does the error rate first rise above 0 %?
 *   The step JUST BEFORE either threshold is your safe operating capacity.
 *
 * Run command
 *   mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.StepUpLoadSimulation
 * ─────────────────────────────────────────────────────────────────────────────
 */
class StepUpLoadSimulation extends Simulation {

  val protocol = karateProtocol(
    "/public/v2/users" -> Nil
  )

  // Read scenario — GET /users page 1.
  // Read ops are better for step-up than writes because:
  //   (a) They're idempotent — no data accumulation per step
  //   (b) They're typically the hot path in real traffic
  val readLoad = scenario("Read Load — Step-Up GET /users (staircase)")
    .exec(karateFeature("classpath:performanceTesting/features/pt_GetUsers.feature@GetAllUsers"))

  setUp(
    readLoad.inject(
      // incrementUsersPerSec(n) builds a staircase injection:
      //   .times(5)                → 5 steps total
      //   .eachLevelLasting(30 s)  → each plateau holds for 30 seconds
      //   .separatedByRampsLasting → short linear ramp between steps (not instant jump)
      //   .startingFrom(5)         → first plateau = 5 users/sec (not 0)
      //
      // Resulting levels: 5 → 10 → 15 → 20 → 25 users/sec
      incrementUsersPerSec(5)
        .times(5)
        .eachLevelLasting(30.seconds)
        .separatedByRampsLasting(5.seconds)
        .startingFrom(5)
    )
  ).protocols(protocol)
   .assertions(
     // p95 response time must stay under 4 s across all load levels
     global.responseTime.percentile(95).lt(4000),
     // Max response time must not exceed 8 s even at the highest step (25/s)
     global.responseTime.max.lt(8000),
     // At least 98 % of requests must succeed
     global.successfulRequests.percent.gt(98.0)
   )
}
