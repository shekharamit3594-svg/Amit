package tests.may29th2026;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import tests.may20th2025.BaseTest;
import tests.may25th2026.request.RequestPOJO;
import tests.may25th2026.response.ResponsePOJO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;

/*
 * Topic: RequestLoggingFilter and ResponseLoggingFilter
 *
 * .log().body()          → prints to console (System.out)
 * .log().all()           → prints all details to console
 *
 * Filters give programmatic control over what gets logged and where:
 *   new RequestLoggingFilter(LogDetail.BODY)   ← logs request body to System.out
 *   new ResponseLoggingFilter(LogDetail.BODY)  ← logs response body to System.out
 *   new RequestLoggingFilter(LogDetail.BODY, printStream) ← redirect to any stream
 *
 * LogDetail enum maps 1-to-1 with the .log() shortcuts:
 *   LogDetail.BODY     → .log().body()
 *   LogDetail.ALL      → .log().all()
 *   LogDetail.HEADERS  → .log().headers()
 *   LogDetail.PARAMS   → .log().params()
 *   LogDetail.METHOD   → .log().method()
 *   LogDetail.URI      → .log().uri()
 *   LogDetail.STATUS   → .log().status()   (response only)
 *
 * Two approaches:
 *  1. Inline  — add filters directly in the given() chain (per-test control)
 *  2. Via SpecBuilders.withLogStream(stream) — call once in @BeforeClass; every
 *     subsequent spec-built request logs automatically (used in this class)
 */
public class UnderstandingLoggingFilters extends BaseTest {

    PrintStream logStream;
    String accountId;

    // -----------------------------------------------------------------------
    // Setup: open the log file and wire its PrintStream into SpecBuilders so
    // every spec-built request automatically writes its body to the file
    // -----------------------------------------------------------------------
    @BeforeClass(dependsOnMethods = "generateJWTToken")
    public void setupLogStream() throws IOException {
        File logDir = new File("src/test/resources/logs");
        logDir.mkdirs();

        // true = append mode; each run adds to the file rather than overwriting
        logStream = new PrintStream(new FileOutputStream("src/test/resources/logs/api-logs.log", true));
        specBuilders.withLogStream(logStream);

        IO.println("Log file → src/test/resources/logs/api-logs.log");
    }

    @AfterClass(alwaysRun = true)
    public void closeLogStream() {
        if (logStream != null) {
            logStream.flush();
            logStream.close();
        }
    }

    // -----------------------------------------------------------------------
    // Approach 1 — Inline filters added directly in the given() chain
    // Use this when you need logging only for specific tests
    // -----------------------------------------------------------------------
    @Test(description = "Create account — inline RequestLoggingFilter / ResponseLoggingFilter", priority = 1)
    public void createAccount_InlineFilters() {

        List<String> accountTypes = Arrays.asList("personal", "business", "joint");
        String accountType = accountTypes.get(ThreadLocalRandom.current().nextInt(accountTypes.size()));

        RequestPOJO request = new RequestPOJO()
                .setAccountHolderName(faker.name().firstName())
                .setInitialBalance(faker.number().numberBetween(1000, 10000))
                .setAccountType(accountType)
                .setCurrency("INR")
                .setEmail(faker.internet().emailAddress())
                .setPhone(faker.phoneNumber().phoneNumber())
                .setAddressLine1(faker.address().streetAddress())
                .setAddressLine2(faker.address().secondaryAddress())
                .setCity(faker.address().city())
                .setState(faker.address().state())
                .setZipCode(faker.address().zipCode())
                .setCountry(faker.address().country());

        ResponsePOJO response = given()
                .spec(specBuilders.getRequestSpecification(request))
                .when()
                .post("/create-account")
                .then()
                .spec(specBuilders.getResponseSpecification(200, request))
                .extract().response().as(ResponsePOJO.class);

        accountId = response.getData().getAccountID();
        IO.println("Account Created: " + accountId);
    }

    // -----------------------------------------------------------------------
    // Approach 2 — Via SpecBuilders.withLogStream()
    // Call specBuilders.withLogStream(printStream) once (e.g. in @BeforeClass)
    // and every subsequent spec produced by SpecBuilders will log automatically.
    // No inline filter calls needed here.
    // -----------------------------------------------------------------------
    @Test(description = "View account — logging via SpecBuilders.withLogStream()", priority = 2)
    public void viewAccount_ViaSpecBuilders() {
        Assert.assertNotNull(accountId, "Account ID must be set by createAccount test");

        given()
                .spec(specBuilders.getRequestSpecification())
                .queryParam("account_id", accountId)
                .when()
                .get("/view-account")
                .then()
                .spec(specBuilders.getResponseSpecification(200))
                .extract().response().as(ResponsePOJO.class);

        IO.println("View account completed");
    }

    // -----------------------------------------------------------------------
    // Cleanup: delete the account created in priority-1 test
    // -----------------------------------------------------------------------
    @Test(description = "Delete account", priority = 3)
    public void deleteAccount() {
        Assert.assertNotNull(accountId, "Account ID must be set by createAccount test");

        given()
                .spec(specBuilders.getRequestSpecification())
                .queryParam("account_id", accountId)
                .when()
                .delete("/delete-account")
                .then()
                .spec(specBuilders.getResponseSpecification(200));

        IO.println("Account deleted: " + accountId);
      }
}
