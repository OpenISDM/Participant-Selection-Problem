import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class PSP_heuristic {

	private static int index = 0;
	
	//PSP parameter
	private static int PARTICIPANT = 1500;
	private static int REGION = 20;
	private static int[][] benefit = null;
    private static int[][] cost = null;
    private static int[][] value = null;
	
    //execution time
    static long start, end;
    
    //CP queue
	private static B2C quality [] = new B2C[PARTICIPANT*REGION];
	private static Queue <B2C> queue = new LinkedList<B2C>();
    
    //Budget setting
    private static int Budget = 5000;
    
    //consideration
    private static int objective;
    private static int selectedparticipant;
    private static int totalcost;
    
	public static void main(String[] args) {

        // create a workspace
		String dirpath = "C:/Users/Dog/Desktop";
        for(int i = 1; i <= 1; i++){
        	String input = setPath(dirpath, i);
    		getData(new File(input));
    		
    		Initconsider();
    		//execute Prof. Chu algo
    		Calculate();
        }
    }
	
	private static String setPath(String dirpath, int num){
		return dirpath + "\\data" + num + ".xls";
	}
	
	private static void Initconsider(){
		objective = 0;
		selectedparticipant = 0;
		totalcost = 0;
	}
	
	private static void getData(File inputFile){
		Workbook w;
        try {
            w = Workbook.getWorkbook(inputFile);
            
            //read benefits sheet
            Sheet benefits = w.getSheet("benefits");
            benefit = new int[PARTICIPANT+1][REGION+1];
            for (int j = 1; j <= REGION; j++){
                for (int i = 1; i <= PARTICIPANT; i++){
                	benefit[i][j] = Integer.valueOf(benefits.getCell(j, i).getContents());
                }
            }

            //read costs sheet
            Sheet costs = w.getSheet("costs");
            cost = new int[PARTICIPANT+1][REGION+1];
            for (int j = 1; j <= REGION; j++){
                for (int i = 1; i <= PARTICIPANT; i++){
                	cost[i][j] = Integer.valueOf(costs.getCell(j, i).getContents());
                }
            }
            
            //read value sheet
            Sheet values = w.getSheet("values");
            value = new int[PARTICIPANT+1][REGION+1];
            for (int j = 0; j < REGION; j++){
                for (int i = 1; i < 2; i++){
                	value[i][j+1] = Integer.valueOf(values.getCell(j, i).getContents());
                }
            }

            w.close();
        } catch (IOException e) {
        	e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
	}
	public static void Calculate(){
		start = System.currentTimeMillis();
		
		//Calculate all B2C factors (i.e., Qi,k= bik/cik)
		for (int i = 1; i <= PARTICIPANT; i++){
			for(int j = 1; j <= REGION; j++){
				quality[index] = new B2C();
				quality[index++].set(i, j, benefit[i][j], cost[i][j]);
			}
		}
		
		//Sort Qi,k by their values in non-increasing order
		Arrays.sort(quality, new Comparator<B2C>(){
		      @Override
		      public int compare(B2C entry1, B2C entry2) {
		    	  double a = entry1.cp;
		    	  double b = entry2.cp;
		    	  return (int) (b - a);
		      }
		});
		
		//Choosing
		for(int i = 0; i < PARTICIPANT*REGION; i++){
			queue.add(quality[i]);
		}
		
		while(!queue.isEmpty()){
			B2C task = queue.remove();
			int participant = task.participant;
			int region = task.region;
			if((task.select == false) && (value[1][region] - task.benefit >= 0) && (Budget-task.cost >= 0)){
				value[1][region] -= task.benefit;
				Budget -= task.cost;
				objective += task.benefit;
				setSelected(quality, participant, true);
				selectedparticipant++;
				totalcost += task.cost;
				
			}
		}
		end = System.currentTimeMillis();
		System.out.println("Objective Value: " + objective + ", Execution Time: " + (end-start)/1000.0 + 
				", Participants: " + selectedparticipant + ", Totalcost: " + totalcost);
	}
	
	public static void setSelected(B2C quality[], int participant, boolean flag){
		for(int i = 0; i < PARTICIPANT*REGION; i++)
			if(quality[i].participant == participant)
				quality[i].select = flag;
	}
	
	public static class B2C{
		int participant = 0, region = 0, benefit, cost;
		boolean select = false;
		double cp = 0;
		
		
		public B2C(){
			participant = 0;
			region = 0;
			cp = 0;
		}
		
		public void set(int participant, int region, int benefit, int cost){
			this.participant = participant;
			this.region = region;
			this.benefit = benefit;
			this.cost = cost;
			cp = benefit / cost;
		}
		
		public double get(){
			return cp;
		}
		
	}
}
