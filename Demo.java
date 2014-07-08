package sim.app.drones;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;

public class Demo extends SimState{
	
	private static final long serialVersionUID = 1;
	
	protected Continuous2D drones = new Continuous2D(1.0, 100, 100);
	protected Continuous2D captains = new Continuous2D(1.0, 100, 100);
	protected int numCaptains = 1;
	protected int numDrones = 5;
	protected int numData = 1;
	
	protected double initialDroneX = 20.0;
	protected double initialDroneY = 10.0;
	protected double initialCaptainX = 10.0;
	protected double initialCaptainY = 50.0;
	
	protected String[][] encounteringDrones;
	protected String[][] captainConnectedDrones;
	
	public Demo(long seed){
		super(seed);
	}
	
	public String[][] getDronesCommunication(){
		Bag getDrones = drones.allObjects;
		encounteringDrones = new String[getDrones.numObjs][getDrones.numObjs];
		for(int i=0; i<getDrones.numObjs; i++){
			
			((Drone)(getDrones.objs[i])).nearbyDrones = new ArrayList<Integer>();
			
			for(int j=0; j<getDrones.numObjs; j++){
				double x = ((Drone)(getDrones.objs[i])).X() - ((Drone)(getDrones.objs[j])).X();
				double y = ((Drone)(getDrones.objs[i])).Y() - ((Drone)(getDrones.objs[j])).Y();
				double distance = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
				double range = (((Drone)(getDrones.objs[i])).getScale() + ((Drone)(getDrones.objs[j])).getScale());
				
				if(distance<=range){
					encounteringDrones[i][j]="Connected";
					
					// Preventing drone epidemic to itself
					if(i!=j){
						
						((Drone)(getDrones.objs[i])).nearbyDrones.add(j);
						
						epidemic(((Drone)(getDrones.objs[i])),((Drone)(getDrones.objs[j])));
						//sprayAndWait(((Drone)(getDrones.objs[i])),((Drone)(getDrones.objs[j])));
						
						acknowledgement(((Drone)(getDrones.objs[i])),((Drone)(getDrones.objs[j])));
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
		
		captainConnectedDrones = new String[getCaptains.numObjs][getDrones.numObjs];
		
		for(int i=0; i<getCaptains.numObjs; i++){
			
			((Captain)(getCaptains.objs[i])).nearbyDrones = new ArrayList<Integer>();
			
			for(int j=0; j<getDrones.numObjs; j++){
				double x = ((Captain)(getCaptains.objs[i])).X() - ((Drone)(getDrones.objs[j])).X();
				double y = ((Captain)(getCaptains.objs[i])).Y() - ((Drone)(getDrones.objs[j])).Y();
				double distance = Math.sqrt(Math.pow(x, 2)+Math.pow(y, 2));
				double range = ((Captain)(getCaptains.objs[i])).getScale() + ((Drone)(getDrones.objs[j])).getScale();
				
				if(distance <= range){
					captainConnectedDrones[i][j] = "Connected";
					
					((Captain)(getCaptains.objs[i])).nearbyDrones.add(j);
					
					dataDestination(((Captain)(getCaptains.objs[i])), ((Drone)(getDrones.objs[j])));
				}
				else{
					captainConnectedDrones[i][j] = "Not Connected";
				}
			}	
		}
		return captainConnectedDrones;
	}
	
	public void epidemic(Drone A, Drone B){
			for(int i=0; i<B.dataObject.size(); i++){
				if(!A.ACK.contains(B.dataObject.get(i))){	
					if(!A.dataObject.contains(B.dataObject.get(i))){
						A.dataObject.add(B.dataObject.get(i));
					}
				}
			}

		for(int j=0; j<A.dataObject.size(); j++){
			if(!B.ACK.contains(A.dataObject.get(j))){
				if(!B.dataObject.contains(A.dataObject.get(j))){
					B.dataObject.add(A.dataObject.get(j));
				}
			}	
		}
	}
	
	public void sprayAndWait(Drone A, Drone B){
		for(int i=0; i<B.dataObject.size(); i++){
			if(!A.ACK.contains(B.dataObject.get(i))){
				if(!A.dataObject.contains(B.dataObject.get(i))){
					if(B.dataObject.get(i).getSource()==i){
						A.dataObject.add(B.dataObject.get(i));
					}
				}
			}
		}
		
		for(int j=0; j<A.dataObject.size(); j++){
			if(!B.ACK.contains(A.dataObject.get(j))){
				if(!B.dataObject.contains(A.dataObject.get(j))){
					if(A.dataObject.get(j).getSource()==j){
						B.dataObject.add(A.dataObject.get(j));
					}
				}
			}	
		}
	}

	public void dataDestination(Captain captain, Drone drone){
		for(int i=0; i<drone.dataObject.size(); i++){
			if(!captain.dataObject.contains(drone.dataObject.get(i))){
				// If the data has been sent to the captain, the data will be added to the ACK list.
				captain.dataObject.add(drone.dataObject.get(i));		
				// The Captain pass the ACK to the encountered drone.
				//drone.ACK.add(drone.dataObject.get(i));
			}
			else{
				drone.dataObject.remove(drone.dataObject.get(i));
			}
		}
		
		for(int j=0; j<captain.dataObject.size(); j++){
			if(!drone.ACK.contains(captain.dataObject.get(j))){
				drone.ACK.add(captain.dataObject.get(j));
			}
		}
		
		drone.ACKTimestamp = System.nanoTime();
		
		// Sorted the data depend on timestamp
		Collections.sort(captain.dataObject, new Comparator<DataObject>(){
			@Override
			public int compare(DataObject data1, DataObject data2){
				return Long.compare(data1.getTime(), data2.getTime());
			}
		});
	}
	
	public void acknowledgement(Drone A, Drone B){
		if(!A.ACK.isEmpty() && B.ACK.isEmpty()){
			for(int i=0; i<A.ACK.size(); i++){
				B.ACK.add(A.ACK.get(i));
			}
			
			//Collections.copy(B.ACK, A.ACK);
			
			B.ACKTimestamp = System.nanoTime();
		}
		else if(A.ACK.isEmpty() && !B.ACK.isEmpty()){
			for(int j=0; j<B.ACK.size(); j++){
				A.ACK.add(B.ACK.get(j));
			}
			
			//Collections.copy(A.ACK, B.ACK);
			
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
				drone.dataObject.add(data);
			}
			
			drones.setObjectLocation(drone, new Double2D(initialDroneX, initialDroneY));
			
			initialDroneX = initialDroneX + drones.getWidth() * 0.15;
			initialDroneY = initialDroneY + drones.getHeight() * 0.2;
			
			schedule.scheduleRepeating(drone,10.0);
		}
		
		for(int j=0; j<numCaptains; j++){
			Captain captain = new Captain();
			captains.setObjectLocation(captain, new Double2D(initialCaptainX, initialCaptainY));
			
			initialCaptainX = initialCaptainX + captains.getWidth() * 0.4;
			initialCaptainY = initialCaptainY + captains.getHeight() *0.2;
			
		    schedule.scheduleRepeating(captain,10.0);
		}
	}
	
	public static void main(String[] args){
		doLoop(Demo.class, args);
		System.exit(0);
	}

}
