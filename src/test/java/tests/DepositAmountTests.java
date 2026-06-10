package tests;

import framework.constants.StatusCodes;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.request.deposit.DepositAmountRequest;
import pojo.response.deposit.DepositAmountResponse;

import static io.restassured.RestAssured.given;

public class DepositAmountTests extends BaseTest {

    @Test(description = "Depositing the Amount", priority = 1)
    public void performDepositAmount() {

        testUtil.getData_AccountNumbers("Account Numbers").forEach(accountNumber -> {
            DepositAmountRequest request = new DepositAmountRequest();

            request.setAccountId(accountNumber)
                    .setAmount(faker.number().numberBetween(1000, 5000));

            String depositAmountMessage = given()
                    .spec(depositAmountSpecBuilder.getDepositOrWithdrawAmountSpecBuilder(request))
                    .when()
                    .post(bankingEndPoints.getDepositAmount())
                    .then()
                    .statusCode(StatusCodes.SUCCESS.getStatusCode())
                    .extract().response().as(DepositAmountResponse.class).getMessage();

            Assert.assertEquals(depositAmountMessage, "Deposit successful");
        });
    }
}
