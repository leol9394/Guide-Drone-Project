package sim.app.drones;


import java.util.ArrayList;
import java.util.Collections;

import sim.app.drones.DataObject.HashCode;
import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;

public class Drone implements Steppable{
	
	private static final long serialVersionUID = 1;
	
	protected int droneNumber;
	protected double scale;
	protected Double2D me;
	
	/* 1. dataObject: The data that the drone holds.
	 * 2. nearbyDrones: The drones' number which are within the communication area. 
	 * 3. vector: To indicate that what data the drone is holding. Stored by hash code.
	 * 4. ACK: The acknowledge to eliminate the data which has arrived the destination. */
	protected ArrayList<DataObject> dataObject = new ArrayList<DataObject>();
	protected ArrayList<Integer> nearbyDrones;
	protected ArrayList<HashCode> vector = new ArrayList<HashCode>();
	protected ArrayList<HashCode> ACK = new ArrayList<HashCode>();
	
	/* The duration that ACK exist, in simulator steps. */
	protected double ACKDuration = 3000;
	protected double ACKTimestamp;
	protected double currentTime;
	
	/* The attributes for computing the time that the drone pass its own data to the Captain. */
	protected double startTime;
	protected double duration;
	protected double endTime;
	protected boolean isItselfDataSent = false;
	protected boolean isResultWritten = false;
	
	/* The copies for the Spray and Wait Routing Protocol. */
	protected int copies = 1;
	
	/* The Time-to-Live protocol attributes. */
	protected int timeToLive = 100;
	
	/* The attributes of the waypoints. */
	protected ArrayList<Integer> preWaypoints = new ArrayList<Integer>();
	protected double wayPointX;
	protected double wayPointY;
	protected int newWaypoint;
	private int howManyWaypointsNotGoBack = 4;
	
	/* The velocity of the drones, x meters per time step. */
	protected double velocity = 1.5; 
	
	public String toString() {
		if(!dataObject.isEmpty()){
			String result = "Drone: "+droneNumber+" Time: "+duration+" Data: ";
			for(int i=0; i<dataObject.size(); i++){
				result += " " + dataObject.get(i).getData();
			}
			return result;
		}
		else{
			String result = "Drone: "+droneNumber+" Time: "+duration;
			return result;
		}
	}
	
	public void step(SimState state){
		scale = 5.0;

		move(state);
	}
	
