package tests.may26th2026;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import tests.may25th2026.RequestData;
import tests.may25th2026.request.RequestPOJO;

import java.io.PrintStream;
import java.util.Map;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.isA;

//We will maintain the required activities, which are common across all the end points
//It can before the API Call and after the API Call
public class SpecBuilders {

    // When set, request and response bodies are written to this stream instead of the console
    private PrintStream logStream;

    // Fluent setter — call specBuilders.withLogStream(printStream) before building specs
    public SpecBuilders withLogStream(PrintStream logStream) {
        this.logStream = logStream;
        return this;
    }

    //Here we are generating a common templates for the request
    //Below method is known as Request Spec Builder
    public RequestSpecification getRequestSpecification(RequestData... requestData)
    {

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setContentType(ContentType.JSON)
                .addHeader("Authorization",System.getProperty("accessToken"))
                .addFilter(new AllureRestAssured());

        // LogDetail.BODY is the programmatic equivalent of .log().body()
        // RequestLoggingFilter logs the outgoing request body
        // ResponseLoggingFilter logs the incoming response body
        // Both write to logStream (file) when set, otherwise they are skipped here
        // (individual tests can still call .log().body() for console output)
        if (logStream != null) {
            requestSpecBuilder
                    .addFilter(new RequestLoggingFilter(LogDetail.BODY, logStream))
                    .addFilter(new ResponseLoggingFilter(LogDetail.BODY, logStream));
        }

        if(requestData.length > 0)
        {
            requestSpecBuilder.setBody(requestData[0]);
        }

        return requestSpecBuilder.build();
    }

    public RequestSpecification getRequestSpecification_URLEncoded(Map<String, Object>... requestData)
    {

        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setContentType(ContentType.URLENC)
                .addHeader("Authorization",System.getProperty("accessToken"))
                .addFilter(new AllureRestAssured());

        // LogDetail.BODY is the programmatic equivalent of .log().body()
        // RequestLoggingFilter logs the outgoing request body
        // ResponseLoggingFilter logs the incoming response body
        // Both write to logStream (file) when set, otherwise they are skipped here
        // (individual tests can still call .log().body() for console output)
        if (logStream != null) {
            requestSpecBuilder
                    .addFilter(new RequestLoggingFilter(LogDetail.BODY, logStream))
                    .addFilter(new ResponseLoggingFilter(LogDetail.BODY, logStream));
        }

        if(requestData.length > 0)
        {
            //.addFormParams() --> Accepts the complete set of form parameters stored in the map object
            requestSpecBuilder.addFormParams(requestData[0]);
        }

        return requestSpecBuilder.build();
    }

    public ResponseSpecification getResponseSpecification(int statusCode,RequestData... requestData)
    {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();

         responseSpecBuilder.expectStatusCode(statusCode);

         if(requestData.length > 0) {

             RequestPOJO request = null;

             if(requestData[0] instanceof RequestPOJO s)
                 request=s;

             //Checks if all the given keys are present in the JSON Response
            responseSpecBuilder.expectBody("", allOf(hasKey("success"), hasKey("message"), hasKey("data")))

                     //Checks if the message is of String data type or not
                     .expectBody("message", isA(String.class))

                     //Checks if the success field is of Boolean data type or not
                     .expectBody("success", isA(Boolean.class))

                     //Checks if the data type of the "data" is Object or not
                     .expectBody("data", isA(Object.class))

                     //Validating the keys inside the data
                     .expectBody("data", allOf(hasKey("account_id"), hasKey("account_holder_name"), hasKey("account_type"),
                             hasKey("currency"), hasKey("email"), hasKey("phone"), hasKey("address_line1"), hasKey("address_line2"),
                             hasKey("city"), hasKey("state"), hasKey("zip_code"), hasKey("country"), hasKey("balance"), hasKey("created_at")))

                     //Checks if the data type of the balance is Integer
                     .expectBody("data.balance", isA(Integer.class))
                     .expectBody("data.balance", equalTo(request.getInitialBalance()))

                     //Request and Response fields validation
                     .expectBody("data.account_holder_name", isA(String.class))
                     .expectBody("data.account_holder_name", equalToIgnoringCase(request.getAccountHolderName()))

                     .expectBody("data.account_type", isA(String.class))
                     .expectBody("data.account_type", equalToIgnoringCase(request.getAccountType()))

                     .expectBody("data.currency", isA(String.class))
                     .expectBody("data.currency", equalToIgnoringCase(request.getCurrency()))

                     .expectBody("data.email", isA(String.class))
                     .expectBody("data.email", equalToIgnoringCase(request.getEmail()))

                     .expectBody("data.phone", isA(String.class))
                     .expectBody("data.phone", equalToIgnoringCase(request.getPhone()))

                     .expectBody("data.address_line1", isA(String.class))
                     .expectBody("data.address_line1", equalToIgnoringCase(request.getAddressLine1()))

                     .expectBody("data.address_line2", isA(String.class))
                     .expectBody("data.address_line2", equalToIgnoringCase(request.getAddressLine2()))

                     .expectBody("data.city", isA(String.class))
                     .expectBody("data.city", equalToIgnoringCase(request.getCity()))

                     .expectBody("data.state", isA(String.class))
                     .expectBody("data.state", equalToIgnoringCase(request.getState()))

                     .expectBody("data.zip_code", isA(String.class))
                     .expectBody("data.zip_code", equalToIgnoringCase(request.getZipCode()))

                     .expectBody("data.country", isA(String.class))
                     .expectBody("data.country", equalToIgnoringCase(request.getCountry()));
         }

         return responseSpecBuilder.build();
    }

