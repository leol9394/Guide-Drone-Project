package sim.app.drones;


import java.util.ArrayList;

import sim.app.drones.DataObject.HashCode;
import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;

public class Drone implements Steppable{
	
	private static final long serialVersionUID = 1;
	
	protected int droneNumber;
	protected double scale;
	protected Double2D me;
	
	protected ArrayList<DataObject> dataObject = new ArrayList<DataObject>();
	protected ArrayList<Integer> nearbyDrones;
	protected ArrayList<HashCode> hashCode = new ArrayList<HashCode>();
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
	protected int copies = 3;
	
	/* The Time-to-Live protocol attributes. */
	protected int[] ownDataDuration = new int[Demo.numData * 2];
	protected int[] itselfDataPosition = new int[Demo.numData];
	protected int timeToLive = 50;
	
	protected double wayPointX;
	protected double wayPointY;
	protected int waypointPosition;
	
	/* The velocity of the drones, 0.1 meters per time step. */
	protected double velocity = 0.1; 
	
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
	
	public void step(SimState state){
		scale = 5.0;

		move(state);
	}
	
	public void move(SimState state){
		Demo demo = (Demo) state;
		Continuous2D drones = demo.drones;
		
		me = drones.getObjectLocation(this);
		
		if((precision(me.x)==precision(wayPointX)) && (precision(me.y)==precision(wayPointY))){
			ArrayList<Integer> waypointAccess = new ArrayList<Integer>();
			for(int i=0; i<Demo.connectionMatrix.get(waypointPosition).size(); i++){
				if(Demo.connectionMatrix.get(waypointPosition).get(i).equals(1)){
					waypointAccess.add(i);
				}
			}
			int newWaypoint = demo.random.nextInt(waypointAccess.size());
			
			wayPointX = Demo.waypointCoordinate.get(waypointAccess.get(newWaypoint)).get(0);
			wayPointY = Demo.waypointCoordinate.get(waypointAccess.get(newWaypoint)).get(1);
			waypointPosition = waypointAccess.get(newWaypoint);			
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
						if(hashCode.contains(ACK.get(i))){
							hashCode.remove(ACK.get(i));
							
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
	
	public void timeToLive(Demo demo){
		if(Demo.isTimeToLiveStart){
//			TTL(demo);
			droneItselfDataPosition();
			itselfDataDuration(demo);
			itselfDataDurationCheckerAndRemoval();
		}	
	}
	
	public void itselfDataDuration(Demo demo){
		if(!dataObject.isEmpty()){
			int j=0;
			for(int i=0; i<itselfDataPosition.length; i++){
				ownDataDuration[j] = ((int)(demo.schedule.getTime() - dataObject.get(itselfDataPosition[i]).getTimeStep()));
				ownDataDuration[j+1] = itselfDataPosition[i];
				j = j+2;
			}
		}
	}
	
	public void itselfDataDurationCheckerAndRemoval(){
		if(!dataObject.isEmpty()){
			for(int i=0; i<ownDataDuration.length; i=i+2){
				if(ownDataDuration[i]==timeToLive){
					dataObject.remove(ownDataDuration[i+1]);
				}
			}
		}
	}
	
	public void droneItselfDataPosition(){
		int j = 0;
		for(int i=0; i<dataObject.size(); i++){
			if(dataObject.get(i).getSource()==droneNumber){
				itselfDataPosition[j] = i;
				j++;
			}
		}
	}
	
	public double precision(double number){
		/* Results with one decimal place without rounding off. */
		double result = Math.floor(number * 10) / 10;
		/* Results rounded with one decimal place. */
//		DecimalFormat df = new DecimalFormat("#.0");
//		String string = df.format(number);
//		double result = Double.parseDouble(string);
		return result;
	}
	
	/* This method just consider that the drone just has only one data. */
//	public void TTL(Demo demo){
//	if(!droneItselfDataSentChecker()){
//		for(int i=0; i<dataObject.size(); i++){
//			if(dataObject.get(i).getSource()==droneNumber){
//				if((demo.schedule.getTime() - dataObject.get(i).getTimeStep()) > timeToLive){
//					dataObject.remove(i);
//				}
//			}
//		}
//	}
//}
	
//	public ArrayList<Long> getTime(){
//	ArrayList<Long> time = new ArrayList<Long>();
//	for(int i=0; i<dataObject.size(); i++){
//		time.add(dataObject.get(i).getTime());
//	}
//	return time;
//}

//public ArrayList<Integer> getDataSource(){
//	ArrayList<Integer> dataSource = new ArrayList<Integer>();
//	for(int i=0; i<dataObject.size(); i++){
//		dataSource.add(dataObject.get(i).getSource());
//	}
//	return dataSource;
//}

//public ArrayList<Integer> getDataContent(){
//	ArrayList<Integer> dataContent = new ArrayList<Integer>();
//	for(int i=0; i<dataObject.size(); i++){
//		dataContent.add(dataObject.get(i).getData());
//	}
//	return dataContent;
//}

//public ArrayList<Integer> getACK(){
//	ArrayList<Integer> dataACK = new ArrayList<Integer>();
//	for(int i=0; i<ACK.size(); i++){
//		dataACK.add(ACK.get(i).getHashCode());
//	}
//	return dataACK;
//}

//public double getScale(){
//	return scale;
//}

//public double getWayPointX(){
//	return wayPointX;
//}
//
//public double getWayPointY(){
//	return wayPointY;
//}

//public int getDroneNumber(){
//	return droneNumber;
//}
	
}
