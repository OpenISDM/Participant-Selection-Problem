import java.io.FileWriter;
import java.io.IOException;

import org.neos.client.ResultCallback;
import org.neos.gams.SolutionData;
import org.neos.gams.SolutionParser;
import org.neos.gams.SolutionRow;

public class JobResult implements ResultCallback {
	String parseName;
	public JobResult(String pID){
		parseName = "parse";
		parseName += pID + ".txt";
	}
	
	public void handleJobInfo(int jobNo, String pass) {
		System.out.println("Job Number : " + jobNo);
		System.out.println("Password   : " + pass);
	}
	
	static double totalpeople;
	static int region;
	double totalwaste = 0;
	double onlywaste = 0;
	
	public void handleFinalResult(String results){
		
		final int location_Num = 50;
		final int agent_Num = 1000;
		
		
		/* write into result text files (origin information) */
		try {
			FileWriter fw = new FileWriter("result.txt");
			fw.write(results);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* parse information from NEOS results */
		try{
			FileWriter fw = new FileWriter("C:\\Users\\Dog\\Desktop\\NEOS\\output\\" + parseName);
			SolutionParser parser = new SolutionParser(results);
			//System.out.printf("Objective :%f \n\n", parser.getObjective());
			fw.write("{\"Objective\" : " + "\"" + parser.getObjective() + "\",\r\n");
			
			
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
			fw.write("\"People\": " + "\"" + people.getRows().get(0).getLevel() + "\",\r\n");
			
			
			//waste
			SolutionData region = parser.getSymbol("y", SolutionData.VAR, 1);
			SolutionData waste = parser.getSymbol("sub5", SolutionData.EQU, 1);
			for(int i = 0; i < region.getRows().size(); i++){
				double temp = region.getRows().get(i).getLevel();
				double spend = waste.getRows().get(i).getLevel();
				if(temp == 1.0)
					onlywaste += spend;
				totalwaste += spend;
			}
			
			fw.write("\"Total waste\": " + "\"" + totalwaste + "\",\r\n\"Only waste\": " + "\"" + onlywaste +"\"}\r\n");
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\nSolver Done!!");

	}
}