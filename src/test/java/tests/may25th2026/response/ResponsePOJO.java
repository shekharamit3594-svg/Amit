package tests.may25th2026.response;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true) // Prevents deserialization failure if API adds new top-level fields
public class ResponsePOJO {

    boolean success;
    String message;
    Data data;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @ToString //It will automatically overrides the .toString() method present in the Object class and generate a proper data
    @JsonIgnoreProperties(ignoreUnknown = true) // Prevents deserialization failure if API adds new data fields
    public static class Data
    {
        @JsonProperty("account_id")
        String accountID;

        @JsonProperty("account_holder_name")
        String accountHolderName;

        @JsonProperty("initial_balance")
        int initialBalance;

        @JsonProperty("account_type")
        String accountType;
        String currency;

        //@NonNull //Ensuring that this particular field is not a null value
        String email;
        String phone;

        @JsonProperty("address_line1")
        String addressLine1;

        @JsonProperty("address_line2")
        String addressLine2;
        String city;
        String state;

        @JsonProperty("zip_code")
        String zipCode;
        String country;
        String balance;

        @JsonAlias({"created_at"}) // Accepts "created_at" from the API response in addition to the default field name
        String createdDate;
    }


}
