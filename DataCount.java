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
	private static String buildingXFile = "/Users/Leo/Documents/MASON/mason/sim/app/drones/BuildingX.txt";
	private static String buildingYFile = "/Users/Leo/Documents/MASON/mason/sim/app/drones/BuildingY.txt";

	public static void main(String[] args) {
		System.out.println(FileInputOutput.lineCount(EpidemicallDataReceived));
		System.out.println(FileInputOutput.lineCount(EpidemicstopAtFixedTimeStep));
		System.out.println(FileInputOutput.lineCount(SAndRallDataReceived));
		System.out.println(FileInputOutput.lineCount(SAndRstopAtFixedTimeStep));
		System.out.println(FileInputOutput.lineCount(TTLAllDataReceived));
		System.out.println(FileInputOutput.lineCount(TTLStopAtFixedTimeStep));
		
		/* Display the way point coordinate. */
		ArrayList<ArrayList<Double>> arrayListDouble = new ArrayList<ArrayList<Double>>();
		arrayListDouble = DataConverter.stringToDouble(FileInputOutput.readFile(waypointCoordinate));
		for(int i=0; i<arrayListDouble.size(); i++){
			System.out.println(arrayListDouble.get(i).get(0)+" "+arrayListDouble.get(i).get(1));
		}
		
		/* Display the connection matrix. */
		ArrayList<ArrayList<Integer>> arrayListInteger = new ArrayList<ArrayList<Integer>>();
		arrayListInteger = DataConverter.stringToInteger(FileInputOutput.readFile(connectionMatrixFile));
		for(int i=0; i<arrayListInteger.size(); i++){
			String result = "";
			for(int j=0; j<arrayListInteger.get(i).size(); j++){
				result += arrayListInteger.get(i).get(j)+" ";
			}
			System.out.println(result);
		}
		
		/* Display the buildings' X and Y coordinate. */
		ArrayList<double[]> arrayDoubleX = new ArrayList<double[]>();
		ArrayList<double[]> arrayDoubleY = new ArrayList<double[]>();
		arrayDoubleX = DataConverter.stringToDoubleArray(FileInputOutput.readFile(buildingXFile));
		arrayDoubleY = DataConverter.stringToDoubleArray(FileInputOutput.readFile(buildingYFile));
		for(int i=0; i<arrayDoubleX.size(); i++){
			String X = "";
			String Y = "";
			for(int j=0; j<arrayDoubleX.get(i).length; j++){
				X += arrayDoubleX.get(i)[j]+" ";
				Y += arrayDoubleY.get(i)[j]+" ";
			}
			System.out.println("X: "+X);
			System.out.println("Y: "+Y);
		}
		
	}

}
