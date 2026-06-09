package specbuilders.request;

import framework.PropertiesUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import pojo.request.RequestPOJO;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class LoginSpecBuilder {

    PropertiesUtil.BankingEndPoints bankingEndPoints;

    public RequestSpecification getLoginRequestSpec(RequestPOJO... request)
    {
        RequestSpecBuilder specBuilder = new RequestSpecBuilder();
        specBuilder.setContentType(ContentType.JSON)
                .addHeader("apikey", bankingEndPoints.getAPIKey());

        if(request.length > 0)
        {
            specBuilder.setBody(request[0]);
        }

        return specBuilder.build();
    }
}
