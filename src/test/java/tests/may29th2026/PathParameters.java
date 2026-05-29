package tests.may29th2026;

import io.restassured.http.ContentType;
import net.datafaker.Faker;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class PathParameters {

    @Test(description = "Understanding the concept of Form parameters", priority = 1)
    public void handlePathParamters() {
        Faker f1 = new Faker();
        String jsonPayload = """
                {
                    "name":"%s",
                    "email":"%s",
                    "gender":"%s",
                    "status":"%s",
                    "phone":"%s"
                }
                """.formatted(f1.name().firstName(), f1.internet().emailAddress(), "male", "active", f1.phoneNumber().phoneNumber());
        int userID = given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer c7e6314abf3582dad8fd06305883a0d90c9c39561382dd4c03badd589821cce5")
                .body(jsonPayload)
                .when()
                .post("https://xpjhgxfthnjvkgftggik.supabase.co/functions/v1/rest-api/public/v2/users")
                .then()
                .extract().response().jsonPath().getInt("id");

        IO.println(userID);

        int actualUserID=given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer c7e6314abf3582dad8fd06305883a0d90c9c39561382dd4c03badd589821cce5")
                .pathParam("userID", userID) //Path Parameter
                .when()
                .get("https://xpjhgxfthnjvkgftggik.supabase.co/functions/v1/rest-api/public/v2/users/{userID}")
                .then()
                .extract().response().jsonPath().getInt("id");

        Assert.assertEquals(actualUserID,userID);
    }
}
