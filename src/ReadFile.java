import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

class ReadFile {
	// 1-this class reads an excel(csv) file into an arraylist of arrayStrings.
	List<String[]> AllData = new ArrayList<String[]>();
	

	public List<String[]> readCSV() {
		List<String[]> D= new ArrayList<String[]>();

		try {
			// Create csvReader object and skip first line
			FileReader filereader = new FileReader("car_data.csv");
			CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
			// Read all data in the csv and set each line in string[i]= 11;22;33

			AllData = csvReader.readAll();

			for (int i = 0; i < AllData.size(); i++) {
				String currentLine = Arrays.toString(AllData.get(i));
				// the string currentLine carries now "[1,2,3,4,5"
				String currentLineNew = currentLine.substring(currentLine.indexOf('[') + 1,
						currentLine.lastIndexOf(']'));
				// the string currentLineNew carries now "1, 2, 3, 4, 5"
				
				String[] valuesOfEachLine = currentLineNew.split(",");
				
				D.add(valuesOfEachLine);
				
			}
			csvReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return D;
	}

}