package sim.app.drones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sim.app.drones.DataObject.HashCode;
import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;

public class Demo extends SimState{
	
	private static final long serialVersionUID = 1;
	
	/* The paths of all files. */
	private static String basePath = "/Users/Leo/Documents/MASON/mason/sim/app/drones/";
	private static String waypointFile = basePath+"Waypoint.txt";
	private static String connectionMatrixFile = basePath+"ConnectionMatrix.txt";
	private static String buildingXFile = basePath+"BuildingX.txt";
	private static String buildingYFile = basePath+"BuildingY.txt";
	private static String mapSizeFile = basePath+"MapSize.txt";
//	private static String allDataReceived = basePath+"EpidemicAllDataReceived.txt";
//	private static String stopAtFixedTimeStep = basePath+"EpidemicStopAtFixedTimeStep.txt";
//	private static String allDataReceived = basePath+"SAndRAllDataReceived.txt";
//	private static String stopAtFixedTimeStep = basePath+"SAndRStopAtFixedTimeStep.txt";
	private static String allDataReceived = basePath+"TTLAllDataReceived.txt";
	private static String stopAtFixedTimeStep = basePath+"TTLStopAtFixedTimeStep.txt";
	
	/* The attributes of the simulator map. */
	private static ArrayList<ArrayList<Double>> mapSize = DataConverter.stringToDouble(FileInputOutput.readFile(mapSizeFile));
	protected static int mapWidth = (int)Math.abs(mapSize.get(0).get(0) - mapSize.get(1).get(0));
	protected static int mapHeight = (int)Math.abs(mapSize.get(0).get(1) - mapSize.get(2).get(1));
	protected static double originX = mapSize.get(0).get(0);
	protected static double originY = mapSize.get(0).get(1);
	protected Continuous2D drones = new Continuous2D(1.0, mapWidth, mapHeight);
	protected Continuous2D captains = new Continuous2D(1.0, mapWidth, mapHeight);
	protected Continuous2D waypoints = new Continuous2D(1.0, mapWidth, mapHeight);
	protected Continuous2D buildings = new Continuous2D(1.0, mapWidth, mapHeight);
	
	/* The fixed time step that output the state. */
	private int recordTimeStep = 1000;
	
	/* The waypoint and building attributes. */
	protected static ArrayList<ArrayList<Double>> waypointCoordinate = DataConverter.stringToDouble(FileInputOutput.readFile(waypointFile));
	protected static ArrayList<ArrayList<Integer>> connectionMatrix = DataConverter.stringToInteger(FileInputOutput.readFile(connectionMatrixFile));
	protected static ArrayList<double[]> buildingX = DataConverter.stringToDoubleArray(FileInputOutput.readFile(buildingXFile));
	protected static ArrayList<double[]> buildingY = DataConverter.stringToDoubleArray(FileInputOutput.readFile(buildingYFile));
	
	protected static int numCaptains = 1;
	protected static int numDrones = 5;
	protected static int numData = 1;
	protected static int numBuildings = buildingX.size();
	
	private static String[][] encounteringDrones;
	private static String[][] captainConnectedDrones;
	private boolean[] allDataReceivedOutputState = new boolean[numDrones+numCaptains];
	private String[] allDataReceivedOutput = new String[numDrones+numCaptains];
	private String[] stopAtFixedTimeStepOutput = new String[numDrones+numCaptains];
	private boolean allDataOutput = false;
	private boolean fixedTimeDataOutput = false;
	protected static boolean isTimeToLiveStart = false;
	
	public Demo(long seed){
		super(seed);
	}
	
	public String[][] getDronesCommunication(){
		/* Start TTL or not. */
		//timeToLive();
		
		Bag getDrones = drones.allObjects;
		encounteringDrones = new String[numDrones][numDrones];
		
		for(int i=0; i<getDrones.numObjs; i++){
			
			((Drone)(getDrones.objs[i])).nearbyDrones = new ArrayList<Integer>();
			
			for(int j=0; j<getDrones.numObjs; j++){
				double x = ((Drone)(getDrones.objs[i])).me.x - ((Drone)(getDrones.objs[j])).me.x;
				double y = ((Drone)(getDrones.objs[i])).me.y - ((Drone)(getDrones.objs[j])).me.y;
				double distance = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
				double range = (((Drone)(getDrones.objs[i])).scale + ((Drone)(getDrones.objs[j])).scale);
				
				if(distance<=range){
					encounteringDrones[i][j]="Connected";

					if(i!=j){
						
						((Drone)(getDrones.objs[i])).nearbyDrones.add(j);
						
						/* Pure Epidemic Protocol or Spray and Wait Protocol */
						epidemic(((Drone)(getDrones.objs[i])),((Drone)(getDrones.objs[j])));
						//sprayAndWait(((Drone)(getDrones.objs[i])),((Drone)(getDrones.objs[j])), i, j);
						
						dronesACKsCommunication(((Drone)(getDrones.objs[i])),((Drone)(getDrones.objs[j])));
					}
				}
				else{
					encounteringDrones[i][j]="Not Connected";
				}
			}
			encounteringDrones[i][i]="Itself";
		}
		return encounteringDrones;
	}

