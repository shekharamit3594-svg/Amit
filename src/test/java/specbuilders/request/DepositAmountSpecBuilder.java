package specbuilders.request;

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
public class DepositAmountSpecBuilder {

    TestUtil testUtil;

    public RequestSpecification getDepositAmountSpecBuilder(RequestPOJO... request)
    {

        RequestSpecBuilder specBuilder = new RequestSpecBuilder();
        specBuilder.setContentType(ContentType.JSON)
                .addHeader("Authorization","Bearer "+testUtil.getData("AccessToken"))
                .addHeader("apikey","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFuYWpicXhtcGJtbmRud2Rxc3dxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQyMDA3OTMsImV4cCI6MjA3OTc3Njc5M30.gu96y5-EvK1a1BQWxNvQpJ-8st-fP_zLFsuVVtGif9c")
                .addFilter(new AllureRestAssured());

        if(request.length>0)
            specBuilder.setBody(request[0]);

        return specBuilder.build();
    }
}
