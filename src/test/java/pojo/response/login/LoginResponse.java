package pojo.response.login;

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
public class LoginResponse implements ResponsePOJO {
    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("token_type")
    String tokenType;

    @JsonProperty("expires_in")
    Integer expiresIn;

    @JsonProperty("expires_at")
    Integer expiresAt;

    @JsonProperty("refresh_token")
    String refreshToken;

    @JsonProperty("user")
    User user;

    @JsonProperty("weak_password")
    Object weakPassword;

    @Override
    public String toString() {
        return """
            {
    "access_token": "%s",
    "token_type": "%s",
    "expires_in": %d,
    "expires_at": %d,
    "refresh_token": "%s",
    "user": %s,
    "weak_password": %s
}""".formatted(
                accessToken,
                tokenType,
                expiresIn,
                expiresAt,
                refreshToken,
                user,
                weakPassword);
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
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class User {
        @JsonProperty("id")
        String id;

        @JsonProperty("aud")
        String aud;

        @JsonProperty("role")
        String role;

        @JsonProperty("email")
        String email;

        @JsonProperty("email_confirmed_at")
        String emailConfirmedAt;

        @JsonProperty("phone")
        String phone;

        @JsonProperty("confirmed_at")
        String confirmedAt;

        @JsonProperty("recovery_sent_at")
        String recoverySentAt;

        @JsonProperty("last_sign_in_at")
        String lastSignInAt;

        @JsonProperty("app_metadata")
        AppMetadata appMetadata;

        @JsonProperty("user_metadata")
        UserMetadata userMetadata;

        @JsonProperty("identities")
        List<Identitie> identities;

        @JsonProperty("created_at")
        String createdAt;

        @JsonProperty("updated_at")
        String updatedAt;

        @JsonProperty("is_anonymous")
        Boolean isAnonymous;

        @Override
        public String toString() {
            return """
                {
    "id": "%s",
    "aud": "%s",
    "role": "%s",
    "email": "%s",
    "email_confirmed_at": "%s",
    "phone": "%s",
    "confirmed_at": "%s",
    "recovery_sent_at": "%s",
    "last_sign_in_at": "%s",
    "app_metadata": %s,
    "user_metadata": %s,
    "identities": %s,
    "created_at": "%s",
    "updated_at": "%s",
    "is_anonymous": %b
}""".formatted(
                    id,
                    aud,
                    role,
                    email,
                    emailConfirmedAt,
                    phone,
                    confirmedAt,
                    recoverySentAt,
                    lastSignInAt,
                    appMetadata,
                    userMetadata,
                    formatArray(identities),
                    createdAt,
                    updatedAt,
                    isAnonymous);
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
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class AppMetadata {
            @JsonProperty("provider")
            String provider;

            @JsonProperty("providers")
            List<String> providers;

            @Override
            public String toString() {
                return """
                    {
    "provider": "%s",
    "providers": %s
}""".formatted(
                        provider,
                        formatArray(providers));
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
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class UserMetadata {
            @JsonProperty("email")
            String email;

            @JsonProperty("email_verified")
            Boolean emailVerified;

            @JsonProperty("phone_verified")
            Boolean phoneVerified;

            @JsonProperty("sub")
            String sub;

            @Override
            public String toString() {
                return """
                    {
    "email": "%s",
    "email_verified": %b,
    "phone_verified": %b,
    "sub": "%s"
}""".formatted(
                        email,
                        emailVerified,
                        phoneVerified,
                        sub);
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
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE)
        public static class Identitie {
            @JsonProperty("identity_id")
            String identityId;

            @JsonProperty("id")
            String id;

            @JsonProperty("user_id")
            String userId;

            @JsonProperty("identity_data")
            IdentityData identityData;

            @JsonProperty("provider")
            String provider;

            @JsonProperty("last_sign_in_at")
            String lastSignInAt;

            @JsonProperty("created_at")
            String createdAt;

            @JsonProperty("updated_at")
            String updatedAt;

            @JsonProperty("email")
            String email;

            @Override
            public String toString() {
                return """
                    {
    "identity_id": "%s",
    "id": "%s",
    "user_id": "%s",
    "identity_data": %s,
    "provider": "%s",
    "last_sign_in_at": "%s",
    "created_at": "%s",
    "updated_at": "%s",
    "email": "%s"
}""".formatted(
                        identityId,
                        id,
                        userId,
                        identityData,
                        provider,
                        lastSignInAt,
                        createdAt,
                        updatedAt,
                        email);
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
            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            @FieldDefaults(level = AccessLevel.PRIVATE)
            public static class IdentityData {
                @JsonProperty("email")
                String email;

                @JsonProperty("email_verified")
                Boolean emailVerified;

                @JsonProperty("phone_verified")
                Boolean phoneVerified;

                @JsonProperty("sub")
                String sub;

                @Override
                public String toString() {
                    return """
                        {
    "email": "%s",
    "email_verified": %b,
    "phone_verified": %b,
    "sub": "%s"
}""".formatted(
                            email,
                            emailVerified,
                            phoneVerified,
                            sub);
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
    }
}