	public String[][] getCaptainCommunication(){
		Bag getDrones = drones.allObjects;
		Bag getCaptains = captains.allObjects;
		captainConnectedDrones = new String[numCaptains][numDrones];
		
		for(int i=0; i<getCaptains.numObjs; i++){
			
			((Captain)(getCaptains.objs[i])).nearbyDrones = new ArrayList<Integer>();
			
			for(int j=0; j<getDrones.numObjs; j++){
				double x = ((Captain)(getCaptains.objs[i])).me.x - ((Drone)(getDrones.objs[j])).me.x;
				double y = ((Captain)(getCaptains.objs[i])).me.y - ((Drone)(getDrones.objs[j])).me.y;
				double distance = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
				double range = ((Captain)(getCaptains.objs[i])).scale + ((Drone)(getDrones.objs[j])).scale;
				
				if(distance <= range){
					captainConnectedDrones[i][j] = "Connected";
					
					((Captain)(getCaptains.objs[i])).nearbyDrones.add(j);
					
					captainsDronesDataACKsCommunication(((Captain)(getCaptains.objs[i])), ((Drone)(getDrones.objs[j])));
					
				}
				else{
					captainConnectedDrones[i][j] = "Not Connected";
				}
			}	
		}
		return captainConnectedDrones;
	}
	
	public String[] getDataOutput(){
		Bag getCaptains = captains.allObjects; 
		Bag getDrones = drones.allObjects;
		
		for(int i=0; i<getCaptains.numObjs; i++){
			if(((Captain)(getCaptains.objs[i])).isAllDataReceivedYet){
				if(!((Captain)(getCaptains.objs[i])).isResultWritten){
					String durationOutput = String.valueOf(((Captain)(getCaptains.objs[i])).duration);
					allDataReceivedOutput[i] = durationOutput;
					((Captain)(getCaptains.objs[i])).isResultWritten = true;
					allDataReceivedOutputState[i] = ((Captain)(getCaptains.objs[i])).isResultWritten;
				}
			}
			
			if(schedule.getTime()==recordTimeStep){
				String numCaptainHoldObjects = String.valueOf(((Captain)(getCaptains.objs[i])).dataObject.size());
				stopAtFixedTimeStepOutput[i] = numCaptainHoldObjects;
			}
		}
		
		for(int j=0; j<getDrones.numObjs; j++){
			if(droneItselfDataSentChecker(((Drone)(getDrones.objs[j])))){
				if(!((Drone)(getDrones.objs[j])).isResultWritten){
					String durationOutput = String.valueOf(((Drone)(getDrones.objs[j])).duration);
					allDataReceivedOutput[j+numCaptains] = durationOutput;
					((Drone)(getDrones.objs[j])).isResultWritten = true;
					allDataReceivedOutputState[j+numCaptains] = ((Drone)(getDrones.objs[j])).isResultWritten;
				}
			}
			
			if(schedule.getTime()==recordTimeStep){
				String isItselfDataSent;
				if(droneItselfDataSentChecker(((Drone)(getDrones.objs[j])))){
					isItselfDataSent = "1";
				}
				else{
					isItselfDataSent = "0";
				}
				stopAtFixedTimeStepOutput[j+numCaptains] = isItselfDataSent;
			}
		}
		
		if(!allDataOutput){
			if(outputStateChecker(allDataReceivedOutputState)){
				FileInputOutput.insertFile(allDataReceived, allDataReceivedOutput);
				allDataOutput = true;
			}
		}
		
		if(schedule.getTime()==recordTimeStep){
			FileInputOutput.insertFile(stopAtFixedTimeStep, stopAtFixedTimeStepOutput);
			fixedTimeDataOutput = true;
		}
		
		if(allDataOutput && fixedTimeDataOutput){
//			if(FileInputOutput.lineCount(allDataReceived)==260){
//				System.exit(0);
//			}
//			else{
//				String[] args = {};
//				MouseMovement.main(args);
//			}
		}
		
		return allDataReceivedOutput;
	}
	
