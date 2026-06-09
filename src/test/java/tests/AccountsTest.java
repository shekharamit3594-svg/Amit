package tests;

import dataProviders.FetchDataFromCSVFile;
import framework.GenericExceptions;
import framework.constants.StatusCodes;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.request.account.CreateAccountRequest;
import pojo.response.account.CreateAccountResponse;
import pojo.response.account.ListOfAccountsResponse;
import pojo.response.account.ViewAccountResponse;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AccountsTest extends BaseTest {

    @Test(description = "Viewing all the accounts", priority = 1)
    public void viewAllAccounts() {

        ListOfAccountsResponse listOfAccountsResponse = given()
                .spec(accountSpecBuilder.getAccountSpecBuilder())
                .when()
                .get(bankingEndPoints.getListAccountEndPoint())
                .then()
                .statusCode(StatusCodes.SUCCESS.getStatusCode())
                .extract().response().as(ListOfAccountsResponse.class);
    }

    @Test(description = "Creating a New Account", priority = 2, dataProviderClass = FetchDataFromCSVFile.class,
            dataProvider = "sampleData")
    public void createNewAccount(Map<String, String> data) {

        CreateAccountRequest request = new CreateAccountRequest();

        List<String> accountTypes = List.of("personal", "business", "joint");

        //accountTypes.get(ThreadLocalRandom.current().nextInt(0, accountTypes.size()))
        request.setAccountType(data.get("accounttype"))
                .setAccountHolderName(data.get("accountholdername"))
                .setInitialBalance(Integer.valueOf(data.get("initialbalance")))
                .setCurrency(data.get("currency"))
                .setEmail(data.get("email"))
                .setPhone(data.get("phone"))
                .setCity(data.get("city"))
                .setAddressLine1(data.get("addressline1"))
                .setAddressLine2(data.get("addressline2"))
                .setState(data.get("state"))
                .setZipCode(data.get("zipcode"))
                .setCountry(data.get("country"));

        Response response = given()
                .spec(accountSpecBuilder.getAccountSpecBuilder(request))
                .when()
                .post(bankingEndPoints.getCreateAccountEndPoint())
                .then()
                .extract().response();

        int statusCode = response.getStatusCode();

        if (statusCode == StatusCodes.BAD_REQUEST.getStatusCode())
            throw new GenericExceptions("Please check the body request before performing the create account operation");
        else if (statusCode == StatusCodes.UNAUTHORIZED.getStatusCode())
            throw new GenericExceptions("Please check the auth token used to perform the create account operation");
        else if (statusCode == StatusCodes.PAGE_NOT_FOUND.getStatusCode())
            throw new GenericExceptions("Endpoint not found or the given resource not found");
        else if(statusCode == StatusCodes.DUPLICATE_DATA.getStatusCode())
            throw new GenericExceptions("Account already exists for: "+data.get("accountholdername"));

        assertThat(statusCode, equalTo(StatusCodes.SUCCESS.getStatusCode()));

        String accountId = response.as(CreateAccountResponse.class).getData().getAccountId();
        testUtil.setData_AccountNumbers("Account Numbers", accountId);
    }

    @Test(description = "Viewing that particular account", priority = 3)
    public void viewAccountDetails() {

        testUtil.getData_AccountNumbers("Account Numbers")
                .forEach(accountNumber -> {
                    ViewAccountResponse response = given()
                            .spec(accountSpecBuilder.getAccountSpecBuilder())
                            .queryParam("account_id", accountNumber)
                            .when()
                            .get(bankingEndPoints.getViewAccountEndPoint())
                            .then()
                            .spec(accountResponseSpecBuilder.getAccountResponseSpec(StatusCodes.SUCCESS.getStatusCode()))
                            .extract().response().as(ViewAccountResponse.class);

                    Assert.assertEquals(response.getData().getAccountId(), accountNumber);
                });
    }
}
