package framework;

import com.google.common.collect.ArrayListMultimap;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class TestUtil {

    Map<String, String> bufferData=new HashMap<>();

    //For a single key, it can store a collection of values
    ArrayListMultimap<String, String> groups=ArrayListMultimap.create();

    public void setData(String key, String value) {
        bufferData.put(key, value);
    }

    public String getData(String key) {
        return bufferData.getOrDefault(key,key+" not found");
    }

    public void setData_AccountNumbers(String key, String value) {
        groups.put(key,value);
    }

    public List<String> getData_AccountNumbers(String key) {
        return groups.get(key);
    }
}
