package framework;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;

import java.io.FileInputStream;
import java.io.InputStreamReader;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PropertiesUtil {

    InputStreamReader inputStreamReader;
    PropertiesConfiguration propertiesConfiguration;
    PropertiesConfigurationLayout propertiesConfigurationLayout;

    @SneakyThrows
    public PropertiesUtil() {
        inputStreamReader = new InputStreamReader(new FileInputStream("Config.properties"));
        propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfigurationLayout=new PropertiesConfigurationLayout();

        propertiesConfigurationLayout.load(propertiesConfiguration,inputStreamReader);
    }

    private String getProperty(String key){
        return propertiesConfiguration.getString(key,key+" not found");
    }

    public String getBrowser()
    {
        return getProperty("Browser");
    }

    public String getURL()
    {
        return getProperty("URL");
    }

    public String closeBrowser()
    {
        return getProperty("CloseBrowser");
    }

    public String killBrowsers()
    {
        return getProperty("KillBrowsers");
    }
}
