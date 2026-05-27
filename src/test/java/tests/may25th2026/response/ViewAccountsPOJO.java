package tests.may25th2026.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ViewAccountsPOJO {
    @JsonProperty("success")
    Boolean success;

    @JsonProperty("data")
    Data data;

    @Override
    public String toString() {
        return """
                {
                    "success": %b,
                    "data": %s
                }""".formatted(
                success,
                data);
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

    @lombok.Data
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

        @JsonProperty("updated_at")
        String updatedAt;

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
                        "country": "%s",
                        "balance": %d,
                        "created_at": "%s",
                        "updated_at": "%s"
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
                    country,
                    balance,
                    createdAt,
                    updatedAt);
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
}