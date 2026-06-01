package framework;

import lombok.experimental.UtilityClass;

import java.util.Base64;

@UtilityClass //All the methods will be static in nature and no one cannot create the object of this class
public class PathUtils {

    public String encodeData(String data){
        return Base64.getEncoder().encodeToString(data.getBytes());
    }

    public String decodeData(String data){
        return new String(Base64.getDecoder().decode(data));
    }

    public String getPropertiesFolderPath()
    {
        return System.getProperty("user.dir")+"//src//test//resources//PropertiesFiles";
    }
}
