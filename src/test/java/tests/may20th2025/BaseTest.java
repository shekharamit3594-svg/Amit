package tests.may20th2025;

import framework.PathUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.util.Optional;

import static io.restassured.RestAssured.given;

public class BaseTest {

    protected Faker faker;
    @BeforeClass
    public void generateJWTToken(){

        faker = new Faker();

        String bearerToken = Optional.ofNullable(System.getProperty("accessToken")).orElseGet(() -> {
            String loginPayload = """
                    {
                        "email" : "%s",
                        "password" : "%s"
                    }
                    """.formatted("sprahul333@gmail.com", PathUtils.decodeData("MjA0MnJhaHVANjY3MlA="));

            Response response = given()
                    .contentType(ContentType.JSON)
                    .header("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFuYWpicXhtcGJtbmRud2Rxc3dxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQyMDA3OTMsImV4cCI6MjA3OTc3Njc5M30.gu96y5-EvK1a1BQWxNvQpJ-8st-fP_zLFsuVVtGif9c")
                    .body(loginPayload)
                    .when()
                    .post("https://qnajbqxmpbmndnwdqswq.supabase.co/auth/v1/token?grant_type=password")
                    .then()
                    .statusCode(200) //We are checking whether the status code is 200 or not, if it is not matching it throws an assertion error
                    .extract().response();

            //Helps us fetch the status code of the response
            IO.println(response.statusCode());

            String accessToken = response.jsonPath().getString("access_token");

            return accessToken;
        });

       System.setProperty("accessToken", "Bearer "+bearerToken);
    }

    @BeforeMethod
    public void setUpData()
    {
        RestAssured.baseURI="https://qnajbqxmpbmndnwdqswq.supabase.co/functions/v1";
    }
}
