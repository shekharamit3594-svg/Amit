package tests;

import framework.constants.StatusCodes;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import pojo.response.currencies.ListOfAllCurrencies;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static io.restassured.RestAssured.given;

public class CurrencyTest extends BaseTest {

    @Test(description = "Viewing the List of all the currencies",priority = 1)
    public void viewAllCurrencies() {

        ListOfAllCurrencies response=given()
                .contentType(ContentType.JSON)
                .header("Authorization","Bearer "+testUtil.getData("AccessToken"))
        .when()
                .get(bankingEndPoints.getListOfCurrencies())
        .then()
                .statusCode(StatusCodes.SUCCESS.getStatusCode())
                .extract().response().as(ListOfAllCurrencies.class);

        List<ListOfAllCurrencies.Currencies> currencies=response.getCurrencies();

        //Picking a random Currency Code from the response
        String currencyValue=currencies.get(ThreadLocalRandom.current().nextInt(0,currencies.size())).getCode();
        System.setProperty("Currency",currencyValue);
    }
}
