package tests.may25th2026;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import tests.may20th2025.BaseTest;
import tests.may25th2026.request.RequestPOJO;
import tests.may25th2026.response.ResponsePOJO;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

//Serialization --> Converting the JAVA Object to JSON
//Deserialization --> Converting the JSON to JAVA Object

//There are multiple libraries which supports the process of Serialization and Deserialization
//Few of them are jackson-databind, gson etc..
public class UnderstandingSerializationAndDeserialization extends BaseTest {

    String accountNumber = "";

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

//        String jsonPayload = """
//                    {
//                     "account_holder_name": "%s",
//                    "initial_balance": %d,
//                    "account_type": "%s",
//                    "currency": "%s",
//                    "email": "%s",
//                    "phone": "%s",
//                    "address_line1": "%s",
//                    "address_line2": "%s",
//                    "city": "%s",
//                    "state": "%s",
//                    "zip_code": "%s",
//                    "country": "%s"
//                    }
//                """.formatted(faker.name().firstName(), randomBalance, accountType, "INR", faker.internet().emailAddress(), faker.phoneNumber().phoneNumber(),
//                faker.address().streetAddress(), faker.address().secondaryAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), faker.address().country());

        ResponsePOJO response = given()
                .contentType(ContentType.JSON)
                .filter(new AllureRestAssured()) //This will be responsible for generating the allure reports
                .header("Authorization", System.getProperty("accessToken"))
                .body(request) //Request Body (Serialization --> Converting the JAVA Object to JSON)
                .when()
                .post("/create-account") //Performing the POST API Call
                .then()

                .statusCode(200)

                //Checks if all the given keys are present in the JSON Response
                .body("", allOf(hasKey("success"), hasKey("message"), hasKey("data")))

                //Checks if the message is of String data type or not
                .body("message", isA(String.class))

                //Checks if the success field is of Boolean data type or not
                .body("success", isA(Boolean.class))

                //Checks if the data type of the "data" is Object or not
                .body("data", isA(Object.class))

                //Validating the keys inside the data
                .body("data", allOf(hasKey("account_id"), hasKey("account_holder_name"), hasKey("account_type"),
                        hasKey("currency"), hasKey("email"), hasKey("phone"), hasKey("address_line1"), hasKey("address_line2"),
                        hasKey("city"), hasKey("state"), hasKey("zip_code"), hasKey("country"), hasKey("balance"), hasKey("created_at")))

                //Checks if the data type of the balance is Integer
                .body("data.balance", isA(Integer.class))
                .extract().response().as(ResponsePOJO.class); //Deserialization of data(Converting the JSON Response to JAVA Object)

        IO.println(response);

        accountNumber = response.getData().getAccountID();
    }


}
