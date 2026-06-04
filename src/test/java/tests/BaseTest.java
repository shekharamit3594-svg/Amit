package tests;

import framework.PropertiesUtil;
import framework.TestUtil;
import framework.constants.StatusCodes;
import io.restassured.RestAssured;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.datafaker.Faker;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import pojo.request.login.LoginRequest;
import pojo.response.login.LoginResponse;
import specbuilders.request.AccountSpecBuilder;
import specbuilders.request.DepositAmountSpecBuilder;
import specbuilders.request.LoginSpecBuilder;
import specbuilders.response.AccountResponseSpecBuilder;

import static io.restassured.RestAssured.given;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseTest {

    private PropertiesUtil propertiesUtil;
    PropertiesUtil.BankingEndPoints bankingEndPoints;
    LoginSpecBuilder loginSpecBuilder;
    AccountSpecBuilder accountSpecBuilder;
    AccountResponseSpecBuilder accountResponseSpecBuilder;
    DepositAmountSpecBuilder depositAmountSpecBuilder;
    TestUtil testUtil;
    Faker faker;

    @BeforeSuite
    public void setupOfInformation()
    {
    }

    @BeforeClass
    public void beforeClass() {

        loginSpecBuilder = new LoginSpecBuilder();
        propertiesUtil = new PropertiesUtil();
        testUtil = new TestUtil();
        accountSpecBuilder=new AccountSpecBuilder(testUtil);
        faker = new Faker();
        accountResponseSpecBuilder=new AccountResponseSpecBuilder();
        depositAmountSpecBuilder=new DepositAmountSpecBuilder(testUtil);

        if(propertiesUtil.getTypeOfAPI().equalsIgnoreCase("Banking"))
        {
            bankingEndPoints=new PropertiesUtil.BankingEndPoints();
            RestAssured.baseURI=bankingEndPoints.getBaseURL();
            loginToTheAPI();
        }

    }

    public void loginToTheAPI()
    {
        if(testUtil.getData("AccessToken").isBlank() || testUtil.getData("AccessToken").contains("not found"))
        {
            LoginRequest request = new LoginRequest();
            request.setEmail(bankingEndPoints.getUserName())
                    .setPassword(bankingEndPoints.getPassword());

            LoginResponse response = given()
                    .spec(loginSpecBuilder.getLoginRequestSpec(request))
                    .when()
                    .post(bankingEndPoints.getLoginURL())
                    .then()
                    .statusCode(StatusCodes.SUCCESS.getStatusCode())
                    .extract().response().as(LoginResponse.class);

            testUtil.setData("AccessToken", response.getAccessToken());
        }
    }
}
