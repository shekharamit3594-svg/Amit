package tests.may19th2026;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ContinuationOfUnderstandingHTTPMethods {

    @BeforeClass
    public void setup()
    {
        RestAssured.baseURI="https://qnajbqxmpbmndnwdqswq.supabase.co/functions/v1";
    }

    @Test(description = "Viewing the Account Details",priority = 1)
    public void viewAccountDetails(){

       Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZmVmNzNmNS05YTA4LTRmMmUtOGIzYy05M2UwMGYwM2EwMDAiLCJjbGllbnRfaWQiOiJqd3Rfazg3Sk9BaEd4MDA5Qk12TyIsIm5hbWUiOiJzcHJhaHVsMzMzQGdtYWlsLmNvbSIsInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiaWF0IjoxNzc5MjAxNDUxLCJleHAiOjE3NzkyMDUwNTF9.QLapbEsduG2yhou1ztFppCg5Ww-75EQP8O1RKFrh_PE")
                .queryParam("account_id",System.getProperty("Account Number")) //Syntax for passing the query parameter
        .when()
                .get("/view-account")
        .then()
                .extract().response();

        Assert.assertTrue(response.jsonPath().getBoolean("success"));
    }

    @Test(description = "Modifying the Account Details",priority = 2)
    public void modifyAccountDetails(){

        Faker faker = new Faker();
        String jsonPayload= """
                {
                    "account_id": "%s",
                    "email": "%s",
                    "phone": "%s"
                }
                """.formatted(System.getProperty("Account Number"),faker.internet().emailAddress(),faker.phoneNumber().phoneNumber());
       Response response = given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZmVmNzNmNS05YTA4LTRmMmUtOGIzYy05M2UwMGYwM2EwMDAiLCJjbGllbnRfaWQiOiJqd3Rfazg3Sk9BaEd4MDA5Qk12TyIsIm5hbWUiOiJzcHJhaHVsMzMzQGdtYWlsLmNvbSIsInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiaWF0IjoxNzc5MjAxNDUxLCJleHAiOjE3NzkyMDUwNTF9.QLapbEsduG2yhou1ztFppCg5Ww-75EQP8O1RKFrh_PE")
                .body(jsonPayload)
            .when()
                .patch("/patch-account")
            .then()
                .extract().response();

       IO.println(response.getBody().asPrettyString());
       Assert.assertTrue(response.jsonPath().getBoolean("success"));

       String message=response.jsonPath().getString("message");
       Assert.assertEquals(message,"Account patched successfully");
    }

    @Test(description = "Deleting the Account Details",priority = 3)
    public void deleteAccountDetails(){

        String jsonPayload= """
                {
                    "account_id": "%s"
                }
        """.formatted(System.getProperty("Account Number"));

       Response response = given()
            .contentType(ContentType.JSON)
            .body(jsonPayload)
            .header("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZmVmNzNmNS05YTA4LTRmMmUtOGIzYy05M2UwMGYwM2EwMDAiLCJjbGllbnRfaWQiOiJqd3Rfazg3Sk9BaEd4MDA5Qk12TyIsIm5hbWUiOiJzcHJhaHVsMzMzQGdtYWlsLmNvbSIsInJvbGUiOiJhdXRoZW50aWNhdGVkIiwiaWF0IjoxNzc5MjAxNDUxLCJleHAiOjE3NzkyMDUwNTF9.QLapbEsduG2yhou1ztFppCg5Ww-75EQP8O1RKFrh_PE")
        .when()
                .delete("/delete-account")
        .then()
                .extract().response();

       IO.println(response.getBody().asPrettyString());

       Assert.assertTrue(response.jsonPath().getBoolean("success"));

    }
}
