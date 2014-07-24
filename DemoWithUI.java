package sim.app.drones;

import sim.portrayal.Inspector;
import sim.portrayal.continuous.*;
import sim.display.Console;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.simple.*;
import javax.swing.*;

import java.awt.Color;

public class DemoWithUI extends GUIState{
	
	public Display2D display;
	public JFrame displayFrame;
	ContinuousPortrayal2D dronePortrayal = new ContinuousPortrayal2D();
	ContinuousPortrayal2D captainPortrayal = new ContinuousPortrayal2D();

	public static void main(String[] args) {
		DemoWithUI vid = new DemoWithUI();
		Console c = new Console(vid);
		c.setVisible(true);
		
	}
	
	public DemoWithUI(){
		super(new Demo(System.currentTimeMillis()));
	}
	
	public DemoWithUI(SimState state){
		super(state);
	}
	
	public static String getName(){
		return "Drone Navigation";
	}
	
	public Object getSimulationInspectedObject(){
		return state;
	}
	
	public Inspector getInspector(){
		Inspector i = super.getInspector();
		i.setVolatile(true);
		return i;
	}
	
	public void start(){
		super.start();
		setupPortrayals();
	}
	
	public void load(SimState state){
		super.load(state);
		setupPortrayals();
	}

	private void setupPortrayals() {
		Demo demo = (Demo) state;
		
		dronePortrayal.setField(demo.drones);
		dronePortrayal.setPortrayalForAll(
				new MovablePortrayal2D(
						new CircledPortrayal2D(
								new CircledPortrayal2D(
									new LabelledPortrayal2D(
											new OvalPortrayal2D(),
											5.0, null, Color.black, false),
											0, 10.0, Color.green, false),
											0, 10.0, Color.red, true)));
		
		captainPortrayal.setField(demo.captains);
		captainPortrayal.setPortrayalForAll(
				new MovablePortrayal2D(
						new CircledPortrayal2D(
								new LabelledPortrayal2D(
										new RectanglePortrayal2D(5.0),
										5.0, null, Color.black, false),
										0, 20.0, Color.blue, false)));
		
		display.reset();
		
		display.repaint();	
	}
	
	public void init(Controller c){
		super.init(c);
		display = new Display2D(500,500,this);
		display.setClipping(true);
		
		display.setBackdrop(Color.white);
		
		displayFrame = display.createFrame();
		displayFrame.setTitle("Drone Space Display");
		c.registerFrame(displayFrame);
		displayFrame.setVisible(true);
		display.attach(dronePortrayal, "Drones");
		display.attach(captainPortrayal, "Captains");
	}
	
	public void quit(){
		super.quit();
		if(displayFrame!=null){
			displayFrame.dispose();
		}
		displayFrame = null;
		display = null;
	}

}
