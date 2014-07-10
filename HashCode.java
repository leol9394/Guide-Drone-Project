package sim.app.drones;

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
