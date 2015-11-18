import org.neos.client.FileUtils;
import org.neos.client.NeosClient;
import org.neos.client.NeosJobXml;

public class NEOS implements Runnable{	
	/* set the HOST and the PORT fields of the NEOS XML-RPC server */
	private static final String HOST="neos-server.org";
	private static final String PORT="3332";
	private static final String srcPath = "C:\\Users\\Dog\\Desktop\\NEOS\\data\\in";
	/* set the attribute of the Neos Job */
	private static final String type = "milp";
	private static final String solver[] = {"Gurobi", "MOSEK", "XpressMP", "CPLEX", "Cbc", "scip"};
	private static final String input = "GAMS";
	/* create NeosClient object client with server information */
	private static NeosClient client;
	
	/* create NeosJobXml object exJob with problem type nco for nonlinearly */
	/* constrained optimization, KNITRO for the solver, GAMS for the input */
	private static NeosJobXml PSPJob;
	
	public static void main(String[] args) throws InterruptedException {
		
		/* create FileUtils object to facilitate reading model file ChemEq.txt */
		/* into a string called example */
		FileUtils fileUtils = FileUtils.getInstance(FileUtils.APPLICATION_MODE);
		String gms = fileUtils.readFile("practice.txt");
		
		for(int i = 5; i < 6; i++){
			String passPath = srcPath;
			passPath = passPath + 3 + ".gdx";
			/* Read gdx file into byte array */
			byte[] gdx = fileUtils.readBinaryFile(passPath);
			client = new NeosClient(HOST, PORT);
			PSPJob = new NeosJobXml(type, solver[i%6], input);
			/* add contents of string gms to model field of job XML */
			PSPJob.addParam("model", gms);
			PSPJob.addParam("email", "hoyusun@yahoo.com.tw");
			/* Add GDX into parameter */
			PSPJob.addBinaryParam("gdx", gdx);
			Thread t = new Thread(new NEOS()); // 產生Thread物件
			
			int length = passPath.length();
			t.setName(passPath.substring(31, length-4));
	        t.start(); // 開始執行Runnable.run();
	        
	        //avoid NEOS server fail
	        Thread.sleep(500);
		}
	}
	
	public void run() {
		// TODO Auto-generated method stub
		System.out.println(Thread.currentThread().getName());
		JobResult jobResult = new JobResult(Thread.currentThread().getName());
		
		/* call submitJob() method with string representation of job XML */
		client.submitJobNonBlocking(PSPJob.toXMLString(), jobResult);
	}
}