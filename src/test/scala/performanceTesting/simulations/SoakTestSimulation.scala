package performanceTesting.simulations

import com.intuit.karate.gatling.PreDef._
import io.gatling.core.Predef._
import scala.concurrent.duration._

/**
 * PT-SIM-4 : Soak Test Simulation  (Endurance Test)
 * ─────────────────────────────────────────────────────────────────────────────
 * Purpose
 *   Detect memory leaks, connection-pool exhaustion, GC pressure, and
 *   gradual performance degradation by sustaining moderate load for a long
 *   period.  A soak test reveals bugs that don't appear in short load tests
 *   because they only manifest after thousands of requests.
 *
 * Classic soak-test failure patterns
 *   ● Memory leak         — heap grows steadily, GC pauses lengthen, eventually OOM
 *   ● Connection leak     — DB connections not returned to pool, 503s increase over time
 *   ● File handle leak    — log files not rotated, disk fills, writes start failing
 *   ● Cache bloat         — in-memory cache grows unbounded, eviction starts thrashing
 *
 * Why the CRUD lifecycle scenario?
 *   The full Create → Read → Update → Delete cycle exercises all four DB operations.
 *   The DELETE step is mandatory in a soak test — without it, each iteration adds
 *   a new row to the database.  Over 5 minutes × 20 users that's thousands of stale
 *   rows, which slow down subsequent GET queries and invalidate your latency data.
 *
 * Duration note
 *   This demo runs for 5 minutes.  In real environments, extend to 4–8 hours
 *   (or overnight) to expose slower leaks.  Update the constants below:
 *     val RAMP_DURATION    = 30.seconds  →  2.minutes
 *     val SUSTAIN_DURATION = 5.minutes   →  8.hours
 *
 * Run command
 *   mvn gatling:test -Dgatling.simulationClass=performanceTesting.simulations.SoakTestSimulation
 * ─────────────────────────────────────────────────────────────────────────────
 */
class SoakTestSimulation extends Simulation {

  // ── Tunable constants ─────────────────────────────────────────────────────
  // Change these to scale the soak duration without touching the logic below.
  val RAMP_DURATION    = 30.seconds   // time to reach working load from 0
  val SUSTAIN_DURATION = 5.minutes    // how long to hold steady state
  val PEAK_USERS       = 20           // concurrent virtual users at steady state
  val ARRIVAL_RATE     = 5            // users/second during sustained phase

  val protocol = karateProtocol(
    "/public/v2/users" -> Nil
  )

  // Full CRUD lifecycle — each user creates, reads, updates, then deletes a record.
  val lifecycleLoad = scenario("CRUD Lifecycle Soak — PT-4 (5 min endurance)")
    .exec(karateFeature("classpath:performanceTesting/features/pt_UserLifecycle.feature@UserLifecycle"))

  setUp(
    lifecycleLoad.inject(
      // Ramp up over 30 s to avoid a cold-start spike distorting baseline metrics.
      // Gradual ramp also gives the server time to warm JVM JIT caches.
      rampUsers(PEAK_USERS).during(RAMP_DURATION),

      // Sustain: constant arrival rate for the full soak duration.
      // constantUsersPerSec vs constantConcurrentUsers:
      //   constantUsersPerSec  → controls arrival rate (open model, more realistic)
      //   constantConcurrentUsers → controls active users at any moment (closed model)
      // For soak tests the open model is preferred — it more accurately models
      // independent API clients who don't wait for each other.
      constantUsersPerSec(ARRIVAL_RATE).during(SUSTAIN_DURATION)
    )
  ).protocols(protocol)
   .assertions(
     // Mean RT must not drift upward — if it creeps up 10 % per minute,
     // that signals a gradual resource leak (connection pool, heap, etc.)
     global.responseTime.mean.lt(2000),
     // p99 must stay under 5 s for the entire duration
     global.responseTime.percentile(99).lt(5000),
     // Error rate must stay under 1 % — soak tests demand higher reliability
     // than spike tests because sustained errors indicate a structural problem
     global.successfulRequests.percent.gt(99.0)
   )
}
