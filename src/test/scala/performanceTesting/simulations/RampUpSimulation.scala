package performanceTesting.simulations

import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._
import scala.concurrent.duration._

/**
 * PT-SIM-2 : Ramp-Up Simulation
 * ─────────────────────────────────────────────────────────────────────────────
 * Purpose
 *   Gradually increase concurrency to find the "knee of the curve" —
 *   the point where latency begins rising non-linearly relative to load.
 *   This is the most important simulation for capacity planning.
 *
 * Injection pattern: multi-phase ramp
 *   Phase 1 — 1 user for 5 s  (warm-up, avoids cold-start distortion)
 *   Phase 2 — idle 5 s         (pause before main ramp)
 *   Phase 3 — ramp 0 → 30 users over 60 s  (linear increase)
 *   Phase 4 — constant 10 req/s for 30 s   (sustain peak, measure stability)
 *
 *   Wall-clock total: ~100 seconds
 *
 * Key metrics to watch in the Gatling HTML report
 *   ● Response time percentiles — look for p95 "hockey stick" inflection
 *   ● Active users over time    — should track the injection model shape
 *   ● Requests/sec              — should plateau during phase 4
 *
 * Run command
 *   mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.RampUpSimulation
 * ─────────────────────────────────────────────────────────────────────────────
 */
class RampUpSimulation extends Simulation {

  val protocol = karateProtocol(
    "/public/v2/users" -> Nil
  )

  // Read-heavy: GET /users page 1 — the workhorse read scenario
  val readLoad = scenario("Read Load — GET /users (ramp-up)")
    .exec(karateFeature("classpath:performanceTesting/features/pt_GetUsers.feature@GetAllUsers"))

  setUp(
    readLoad.inject(
      // Phase 1: Single warm-up user — avoids JVM/GC cold-start skewing p99
      atOnceUsers(1),

      // Phase 2: Short idle gap before ramping — let the warm-up complete
      nothingFor(5.seconds),

      // Phase 3: Linear ramp from ~0 to 30 users over 60 seconds.
      // Gatling adds roughly 1 new user every 2 seconds (30 / 60 = 0.5/s).
      // Watch the response time graph — if it "elbows" sharply at a certain
      // user count, that count is your concurrency ceiling.
      rampUsers(30).during(60.seconds),

      // Phase 4: Short pause, then sustain at constant arrival rate.
      // constantUsersPerSec differs from rampUsers: it controls ARRIVAL RATE
      // (users/second) rather than concurrent users.  This is more realistic
      // for API endpoints where clients arrive independently.
      nothingFor(5.seconds),
      constantUsersPerSec(10).during(30.seconds)
    )
  ).protocols(protocol)
   .assertions(
     // 95th-percentile RT must stay under 3 s at peak load
     global.responseTime.percentile(95).lt(3000),
     // Mean RT should stay under 1.5 s across the full ramp
     global.responseTime.mean.lt(1500),
     // At least 99 % of requests must succeed (1 % error budget)
     global.successfulRequests.percent.gt(99.0)
   )
}
