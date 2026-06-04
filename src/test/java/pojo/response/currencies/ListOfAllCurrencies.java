package pojo.response.currencies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
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
public class ListOfAllCurrencies implements ResponsePOJO {
    @JsonProperty("currencies")
    List<Currencies> currencies;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Currencies {
        @JsonProperty("id")
        String id;

        @JsonProperty("code")
        String code;

        @JsonProperty("name")
        String name;

        @JsonProperty("symbol")
        String symbol;

        @JsonProperty("is_active")
        Boolean isActive;

        @JsonProperty("created_at")
        String createdAt;

        @JsonProperty("updated_at")
        String updatedAt;
    }
}