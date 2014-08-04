package sim.app.drones;

import java.util.ArrayList;

public class DataCount {
	
	private static String EpidemicallDataReceived = "/Users/Leo/Documents/MASON/mason/sim/app/drones/EpidemicAllDataReceived.txt";
	private static String EpidemicstopAtFixedTimeStep = "/Users/Leo/Documents/MASON/mason/sim/app/drones/EpidemicStopAtFixedTimeStep.txt";
	private static String SAndRallDataReceived = "/Users/Leo/Documents/MASON/mason/sim/app/drones/SAndRAllDataReceived.txt";
	private static String SAndRstopAtFixedTimeStep = "/Users/Leo/Documents/MASON/mason/sim/app/drones/SAndRStopAtFixedTimeStep.txt";
	private static String TTLAllDataReceived = "/Users/Leo/Documents/MASON/mason/sim/app/drones/TTLAllDataReceived.txt";
	private static String TTLStopAtFixedTimeStep = "/Users/Leo/Documents/MASON/mason/sim/app/drones/TTLStopAtFixedTimeStep.txt";
	private static String waypointCoordinate = "/Users/Leo/Documents/MASON/mason/sim/app/drones/Waypoint.txt";
	private static String connectionMatrixFile = "/Users/Leo/Documents/MASON/mason/sim/app/drones/connectionMatrix.txt";

	public static void main(String[] args) {
		System.out.println(FileInputOutput.lineCount(EpidemicallDataReceived));
		System.out.println(FileInputOutput.lineCount(EpidemicstopAtFixedTimeStep));
		System.out.println(FileInputOutput.lineCount(SAndRallDataReceived));
		System.out.println(FileInputOutput.lineCount(SAndRstopAtFixedTimeStep));
		System.out.println(FileInputOutput.lineCount(TTLAllDataReceived));
		System.out.println(FileInputOutput.lineCount(TTLStopAtFixedTimeStep));
		
		/* Display the way point coordinate. */
		ArrayList<ArrayList<Double>> arrayDouble = new ArrayList<ArrayList<Double>>();
		arrayDouble = DataConverter.stringToDouble(FileInputOutput.readFile(waypointCoordinate));
		for(int i=0; i<arrayDouble.size(); i++){
			System.out.println(arrayDouble.get(i).get(0)+" "+arrayDouble.get(i).get(1));
		}
		
		/* Display the connection matrix. */
		ArrayList<ArrayList<Integer>> arrayInteger = new ArrayList<ArrayList<Integer>>();
		arrayInteger = DataConverter.stringToInteger(FileInputOutput.readFile(connectionMatrixFile));
		for(int i=0; i<arrayInteger.size(); i++){
			String result = "";
			for(int j=0; j<arrayInteger.get(i).size(); j++){
				result += arrayInteger.get(i).get(j)+" ";
			}
			System.out.println(result);
		}
		
	}

}
