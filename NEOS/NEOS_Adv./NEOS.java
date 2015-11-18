import org.neos.client.FileUtils;
import org.neos.client.NeosClient;
import org.neos.client.NeosJob;
import org.neos.client.NeosJobXml;

public class NEOS {	
	/* set the HOST and the PORT fields of the NEOS XML-RPC server */
	private static final String HOST="neos-server.org";
	private static final String PORT="3332";
	
	/* set the attribute of the Neos Job */
	private static final String type = "lp";
	private static final String solver = "Gurobi";
	private static final String input = "GAMS";
	
	public static void main(String[] args) {
		/* create NeosClient object client with server information */
		NeosClient client = new NeosClient(HOST, PORT);
		
		/* create NeosJobXml object exJob with problem type nco for nonlinearly */
		/* constrained optimization, KNITRO for the solver, GAMS for the input */
		NeosJobXml PSPJob = new NeosJobXml(type, solver, input);
		
		/* create FileUtils object to facilitate reading model file ChemEq.txt */
		/* into a string called example */
		FileUtils fileUtils = FileUtils.getInstance(FileUtils.APPLICATION_MODE);
		String gms = fileUtils.readFile("practice.txt");
      
		/* Read gdx file into byte array */
		byte[] gdx = fileUtils.readBinaryFile("in.gdx");
		
		/* add contents of string gms to model field of job XML */
		PSPJob.addParam("model", gms);
		/* Add GDX into parameter */
		PSPJob.addBinaryParam("gdx", gdx);
		
		JobResult jobResult = new JobResult();
		
		/* call submitJob() method with string representation of job XML */
		client.submitJobNonBlocking(PSPJob.toXMLString(), jobResult);
	}
}
