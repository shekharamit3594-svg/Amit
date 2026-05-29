package tests.may29th2026;

import org.testng.annotations.Test;
import tests.may20th2025.BaseTest;
import tests.may25th2026.response.ResponsePOJO;
import tests.may25th2026.response.ViewCurrenciesPOJO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class FormParameters extends BaseTest {

    //Form Parameter:
    //Instead of maintaining the payload in JSON, we will maintain in the form format

    List<String> currencyNames;

    @Test(description = "Fetching the list of currencies", priority = 1)
    public void fetchCurrencies() {
        ViewCurrenciesPOJO response = given()
                .spec(specBuilders.getRequestSpecification())
                .queryParam("include_inactive", true)
                .when()
                .get("/manage-currencies")
                .then()
                .spec(specBuilders.getResponseSpecification(200))
                .extract().response().as(ViewCurrenciesPOJO.class);

        currencyNames = response.getCurrencies().stream().map(s -> s.getCode())
                .collect(Collectors.toList());
    }

    @Test(description = "Create Account Using Form Parameters", priority = 2, dependsOnMethods = "fetchCurrencies")
    public void createAccount() {

        int randomBalance = faker.number().numberBetween(1000, 10000);

        while (randomBalance < 1000) {
            randomBalance = randomBalance + faker.number().numberBetween(1000, 10000);
        }

        List<String> typesOfAccounts = Arrays.asList("personal", "business", "joint");

        //Picks a random number between 0 to 3 in this case
        int number = ThreadLocalRandom.current().nextInt(0, typesOfAccounts.size());
        String accountType = typesOfAccounts.get(number);

        Map<String, Object> requestData = new HashMap<>();
        requestData.put("account_holder_name", faker.name().firstName());
        requestData.put("account_type", accountType);
        requestData.put("initial_balance", randomBalance);
        requestData.put("currency", currencyNames.get(ThreadLocalRandom.current().nextInt(0, currencyNames.size())));
        requestData.put("email", faker.internet().emailAddress());
        requestData.put("phone", faker.phoneNumber().phoneNumber());
        requestData.put("address_line1", faker.address().streetAddress());
        requestData.put("address_line2", faker.address().secondaryAddress());
        requestData.put("city", faker.address().city());
        requestData.put("state", faker.address().state());
        requestData.put("zip_code", faker.address().zipCode());
        requestData.put("country", faker.address().country());

        ResponsePOJO response=given()
                //.formParams(requestData) --> if we want to pass the form parameters directly
                .spec(specBuilders.getRequestSpecification_URLEncoded(requestData))
        .when()
                .post("/create-account")
        .then()
                //.log().all() //Logs all the headers, body, url, and the complete response details
                .spec(specBuilders.getResponseSpecification_FormParameters(200,requestData))
                .extract().response().as(ResponsePOJO.class);

    }
}
