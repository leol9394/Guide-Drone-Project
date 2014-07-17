package sim.app.drones;

import java.util.ArrayList;

import sim.app.drones.DataObject.HashCode;
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
	
	//The attributes for computing the time that the Captain has received all the data.
	protected double startTime;
	protected double duration;
	protected double endTime;
	protected boolean isAllDataReceivedYet = false;
	protected boolean isResultReadyToWrite = false;
	protected boolean isResultWritten = false;
	
	public String toString() {
		if(!dataObject.isEmpty()){
			String result = "Captain Time: "+duration+" Data: ";
			for(int i=0; i<dataObject.size(); i++){
				result += " " + dataObject.get(i).getData();
			}
			return result;
		}
		else{
			String result = "Captain Time: "+duration;
			return result;
		}
	}
	
	public double getDuration(){
		return duration;
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
	
	public ArrayList<Long> getTime(){
		ArrayList<Long> time = new ArrayList<Long>();
		for(int i=0; i<dataObject.size(); i++){
			time.add(dataObject.get(i).getTime());
		}
		return time;
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
		
		timer(demo);
		
	}
	
	public void timer(Demo demo){
		if(!isAllDataReceivedYet){
			if(!(dataObject.size()==(demo.numDrones*demo.numData))){
				duration = (demo.schedule.getTime() - startTime);
			}
			else{
				endTime = demo.schedule.getTime();
				isAllDataReceivedYet = true;
			}
		}
		else{
			duration = (endTime - startTime);
		}

//		This is the wallock time.
//		boolean isAllDataReceivedYet = false;
//		if(!isAllDataReceivedYet){
//			if(!(dataObject.size()==(demo.numDrones*demo.numData))){
//				duration = (System.currentTimeMillis() - startTime);
//			}
//			else{
//				endTime = System.currentTimeMillis();
//				isAllDataReceivedYet = true;
//			}
//		}
//		else{
//			duration = (endTime - startTime);
//		}
	}
}
