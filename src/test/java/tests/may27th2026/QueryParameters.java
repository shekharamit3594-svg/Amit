package tests.may27th2026;

import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import tests.may20th2025.BaseTest;
import tests.may25th2026.request.PatchAccountRequestPOJO;
import tests.may25th2026.request.RequestPOJO;
import tests.may25th2026.response.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasSize;

public class QueryParameters extends BaseTest {

    String accountNumber;
    @Test(description = "Perform Creation of Accounts", priority = 1)
    public void createAccount() {
        int randomBalance = faker.number().numberBetween(1000, 10000);

        while (randomBalance <1000)
        {
            randomBalance = randomBalance + faker.number().numberBetween(1000, 10000);
        }

        List<String> typesOfAccounts = Arrays.asList("personal", "business", "joint");

        //Picks a random number between 0 to 3 in this case
        int number = ThreadLocalRandom.current().nextInt(0, typesOfAccounts.size());
        String accountType = typesOfAccounts.get(number);

        RequestPOJO request = new RequestPOJO();

        request.setAccountHolderName(faker.name().firstName())
                .setInitialBalance(randomBalance)
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
                .spec(specBuilders.getRequestSpecification(request)) //Instead of writing so many lines of code for each and every request, we have stored everything in a spec builder
                .when()
                .post("/create-account") //Performing the POST API Call
                .then()
                .log().body()
                .spec(specBuilders.getResponseSpecification(200,request))
                .extract().response().as(ResponsePOJO.class); //Deserialization of data(Converting the JSON Response to JAVA Object)

        IO.println(response);

        accountNumber = response.getData().getAccountID();
    }

    @Test(description = "Viewing the Account Details",priority = 2)
    public void viewAccountDetails() {

        ViewAccountsPOJO response=given()
                .spec(specBuilders.getRequestSpecification())
                //.queryParam is used to pass the query parameters
                .queryParam("account_id", accountNumber)
        .when()
                .get("/view-account")
        .then()
                .spec(specBuilders.getResponseSpecification(200))
                .extract().response().as(ViewAccountsPOJO.class);

        Assert.assertEquals(response.getData().getAccountId(), accountNumber);
    }

    @Test(description = "Viewing Currency Details",priority = 3)
    public void viewCurrencyDetails() {

        ViewCurrenciesPOJO response=given()
                .spec(specBuilders.getRequestSpecification())
                .queryParam("include_inactive", true)
        .when()
                .get("/manage-currencies")
        .then()
                .spec(specBuilders.getResponseSpecification(200))
                .extract().response().as(ViewCurrenciesPOJO.class);

        List<String> currencyNames=response.getCurrencies().stream().map(s-> s.getName())
                .collect(Collectors.toList());

        IO.println(currencyNames);

    }

    @Test(description = "Performing the API Modifications",priority = 4)
    public void patchAccountDetails()
    {
        PatchAccountRequestPOJO request = new PatchAccountRequestPOJO();
        request.setAccountId(accountNumber)
                .setAccountHolderName(faker.name().firstName());

        ResponsePOJO response=given()
                .spec(specBuilders.getRequestSpecification(request))
                //.log().body() //Logs the request body onto the console
        .when()
                .patch("/patch-account")
        .then()
                .spec(specBuilders.getResponseSpecification(200))
                .extract().response().as(ResponsePOJO.class);

        IO.println(response);
    }

    @Test(description = "Viewing the Account Details",priority = 5)
    public void viewAccountDetails_All() {

        Map<String,Object> queryParams = new HashMap<>();
        queryParams.put("currency", "INR");
        queryParams.put("limit", 50);
        queryParams.put("sort_by", "account_holder_name");
        queryParams.put("order", "asc");

        ViewAllAccountsResponsePOJO response=given()
                .spec(specBuilders.getRequestSpecification())
                .queryParams(queryParams) //We will be passing all the query parameters in the form of map
                .when()
                .get("/list-accounts")
                .then()
                //Checks if there are exactly 50 records under the data node
                //.body("data", hasSize(50))
                .extract().response().as(ViewAllAccountsResponsePOJO.class);

        int actuallimit = queryParams.get("limit") instanceof Integer i? i:20;

        Assert.assertTrue(response.getData().size()<=actuallimit);
    }

}
