package pojo.response.withdraw;

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
public class WithdrawAmountResponse implements ResponsePOJO {
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

        @JsonProperty("previous_balance")
        Integer previousBalance;

        @JsonProperty("amount_deposited")
        Integer amountDeposited;

        @JsonProperty("new_balance")
        Integer newBalance;

        @JsonProperty("updated_at")
        String updatedAt;
    }
}