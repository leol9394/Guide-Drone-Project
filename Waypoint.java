package sim.app.drones;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.field.continuous.Continuous2D;
import sim.util.Double2D;
import sim.util.MutableDouble2D;

public class Waypoint implements Steppable{
	
	private static final long serialVersionUID = 1;
	
	protected Double2D me;
	protected int waypointNumber;
	
	public String toString(){
		return " ";
	}

	public void step(SimState state){
		Demo demo = (Demo) state;
		
		Continuous2D waypoints = demo.waypoints;
		
		me = waypoints.getObjectLocation(this);
		
		MutableDouble2D sumNavigation = new MutableDouble2D();
		
		sumNavigation.addIn(me);
		
		demo.waypoints.setObjectLocation(this, new Double2D(sumNavigation));
	}
}
