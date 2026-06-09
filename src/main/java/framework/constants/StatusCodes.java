package framework.constants;

import lombok.Getter;

@Getter
public enum StatusCodes {

    SUCCESS(200),
    CREATED(201),
    PAGE_NOT_FOUND(404),
    BAD_REQUEST(400),
    UNAUTHORIZED(401),
    METHOD_NOT_ALLOWED(405),
    DUPLICATE_DATA(409),
    NO_CONTENT(204);

    int statusCode;

    StatusCodes(int statusCode) {
        this.statusCode = statusCode;
    }
}