	public void move(SimState state){
		Demo demo = (Demo) state;
		Continuous2D drones = demo.drones;
		
		me = drones.getObjectLocation(this);
		
		/* The previous waypoints will not be added to the available waypoints,
		 * making sure that the drone will not go back to the previous waypoint. */
		if(droneArriveWaypoint(drones)){
			ArrayList<Integer> availableWaypoints = new ArrayList<Integer>();
			if(Demo.connectionMatrix.get(newWaypoint).size() != 1){
				for(int i=0; i<Demo.connectionMatrix.get(newWaypoint).size(); i++){
					if(notInPreWaypointListChecker(Demo.connectionMatrix.get(newWaypoint).get(i))){
						availableWaypoints.add(Demo.connectionMatrix.get(newWaypoint).get(i));
					}
				}
				/* If all the available waypoints are all in previous waypoints,
				 * the oldest one in previous waypoints will be pointed as the new waypoint. */
				if(preWaypoints.containsAll(availableWaypoints)){
					ArrayList<Integer> availableWaypointsPositionInPreWaypoints = new ArrayList<Integer>();
					for(int i=0; i<Demo.connectionMatrix.get(newWaypoint).size(); i++){
						availableWaypointsPositionInPreWaypoints.add(preWaypoints.indexOf(Demo.connectionMatrix.get(newWaypoint).get(i)));
					}
					int oldestPosition = Collections.min(availableWaypointsPositionInPreWaypoints);
					availableWaypoints.add(preWaypoints.get(oldestPosition));
				}
			}
			else{
				availableWaypoints.add(Demo.connectionMatrix.get(newWaypoint).get(0));
			}
			
			/* The previous waypoint has been updated. */
			preWaypoints.add(newWaypoint);
			
			/* The new waypoint is randomly selected, then it is updated. */
			int selectRandomWaypoint = demo.random.nextInt(availableWaypoints.size());
			int waypointAllocation = availableWaypoints.get(selectRandomWaypoint);
			wayPointX = Math.abs(Demo.waypointCoordinate.get(waypointAllocation).get(0) - Demo.originX);
			wayPointY = Math.abs(Demo.waypointCoordinate.get(waypointAllocation).get(1) - Demo.originY);
			newWaypoint = waypointAllocation;
			
			/* Output the new available waypoints. */
//			String b="";
//			for(int bb:availableWaypoints){
//			b += bb+" ";
//			}
//			System.out.println("New waypoints: "+b);
			
			if(preWaypoints.size() > howManyWaypointsNotGoBack){
				for(int i=0; i<howManyWaypointsNotGoBack; i++){
					preWaypoints.remove(0);
				}
			}
			
			/* Output the previous waypoints. */
//			String a="";
//			for(int aa:preWaypoints){
//			a += aa+" ";
//			}
//			System.out.println("Previous waypoints: "+a);
		}
		else{
			double Mx = me.x;
			double My = me.y;
			double Wx = wayPointX;
			double Wy = wayPointY;
			
			double distance = Math.sqrt(Math.pow((Mx - Wx), 2) + Math.pow((My - Wy), 2));
			
			double sinT = (My - Wy) / distance;
			double cosT = (Mx - Wx) / distance;
			
			double Sy = velocity * sinT;
			double Sx = velocity * cosT;
			
			double newX = Mx - Sx;
			double newY = My - Sy;
			
			demo.drones.setObjectLocation(this, new Double2D(newX,newY));
			
			/* This is for checking the precision of the drone's current location
			 * and the precision of the waypoint location. */
//			System.out.println((int)(me.x)+" "+(int)(wayPointX));
//			System.out.println((int)(me.y)+" "+(int)(wayPointY));
		}
		timeToLive(demo);
		vaccination(demo);
		timer(demo);
	}
	
	public void vaccination(Demo demo){
		currentTime = demo.schedule.getTime();
		
		if(!ACK.isEmpty()){
			if((currentTime - ACKTimestamp) < ACKDuration){
				if(!dataObject.isEmpty()){
					for(int i=0; i<ACK.size(); i++){
						if(vector.contains(ACK.get(i))){
							vector.remove(ACK.get(i));
							
							for(int j=0; j<dataObject.size(); j++){
								if(dataObject.get(j).getHashCode().equals(ACK.get(i))){
									dataObject.remove(j);
								}	
							}
						}
					}
				}
			}
			else{
				ACK.clear();
				ACKTimestamp = 0;
			}
		}
	}
	
	public void timer(Demo demo){
		if(isItselfDataSent){
			duration = (endTime - startTime);
		}
		else{	
			if(droneItselfDataSentChecker()){
				endTime = demo.schedule.getTime();
				isItselfDataSent = true;
			}
			else{
				duration = (demo.schedule.getTime() - startTime);
			}
		}
	}
	
	public boolean droneItselfDataSentChecker(){
		for(int i=0; i<dataObject.size(); i++){
			if(dataObject.get(i).getSource()==droneNumber){
				return false;
			}
		}
		return true;
	}
	
	public boolean notInPreWaypointListChecker(int waypoint){
		for(int i=0; i<preWaypoints.size(); i++){
			if(waypoint == preWaypoints.get(i)){
				return false;
			}
		}
		return true;
	}
	
	public void timeToLive(Demo demo){
		if(Demo.timeToLiveStartTimeStep){			
			for(int i=0; i<dataObject.size(); i++){
				if((demo.schedule.getTime() - dataObject.get(i).getTimeStep()) > timeToLive){
					dataObject.remove(i);
				}
			}
		}	
		if(Demo.timeToLiveStartHopCount){
			for(int i=0; i<dataObject.size(); i++){
				if(dataObject.get(i).getHopCount()==0){
					dataObject.remove(i);
				}
			}
		}
	}
	