	public boolean droneItselfDataSentChecker(Drone drone){
		for(int i=0; i<drone.dataObject.size(); i++){
			if(drone.dataObject.get(i).getSource()==drone.droneNumber){
				return false;
			}
		}
		return true;
	}
	
	public boolean outputStateChecker(boolean[] array){
		for(int i=0; i<array.length; i++){
			if(!array[i]){
				return false;
			}
		}
		return true;
	}
	
	public void epidemic(Drone A, Drone B){
			for(int i=0; i<B.dataObject.size(); i++){
				if(!A.ACK.contains(B.dataObject.get(i).getHashCode())){	
					if(!A.hashCode.contains(B.dataObject.get(i).getHashCode())){
						
						A.hashCode.add(B.dataObject.get(i).getHashCode());
						A.dataObject.add(B.dataObject.get(i));
					}
				}
			}

		for(int j=0; j<A.dataObject.size(); j++){
			if(!B.ACK.contains(A.dataObject.get(j).getHashCode())){
				if(!B.hashCode.contains(A.dataObject.get(j).getHashCode())){
					
					B.hashCode.add(A.dataObject.get(j).getHashCode());
					B.dataObject.add(A.dataObject.get(j));
				}
			}	
		}
	}
	
	public void sprayAndWait(Drone A, Drone B, int sourceA, int sourceB){
		for(int i=0; i<B.dataObject.size(); i++){
			if(!A.ACK.contains(B.dataObject.get(i).getHashCode())){
				if(!A.hashCode.contains(B.dataObject.get(i).getHashCode())){
					if(B.dataObject.get(i).getSource()==sourceB){
						if(!(B.copies==0)){
							A.hashCode.add(B.dataObject.get(i).getHashCode());
							A.dataObject.add(B.dataObject.get(i));
							B.copies --;
						}
					}
				}
			}
		}
		
		for(int j=0; j<A.dataObject.size(); j++){
			if(!B.ACK.contains(A.dataObject.get(j).getHashCode())){
				if(!B.hashCode.contains(A.dataObject.get(j).getHashCode())){
					if(A.dataObject.get(j).getSource()==sourceA){
						if(!(A.copies==0)){
							B.hashCode.add(A.dataObject.get(j).getHashCode());
							B.dataObject.add(A.dataObject.get(j));
							A.copies--;
						}
					}
				}
			}	
		}
	}
	
	public void timeToLive(){
		isTimeToLiveStart = true;
	}

	public void captainsDronesDataACKsCommunication(Captain captain, Drone drone){
		for(int i=0; i<drone.dataObject.size(); i++){
			if(!captain.hashCode.contains(drone.dataObject.get(i).getHashCode())){

				captain.hashCode.add(drone.dataObject.get(i).getHashCode());
				captain.dataObject.add(drone.dataObject.get(i));		
				
			}
			else{
				drone.hashCode.remove(drone.dataObject.get(i).getHashCode());
				drone.dataObject.remove(drone.dataObject.get(i));
			}
		}
		
		for(int j=0; j<captain.hashCode.size(); j++){
			if(!drone.ACK.contains(captain.hashCode.get(j))){
				drone.ACK.add(captain.hashCode.get(j));
			}
		}
		
		drone.ACKTimestamp = schedule.getTime();
		
		Collections.sort(captain.dataObject, new Comparator<DataObject>(){
			@Override
			public int compare(DataObject data1, DataObject data2){
				return Long.compare(data1.getTime(), data2.getTime());
			}
		});
		
		Collections.sort(captain.hashCode, new Comparator<HashCode>(){
			@Override
			public int compare(HashCode hashCode1, HashCode hashCode2){
				return Long.compare(hashCode1.getHashCodeGeneratedTime(), hashCode2.getHashCodeGeneratedTime());
			}
		});
	}
	
