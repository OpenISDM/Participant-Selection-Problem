import java.io.FileWriter;
import java.io.IOException;

import org.neos.client.ResultCallback;
import org.neos.gams.SolutionData;
import org.neos.gams.SolutionParser;
import org.neos.gams.SolutionRow;

public class JobReceiver implements ResultCallback {
	String parseName;
	public JobReceiver(String pID){
		parseName = "parse";
		parseName += pID + ".txt";
	}
	
	public void handleJobInfo(int jobNo, String pass) {
		System.out.println("Job Number : " + jobNo);
		System.out.println("Password   : " + pass);
		try {
			FileWriter fw = new FileWriter("C:\\Users\\JaneLab_2\\Desktop\\Results\\IDtemp\\output\\" + parseName);
			fw.write("Job Number : " + jobNo + "\r\n");
			fw.write("Password : " + pass + "\r\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	static double totalpeople;
	static int region;
	double totalwaste = 0;
	double onlywaste = 0;
	
	public void handleFinalResult(String results){
		
		/* parse information from NEOS results */
		try{
			FileWriter fw = new FileWriter("C:\\Users\\JaneLab_2\\Desktop\\Results\\output\\" + parseName);
			SolutionParser parser = new SolutionParser(results);
			//System.out.printf("Objective :%f \n\n", parser.getObjective());
			fw.write("{\"Objective\" : " + "\"" + parser.getObjective() + "\",\r\n");
			
			//Execution time
			int timeStart = results.indexOf("RESOURCE USAGE, LIMIT");
			int timeEnd = results.indexOf("ITERATION COUNT, LIMIT");
			int pS = timeStart + 21;
			int pE = timeEnd - 5;
			while(results.charAt(pS) == ' ')
				pS++;
			if(pE < 0)
				System.out.println(pE);
			while(results.charAt(pE) != ' ')
				pE--;
			
			timeStart = pS;
			timeEnd = pE;
			String execute = results.substring(timeStart, timeEnd);
			fw.write("\"Execution time\": " + "\"" + execute + "\",\r\n");
			
			/* agent is assigned to location */
			//System.out.println("================Agent assigned to location================");
			/*fw.write("\n=============Participant assigned to location=============\r\n");
			SolutionData participant2region = parser.getSymbol("x", SolutionData.VAR, 2);
			for(SolutionRow row : participant2region.getRows()) {
				if(!(row.getLevel().toString().equals("0.0")))
					fw.write(row.getIndex(0) + " is assigned to " + row.getIndex(1) + "\r\n");
					//System.out.printf("%s is assigned to %s\n" , row.getIndex(0),row.getIndex(1));
			}*/
			
			//cost
			SolutionData cost = parser.getSymbol("usebudget", SolutionData.VAR, 0);
			fw.write("\"Used budget\": " + "\"" + cost.getRows().get(0).getLevel() + "\",\r\n");
			
			
			//people
			SolutionData people = parser.getSymbol("people", SolutionData.VAR, 0);
			fw.write("\"People\": " + "\"" + people.getRows().get(0).getLevel() + "\"}\r\n");
			
			/*	
			//waste
			SolutionData region = parser.getSymbol("y", SolutionData.VAR, 1);
			SolutionData waste = parser.getSymbol("sub5", SolutionData.EQU, 1);
			
			int REGION = region.getRows().size();
			double fullregion[] = new double[REGION];
			double onlyregion [] = new double[REGION];
			
			for(int i = 0; i < region.getRows().size(); i++){
				double temp = region.getRows().get(i).getLevel();
				double spend = waste.getRows().get(i).getLevel();
				if(temp == 1.0){
					onlywaste += spend;
					onlyregion[i] += spend;
				}
				totalwaste += spend;
				fullregion[i] += spend;
			}
			
			fw.write("\"Total waste\": " + "\"" + totalwaste + "\",\r\n\"Only waste\": " + "\"" + onlywaste +"\",\r\n");
			for(int i = 0; i < region.getRows().size()-1; i++)
				fw.write("\"Region " +(i+1) + " Only Region" + "\": " + "\"" + onlyregion[i] + "\",\r\n\"Region " +(i+1) + " Full region" + "\": " + "\"" + fullregion[i] +"\",\r\n");
			for(int i = region.getRows().size()-1; i < region.getRows().size(); i++)
				fw.write("\"Region " +(i+1) + " Only Region" + "\": " + "\"" + onlyregion[i] + "\",\r\n\"Region " +(i+1) + " Full region" + "\": " + "\"" + fullregion[i] +"\"}\r\n");
				*/
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\nSolver Done!!");

	}
}