	public boolean droneArriveWaypoint(Continuous2D drones){
		me = drones.getObjectLocation(this);
		int Mx = (int)me.x;
		int My = (int)me.y;
		int Wx = (int)wayPointX;
		int Wy = (int)wayPointY;
		if((Mx == Wx) && (My == Wy)){
			return true;
		}
		else if((Mx +1 == Wx) && (My == Wy)){
			return true;
		}
		else if((Mx == Wx) && (My +1 == Wy)){
			return true;
		}
		else if((Mx -1 == Wx) && (My == Wy)){
			return true;
		}
		else if((Mx == Wx) && (My -1 == Wy)){
			return true;
		}
		else if((Mx +1 == Wx) && (My +1 == Wy)){
			return true;
		}
		else if((Mx +1 == Wx) && (My -1 == Wy)){
			return true;
		}
		else if((Mx -1 == Wx) && (My +1 == Wy)){
			return true;
		}
		else if((Mx -1 == Wx) && (My -1 == Wy)){
			return true;
		}
		else{
			return false;
		}
	}
	
	/** This method is for rounding the location of the drone and the waypoints. */
//	public double precision(double number){
//		/* Results with one decimal place without rounding off. */
//		double result = Math.floor(number * 10) / 10;
//		/* Results rounded with one decimal place. */
////		DecimalFormat df = new DecimalFormat("#.0");
////		String string = df.format(number);
////		double result = Double.parseDouble(string);
//		return result;
//	}

	/** The following method is for inspecting drone status in Model. */
	public ArrayList<Double> getTimeStep(){
		ArrayList<Double> time = new ArrayList<Double>();
		for(int i=0; i<dataObject.size(); i++){
			time.add(dataObject.get(i).getTimeStep());
		}
		return time;
	}
	
	public ArrayList<Integer> getNearbyDrones(){
		return nearbyDrones;
	}
	
	public double getACKDuration(){
		if(ACKTimestamp == 0){
			return ACKTimestamp;
		}
		else{
			return (currentTime - ACKTimestamp);
		}
	}
	
	public ArrayList<Integer> getHopCount(){
		ArrayList<Integer> hopCount = new ArrayList<>();
		for(int i=0; i<dataObject.size(); i++){
			hopCount.add(dataObject.get(i).getHopCount());
		}
		return hopCount;
	}
	
//	public ArrayList<Integer> getHashCode(){
//		ArrayList<Integer> result = new ArrayList<Integer>();
//		for(int i=0; i<hashCode.size(); i++){
//			result.add(hashCode.get(i).getHashCode());
//		}
//		return result;
//	}
	
//	public ArrayList<Long> getTime(){
//		ArrayList<Long> time = new ArrayList<Long>();
//		for(int i=0; i<dataObject.size(); i++){
//			time.add(dataObject.get(i).getTime());
//		}
//		return time;
//	}

//	public ArrayList<Integer> getDataSource(){
//		ArrayList<Integer> dataSource = new ArrayList<Integer>();
//		for(int i=0; i<dataObject.size(); i++){
//			dataSource.add(dataObject.get(i).getSource());
//		}
//		return dataSource;
//	}

//	public ArrayList<Integer> getDataContent(){
//		ArrayList<Integer> dataContent = new ArrayList<Integer>();
//		for(int i=0; i<dataObject.size(); i++){
//			dataContent.add(dataObject.get(i).getData());
//		}
//		return dataContent;
//	}

//	public ArrayList<Integer> getACK(){
//		ArrayList<Integer> dataACK = new ArrayList<Integer>();
//		for(int i=0; i<ACK.size(); i++){
//			dataACK.add(ACK.get(i).getHashCode());
//		}
//		return dataACK;
//	}

//	public double getScale(){
//		return scale;
//	}

//	public double getWayPointX(){
//		return wayPointX;
//	}

//	public double getWayPointY(){
//		return wayPointY;
//	}

//	public int getDroneNumber(){
//		return droneNumber;
//	}
	
}
