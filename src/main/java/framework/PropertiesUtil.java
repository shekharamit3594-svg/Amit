package framework;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.PropertiesConfigurationLayout;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

//We use this class to read the data from the property file
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PropertiesUtil {

    InputStreamReader inputStreamReader;
    PropertiesConfiguration propertiesConfiguration;
    PropertiesConfigurationLayout propertiesConfigurationLayout;
    static String typeOfAPI;

    @SneakyThrows //Alternate to using "throws Exception" at the method level
    public PropertiesUtil() {
         //Inputstream reader is used to deserialize the data from file to JAVA Objects
         inputStreamReader = new InputStreamReader(new FileInputStream("Config.properties"));

         //Helps us to maintain the data present in the properties file in the form of a map object
         propertiesConfiguration = new PropertiesConfiguration();
         propertiesConfigurationLayout = new PropertiesConfigurationLayout();

         //PropertiesConfigurationLayout helps us in loading the data from the properties file to the PropertiesConfiguration Object
         propertiesConfigurationLayout.load(propertiesConfiguration,inputStreamReader);
    }

    private String getProperty(String key) {
        return propertiesConfiguration.getString(key,key+" not found");
    }

    public String getTypeOfAPI()
    {
        typeOfAPI = getProperty("TypeOfAPI");
        return typeOfAPI;
    }


    /****************************************************************************************/

    public static class BankingEndPoints {

        @SneakyThrows
        private PropertiesConfiguration getAPIBasedConfiguration()
        {
            InputStreamReader isr=new InputStreamReader(new FileInputStream(PathUtils.getPropertiesFolderPath()+"//"+typeOfAPI+"Config.properties"));
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
            PropertiesConfigurationLayout propertiesConfigurationLayout = new PropertiesConfigurationLayout();

            propertiesConfigurationLayout.load(propertiesConfiguration,isr);
            return propertiesConfiguration;
        }

        private String getProperty_API(String key)
        {
            return getAPIBasedConfiguration().getString(key,key+" not found");
        }

        public String getUserName() {
            return getProperty_API("UserName");
        }

        public String getPassword() {
            return getProperty_API("Password");
        }

        public String getLoginURL() {
            return getProperty_API("LoginURL");
        }
        public String getBaseURL() {
            return getProperty_API("BaseURL");
        }

        public String getCreateAccountEndPoint() {
            return getProperty_API("CreateAccount");
        }

        public String getViewAccountEndPoint() {
            return getProperty_API("ViewAccount");
        }

        public String getListAccountEndPoint() {
            return getProperty_API("ListAccounts");
        }

        public String getModifyAccountEndPoint() {
            return getProperty_API("ModifyAccount");
        }

        public String getDeleteAccountEndPoint() {
            return getProperty_API("DeleteAccount");
        }
    }

}
