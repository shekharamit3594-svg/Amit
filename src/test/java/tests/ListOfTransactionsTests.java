package tests;

import framework.constants.StatusCodes;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.response.listOfTransactions.ListOfTransactionsResponse;

import static io.restassured.RestAssured.given;

public class ListOfTransactionsTests extends BaseTest {

    @Test(description = "Displaying the List Of Transactions",priority = 1)
    public void displayListOfTransactions() {

        ListOfTransactionsResponse response=given()
                .spec(depositAmountSpecBuilder.getDepositOrWithdrawAmountSpecBuilder())
                .queryParam("account_id",System.getProperty("Account Number"))
                .queryParam("page",1)
                .queryParam("limit",20)
        .when()
                .get(bankingEndPoints.getListOfTransactions())
        .then()
                .statusCode(StatusCodes.SUCCESS.getStatusCode())
                .extract().response().as(ListOfTransactionsResponse.class);

        Assert.assertTrue(response.getSuccess());
    }
}
