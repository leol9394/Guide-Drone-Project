package sim.app.drones;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class DataRecord {

	public static void main(String[] args) {
		
		try {
			Robot mouse = new Robot();
			// It is corresponding to the resolution of Retina Macbook 13, But also used in iMac.
			mouse.mouseMove(535, 30);
			int buttons = InputEvent.BUTTON1_DOWN_MASK;
			mouse.mousePress(buttons);
			mouse.mouseRelease(buttons);
		} catch (AWTException e) {
			e.printStackTrace();
		}

		DemoWithUI.main(args);
		
		try {
			Robot mouse = new Robot();
			// It is corresponding to the resolution of Retina Macbook 13, But also used in iMac.
			mouse.mouseMove(535, 385);
			int buttons = InputEvent.BUTTON1_DOWN_MASK;
			mouse.mousePress(buttons);
			mouse.mouseRelease(buttons);
		} catch (AWTException e) {
			e.printStackTrace();
		}	
		

	}

}
