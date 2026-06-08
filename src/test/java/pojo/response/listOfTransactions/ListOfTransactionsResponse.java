package pojo.response.listOfTransactions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import pojo.response.ResponsePOJO;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class ListOfTransactionsResponse implements ResponsePOJO {
    @JsonProperty("success")
    Boolean success;

    @JsonProperty("data")
    List<Data> data;

    @JsonProperty("pagination")
    Pagination pagination;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Accessors(chain = true)
    public static class Data {
        @JsonProperty("id")
        String id;

        @JsonProperty("account_id")
        String accountId;

        @JsonProperty("user_id")
        String userId;

        @JsonProperty("type")
        String type;

        @JsonProperty("amount")
        Integer amount;

        @JsonProperty("balance_after")
        Integer balanceAfter;

        @JsonProperty("description")
        String description;

        @JsonProperty("related_transaction_id")
        Object relatedTransactionId;

        @JsonProperty("created_at")
        String createdAt;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Accessors(chain = true)
    public static class Pagination {
        @JsonProperty("page")
        Integer page;

        @JsonProperty("limit")
        Integer limit;

        @JsonProperty("total")
        Integer total;

        @JsonProperty("total_pages")
        Integer totalPages;
    }
}