package tests.may25th2026.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import tests.may25th2026.RequestData;

@Data
@NoArgsConstructor //Generates a default constructor at the run time
@AllArgsConstructor //Generates a Parameterized constructor based on the variables declared at the run time
@Accessors(chain=true) //All the setter methods will be chained
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PatchAccountRequestPOJO implements RequestData {
    @JsonProperty("account_id")
    String accountId;

    @JsonProperty("account_holder_name")
    String accountHolderName;

    @JsonProperty("account_type")
    @JsonIgnore
    String accountType;

    @JsonProperty("currency")
    @JsonIgnore //This particular field shall not be a part of the JSON Payload
    String currency;

    @JsonProperty("email")
    @JsonIgnore
    String email;

    @JsonProperty("phone")
    @JsonIgnore
    String phone;

    @JsonProperty("address_line1")
    @JsonIgnore
    String addressLine1;

    @JsonProperty("address_line2")
    @JsonIgnore
    String addressLine2;

    @JsonProperty("city")
    @JsonIgnore
    String city;

    @JsonProperty("state")
    @JsonIgnore
    String state;

    @JsonProperty("zip_code")
    @JsonIgnore
    String zipCode;

    @JsonProperty("country")
    @JsonIgnore
    String country;

    @Override
    public String toString() {
        return """
            {
    "account_id": "%s",
    "account_holder_name": "%s",
    "account_type": "%s",
    "currency": "%s",
    "email": "%s",
    "phone": "%s",
    "address_line1": "%s",
    "address_line2": "%s",
    "city": "%s",
    "state": "%s",
    "zip_code": "%s",
    "country": "%s"
}""".formatted(
                accountId,
                accountHolderName,
                accountType,
                currency,
                email,
                phone,
                addressLine1,
                addressLine2,
                city,
                state,
                zipCode,
                country);
    }

    private String formatArray(List<?> list) {
        if (list == null) return "null";
        return "[" + list.stream()
                .map(item -> {
                    if (item == null) return "null";
                    if (item instanceof String) return "\"" + item + "\"";
                    return item.toString();
                })
                .collect(Collectors.joining(", ")) + "]";
    }
}