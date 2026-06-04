package specbuilders.response;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;
import pojo.request.RequestPOJO;

public class AccountResponseSpecBuilder {

    public ResponseSpecification getAccountResponseSpec(int statusCode, RequestPOJO...request)
    {

        ResponseSpecBuilder responseBuilder = new ResponseSpecBuilder();

        responseBuilder.expectStatusCode(statusCode)
                .expectContentType(ContentType.JSON);

        return responseBuilder.build();
    }
}