	public void dronesACKsCommunication(Drone A, Drone B){
		if(!A.ACK.isEmpty() && B.ACK.isEmpty()){
			for(int i=0; i<A.ACK.size(); i++){
				B.ACK.add(A.ACK.get(i));
			}
			B.ACKTimestamp = System.nanoTime();
		}
		else if(A.ACK.isEmpty() && !B.ACK.isEmpty()){
			for(int j=0; j<B.ACK.size(); j++){
				A.ACK.add(B.ACK.get(j));
			}
			A.ACKTimestamp = System.nanoTime();
		}
		else if(!A.ACK.isEmpty() && !B.ACK.isEmpty()){
			for(int i=0; i<B.ACK.size(); i++){
				if(!A.ACK.contains(B.ACK.get(i))){
					A.ACK.add(B.ACK.get(i));
				}
			}
			for(int j=0; j<A.ACK.size(); j++){
				if(!B.ACK.contains(A.ACK.get(j))){
					B.ACK.add(A.ACK.get(j));
				}
			}
		}
		else{
			/* Do nothing. */
		}
	}

	public void start(){
		super.start();
		
		drones.clear();
		
		captains.clear();
		
		/* Set up the way points. */
		for(int i=0; i<waypointCoordinate.size(); i++){
			Waypoint waypoint = new Waypoint();
			double waypointX = Math.abs(waypointCoordinate.get(i).get(0) - originX);
			double waypointY = Math.abs(waypointCoordinate.get(i).get(1) - originY);
			waypoints.setObjectLocation(waypoint, new Double2D(waypointX, waypointY));
			waypoint.waypointNumber = i;
		}
		
		/* Set up the drones. */
		for(int i=0; i<numDrones; i++){
			Drone drone = new Drone();
			drone.droneNumber = i;
			
			/* Set up the drone's position. */
			int initialDronePosition = i+random.nextInt(10);
			int selectRandomWaypoint = random.nextInt(connectionMatrix.get(initialDronePosition).size());
			int waypointAllocation = connectionMatrix.get(initialDronePosition).get(selectRandomWaypoint);
			drone.wayPointX = Math.abs(waypointCoordinate.get(waypointAllocation).get(0) - originX);
			drone.wayPointY = Math.abs(waypointCoordinate.get(waypointAllocation).get(1) - originY);
			drone.preWaypoint = initialDronePosition;
			drone.newWaypoint = waypointAllocation;

			/* Set up the data in each drone. */
			for(int k=0; k<numData; k++){
				DataObject data = new DataObject();
				data.setSource(drone.droneNumber);
				data.setData((int)i*10);
				data.setTime(System.nanoTime());
				data.setHashCodeGeneratedTime(data.getTime());
				data.setTimeStep(schedule.getTime() + 1.0);
				data.setHashCode(data.hashCode());
				drone.hashCode.add(data.getHashCode());
				drone.dataObject.add(data);
			}
			
			drones.setObjectLocation(drone, new Double2D(Math.abs(waypointCoordinate.get(initialDronePosition).get(0) - originX), Math.abs(waypointCoordinate.get(initialDronePosition).get(1) - originY)));
			
			drone.startTime = schedule.getTime();
			
			schedule.scheduleRepeating(drone);
		}
		
		/* Set up the Captains. */
		for(int i=0; i<numCaptains; i++){
			Captain captain = new Captain();
			
			int initialCaptainPosition = 14;
			
			captains.setObjectLocation(captain, new Double2D(Math.abs(waypointCoordinate.get(initialCaptainPosition).get(0) - originX), Math.abs(waypointCoordinate.get(initialCaptainPosition).get(1) - originY)));
			
			captain.startTime = schedule.getTime();
			
		    schedule.scheduleRepeating(captain);
		}
		
		/* Set up the buildings. */
		for(int i=0; i<numBuildings; i++){
			double[] x = new double[buildingX.get(i).length];
			double[] y = new double[buildingY.get(i).length];
			for(int j=0; j<buildingX.get(i).length; j++){
				
				x[j] = buildingX.get(i)[j] - originX;
				
				if(buildingY.get(i)[j] > originY){
					y[j] = -(buildingY.get(i)[j] - originY);
				}
				else{
					y[j] = Math.abs(buildingY.get(i)[j] - originY);
				}
				
			}
			Building building =new Building(x, y, false);
			buildings.setObjectLocation(building, new Double2D(0,0));
		}
	}
	
	public static void main(String[] args){
		doLoop(Demo.class, args);
		System.exit(0);
	}

}
