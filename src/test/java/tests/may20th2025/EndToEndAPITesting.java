package tests.may20th2025;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

//Hamcrest Library is used to perform the response validations as per the requirement. It is inbuilt library of rest assured
//If we want to use the latest version of Hamcrest library then only we explicitly add it in the pom.xml file

public class EndToEndAPITesting extends BaseTest {

    String accountNumber="";
    @Test(description = "Perform Creation of Accounts",priority = 1)
    public void createAccount(){
        int randomBalance = faker.number().numberBetween(1000,10000);

        List<String> typesOfAccounts = Arrays.asList("personal","business","joint");

        //Picks a random number between 0 to 3 in this case
        int number=ThreadLocalRandom.current().nextInt(0,typesOfAccounts.size());
        String accountType = typesOfAccounts.get(number);

        String jsonPayload = """ 
                    {
                     "account_holder_name": "%s",
                    "initial_balance": %d,
                    "account_type": "%s",
                    "currency": "%s",
                    "email": "%s",
                    "phone": "%s",
                    "address_line1": "%s",
                    "address_line2": "%s",
                    "city": "%s",
                    "state": "%s",
                    "zip_code": "%s",
                    "country": "%s" 
                    }
                """.formatted(faker.name().firstName(),randomBalance,accountType,"INR",faker.internet().emailAddress(),faker.phoneNumber().phoneNumber(),
                faker.address().streetAddress(),faker.address().secondaryAddress(),faker.address().city(),faker.address().state(),faker.address().zipCode(),faker.address().country());

        Response response=given()
                .contentType(ContentType.JSON)
                .header("Authorization",System.getProperty("accessToken"))
                .body(jsonPayload) //Request Body
                .when()
                .post("/create-account") //Performing the POST API Call
                .then()

                .statusCode(200)

                //Checks if all the given keys are present in the JSON Response
                .body("",allOf(hasKey("success"),hasKey("message"),hasKey("data")))

                //Checks if the message is of String data type or not
                .body("message",isA(String.class))

                //Checks if the success field is of Boolean data type or not
                .body("success",isA(Boolean.class))

                //Checks if the data type of the "data" is Object or not
                .body("data",isA(Object.class))

                //Validating the keys inside the data
                .body("data",allOf(hasKey("account_id"),hasKey("account_holder_name"),hasKey("account_type"),
                        hasKey("currency"),hasKey("email"),hasKey("phone"),hasKey("address_line1"),hasKey("address_line2"),
                        hasKey("city"),hasKey("state"),hasKey("zip_code"),hasKey("country"),hasKey("balance"),hasKey("created_at")))

                //Checks if the data type of the balance is Integer
                .body("data.balance",isA(Integer.class))
                .extract().response();

        IO.println(response.getBody().asPrettyString());

        accountNumber=response.jsonPath().getString("data.account_id");
    }

    @Test(description = "Viewing the Account Details",priority = 2)
    public void viewAccountDetails(){

        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization",System.getProperty("accessToken"))
                .queryParam("account_id",accountNumber) //Syntax for passing the query parameter
                .when()
                .get("/view-account")
                .then()
                .statusCode(200)
                .extract().response();

        Assert.assertTrue(response.jsonPath().getBoolean("success"));
    }

    @Test(description = "Modifying the Account Details",priority = 3)
    public void modifyAccountDetails(){

        Faker faker = new Faker();
        String jsonPayload= """
                {
                    "account_id": "%s",
                    "email": "%s",
                    "phone": "%s"
                }
                """.formatted(accountNumber,faker.internet().emailAddress(),faker.phoneNumber().phoneNumber());
        Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization",System.getProperty("accessToken"))
                .body(jsonPayload)
                .when()
                .patch("/patch-account")
                .then()
                .statusCode(200)
                .extract().response();

        IO.println(response.getBody().asPrettyString());
        Assert.assertTrue(response.jsonPath().getBoolean("success"));

        String message=response.jsonPath().getString("message");
        Assert.assertEquals(message,"Account patched successfully");
    }

    @Test(description = "Deleting the Account Details",priority = 4)
    public void deleteAccountDetails(){

        String jsonPayload= """
                {
                    "
                    account_id": "%s"
                }
        """.formatted(accountNumber);

        Response response = given()
                .contentType(ContentType.JSON)
                .body(jsonPayload)
                .header( "Authorization",System.getProperty("accessToken"))
                .when()
                .delete("/delete-account")
                .then()
                .log().ifValidationFails() //Log the response onto the console only when the assertion fails
                .statusCode(204)
                .extract().response();

        IO.println(response.getBody().asPrettyString());

        Assert.assertTrue(response.jsonPath().getBoolean("success"));

    }
}
