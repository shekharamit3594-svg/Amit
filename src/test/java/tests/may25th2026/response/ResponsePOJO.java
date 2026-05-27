package tests.may25th2026.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class ResponsePOJO {

    boolean success;
    String message;
    Data data;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @ToString //It will automatically overrides the .toString() method present in the Object class and generate a proper data
    public static class Data
    {
        @JsonProperty("account_id")
        String accountID;

        @JsonProperty("account_holder_name") //Here we are passing the actual JSON key
        String accountHolderName; //Here we can maintain the variable name of your choice

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
        @JsonProperty("created_at")
        String createdDate;
    }


}
