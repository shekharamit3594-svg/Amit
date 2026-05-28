package tests.may25th2026.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true) // Prevents deserialization failure if API adds new top-level fields
public class ViewCurrenciesPOJO {
    List<Currencies> currencies;

    @Override
    public String toString() {
        return """
                {
                    "currencies": %s
                }""".formatted(
                formatArray(currencies));
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

    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @JsonIgnoreProperties(ignoreUnknown = true) // Prevents deserialization failure if API adds new currency fields
    public static class Currencies {
        String id;
        String code;
        String name;
        String symbol;

        @JsonProperty("is_active")
        Boolean isActive;

        @JsonProperty("created_at")
        String createdAt;

        @JsonProperty("updated_at")
        String updatedAt;

        @Override
        public String toString() {
            return """
                                    {
                        "id": "%s",
                        "code": "%s",
                        "name": "%s",
                        "symbol": "%s",
                        "is_active": %b,
                        "created_at": "%s",
                        "updated_at": "%s"
                    }""".formatted(
                    id,
                    code,
                    name,
                    symbol,
                    isActive,
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
