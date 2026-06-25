package performanceTesting.simulations

import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._
import scala.concurrent.duration._

/**
 * PT-SIM-7 : Stress Test Simulation
 * ─────────────────────────────────────────────────────────────────────────────
 * Drives pt_StressTest.feature with maximum-pressure injection patterns.
 *
 * WHAT MAKES THIS DIFFERENT FROM LoadTestSimulation
 * ──────────────────────────────────────────────────
 * LoadTestSimulation              │  StressTestSimulation (this)
 * ────────────────────────────────┼────────────────────────────────────────────
 * 50 users, ramped over 30s       │ 200 users, ramped over 10s (much faster)
 * Think time: 1000ms in feature   │ Think time: 50ms in feature (machine speed)
 * SLA: 3s read, 5s write          │ SLA: 10s (relaxed — degradation is expected)
 * Error rate target: < 1%         │ Error rate target: < 15% (some errors OK)
 * Goal: validate SLA              │ Goal: find breaking point
 * Stops at first assertion fail   │ Continues through errors to measure recovery
 * ──────────────────────────────────────────────────────────────────────────────
 *
 * The 50ms think time in pt_StressTest.feature means:
 *   200 users × (1 req / 0.05s think) = 4000 req/s theoretical max
 * In practice, the server will become the bottleneck well before that.
 * That's the point — we want to find and document where it chokes.
 *
 * HOW TO INTERPRET THE GATLING REPORT FOR STRESS TESTS
 * ─────────────────────────────────────────────────────
 *   Look at "Requests/sec over time":
 *     ● If throughput PLATEAUS before user count peaks → server is saturated
 *     ● If throughput DROPS under high user count → active queuing/rejection
 *   Look at "Response Time Distribution":
 *     ● The tail (p99/max) widening sharply → lock contention or GC pressure
 *   Look at "% of failed requests over time":
 *     ● If errors START at a specific user count → that's your breaking point
 *
 * Run command
 *   mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.StressTestSimulation
 * ─────────────────────────────────────────────────────────────────────────────
 */
class StressTestSimulation extends Simulation {

  val protocol = karateProtocol(
    "/public/v2/users" -> Nil
  )

  // Read stress: high-frequency GET at near-machine speed
  val readStress = scenario("Stress Test — Read (max rate GET)")
    .exec(karateFeature("classpath:performanceTesting/features/pt_StressTest.feature@ReadStress"))

  // Write stress: high-frequency POST to probe lock contention
  val writeStress = scenario("Stress Test — Write (max rate POST)")
    .exec(karateFeature("classpath:performanceTesting/features/pt_StressTest.feature@WriteStress"))

  // Recovery probe: after stress, send normal-paced requests to measure recovery speed
  val recoveryProbe = scenario("Stress Recovery — POST-spike normal pace")
    .exec(karateFeature("classpath:performanceTesting/features/pt_StressTest.feature@RecoveryCheck"))

  setUp(
    // Phase 1: Ramp FAST to 150 read users over 10 seconds (15 users/second arrival rate)
    readStress.inject(
      rampUsers(150).during(10.seconds),
      // Sustain peak pressure for 30 seconds — enough to fill thread pools and expose leaks
      constantUsersPerSec(50).during(30.seconds)
    ),
    // Phase 2: Add write stress 5 seconds into the read ramp (overlapping pressure)
    writeStress.inject(
      nothingFor(5.seconds),
      rampUsers(50).during(10.seconds),
      constantUsersPerSec(15).during(30.seconds)
    ),
    // Phase 3: Recovery probe starts after peak, uses 2s think time (normal pace)
    recoveryProbe.inject(
      nothingFor(50.seconds),          // wait for peak to end
      constantUsersPerSec(5).during(30.seconds)   // normal load to measure recovery
    )
  ).protocols(protocol)
   .assertions(
     // Under extreme stress we only assert that the server doesn't completely melt down
     // Max RT < 15s — if any request hangs longer, something is fundamentally broken
     global.responseTime.max.lt(15000),
     // Error rate < 20% — stress tests accept significant error rates
     global.successfulRequests.percent.gt(80.0)
   )
}