    public ResponseSpecification getResponseSpecification_FormParameters(int statusCode,Map<String,Object>... requestData)
    {
        ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();

        responseSpecBuilder.expectStatusCode(statusCode);

        if(requestData.length > 0) {

            Map<String,Object> request = requestData[0];
            //Checks if all the given keys are present in the JSON Response
            responseSpecBuilder.expectBody("", allOf(hasKey("success"), hasKey("message"), hasKey("data")))

                    //Checks if the message is of String data type or not
                    .expectBody("message", isA(String.class))

                    //Checks if the success field is of Boolean data type or not
                    .expectBody("success", isA(Boolean.class))

                    //Checks if the data type of the "data" is Object or not
                    .expectBody("data", isA(Object.class))

                    //Validating the keys inside the data
                    .expectBody("data", allOf(hasKey("account_id"), hasKey("account_holder_name"), hasKey("account_type"),
                            hasKey("currency"), hasKey("email"), hasKey("phone"), hasKey("address_line1"), hasKey("address_line2"),
                            hasKey("city"), hasKey("state"), hasKey("zip_code"), hasKey("country"), hasKey("balance"), hasKey("created_at")))

                    //Checks if the data type of the balance is Integer
                    .expectBody("data.balance", isA(Integer.class))
                    .expectBody("data.balance", equalTo(request.get("initial_balance")))

                    //Request and Response fields validation
                    .expectBody("data.account_holder_name", isA(String.class))
                    .expectBody("data.account_holder_name", equalToIgnoringCase(String.valueOf(request.get("account_holder_name"))))

                    .expectBody("data.account_type", isA(String.class))
                    .expectBody("data.account_type", equalToIgnoringCase(String.valueOf(request.get("account_type"))))

                    .expectBody("data.currency", isA(String.class))
                    .expectBody("data.currency", equalToIgnoringCase(String.valueOf(request.get("currency"))))

                    .expectBody("data.email", isA(String.class))
                    .expectBody("data.email", equalToIgnoringCase(String.valueOf(request.get("email"))))

                    .expectBody("data.phone", isA(String.class))
                    .expectBody("data.phone", equalToIgnoringCase(String.valueOf(request.get("phone"))))

                    .expectBody("data.address_line1", isA(String.class))
                    .expectBody("data.address_line1", equalToIgnoringCase(String.valueOf(request.get("address_line1"))))

                    .expectBody("data.address_line2", isA(String.class))
                    .expectBody("data.address_line2", equalToIgnoringCase(String.valueOf(request.get("address_line2"))))

                    .expectBody("data.city", isA(String.class))
                    .expectBody("data.city", equalToIgnoringCase(String.valueOf(request.get("city"))))

                    .expectBody("data.state", isA(String.class))
                    .expectBody("data.state", equalToIgnoringCase(String.valueOf(request.get("state"))))

                    .expectBody("data.zip_code", isA(String.class))
                    .expectBody("data.zip_code", equalToIgnoringCase(String.valueOf(request.get("zip_code"))))

                    .expectBody("data.country", isA(String.class))
                    .expectBody("data.country", equalToIgnoringCase(String.valueOf(request.get("country"))));
        }

        return responseSpecBuilder.build();
    }
}
