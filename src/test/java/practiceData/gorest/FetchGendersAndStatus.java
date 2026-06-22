package practiceData.gorest;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class FetchGendersAndStatus {

    public static String getGender()
    {
        List<String> genders= List.of("male","female");
        return genders.get(ThreadLocalRandom.current().nextInt(0,genders.size()));
    }

    public static String getStatus()
    {
        List<String> statuses= List.of("active","inactive");
        return statuses.get(ThreadLocalRandom.current().nextInt(0,statuses.size()));
    }
}
