package framework;

//This is generally used to maintain the paths of the framework like excel file paths, resource folder paths etc..

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathUtils {

    @SneakyThrows
    public void applySleep(int sec)
    {
        Thread.sleep(sec*1000);
    }

    @SneakyThrows
    public void applySleep(long sec)
    {
        Thread.sleep(sec);
    }
}
