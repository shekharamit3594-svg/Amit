package framework;

import java.util.Base64;

public class PathUtils {

    public static String encodeData(String data){
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public static String decodeData(String data){
        return new String(Base64.getDecoder().decode(data));
    }
}
