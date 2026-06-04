package pojo.response.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import pojo.response.ResponsePOJO;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateAccountResponse implements ResponsePOJO {
    @JsonProperty("success")
    Boolean success;

    @JsonProperty("message")
    String message;

    @JsonProperty("data")
    Data data;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Data {
        @JsonProperty("account_id")
        String accountId;

        @JsonProperty("account_holder_name")
        String accountHolderName;

        @JsonProperty("account_type")
        String accountType;

        @JsonProperty("currency")
        String currency;

        @JsonProperty("email")
        String email;

        @JsonProperty("phone")
        String phone;

        @JsonProperty("address_line1")
        String addressLine1;

        @JsonProperty("address_line2")
        String addressLine2;

        @JsonProperty("city")
        String city;

        @JsonProperty("state")
        String state;

        @JsonProperty("zip_code")
        String zipCode;

        @JsonProperty("country")
        String country;

        @JsonProperty("balance")
        Integer balance;

        @JsonProperty("created_at")
        String createdAt;
    }
}