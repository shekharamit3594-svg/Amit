package tests;

import framework.PropertiesUtil;
import framework.TestUtil;
import framework.constants.StatusCodes;
import io.restassured.RestAssured;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import pojo.request.login.LoginRequest;
import pojo.response.login.LoginResponse;
import specbuilders.request.AccountSpecBuilder;
import specbuilders.request.LoginSpecBuilder;

import static io.restassured.RestAssured.given;

@FieldDefaults(level = AccessLevel.PROTECTED)
public class BaseTest {

    private PropertiesUtil propertiesUtil;
    PropertiesUtil.BankingEndPoints bankingEndPoints;
    LoginSpecBuilder loginSpecBuilder;
    AccountSpecBuilder accountSpecBuilder;
    TestUtil testUtil;

    @BeforeSuite
    public void setupOfInformation()
    {
        loginSpecBuilder = new LoginSpecBuilder();
        propertiesUtil = new PropertiesUtil();
        testUtil = new TestUtil();
        accountSpecBuilder=new AccountSpecBuilder(testUtil);
    }
    @BeforeClass
    public void beforeClass() {
        if(propertiesUtil.getTypeOfAPI().equalsIgnoreCase("Banking"))
        {
            bankingEndPoints=new PropertiesUtil.BankingEndPoints();
            RestAssured.baseURI=bankingEndPoints.getBaseURL();
            loginToTheAPI();
        }

    }

    public void loginToTheAPI()
    {
        LoginRequest request=new LoginRequest();
        request.setEmail(bankingEndPoints.getUserName())
            .setPassword(bankingEndPoints.getPassword());

        LoginResponse response=given()
                .spec(loginSpecBuilder.getLoginRequestSpec(request))
        .when()
                .post(bankingEndPoints.getLoginURL())
        .then()
                .statusCode(StatusCodes.SUCCESS.getStatusCode())
                .extract().response().as(LoginResponse.class);

        testUtil.setData("AccessToken",response.getAccessToken());
    }
}
