package tests.may18th2026;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class UnderstandingHTTPMethods {

    Faker faker = new Faker();
    @BeforeClass
    public void setup() {

        //We are declaring our Base URL and storing it in baseURI
        RestAssured.baseURI="https://qnajbqxmpbmndnwdqswq.supabase.co/functions/v1";
    }

    @Test(description = "Performing Create Account Operation --> Post",priority = 1)
    public void createAccount() {

        //In Rest Assured, we can write the scripts in two ways:
        //1. BDD Format
        //2. Non BDD Format

        //BDD Format:
        //given() ---> This is where the pre-requisite operations of the API are performed
        //when() --> This is where the actual API call is performed
        //then() --> This is where we validate the response details and required activities post the API Call

        int randomBalance = faker.number().numberBetween(1000,10000);
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
                """.formatted(faker.name().firstName(),randomBalance,"personal","INR",faker.internet().emailAddress(),faker.phoneNumber().phoneNumber(),
                        faker.address().streetAddress(),faker.address().secondaryAddress(),faker.address().city(),faker.address().state(),faker.address().zipCode(),faker.address().country());

        Response response=given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZmVmNzNmNS05YTA4LTRmMmUtOGIzYy05M2UwMGYwM2EwMDAiLCJjbGllbnRfaWQiOiJqd3Rfazg3Sk9BaEd4MDA5Qk12TyIsIm5hbWUiOiJzcHJhaHVsMzMzQGdtYWlsLmNvbSIsInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiaWF0IjoxNzc5MjAxNDUxLCJleHAiOjE3NzkyMDUwNTF9.QLapbEsduG2yhou1ztFppCg5Ww-75EQP8O1RKFrh_PE")
                .body(jsonPayload) //Request Body
        .when()
                .post("/create-account") //Performing the POST API Call
        .then()
                .extract().response();

        //response.jsonPath() is used to iterate/traverse across the JSON body of the response
        boolean accountCreationSuccess=response.jsonPath().getBoolean("success");
        Assert.assertTrue(accountCreationSuccess);

        //We are checking the message while creating the account
        String message=response.jsonPath().getString("message");
        Assert.assertEquals(message,"Account created successfully");

        //We are fetching the account number details from the response
        String accountNumber=response.jsonPath().getString("data.account_id");
        IO.println("Account Number: "+accountNumber);
        System.setProperty("Account Number",accountNumber);
    }
}
