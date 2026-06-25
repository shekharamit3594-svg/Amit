package performanceTesting.simulations

import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._
import scala.concurrent.duration._

/**
 * PT-SIM-3 : Spike Test Simulation
 * ─────────────────────────────────────────────────────────────────────────────
 * Purpose
 *   Verify the system survives a sudden, extreme burst of traffic — the kind
 *   caused by a flash sale, viral post, batch cron firing unexpectedly, or a
 *   DDoS-adjacent surge.  Unlike a ramp test (gradual increase), a spike is
 *   instantaneous: the server has no time to scale-out.
 *
 * Traffic shape
 *   ┌─────────────────────────────────────────────────┐
 *   │  Baseline (5/s)  │  SPIKE (50 at once)  │  Recover (5/s)  │
 *   │  ──────────────  │  ──────────────────  │  ─────────────  │
 *   │     20 s         │       15 s           │      20 s       │
 *   └─────────────────────────────────────────────────┘
 *   Spike is 10× the baseline: server must not cascade-fail.
 *
 * What to look for in the report
 *   ● Error rate during spike — should stay below the 10 % threshold
 *   ● Recovery time — how quickly does p95 RT return to baseline after spike?
 *   ● Max response time — if it exceeds 10 s, circuit breakers may be needed
 *
 * Run command
 *   mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.SpikeTestSimulation
 * ─────────────────────────────────────────────────────────────────────────────
 */
class SpikeTestSimulation extends Simulation {

  val protocol = karateProtocol(
    "/public/v2/users" -> Nil
  )

  // Write load — POST /users.
  // Write operations are more sensitive to spikes than reads because they
  // acquire locks, write to disk, and invalidate caches simultaneously.
  val writeLoad = scenario("Write Spike — POST /users (spike test)")
    .exec(karateFeature("classpath:performanceTesting/features/pt_CreateUser.feature@CreateUser"))

  setUp(
    writeLoad.inject(
      // ── Baseline phase (20 s) ────────────────────────────────────────────
      // 5 users/sec for 20 seconds — establishes a "normal" operating baseline.
      // Gather these metrics as your pre-spike reference point.
      constantUsersPerSec(5).during(20.seconds),

      // ── Spike (instantaneous) ────────────────────────────────────────────
      // Inject 50 users all at once — 10× the baseline arrival rate.
      // atOnceUsers() fires them at the exact same millisecond: the hardest
      // possible scenario for a server to absorb (no buffering, no queue).
      nothingFor(2.seconds),
      atOnceUsers(50),

      // ── Sustain spike (15 s) ─────────────────────────────────────────────
      // Hold the spike long enough for the server to either:
      //   (a) Handle it gracefully — error rate stays low
      //   (b) Trip a circuit breaker — 503s start appearing
      //   (c) Fall over — connection timeouts spike to 100 %
      nothingFor(15.seconds),

      // ── Recovery phase (20 s) ────────────────────────────────────────────
      // Return to baseline load and observe how quickly the system recovers.
      // A healthy system should return to baseline latency within 1–2 seconds.
      // Slow recovery indicates connection pool exhaustion or GC pressure.
      rampUsers(5).during(20.seconds)
    )
  ).protocols(protocol)
   .assertions(
     // During a spike we accept higher latency — max must still stay under 10 s
     global.responseTime.max.lt(10000),
     // Error rate must stay under 10 % even at the peak of the spike
     global.successfulRequests.percent.gt(90.0)
   )
}
