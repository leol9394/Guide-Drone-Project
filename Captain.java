package sim.app.drones;

import java.util.ArrayList;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

public class Captain implements Steppable{
	private static final long serialVersionUID = 1;
	
	protected Double2D me;
	protected double scale;
	protected ArrayList<DataObject> dataObject = new ArrayList<DataObject>();
	protected ArrayList<Integer> nearbyDrones;
	protected ArrayList<HashCode> hashCode = new ArrayList<HashCode>();
	
	public String toString() {
		return "Captain";
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
	
	public ArrayList<Integer> getHashCode(){
		ArrayList<Integer> hash = new ArrayList<Integer>();
		for(int i=0; i<hashCode.size(); i++){
			hash.add(hashCode.get(i).getHashCode());
		}
		return hash;
	}
	
	public int getScale(){
		return (int)scale;
	}
	
	public double X(){
		return me.x;
	}
	
	public double Y(){
		return me.y;
	}
	
	public void step(SimState state){	
		scale = 10.0;
		
		move(state);
	}
	
	public void move(SimState state){
		Demo demo = (Demo) state;
		
		Continuous2D captains = demo.captains;
		
		me = captains.getObjectLocation(this);
		
		MutableDouble2D sumNavigation = new MutableDouble2D();
		
		sumNavigation.addIn(me);
		
		demo.captains.setObjectLocation(this, new Double2D(sumNavigation));
		
	}
}
