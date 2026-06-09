package dataProviders;

import com.opencsv.CSVReader;
import lombok.SneakyThrows;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public class ReadingDataFromCSVFile {

    @SneakyThrows //Alternate of using throws Exception (Coming from lombok library)
    void main()
    {
        CSVReader csvReader = new CSVReader(new FileReader("sample_data.csv"));

        //Skips the first row of data
        csvReader.skip(1);

        String[] data=csvReader.readNext(); //Read the first row of data
        IO.println(Arrays.toString(data));

        data=csvReader.readNext(); //Reads the second row of data
        IO.println(Arrays.toString(data));

        //.readAll() means it will read the complete data or the remaining set of data
        List<String[]> remainingSetOfData=csvReader.readAll();
        remainingSetOfData.forEach(s->IO.println(Arrays.toString(s)));

        //Closing the CSVReader Object
        csvReader.close();
    }
}
