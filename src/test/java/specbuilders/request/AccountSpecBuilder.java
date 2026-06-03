package specbuilders.request;

import framework.TestUtil;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AccountSpecBuilder {

    TestUtil testUtil;

    public RequestSpecification getAccountSpecBuilder() {

        RequestSpecBuilder specBuilder = new RequestSpecBuilder();
        specBuilder.setContentType(ContentType.JSON)
                .addHeader("Authorization","Bearer "+testUtil.getData("AccessToken"))
                .addHeader("apikey","eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFuYWpicXhtcGJtbmRud2Rxc3dxIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQyMDA3OTMsImV4cCI6MjA3OTc3Njc5M30.gu96y5-EvK1a1BQWxNvQpJ-8st-fP_zLFsuVVtGif9c");

        return specBuilder.build();
    }
}
