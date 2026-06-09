package specbuilders.response;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.ResponseSpecification;

public class AccountResponseSpecBuilder {

    public ResponseSpecification getAccountResponseSpec(int statusCode)
    {

        ResponseSpecBuilder responseBuilder = new ResponseSpecBuilder();

        responseBuilder.expectStatusCode(statusCode)
                .expectContentType(ContentType.JSON);

        return responseBuilder.build();
    }
}
