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
	
	protected ArrayList<HashCode> hashCode = new ArrayList<HashCode>();
	protected ArrayList<HashCode> ACK = new ArrayList<HashCode>();
	
	protected long ACKTimestamp;
	protected long ACKDuration = 30; 	// The duration that ACK exist, in seconds.
	
	protected double wayPointX;
	protected double wayPointY;
	protected double velocity = 0.1;
	
	public String toString() {
		return "Drone: "+droneNumber;
	}
	
	public long getACKDuration(){
		if(ACKTimestamp == 0){
			return ACKTimestamp;
		}
		else{
			return (System.nanoTime() - ACKTimestamp);
		}
	}
	
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
	
	public ArrayList<Integer> getACK(){
		ArrayList<Integer> dataACK = new ArrayList<Integer>();
		for(int i=0; i<ACK.size(); i++){
			dataACK.add(ACK.get(i).getHashCode());
		}
		return dataACK;
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
	
//	public double getWayPointX(){
//	return wayPointX;
//}
//
//public double getWayPointY(){
//	return wayPointY;
//}
	
	public void step(SimState state){
		scale = 5.0;

		move(state);
		
		vaccination();
	}
	
	public void move(SimState state){
		Demo demo = (Demo) state;
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
	}
	
	public void vaccination(){
		
		long currentTime = System.nanoTime();
		
		if(!ACK.isEmpty()){
			if((currentTime - ACKTimestamp) < (ACKDuration * Math.pow(10.0, 9))){
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
}
