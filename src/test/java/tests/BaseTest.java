package tests;

import framework.PropertiesUtil;
import framework.TestUtil;
import framework.constants.StatusCodes;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.datafaker.Faker;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import pojo.request.login.LoginRequest;
import pojo.response.login.LoginResponse;
import specbuilders.request.AccountSpecBuilder;
import specbuilders.request.DepositOrWithDrawAmountSpecBuilder;
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
    DepositOrWithDrawAmountSpecBuilder depositAmountSpecBuilder;
    static TestUtil testUtil;
    Faker faker;

    @BeforeSuite
    public void setupOfInformation()
    {
        testUtil = new TestUtil();
    }

    @BeforeClass
    public void beforeClass() {

        propertiesUtil = new PropertiesUtil();
        faker = new Faker();
        accountResponseSpecBuilder = new AccountResponseSpecBuilder();

        if(propertiesUtil.getTypeOfAPI().equalsIgnoreCase("Banking"))
        {
            bankingEndPoints = new PropertiesUtil.BankingEndPoints();
            loginSpecBuilder = new LoginSpecBuilder(bankingEndPoints);
            accountSpecBuilder = new AccountSpecBuilder(testUtil, bankingEndPoints);
            depositAmountSpecBuilder = new DepositOrWithDrawAmountSpecBuilder(testUtil, bankingEndPoints);
            RestAssured.baseURI = bankingEndPoints.getBaseURL();
            RestAssured.config = RestAssured.config().httpClient(
                    HttpClientConfig.httpClientConfig()
                            .setParam("http.connection.timeout", bankingEndPoints.getConnectionTimeout())
                            .setParam("http.socket.timeout", bankingEndPoints.getSocketTimeout())
            );
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
