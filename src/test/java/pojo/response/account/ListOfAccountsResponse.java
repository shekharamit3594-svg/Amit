package pojo.response.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
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
public class ListOfAccountsResponse implements ResponsePOJO {
    @JsonProperty("success")
    Boolean success;

    @JsonProperty("data")
    List<Data> data;

    @JsonProperty("pagination")
    Pagination pagination;

    @JsonProperty("links")
    Links links;

    @JsonProperty("sort")
    Sort sort;

    @JsonProperty("filters")
    Filters filters;

    @Override
    public String toString() {
        return """
            {
    "success": %b,
    "data": %s,
    "pagination": %s,
    "links": %s,
    "sort": %s,
    "filters": %s
}""".formatted(
                success,
                formatArray(data),
                pagination,
                links,
                sort,
                filters);
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Pagination {
        @JsonProperty("page")
        Integer page;

        @JsonProperty("limit")
        Integer limit;

        @JsonProperty("total")
        Integer total;

        @JsonProperty("total_pages")
        Integer totalPages;

        @JsonProperty("has_next")
        Boolean hasNext;

        @JsonProperty("has_prev")
        Boolean hasPrev;

        @JsonProperty("next_page")
        Integer nextPage;

        @JsonProperty("prev_page")
        Object prevPage;

        @Override
        public String toString() {
            return """
                {
    "page": %d,
    "limit": %d,
    "total": %d,
    "total_pages": %d,
    "has_next": %b,
    "has_prev": %b,
    "next_page": %d,
    "prev_page": %s
}""".formatted(
                    page,
                    limit,
                    total,
                    totalPages,
                    hasNext,
                    hasPrev,
                    nextPage,
                    prevPage);
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Links {
        @JsonProperty("self")
        String self;

        @JsonProperty("first")
        String first;

        @JsonProperty("prev")
        Object prev;

        @JsonProperty("next")
        String next;

        @JsonProperty("last")
        String last;

        @Override
        public String toString() {
            return """
                {
    "self": "%s",
    "first": "%s",
    "prev": %s,
    "next": "%s",
    "last": "%s"
}""".formatted(
                    self,
                    first,
                    prev,
                    next,
                    last);
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Sort {
        @JsonProperty("sort_by")
        String sortBy;

        @JsonProperty("order")
        String order;

        @Override
        public String toString() {
            return """
                {
    "sort_by": "%s",
    "order": "%s"
}""".formatted(
                    sortBy,
                    order);
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

    @JsonIgnoreProperties(ignoreUnknown = true)
    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Filters {
        @JsonProperty("account_type")
        Object accountType;

        @JsonProperty("currency")
        Object currency;

        @JsonProperty("search")
        Object search;

        @JsonProperty("min_balance")
        Object minBalance;

        @JsonProperty("max_balance")
        Object maxBalance;

        @Override
        public String toString() {
            return """
                {
    "account_type": %s,
    "currency": %s,
    "search": %s,
    "min_balance": %s,
    "max_balance": %s
}""".formatted(
                    accountType,
                    currency,
                    search,
                    minBalance,
                    maxBalance);
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
