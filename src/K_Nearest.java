import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class K_Nearest {
	ReadFile file = new ReadFile();
	List<String[]> databaseStrings = file.readCSV();
	List<int[]> database = new ArrayList<int[]>();
	List<int[]> trainingSet = new ArrayList<int[]>();
	List<int[]> testSet = new ArrayList<int[]>();
	
    public int k=0;
	int trainingDataSize = databaseStrings.size() * 3 / 4;
	int numOfColumns = databaseStrings.get(0).length;
	int databaseSize= databaseStrings.size();
	
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
		garbage[0]=0;
		// this array to add to the list of the database to be accessible and changeable
		String s="";
		for (int c=0;c<numOfColumns;c++)
		{
			for(int r=0; r<databaseSize;r++)
			{
				//database.add(garbage);
				s=databaseStrings.get(r)[c];
				if(s.equals("low" )|| s.equals(" low") ||s.equals(" small") || s.equals(" 2"))
				{
					database.get(r)[c]=2;
				}
				
				else if(s.equals("med" )|| s.equals(" med")||s.equals(" 3"))
				{
					database.get(r)[c]=3;
				}
				else if(s.equals("high" )|| s.equals(" high")||s.equals(" 4"))
				{
					database.get(r)[c]=4;
				}
				else if(s.equals("vhigh" )|| s.equals(" vhigh")||s.equals(" 5more") ||s.equals(" more") ||s.equals(" big"))
				{
					database.get(r)[c]=5;
				}
				
				else if (s.equals(" unacc"))
				{
					database.get(r)[c]=1;
				}
				else if (s.equals(" acc"))
				{
					database.get(r)[c]=2;
				}
				else if (s.equals(" good"))
				{
					database.get(r)[c]=3;
				}
				else if (s.equals(" vgood"))
				{
					database.get(r)[c]=4;
				}
				else {
					System.out.println("feeh value mat7atetsh yasta ");
					}
				
			}
		}
		
		}
	void devideData() {
		transformDataToNumericValues();
		List<Integer> trainingSetIndexes = new ArrayList<Integer>();
		List<Integer> testSetIndexes     = new ArrayList<Integer>();
		// first:  fill the list of indexes rondomly
		Random rand= new Random();
		int randomNumber;
		// to fill the indexes of the randomly selected rows in training data
		while(trainingSetIndexes.size() <trainingDataSize) {
			randomNumber= rand.nextInt(databaseSize -1 )+1;
			
			if(!(trainingSetIndexes.contains(randomNumber)))
				trainingSetIndexes.add(randomNumber);
		}
		// the rest of rows in the database will be in the test set 
				
		for (int i = 0; i < database.size(); i++) {
			if(trainingSetIndexes.contains(i))
				trainingSet.add(database.get(i));
			else {
				testSet.add(database.get(i));
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
	// sorting the 2-dim array based on the distances and get the first k smallest values
	for(int i=0;i<k;i++)
	{
		for(int j=0;j<distances.length;j++)
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
		neighbors[i]=distances[i][1];
	}

	return neighbors;	
	
}

int majority(int [] n) {
	int major=0;
	Arrays.sort(n);
	int currCounter=0;
	int maxCounter =0;
	int indexOfMax=0;
	for(int i=0;i<n.length-1;i++)
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
		distancesIndexes[r][0]=distance(testRow,trainingRow);
		distancesIndexes[r][1]=r;
		
	}
	int neighborsIndexes[]= K_nearestIndexes(distancesIndexes);
	classLabel= majority(neighborsIndexes);
	
	return classLabel;
}
void printAccuracyPrecentageOfBasyianClassifier(){
	float accuracy=0;
	int counter=0;
	int sizeOfTestSetRows=0;
	sizeOfTestSetRows= testSet.size();
	int predicted;
	int found;
	int [] predictedRow;
	for(int r=0;r< sizeOfTestSetRows;r++)
	{
		found= testSet.get(r)[numOfColumns-1];
		predictedRow=trainingSet.get(r);
		predicted=labledAt(predictedRow);
		if(found==predicted)
			counter++;
	}
	accuracy=(float)(100*counter)/sizeOfTestSetRows;
	String accuracyText="\n.....The Accuracy of the K_nearest Algorithm is : "+accuracy+" %";
	System.out.println(accuracyText);
}
}
