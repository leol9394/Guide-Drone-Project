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
	private HashCode hashCode = new HashCode();
	//private Object object;
	
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
