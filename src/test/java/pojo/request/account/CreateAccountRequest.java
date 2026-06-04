package pojo.request.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import pojo.request.RequestPOJO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class CreateAccountRequest implements RequestPOJO {
    @JsonProperty("account_holder_name")
    String accountHolderName;

    @JsonProperty("initial_balance")
    Integer initialBalance;

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
}