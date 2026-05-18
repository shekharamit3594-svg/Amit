package tests.may18th2026;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import net.datafaker.Faker;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class UnderstandingHTTPMethods {

    Faker faker = new Faker();
    @BeforeClass
    public void setup() {
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
                """.formatted(faker.name().firstName(),randomBalance,"Savings","INR",faker.internet().emailAddress(),faker.phoneNumber().phoneNumber(),
                        faker.address().streetAddress(),faker.address().secondaryAddress(),faker.address().city(),faker.address().state(),faker.address().zipCode(),faker.address().country());
        String response=given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZmVmNzNmNS05YTA4LTRmMmUtOGIzYy05M2UwMGYwM2EwMDAiLCJjbGllbnRfaWQiOiJqd3RfM2diYUhYUTB6OE9Ic0ZidyIsIm5hbWUiOiJzcHJhaHVsMzMzQGdtYWlsLmNvbSIsInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiaWF0IjoxNzc5MTE1NDc5LCJleHAiOjE3NzkxMTkwNzl9.JEVLDVwsrW8hgbYAKdlof4YIakItMw_s3yf0Rwr9Dws")
                .body(jsonPayload)
        .when()
                .post("/create-account")
        .then()
                .extract().response().prettyPrint();

        IO.println(response);
    }
}
