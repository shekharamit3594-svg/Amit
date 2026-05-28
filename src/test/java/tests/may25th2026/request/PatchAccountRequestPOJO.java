package tests.may25th2026.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain=true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY) // Skips null AND empty-string fields — stricter than NON_NULL
public class PatchAccountRequestPOJO implements RequestData {
    @JsonProperty("account_id")
    String accountId;

    @JsonProperty("account_holder_name")
    String accountHolderName;

    @JsonProperty("account_type")
    String accountType;

    String currency;
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
