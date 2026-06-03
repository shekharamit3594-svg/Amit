package framework;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestUtil {

    Map<String, String> bufferData=new HashMap<>();

    public void setData(String key, String value) {
        bufferData.put(key, value);
    }

    public String getData(String key) {
        return bufferData.getOrDefault(key,key+" not found");
    }
}
