package sim.app.drones;


import java.util.ArrayList;

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
	protected ArrayList<DataObject> ACK = new ArrayList<DataObject>();
	protected long ACKTimestamp;
	// The duration that ACK exist, in seconds.
	protected long ACKDuration = 30;
	
	protected double wayPointX;
	protected double wayPointY;
	protected double velocity = 0.1;
	
	public String toString() {
		return "Drone: "+droneNumber;
	}
	
	public long getACKTimestamp(){
		return ACKTimestamp;
	}
	
	public long getACKDuration(){
		if(ACKTimestamp == 0){
			return ACKTimestamp;
		}
		else{
			return (System.nanoTime() - ACKTimestamp);
		}
	}
	
//	public double getWayPointX(){
//		return wayPointX;
//	}
//	
//	public double getWayPointY(){
//		return wayPointY;
//	}
	
	public ArrayList<Integer> getNearbyDrones(){
		return nearbyDrones;
	}
	
	public ArrayList<Integer> getDataSource(){
		ArrayList<Integer> dataSource = new ArrayList<Integer>();
		for(int i=0; i<dataObject.size(); i++){
			dataSource.add(dataObject.get(i).getSource());
		}
		return dataSource;
	}
	
	public ArrayList<Integer> getDataContent(){
		ArrayList<Integer> dataContent = new ArrayList<Integer>();
		for(int i=0; i<dataObject.size(); i++){
			dataContent.add(dataObject.get(i).getData());
		}
		return dataContent;
	}
	
//	public ArrayList<Long> getDataTime(){
//		ArrayList<Long> dataTime = new ArrayList<Long>();
//		for(int i=0; i<dataObject.size(); i++){
//			dataTime.add(dataObject.get(i).getTime());
//		}
//		return dataTime;
//	}
	
	public ArrayList<Integer> getACK(){
		ArrayList<Integer> dataContent = new ArrayList<Integer>();
		for(int i=0; i<ACK.size(); i++){
			dataContent.add(ACK.get(i).getData());
		}
		return dataContent;
	}
	
	public int getDroneNumber(){
		return droneNumber;
	}
	
	public double getScale(){
		return scale;
	}
	
	public double X(){
		return me.x;
	}
	
	public double Y(){
		return me.y;
	}
	
	public void step(SimState state){
		scale = 5.0;

		move(state);
		
		vaccination();
	}
	
	public void move(SimState state){
		Demo demo = (Demo) state;
//		double now = demo.schedule.getTime();
		Continuous2D drones = demo.drones;
		
		me = drones.getObjectLocation(this);
		
		if((int)me.x!=(int)wayPointX && (int)me.y!=(int)wayPointY){
			double Mx = me.x;
			double My = me.y;
			double Wx = wayPointX;
			double Wy = wayPointY;
			
			double distance = Math.sqrt(Math.pow((Mx - Wx), 2) + Math.pow((My - Wy), 2));
			
			double sinT = (My - Wy) / distance;
			double cosT = (Mx - Wx) / distance;
			
			double Sy = velocity * sinT;
			double Sx = velocity * cosT;
			
			double newX = demo.drones.stx(Mx - Sx);
			double newY = demo.drones.stx(My - Sy);
			
			demo.drones.setObjectLocation(this, new Double2D(newX,newY));
		}
		else{
			wayPointX =drones.getWidth() * demo.random.nextDouble();
			wayPointY =drones.getHeight() * demo.random.nextDouble();
		}	
//		velocityX = scaleX * (waypointX - currentX);
//		velocityY = scaleY * (waypointY - currentY);
//		
//		x += DT * Math.sgn(velocityX) * Math.min(abs(velocityX), maxVelocityX);
//		y += DT * velocityY;	    
	}
	
	public void vaccination(){
		
		long currentTime = System.nanoTime();
		
		if(!ACK.isEmpty()){
			if((currentTime - ACKTimestamp) < (ACKDuration * Math.pow(10.0, 9))){
				if(!dataObject.isEmpty()){
					for(int i=0; i<ACK.size(); i++){
						if(dataObject.contains(ACK.get(i))){
							dataObject.remove(ACK.get(i));
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
}
