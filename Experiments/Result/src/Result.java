import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

public class Result {
	private static final String solver[] = {"Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc"};
	private static int record[][] = new int [5][1000];
	public static void main(String args[]) throws IOException{
		
		int solverNum[] = new int[5];
		int noAns = 0;
		
		initRecord();
		
		double answer[] = new double [5];
		for(int i = 0; i < 5; i++)
			answer[i] = 0;
		
		for(int i = 1; i <= 100; i++){	
			double minT = 2147483647;
			double minO = 2147483647;
			int minOp = -1, minTp = -1;
			int objectNum = 0;
			/*if(i==92||i==7||i==4||i==12 || i==26|| i==27||i==28||i==49||i==69||i==50||i==19)	
				continue;*/
			for(int j = 0; j < 5; j++){
				//if(i == 6 && j==1)	continue;
				FileReader fr = new FileReader("C:\\Users\\Dog\\Desktop\\NEOS\\output\\parseS_" + solver[j] + " F_Dog" + i + ".txt");
				BufferedReader br=new BufferedReader(fr);
			    String line, data="";
				while ((line=br.readLine()) != null)
					data += line;
				JSONObject json;
				try {
					json = new JSONObject(data);
					String objective = (String) json.get("Objective");
					String execution = (String) json.get("Execution time");
					String usedbudget = (String) json.get("Used budget");
					String people = (String) json.get("People");
					String totalbudget = (String) json.get("Total waste");
					String onlywaste = (String) json.get("Only waste");
					double count = Double.valueOf(objective);
					double num = Double.valueOf(onlywaste);
					double peoplen = Double.valueOf(execution);
					answer[j] += peoplen;
					if(count < minO){
						minO = count;
					}
					if(num < minT){
						minT = num;
						minTp = j;
					}
					if(count == minO){
						objectNum++;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(i + " " + minTp);
					minTp = -1;
					break;
				}
			}
			if(minTp <= -1)
				noAns++;
			else{
				//if(objectNum < 5)
					//continue;
				solverNum[minTp]++;
				record[minTp][++record[minTp][0]] = i;
			}
		}
		for(int i = 0; i < 5; i++)
			System.out.println(solverNum[i]);
		for(int i = 0; i < 5; i++){
			System.out.print(solver[i]);
			for(int j = 1; j <= record[i][0]; j++)
				System.out.print(" " + record[i][j]);
			System.out.println("");
		}
		System.out.println("Error: " + noAns);
		for(int i = 0; i < 5; i++)
			System.out.println(answer[i]);
	}
	public static void initRecord(){
		for(int i = 0; i < 5; i++){
			record[i] = new int[1000];
			for(int j = 0; j < 1000; j++){
				record[i][j] = 0;
			}
		}
	}
}
