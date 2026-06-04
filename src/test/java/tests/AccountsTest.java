package tests;

import framework.constants.StatusCodes;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.request.account.CreateAccountRequest;
import pojo.response.account.CreateAccountResponse;
import pojo.response.account.ListOfAccountsResponse;
import pojo.response.account.ViewAccountResponse;
import pojo.response.login.LoginResponse;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
    }

    @Test(description = "Creating a New Account",priority = 2)
    public void createNewAccount() {

        CreateAccountRequest request=new CreateAccountRequest();

        List<String> accountTypes= List.of("personal","business","joint");

        request.setAccountType(accountTypes.get(ThreadLocalRandom.current().nextInt(0,accountTypes.size())))
                .setAccountHolderName(faker.name().fullName())
                .setInitialBalance(faker.number().numberBetween(1000,10000))
                .setCurrency(System.getProperty("Currency"))
                .setEmail(faker.internet().emailAddress())
                .setPhone(faker.phoneNumber().phoneNumber())
                .setCity(faker.address().city())
                .setAddressLine1(faker.address().streetAddress())
                .setAddressLine2(faker.address().streetAddress())
                .setState(faker.address().state())
                .setZipCode(faker.address().zipCode())
                .setCountry(faker.address().country());

        String accountId=given()
                .spec(accountSpecBuilder.getAccountSpecBuilder(request))
        .when()
                .post(bankingEndPoints.getCreateAccountEndPoint())
        .then()
                .spec(accountResponseSpecBuilder.getAccountResponseSpec(StatusCodes.SUCCESS.getStatusCode()))
                .extract().response().as(CreateAccountResponse.class).getData().getAccountId();

        System.setProperty("Account Number",accountId);
    }

    @Test(description = "Viewing that particular account",priority = 3)
    public void viewAccountDetails() {

        ViewAccountResponse response=given()
                .spec(accountSpecBuilder.getAccountSpecBuilder())
                .queryParam("account_id",System.getProperty("Account Number"))
        .when()
                .get(bankingEndPoints.getViewAccountEndPoint())
        .then()
                .spec(accountResponseSpecBuilder.getAccountResponseSpec(StatusCodes.SUCCESS.getStatusCode()))
                .extract().response().as(ViewAccountResponse.class);

        Assert.assertEquals(response.getData().getAccountId(),System.getProperty("Account Number"));
    }
}
