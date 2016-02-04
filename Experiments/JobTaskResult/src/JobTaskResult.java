import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.xmlrpc.XmlRpcException;
import org.neos.client.NeosXmlRpcClient;
import org.neos.client.ResultReceiver;

public class JobTaskResult implements Runnable{	
	/* set the HOST and the PORT fields of the NEOS XML-RPC server */
	private static final String HOST="neos-server.org";
	private static final String PORT="3332";
	private static final String srcPath = "C:\\Users\\JaneLab_2\\Desktop\\Results\\IDtemp\\";
	private static final String solvers[] = {"Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc"};
	/* create NeosClient object client with server information */
	private static NeosXmlRpcClient client = new NeosXmlRpcClient(HOST, PORT);
	
	/* create NeosJobXml object exJob with problem type nco for nonlinearly */
	/* constrained optimization, KNITRO for the solver, GAMS for the input */
	
	public static void main(String[] args){
		
		try {
			client.connect();
		} catch (XmlRpcException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/* into a string called example */
		for (int j = 8; j <= 100; j++){
			for(int i = 0; i < 5; i++){
				String txt = readResult(i, j);
				int JobStart = txt.indexOf("Job Number : ") + "Job Number : ".length();
				int JobEnd = txt.indexOf("Password : ");
				int PassStart = txt.indexOf("Password : ") + "Password : ".length();
				int PassEnd = txt.length();
				int currentJob = Integer.valueOf(txt.substring(JobStart, JobEnd));
				String currentPassword = txt.substring(PassStart, PassEnd);
				
				Thread t = new Thread(new JobTaskResult()); // 產生Thread物件
				t.setName("S_" + solvers[i] + " F_Dog" + j + "ID:" + currentJob + "PW:" + currentPassword);
				t.start(); // 開始執行Runnable.run();*/
				
		        //avoid NEOS server fail
		        try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public static String readResult(int solver, int problem){
		FileReader fr;
		try {
			fr = new FileReader(srcPath + "parseS_" + solvers[solver] + " F_Dog" + problem + ".txt");
			BufferedReader br=new BufferedReader(fr);
		    String line, data="";
			while ((line=br.readLine()) != null)
				data += line;
			return data;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		String Threadname = Thread.currentThread().getName();
		String name = Threadname.substring(0, Threadname.indexOf("ID:"));
		int currentJob = Integer.valueOf(Threadname.substring(Threadname.indexOf("ID:") + "ID:".length(), Threadname.indexOf("PW:")));
		String currentPassword = Threadname.substring(Threadname.indexOf("PW:") + "PW:".length(), Threadname.length());
		JobReceiver jobReceiver = new JobReceiver(name);
		ResultReceiver receiver = new ResultReceiver(client, jobReceiver, currentJob, currentPassword);
		receiver.run();
	}
}