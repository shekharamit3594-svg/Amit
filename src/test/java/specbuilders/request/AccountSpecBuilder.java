package specbuilders.request;

import framework.PropertiesUtil;
import framework.TestUtil;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import pojo.request.RequestPOJO;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AccountSpecBuilder {

    TestUtil testUtil;
    PropertiesUtil.BankingEndPoints bankingEndPoints;

    public RequestSpecification getAccountSpecBuilder(RequestPOJO... request)
    {

        RequestSpecBuilder specBuilder = new RequestSpecBuilder();
        specBuilder.setContentType(ContentType.JSON)
                .addHeader("Authorization","Bearer "+testUtil.getData("AccessToken"))
                .addHeader("apikey", bankingEndPoints.getAPIKey())
                .addFilter(new AllureRestAssured());

        if(request.length>0)
            specBuilder.setBody(request[0]);

        return specBuilder.build();
    }
}
