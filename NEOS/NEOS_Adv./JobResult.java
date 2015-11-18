import java.io.FileWriter;
import java.io.IOException;

import org.neos.client.ResultCallback;
import org.neos.gams.SolutionData;
import org.neos.gams.SolutionParser;
import org.neos.gams.SolutionRow;

public class JobResult implements ResultCallback {
	public void handleJobInfo(int jobNo, String pass) {
		System.out.println("Job Number : " + jobNo);
		System.out.println("Password   : " + pass);
	}
	public void handleFinalResult(String results){
		
		final int location_Num = 10;
		final int agent_Num = 100;
		
		
		/* write into result text files (origin information) */
		try {
			FileWriter fw = new FileWriter("results.txt");
			fw.write(results);
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* parse information from NEOS results */
		try{
			FileWriter fw = new FileWriter("parser.txt");
			SolutionParser parser = new SolutionParser(results);
			//System.out.printf("Objective :%f \n\n", parser.getObjective());
			fw.write("Objective : " + parser.getObjective() + "\n\n");
			
			/* agent assigned check 1: assign, 0: not assign */
			//System.out.println("================Agent assigned check================");
			fw.write("\n================Agent assigned check================\n");
			SolutionData agent = parser.getSymbol("sub2", SolutionData.EQU, 1);
			for(int i = 0; i < agent_Num; i++){
				SolutionRow row = agent.getRows().get(i);
				//System.out.printf("Agent %d : %f\n", i + 1, row.getLevel());
				fw.write("Agent " + (i + 1) + " : " + row.getLevel() + "\n");
			}
			
			/* agent is assigned to location */
			//System.out.println("================Agent assigned to location================");
			fw.write("\n=============Agent assigned to location=============\n");
			SolutionData agent2location = parser.getSymbol("x", SolutionData.VAR, 2);
			for(SolutionRow row : agent2location.getRows()) {
				if(!(row.getLevel().toString().equals("0.0")))
					fw.write(row.getIndex(0) + " is assigned to " + row.getIndex(1) + "\n");
					//System.out.printf("%s is assigned to %s\n" , row.getIndex(0),row.getIndex(1));
			}
			
			/* location explored situation 1: full-explored, 0: not explored */
			//System.out.println("============Location explored situation=============");
			fw.write("\n============Location explored situation=============\n");
			SolutionData location = parser.getSymbol("y", SolutionData.VAR, 1);
			for (int i = 0; i < location_Num; i++){
				SolutionRow row = location.getRows().get(i);
				//System.out.printf("Location %d :%f \n" , i + 1, row.getLevel());
				fw.write("Location " + (i + 1) + " " + row.getLevel()+ "\n");
			}
			
			fw.flush();
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("\nSolver Done!!");

	}
}