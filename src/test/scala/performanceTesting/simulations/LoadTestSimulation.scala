package performanceTesting.simulations

import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._
import scala.concurrent.duration._

/**
 * PT-SIM-6 : Load Test Simulation
 * ─────────────────────────────────────────────────────────────────────────────
 * Drives pt_LoadTest.feature with realistic concurrent user count.
 *
 * WHAT MAKES THIS DIFFERENT FROM BaselineSimulation
 * ──────────────────────────────────────────────────
 * BaselineSimulation              │  LoadTestSimulation (this)
 * ────────────────────────────────┼────────────────────────────────────────────
 * 10 users, atOnceUsers (cold)    │ 50 users, ramped (realistic warm-up)
 * Health check only (1 step)      │ Read + Write scenarios (mixed workload)
 * No think time in feature        │ Feature has karate.pause(1000) think time
 * No SLA assertion in feature     │ Feature asserts elapsed < 3000ms per request
 * Measures: "does it work?"       │ Measures: "is it fast enough under real load?"
 * ──────────────────────────────────────────────────────────────────────────────
 *
 * The think time in pt_LoadTest.feature means 50 virtual users DO NOT produce
 * 50 req/s.  With 1000ms think time, each user fires ~1 req/s, so:
 *   50 users × 1 req/s = ~50 req/s peak throughput
 * This is intentional — it models 50 real users pacing themselves, not bots.
 *
 * Run command
 *   mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.LoadTestSimulation
 * ─────────────────────────────────────────────────────────────────────────────
 */
class LoadTestSimulation extends Simulation {

  val protocol = karateProtocol(
    "/public/v2/users" -> Nil
  )

  // Read workload: GET /users with think time + SLA assertion (from feature file)
  val readScenario = scenario("Load Test — Read (GET /users)")
    .exec(karateFeature("classpath:performanceTesting/features/pt_LoadTest.feature@ReadLoad"))

  // Write workload: POST /users with think time + write SLA assertion
  val writeScenario = scenario("Load Test — Write (POST /users)")
    .exec(karateFeature("classpath:performanceTesting/features/pt_LoadTest.feature@WriteLoad"))

  // Mixed workload: 70% reads, 30% writes (realistic API traffic distribution)
  setUp(
    // Read traffic: ramp to 35 users over 30s, sustain for 60s
    readScenario.inject(
      rampUsers(35).during(30.seconds),
      constantUsersPerSec(7).during(60.seconds)
    ),
    // Write traffic: ramp to 15 users over 30s, sustain for 60s
    writeScenario.inject(
      nothingFor(10.seconds),         // stagger writes slightly after reads warm up
      rampUsers(15).during(30.seconds),
      constantUsersPerSec(3).during(60.seconds)
    )
  ).protocols(protocol)
   .assertions(
     // p95 response time for ALL requests (reads + writes combined) < 5s
     global.responseTime.percentile(95).lt(5000),
     // Mean response time < 2s (reads should pull this down)
     global.responseTime.mean.lt(2000),
     // At least 99% of requests must succeed in a load test
     global.successfulRequests.percent.gt(99.0)
   )
}
