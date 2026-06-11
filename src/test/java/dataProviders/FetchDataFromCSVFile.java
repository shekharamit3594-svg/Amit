package dataProviders;

import com.opencsv.CSVReader;
import framework.PathUtils;
import lombok.SneakyThrows;
import org.testng.annotations.DataProvider;

import java.io.FileReader;
import java.util.*;

public class FetchDataFromCSVFile {

    @DataProvider(name = "sampleData")
    @SneakyThrows
    public Iterator<Map<String, String>> readDataFromCSVFile()
    {
        CSVReader reader = new CSVReader(new FileReader(PathUtils.getTestDataPath()));

        String[] columns = reader.readNext();
        List<String[]> completeSetOfData=reader.readAll();

        //Array to a List<Map<String,String>>

        //Stores all the rows of data
        List<Map<String,String>> data=new ArrayList<>();

        for(int i=0;i<completeSetOfData.size();i++)
        {
            Map<String,String> map=new HashMap<>(); //Storing the information for that particular row
            String[] rowOfData=completeSetOfData.get(i);
            for(int j=0;j<rowOfData.length;j++)
            {
                map.put(columns[j],rowOfData[j]);
            }

            data.add(map);
        }

        reader.close();
        return data.iterator();
    }
}
