package pojo.request.login;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import pojo.request.RequestPOJO;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
public class LoginRequest implements RequestPOJO {

    String email;
    String password;

}
