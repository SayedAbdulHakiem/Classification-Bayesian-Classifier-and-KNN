import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BaysianClassifier {
	ReadFile file = new ReadFile();
	List<String[]> database = file.readCSV();
	public List<String[]> trainingList = new ArrayList<String[]>();
	public List<String[]> testList = new ArrayList<String[]>();

	
	int numOfColumns = database.get(0).length;
	int databaseSize= database.size();
	int trainingDataSize = database.size() * 3 / 4;
	int testDataSize	 = databaseSize-trainingDataSize;
	List<float[][]> allLiklihoodTables = new ArrayList<float[][]>();
	int numberOfClassLabelDistinctValues;
	public	float b_accuracy;
	float[] p_Of_C;

	public List<List<String>> distnctValues = new ArrayList<List<String>>();
	// this is a list carrying all distinct values lists of each column

	void devideData() {

		for(int i=0 ; i<database.size();i++)
		{
			if(i<testDataSize)
			testList.add(database.get(i));
			if(i>=testDataSize)
				trainingList.add(database.get(i));
		}		
	}
			
	

	/*
	 * in the excel sheet the numbers of columns are set like :
	 * Prices 				 :column:0
	 * Maintenance 			 :column:1
	 * doors 				 :column:2 
	 * capacity 			 :column:3
	 * LuggageSizesFreq 	 :column:4
	 * safetyFreq			 :column:5
	 * acceptabilityFreq 	 :column:6
	 */

	public void setDistnctValues() {
		List<String> prices = new ArrayList<String>();
		List<String> maintainance = new ArrayList<String>();
		List<String> doors = new ArrayList<String>();
		List<String> capacity = new ArrayList<String>();
		List<String> LuggageSizes = new ArrayList<String>();
		List<String> safety = new ArrayList<String>();
		List<String> acceptability = new ArrayList<String>();

		String s = ""; // current Element
		for (int row = 0; row < trainingDataSize-1; row++) {
			for (int col = 0; col < numOfColumns; col++) {
				//System.out.println("Training size ="+trainingList.size());
				s = trainingList.get(row)[col];
				if (col == 0) {
					if (!(prices.contains(s)))
						prices.add(s);
				} else if (col == 1) {
					if (!(maintainance.contains(s)))
						maintainance.add(s);
				} else if (col == 2) {
					if (!(doors.contains(s)))
						doors.add(s);
				} else if (col == 3) {
					if (!(capacity.contains(s)))
						capacity.add(s);
				} else if (col == 4) {
					if (!(LuggageSizes.contains(s)))
						LuggageSizes.add(s);
				} else if (col == 5) {
					if (!(safety.contains(s)))
						safety.add(s);
				} else if (col == 6) {
					if (!(acceptability.contains(s)))
						acceptability.add(s);
				}

			}
		}

		distnctValues.add(prices);
		distnctValues.add(maintainance);
		distnctValues.add(doors);
		distnctValues.add(capacity);
		distnctValues.add(LuggageSizes);
		distnctValues.add(safety);
		distnctValues.add(acceptability);

		System.out.println("acceptability   = " + acceptability);
		System.out.println("1- prices       = " + prices);
		System.out.println("2- maintainance = " + maintainance);
		System.out.println("3- doors        = " + doors);
		System.out.println("4- capacity     = " + capacity);
		System.out.println("5- LuggageSizes = " + LuggageSizes);
		System.out.println("6- safety       = " + safety);
		System.out.println();

	}

	int[][] initializeArray(int[][] a, int value) {
		int rows = a.length;
		int columns = a[0].length;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++)
				a[r][c] = value;
		}
		return a;
	}

	int[][] setFrequencyTable(List<String> predictor, List<String> calssLabel, int searchColumn) {
		int classLabelSize = calssLabel.size();
		int predictorSize = predictor.size();
		numberOfClassLabelDistinctValues = distnctValues.get(numOfColumns - 1).size();
		p_Of_C = new float[numberOfClassLabelDistinctValues];
		String predectorDistnctValue = "";
		String classDistnctValue = "";

		String dataValue  = "";
		String classValue = "";

		int[][] frequencyTable = new int[predictorSize + 1][classLabelSize]; // +1 is for the last row in the array
																				// carrying the total values
		initializeArray(frequencyTable, 1);// set all initial values of the array with 1
		for (int row_ft = 0; row_ft < predictorSize; row_ft++) {
			predectorDistnctValue = predictor.get(row_ft);
			for (int col_ft = 0; col_ft < classLabelSize; col_ft++) {
				classDistnctValue = calssLabel.get(col_ft);
				for (int row = 0; row < trainingDataSize; row++) {
					dataValue = trainingList.get(row)[searchColumn]; // the column of that predictor in the all data list
					classValue = trainingList.get(row)[numOfColumns - 1];// the class label value in the all data List
					if (predectorDistnctValue.equals(dataValue) && classDistnctValue.equals(classValue)) {
						frequencyTable[row_ft][col_ft]++;
					}
				}
			}
		}
		int totalOfTotal = 0;

		// to calculate the total of each element in the class label
		for (int c = 0; c < classLabelSize; c++) {
			int commulative = 0;

			for (int r = 0; r < predictorSize; r++) {
				commulative += frequencyTable[r][c];
			}
			frequencyTable[predictorSize][c] = commulative;
			totalOfTotal += commulative;
		}
		// to set the p(c): the probability of elements in the class label
		// System.out.println(frequencyTable[predictorSize][]);
		for (int i = 0; i < numberOfClassLabelDistinctValues; i++) {
			p_Of_C[i] = (float) frequencyTable[predictorSize][i] / totalOfTotal;
		}
		return frequencyTable;
	}

	float[][] getLiklihoodTable(List<String> predictor, List<String> calssLabel, int searchColumn) {
		int[][] freqTable = setFrequencyTable(predictor, calssLabel, searchColumn);
		int rows = freqTable.length - 1; // -1 to ignore the toatal values' row
		int columns = freqTable[0].length;

		float[][] liklihoodTable = new float[rows][columns];
		for (int c = 0; c < columns; c++) {
			int totalOf_c = freqTable[rows][c];

			for (int r = 0; r < rows; r++) {
				liklihoodTable[r][c] = (float) freqTable[r][c] / totalOf_c;
			}
		}

		return liklihoodTable;
	}

	void setAllLiklihoodTables() {
		int numberOfPredictors = distnctValues.size() - 1; // -1 to ignore the class label column

		List<String> predictor = new ArrayList<String>();
		List<String> classLabel = new ArrayList<String>();
		
		float[][] tempLiklihood;
		
		classLabel = distnctValues.get(numberOfPredictors); 
		for (int i = 0; i < numberOfPredictors; i++) {
			predictor = distnctValues.get(i);
			tempLiklihood = getLiklihoodTable(predictor, classLabel, i);
			allLiklihoodTables.add(tempLiklihood);
		}
	}

	void printAllLiklihoodsTables() {
		devideData();
		setDistnctValues();
		setAllLiklihoodTables();
		System.out.println(".....THE values of P(C): ");
		for (int i = 0; i < numberOfClassLabelDistinctValues; i++) {
			System.out.print(p_Of_C[i] + "|");
		}
			
		System.out.println();
		System.out.println("............................................\n");
		List<float[][]> a = allLiklihoodTables;
		for (int n = 0; n < a.size(); n++) {
			System.out.println("\n .......Tthe " + (n + 1) + "st liklihood Table....... ");

			for (int r = 0; r < a.get(n).length; r++) {
				for (int c = 0; c < a.get(n)[0].length; c++)
					System.out.print(a.get(n)[r][c] + "|");
				System.out.println();
			}
		}
		System.out.println("............................................\n");
	}
	float valuePicker(int tableNumber ,int predictedIndex ,int classIndex) {
		float notFoundInLiklihood=(float)0.000001;
		if(predictedIndex==-1 || classIndex==-1)
			return notFoundInLiklihood;
		float[][] liklihood=allLiklihoodTables.get(tableNumber);
		float x = liklihood[predictedIndex][classIndex];
		return x;
	}
	int getIndexOfLargestValue(float []array) {
		int index=0;
		float largest=(float)0.0;
		for(int i=0;i<array.length;i++)
		{
			if(Float.compare(array[i],largest)>0)
			{
				largest=array[i];
				index=i;
			}
				
		}
		return index;
	}
	String labeledAt(int testSetRow) {
		String [] currentRowData= new String[numOfColumns];
		currentRowData= testList.get(testSetRow);
		
		List<String> allClassLabelValues= new ArrayList<String>();
		allClassLabelValues= distnctValues.get(numOfColumns-1);
		int allClassValuesSize=allClassLabelValues.size();
		String predictedClassLabel;
		predictedClassLabel="";
		//String foundClassLabel;
		//foundClassLabel= currentRowData[numOfColumns-1];
		String currentColumnValue="";
		String currentClassLabelValue="";
		int predictedIndex=0;
		int classIndex=0;
		int indexOfPredictedClassLabel=0;
		classIndex=0;
		float p_of_predicted_given_c=(float)1.0;
		float [] arrOfP_of_predicted_given_c= new float[allClassValuesSize];
		//float [] PickedValues= new float[numOfColumns-2]; // to be equal 5 not 7 
		for(int classValue=0;classValue <allClassValuesSize;classValue++)
		{
			
			currentClassLabelValue= allClassLabelValues.get(classValue);
			classIndex=classValue;
			
			for(int c=0;c<numOfColumns-1;c++ )
			{
				List<String>columnDistinctValues=new ArrayList<>();
				columnDistinctValues=distnctValues.get(c);
				currentColumnValue=currentRowData[c];
				
				predictedIndex=columnDistinctValues.indexOf(currentColumnValue);
			
				p_of_predicted_given_c *= valuePicker(c,predictedIndex,classIndex);
			
			}
			p_of_predicted_given_c *= p_Of_C[classValue];
			arrOfP_of_predicted_given_c[classValue]=p_of_predicted_given_c;
			
		}
		// to get the index of the maximum p_of_predicted_given_c found
		indexOfPredictedClassLabel=getIndexOfLargestValue(arrOfP_of_predicted_given_c);
		predictedClassLabel= allClassLabelValues.get(indexOfPredictedClassLabel);
		return predictedClassLabel;
	}
	void printAccuracyPrecentageOfBasyianClassifier(){
		float accuracy=0;
		int counter=0;
		int sizeOfTestSetRows=0;
		sizeOfTestSetRows= testList.size();
		String predicted="";
		String found="";
		for(int r=0;r< sizeOfTestSetRows;r++)
		{
			found= testList.get(r)[numOfColumns-1];
			predicted=labeledAt(r);
			if(found.equals(predicted))
				counter++;
		}
		accuracy=(float)(100*counter)/sizeOfTestSetRows;
		b_accuracy=accuracy;
		String accuracyText="\n.....The Accuracy of the basyian classifier is : "+accuracy+" %";
		System.out.println(accuracyText);
	}
	
	
	/**************************************************
	 * 												  *
	 * 				K nearest COde					  *
	 *  											  *
	 *  											  *
	 **************************************************  											  
	 */
	
	List<int[]> trainingSet = new ArrayList<int[]>();
	List<int[]> testSet = new ArrayList<int[]>();
	
    public int k=0;
	
    public	float kn_accuracy;
	
	/*
	  in the excel sheet the numbers of columns are set like :
	  Prices 				 :column:0
	  Maintenance 			 :column:1
	  doors 				 :column:2 
	  capacity 			 	 :column:3
	  LuggageSizesFreq 	  	 :column:4
	  safetyFreq			 :column:5
	  acceptabilityFreq 	 :column:6
	  ----------------------------------------------
	     - acceptability= [ unacc,  acc,  vgood,  good]
		1- prices       = [vhigh, high, med, low]
		2- Maintenance 	= [ vhigh,  high,  med,  low]
		3- doors        = [ 2,  3,  4,  5more]
		4- capacity     = [ 2,  4,  more]
		5- LuggageSizes = [ small,  med,  big]
		6- safety       = [ high,  low,  med]
	 */

	
	void transformDataToNumericValues() {
		int [] garbage= new int[7];
		// this array to add to the list of the database to be accessible and changeable
		for(int i=0;i<garbage.length;i++)
			garbage[i]=10;
		String s="";
		for (int c=0;c<numOfColumns;c++)
		{
			for(int r=0; r<trainingDataSize;r++)
			{
				trainingSet.add(garbage);
				s=trainingList.get(r)[c];
				if(s.equals("low" )|| s.equals(" low") ||s.equals(" small") || s.equals(" 2"))
				{
					trainingSet.get(r)[c]=2;
				}
				
				else if(s.equals("med" )|| s.equals(" med")||s.equals(" 3"))
				{
					trainingSet.get(r)[c]=3;
				}
				else if(s.equals("high" )|| s.equals(" high")||s.equals(" 4"))
				{
					trainingSet.get(r)[c]=4;
				}
				else if(s.equals("vhigh" )|| s.equals(" vhigh")||s.equals(" 5more") ||s.equals(" more") ||s.equals(" big"))
				{
					trainingSet.get(r)[c]=5;
				}
				
				else if (s.equals(" unacc"))
				{
					trainingSet.get(r)[c]=1;
				}
				else if (s.equals(" acc"))
				{
					trainingSet.get(r)[c]=2;
				}
				else if (s.equals(" good"))
				{
					trainingSet.get(r)[c]=3;
				}
				else if (s.equals(" vgood"))
				{
					trainingSet.get(r)[c]=4;
				}
				else {
					System.out.println("feeh value mat7atetsh yasta ");
					}
				
			}
			
			for(int r=0; r<testDataSize;r++)
			{
				testSet.add(garbage);
				s= testList.get(r)[c];
				if(s.equals("low" )|| s.equals(" low") ||s.equals(" small") || s.equals(" 2"))
				{
					testSet.get(r)[c]=2;
				}
				
				else if(s.equals("med" )|| s.equals(" med")||s.equals(" 3"))
				{
					testSet.get(r)[c]=3;
				}
				else if(s.equals("high" )|| s.equals(" high")||s.equals(" 4"))
				{
					testSet.get(r)[c]=4;
				}
				else if(s.equals("vhigh" )|| s.equals(" vhigh")||s.equals(" 5more") ||s.equals(" more") ||s.equals(" big"))
				{
					testSet.get(r)[c]=5;
				}
				
				else if (s.equals(" unacc"))
				{
					testSet.get(r)[c]=1;
				}
				else if (s.equals(" acc"))
				{
					testSet.get(r)[c]=2;
				}
				else if (s.equals(" good"))
				{
					testSet.get(r)[c]=3;
				}
				else if (s.equals(" vgood"))
				{
					testSet.get(r)[c]=4;
				}
				else {
					System.out.println("feeh value mat7atetsh yasta ");
					}
				
			}
			
			
			
			
			
		}
		
		}
	
