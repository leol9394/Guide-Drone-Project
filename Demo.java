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
	
	protected Continuous2D drones = new Continuous2D(1.0, 100, 100);
	protected Continuous2D captains = new Continuous2D(1.0, 100, 100);
	protected static int numCaptains = 1;
	protected static int numDrones = 5;
	protected static int numData = 1;
	
	protected double initialDroneX;
	protected double initialDroneY;
	protected double initialCaptainX = 20.0;
	protected double initialCaptainY = 50.0;
	protected double initialDroneAngleDegree = 80.0;
	protected double initialDroneAngleRadian;
	protected double captainsDronesRadius = 45.0;
	protected double dronePositionGapDegree = 40.0;
	
	private static String[][] encounteringDrones;
	private static String[][] captainConnectedDrones;
	
	private int recordTimeStep = 1000;
//	private String allDataReceived = "/Users/Leo/Documents/MASON/mason/sim/app/drones/EpidemicAllDataReceived.txt";
//	private String stopAtFixedTimeStep = "/Users/Leo/Documents/MASON/mason/sim/app/drones/EpidemicStopAtFixedTimeStep.txt";
//	private String allDataReceived = "/Users/Leo/Documents/MASON/mason/sim/app/drones/SAndRAllDataReceived.txt";
//	private String stopAtFixedTimeStep = "/Users/Leo/Documents/MASON/mason/sim/app/drones/SAndRStopAtFixedTimeStep.txt";
	private String allDataReceived = "/Users/Leo/Documents/MASON/mason/sim/app/drones/TTLAllDataReceived.txt";
	private String stopAtFixedTimeStep = "/Users/Leo/Documents/MASON/mason/sim/app/drones/TTLStopAtFixedTimeStep.txt";
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
		//Start TTL or not.
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
					
					captainsDronesCommunication(((Captain)(getCaptains.objs[i])), ((Drone)(getDrones.objs[j])));
					
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
				FileOutput.insertFile(allDataReceived, allDataReceivedOutput);
				allDataOutput = true;
			}
		}
		
		if(schedule.getTime()==recordTimeStep){
			FileOutput.insertFile(stopAtFixedTimeStep, stopAtFixedTimeStepOutput);
			fixedTimeDataOutput = true;
		}
		
		if(allDataOutput && fixedTimeDataOutput){
//			if(FileOutput.readFile(allDataReceived)==260){
//				System.exit(0);
//			}
//			else{
//				String[] args = {};
//				DataRecord.main(args);
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

	public void captainsDronesCommunication(Captain captain, Drone drone){
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
			// Do nothing.
		}
	}

	public void start(){
		super.start();
		
		drones.clear();
		
		captains.clear();
		
		initialDroneAngleRadian = Math.toRadians(initialDroneAngleDegree);
		initialDroneX = initialCaptainX + captainsDronesRadius * Math.cos(initialDroneAngleRadian);
		initialDroneY = initialCaptainY - captainsDronesRadius * Math.sin(initialDroneAngleRadian);
		
		for(int i=0; i<numDrones; i++){
			Drone drone = new Drone();
			drone.droneNumber=i;
			drone.wayPointX = drones.getWidth() * random.nextDouble();
			drone.wayPointY = drones.getHeight() * random.nextDouble();

			for(int j=0; j<numData; j++){
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
			
			drones.setObjectLocation(drone, new Double2D(initialDroneX, initialDroneY));
			
			initialDroneAngleDegree = initialDroneAngleDegree - dronePositionGapDegree;
			initialDroneAngleRadian = Math.toRadians(initialDroneAngleDegree);
			initialDroneX = initialCaptainX + captainsDronesRadius * Math.cos(initialDroneAngleRadian);
			initialDroneY = initialCaptainY - captainsDronesRadius * Math.sin(initialDroneAngleRadian);
			
			drone.startTime = schedule.getTime();
			
			schedule.scheduleRepeating(drone);
		}
		
		for(int j=0; j<numCaptains; j++){
			Captain captain = new Captain();
			captains.setObjectLocation(captain, new Double2D(initialCaptainX, initialCaptainY));
			
			initialCaptainX = initialCaptainX + captains.getWidth() * 0.4;
			initialCaptainY = initialCaptainY + captains.getHeight() * 0.2;
			
			captain.startTime = schedule.getTime();
			
		    schedule.scheduleRepeating(captain);
		}
	}
	
	public static void main(String[] args){
		doLoop(Demo.class, args);
		System.exit(0);
	}

}
