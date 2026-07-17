package framework;

//This is generally used to maintain the paths of the framework like excel file paths, resource folder paths etc..

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public String getCurrentDateTime(String format)
    {
        //DateTimeFormatter is a library that is present since JDK 8 to understand the date time format that you have passed
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        LocalDateTime now = LocalDateTime.now();

        return dtf.format(now); //Format the current date time as per the desired requirement
    }

    public String getScreenshotPath(String... imgPath)
    {
        File f1=new File(System.getProperty("user.dir")+"//Screenshots");
        f1.mkdirs();

        if(imgPath.length==0)
        {
            return f1.getPath()+"//"+getCurrentDateTime("yyyy-MM-dd hh-mm-ss");
        }

        else
        {
            return f1.getPath()+"//"+imgPath[0];
        }
    }
}
