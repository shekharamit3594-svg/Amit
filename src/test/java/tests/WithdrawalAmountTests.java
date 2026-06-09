package tests;

import framework.constants.StatusCodes;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.request.withdraw.WithdrawAmountRequest;
import pojo.response.withdraw.WithdrawAmountResponse;

import static io.restassured.RestAssured.given;

public class WithdrawalAmountTests extends BaseTest {

    @Test(description = "Withdraw the Amount", priority = 1)
    public void performWithdrawalOfAmount() {

        WithdrawAmountRequest request = new WithdrawAmountRequest();

        request.setAccountId(System.getProperty("Account Number"))
                .setAmount(faker.number().numberBetween(1000, 5000));

        String withdrawAmountMessage = given()
                .spec(depositAmountSpecBuilder.getDepositOrWithdrawAmountSpecBuilder(request))
                .when()
                .post(bankingEndPoints.getWithdrawAmount())
                .then()
                .statusCode(StatusCodes.SUCCESS.getStatusCode())
                .extract().response().as(WithdrawAmountResponse.class).getMessage();

        Assert.assertEquals(withdrawAmountMessage, "Withdrawal successful");
    }
}
