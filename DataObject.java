package sim.app.drones;

public class DataObject {
	private int source;
	private int data;
	private long time;
	//private boolean ACK;
	
	public void setSource(int source){
		this.source = source;
	}
	
	public int getSource(){
		return source;
	}
	
	public void setData(int data){
		this.data = data;
	}
	
	public int getData(){
		return data;
	}
	
	public void setTime(long time){
		this.time = time;
	}
	
	public long getTime(){
		return time;
	}
	
//	public void setACK(boolean state){
//		this.ACK = state;
//	}
//	
//	public boolean getACK(){
//		return ACK;
//	}

}
