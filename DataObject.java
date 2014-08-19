package sim.app.drones;

public class DataObject {
	
	public class HashCode {
		private int hashCode;
		private long hashCodeGeneratedTime;
		
		public void setHashCode(int hashCode){
			this.hashCode = hashCode;
		}
		
		public int getHashCode(){
			return hashCode;
		}
		
		public void setHashCodeGeneratedTime(long time){
			this.hashCodeGeneratedTime = time;
		}
		
		public long getHashCodeGeneratedTime(){
			return hashCodeGeneratedTime;
		}
	}
	
	private int source;
	private int data;
	private long generatedTime;
	private double timeStep;
	private int hopCount;
	private HashCode hashCode = new HashCode();
//	private Object object;
	
	public DataObject(){}
	
	public DataObject(DataObject another){
		this.source = another.source;
		this.data = another.data;
		this.generatedTime = another.generatedTime;
		this.timeStep = another.timeStep;
		this.hopCount = another.hopCount;
		this.hashCode = another.hashCode;
//		this.object = another.object;
				
	}
	
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
		this.generatedTime = time;
	}
	
	public long getTime(){
		return generatedTime;
	}
	
	public void setTimeStep(double timeStep){
		this.timeStep = timeStep;
	}
	
	public double getTimeStep(){
		return timeStep;
	}
	
	public void setHopCount(int hopCount){
		this.hopCount = hopCount;
	}
	
	public int getHopCount(){
		return hopCount;
	}
	
	public void setHashCode(int hashCode){
		this.hashCode.setHashCode(hashCode);
	}
	
	public void setHashCodeGeneratedTime(long time){
		this.hashCode.setHashCodeGeneratedTime(time);
	}
	
	public HashCode getHashCode(){
		return hashCode;
	}
	
//	public void setObject(Object object){
//		this.object = object;
//	}
//	
//	public Object getObject(){
//		return object;
//	}
	
}