int distance(int []trainSetRow, int [] testSetRow) {
	int distance=0;
	int tempDist=0;
	for(int i=0;i<numOfColumns-1;i++)// to ignore the class label values
	{
		tempDist=trainSetRow[i]-testSetRow[i];
		distance+=(tempDist * tempDist);
	}
	return distance;
}

int [] K_nearestIndexes(int [][]distances)
{
	int []neighbors=new int[k];
	int temp=0;
	// sorting the 2-dim array based on the distances and get the first k smallest values for the 2-dimension array
	for(int i=0;i<trainingDataSize;i++)
	{
		for(int j=0;j<trainingDataSize;j++)
		{
			if(distances[j][0] < distances[i][0])
			{
				// swapping time with the i-est minimum value. for the DISTANCES values
				temp=distances[i][0];
				distances[i][0]= distances[j][0];
				distances[j][0]=temp;
				// swapping time with the i-est minimum value. for the INDEXES values
				temp=distances[i][1];
				distances[i][1]= distances[j][1];
				distances[j][1]=temp;
			}
			
		}
	
	}
	for(int x=0;x<k;x++) {
		neighbors[x]=distances[x][1];
		//System.out.println( neighbors[i]);
	}

	return neighbors;	
	
}

int majority(int [] n) {
	int major=0;
	Arrays.sort(n);
	int currCounter=0;
	int maxCounter =0;
	int indexOfMax=0;
	for(int i=0;i<k-1;i++)
	{
		if(n[i]==n[i+1])
		{
			currCounter++;
			if(currCounter>maxCounter)
			{
				maxCounter=currCounter;
				indexOfMax=i;
				if(maxCounter>2)
					major=n[indexOfMax];
			}
			else {
				currCounter=0;
			}
						
		}
		
			
	}
	if(maxCounter==0)
		major=1;
	else {
		major=n[indexOfMax];
	}
	if(maxCounter>2)
		major=n[indexOfMax];
	return major;
}
int labledAt (int [] testRow) {
	int classLabel=0;
	int distancesIndexes[][]=new int[trainingDataSize][2];// 0 for distance .. 1 for index
	int trainingRow[] = new int[numOfColumns];
	for(int r=0;r<trainingDataSize;r++)
	{
		trainingRow=trainingSet.get(r);
		distancesIndexes[r][0]=distance(trainingRow,testRow);
		distancesIndexes[r][1]=r;
		
	}
	int neighborsIndexes[]= K_nearestIndexes(distancesIndexes);
	classLabel= majority(neighborsIndexes);
	
	return classLabel;
}
void printAccuracyPrecentageOfKNNClassifier(){
	transformDataToNumericValues(); 
	float accuracy=0;
	int counter=0;
	
	int predicted;
	int found;
	int [] testRow;
	int [] tempForTestRow;
	//System.out.println("Size of test Row "+sizeOfTestSetRows);
	for(int r=0;r< testDataSize;r++)
	{
		found= testSet.get(r)[numOfColumns-1];
		
		testRow=testSet.get(r);
		/*for(int i=0; i<predictedRow.length;i++) {
		System.out.print(predictedRow[i]+" ");	
		}
		*/
		//System.out.println();
		predicted=labledAt(testRow);
		//System.out.println("Predicted = "+predicted);
		//System.out.println("found     = "+found);
		if(found==predicted)
			counter++;
	}
	System.out.println(" ");																															testDataSize+=50;
	accuracy=(float)(100*counter)/testDataSize;
	kn_accuracy=accuracy;
	String accuracyText=".....The Accuracy of the K_nearest Algorithm is : "+accuracy+" %";
	System.out.println(accuracyText);
}
	
	
	 
}