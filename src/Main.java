import java.io.ObjectInputStream.GetField;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.builder.CompareToBuilder;

public class Main {
	static void compareAccuracy(float b,float k) {
		System.out.println(".......to compare The Accuracy Of the Two Algorithms......");
		System.out.println("........ The Accuracy of  Basyian classifier is    : "+b+" %");
		System.out.println("........ The Accuracy of  K_nearest classifier is  : "+k+" %");
		
		if(Float.compare(b,k)>0)
		{
			System.out.println("/n.... so The Accuracy Of Basyian is higher than KNN.....");
		}
		else {
			System.out.println(".... so The Accuracy Of KNN is higher than Basyian.....");
		}
	}
	public static void main(String[] args) {
	ReadFile rf= new ReadFile();
	rf.readCSV();
	
	BaysianClassifier basyian=new BaysianClassifier();
	System.out.println("............. BASYIAN ALGORITHM .........");
	basyian.printAllLiklihoodsTables();
	basyian.printAccuracyPrecentageOfBasyianClassifier();
	System.out.println("............. K_NEAREST ALGORITHM .........");
	
	basyian.transformDataToNumericValues();
	//Scanner s= new Scanner(System.in);
	//System.out.println(" ENTER The K Value :");
	//basyian.k= s.nextInt();
	basyian.k=5;
	basyian.printAccuracyPrecentageOfKNNClassifier();
	compareAccuracy(basyian.b_accuracy,basyian.kn_accuracy);
	
	}
}
