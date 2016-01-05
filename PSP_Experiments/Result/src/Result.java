import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class Result {
	private static final String solver[] = {"Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc"};
	public static void main(String args[]) throws IOException{
		
		int solverNum[] = new int[5];
		int noAns = 0;
		for(int i = 1; i <= 100; i++){	
			double minT = 2147483647;
			double minO = 2147483647;
			int minOp = -1, minTp = -1;
			for(int j = 0; j < 5; j++){
				FileReader fr = new FileReader("C:\\Users\\Dog\\Desktop\\NEOS\\Case1_output\\parseS_" + solver[j] + " F_Dog" + i + ".txt");
				BufferedReader br=new BufferedReader(fr);
			    String line, data="";
				while ((line=br.readLine()) != null)
					data += line;
				JSONObject json;
				try {
					json = new JSONObject(data);
					String objective = (String) json.get("Objective");
					String usedbudget = (String) json.get("Used budget");
					String people = (String) json.get("People");
					String totalbudget = (String) json.get("Total waste");
					String onlywaste = (String) json.get("Only waste");
					double num = Double.valueOf(onlywaste);
					if(num < minT){
						minT = num;
						minTp = j;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(i + " " + minTp);
				}
				
			}
			if(minTp < -1)
				noAns++;
			else
				solverNum[minTp]++;
		}
		for(int i = 0; i < 5; i++)
			System.out.println(solverNum[i]);
		System.out.println("Error: " + noAns);
	}
}
