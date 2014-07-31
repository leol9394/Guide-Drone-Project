package sim.app.drones;

public class DataCount {
	
	private static String EpidemicallDataReceived = "/Users/Leo/Documents/MASON/mason/sim/app/drones/EpidemicAllDataReceived.txt";
	private static String EpidemicstopAtFixedTimeStep = "/Users/Leo/Documents/MASON/mason/sim/app/drones/EpidemicStopAtFixedTimeStep.txt";
	private static String SAndRallDataReceived = "/Users/Leo/Documents/MASON/mason/sim/app/drones/SAndRAllDataReceived.txt";
	private static String SAndRstopAtFixedTimeStep = "/Users/Leo/Documents/MASON/mason/sim/app/drones/SAndRStopAtFixedTimeStep.txt";
	private static String TTLAllDataReceived = "/Users/Leo/Documents/MASON/mason/sim/app/drones/TTLAllDataReceived.txt";
	private static String TTLStopAtFixedTimeStep = "/Users/Leo/Documents/MASON/mason/sim/app/drones/TTLStopAtFixedTimeStep.txt";

	public static void main(String[] args) {
		System.out.println(FileOutput.readFile(EpidemicallDataReceived));
		System.out.println(FileOutput.readFile(EpidemicstopAtFixedTimeStep));
		System.out.println(FileOutput.readFile(SAndRallDataReceived));
		System.out.println(FileOutput.readFile(SAndRstopAtFixedTimeStep));
		System.out.println(FileOutput.readFile(TTLAllDataReceived));
		System.out.println(FileOutput.readFile(TTLStopAtFixedTimeStep));
		
	}

}
