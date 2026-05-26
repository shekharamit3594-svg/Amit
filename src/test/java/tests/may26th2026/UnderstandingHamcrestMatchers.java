package tests.may26th2026;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import tests.may20th2025.BaseTest;
import tests.may25th2026.RequestPOJO;
import tests.may25th2026.ResponsePOJO;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UnderstandingHamcrestMatchers extends BaseTest {

    String accountNumber = "";
    SpecBuilders specBuilders = new SpecBuilders();

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


}
