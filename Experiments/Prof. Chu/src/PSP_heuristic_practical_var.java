import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class PSP_heuristic_practical_var {

	private static int index = 0;
	
	//PSP parameter
	private static int PARTICIPANT = 1500;
	private static int REGION = 10;
	private static int[][] benefit = null;
    private static int[][] cost = null;
    private static int[][] value = null;
    
    //Calculate region value is up to be full.
    private static double [] RegionCP = null;
    private static int [] RegionBenefit = null;
    private static int [] RegionCost = null;
    private static boolean [] RegionIsFull = null;
    private static int [] RegionParticipant = null;
    private static RegionOrder [] RegionVisitOrder = null;
	
    //execution time
    static long start, end;
    
    //CP queue
	private static B2C quality [] = new B2C[PARTICIPANT*REGION];
	private static Queue <B2C> queue = new LinkedList<B2C>();
    
    //Budget setting
    private static int Budget;
    
    //consideration
    private static int objective;
    private static int selectedparticipant;
    private static int totalcost;
    private static int waste;
	public static void main(String[] args) {

        // create a workspace
		String dirpath = "C:/Users/Dog/Desktop/NEOS";
        
		Initconsider();
		InitCalRegion();
		start = System.currentTimeMillis();
		
		for(int i = 1; i <= 100; i++){
			index = 0;
    		Budget = 5000;
			String input = setPath(dirpath, i);
    		getData(new File(input));	
    		//execute Prof. Chu algo
    		Calculate();
        }
		end = System.currentTimeMillis();
		
		System.out.println("Objective Value: " + objective + ", Waste: " + waste 
		+ ", Participants: " + selectedparticipant + ", Totalcost: " + totalcost + ", Execution Time: " + (end-start)/1000.0);
    }
	
	private static String setPath(String dirpath, int num){
		return dirpath + "/xls/data" + num + ".xls";
	}
	
	private static void Initconsider(){
		objective = 0;
		selectedparticipant = 0;
		totalcost = 0;
		waste = 0;
		benefit = new int[PARTICIPANT+1][REGION+1];
		cost = new int[PARTICIPANT+1][REGION+1];
		value = new int[2][REGION+1];
	}
	
	private static void InitCalRegion(){
		RegionCP = new double [REGION+1];
		RegionBenefit = new int [REGION+1];
		RegionParticipant = new int [REGION+1];
		RegionCost = new int [REGION+1];
		RegionIsFull = new boolean [REGION+1];
		RegionVisitOrder = new RegionOrder [REGION];
	}
	
	private static void getData(File inputFile){
		Workbook w;
        try {
            w = Workbook.getWorkbook(inputFile);
            
            //read benefits sheet
            Sheet benefits = w.getSheet("benefits");
            for (int j = 1; j <= REGION; j++){
                for (int i = 1; i <= PARTICIPANT; i++){
                	benefit[i][j] = Integer.valueOf(benefits.getCell(j, i).getContents());
                }
            }

            //read costs sheet
            Sheet costs = w.getSheet("costs");
            for (int j = 1; j <= REGION; j++){
                for (int i = 1; i <= PARTICIPANT; i++){
                	cost[i][j] = Integer.valueOf(costs.getCell(j, i).getContents());
                }
            }
            
            //read value sheet
            Sheet values = w.getSheet("values");
            for (int j = 0; j < REGION; j++){
                for (int i = 1; i < 2; i++){
                	value[i][j+1] = Integer.valueOf(values.getCell(j, i).getContents());
                	
                	//init region
                	RegionCP[j+1] = RegionBenefit[j+1] = RegionCost[j+1] = RegionParticipant[j+1] = 0;
                	RegionIsFull[j+1] = false;
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
		
		//Put the B/C into queue by decreasing order
		for(int i = 0; i < PARTICIPANT*REGION; i++){
			queue.add(quality[i]);
			B2C task = quality[i];
			RegionCP[task.region] += task.cp;
		}
		
		//Classify participant to each region
		while(!queue.isEmpty()){
			B2C task = queue.remove();
			int benefit = task.benefit;
			int cost = task.cost;
			int region = task.region;
			if(RegionIsFull[region] == false){
				RegionBenefit[region] += benefit;
				RegionCost[region] += cost;
				RegionParticipant[region]++;
				//if cumulative region value is larger than its full value, quit assigning and this region is over.
				if(RegionBenefit[region] >= value[1][region]){
					RegionIsFull[region] = true;
				}
			}
		}
		
		for(int i = 0; i < REGION; i++){
			RegionOrder order = new RegionOrder(i+1, RegionCP[i+1]);
			RegionVisitOrder[i] = order;
		}
		
		//Sort RegionCP in non-increasing order.
		Arrays.sort(RegionVisitOrder, new Comparator<RegionOrder>(){
		    @Override
			public int compare(RegionOrder entry1, RegionOrder entry2) {
		    	double a = entry1.TotalCP;
				double b = entry2.TotalCP;
				return (int) (b - a);
			}
		});
		//Choosing
		for(int i = 0; i < REGION; i++){
			RegionOrder order = RegionVisitOrder[i];
			int pos = order.id;
			if(Budget - RegionCost[pos] >= 0){
				objective += value[1][pos];
				waste += RegionBenefit[pos]-value[1][pos];
				Budget -= RegionCost[pos];
				selectedparticipant += RegionParticipant[pos];
				totalcost += RegionCost[pos];
			}
		}
	}
	
	public static class RegionOrder{
		int id;
		double TotalCP;
		
		public RegionOrder(){
			id = 0;
			TotalCP = 0;
		}
		
		public RegionOrder(int id, double cp){
			this.id = id;
			this.TotalCP = cp;
		}
	}
	
	public static class B2C{
		int participant = 0;
		int region = 0;
		int benefit;
		int cost;
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
			this.cp = benefit / cost;
		}
	
		public double get(){
			return cp;
		}
	}
}
