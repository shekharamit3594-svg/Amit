package pojo.request.withdraw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import pojo.request.RequestPOJO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class WithdrawAmountRequest implements RequestPOJO {

    @JsonProperty("account_id")
    String accountId;

    @JsonProperty("amount")
    Integer amount;
}
