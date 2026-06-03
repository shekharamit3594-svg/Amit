package tests;

import framework.constants.StatusCodes;
import org.testng.annotations.Test;
import pojo.response.account.ListOfAccountsResponse;
import pojo.response.login.LoginResponse;

import static io.restassured.RestAssured.given;

public class AccountsTest extends BaseTest {

    @Test(description = "Viewing all the accounts",priority = 1)
    public void viewAllAccounts() {

        ListOfAccountsResponse listOfAccountsResponse= given()
                .spec(accountSpecBuilder.getAccountSpecBuilder())
        .when()
                .get(bankingEndPoints.getListAccountEndPoint())
        .then()
                .statusCode(StatusCodes.SUCCESS.getStatusCode())
                .extract().response().as(ListOfAccountsResponse.class);

       IO.println(listOfAccountsResponse);
    }
}
