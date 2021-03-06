package sim.app.drones;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class MouseMovement {

	public static void main(String[] args) {
		
		try {
			Robot mouse = new Robot();
			// It is corresponding to the resolution of Retina Macbook 13, But also used in iMac.
			mouse.mouseMove(490, 30);
			int buttons = InputEvent.BUTTON1_MASK;
			mouse.mousePress(buttons);
			mouse.mouseRelease(buttons);
		} catch (AWTException e) {
			e.printStackTrace();
		}

		DemoWithUI.main(args);
		
		try {
			Robot mouse = new Robot();
			// It is corresponding to the resolution of Retina Macbook 13, But also used in iMac.
			mouse.mouseMove(490, 385);
			int buttons = InputEvent.BUTTON1_MASK;
			mouse.mousePress(buttons);
			mouse.mouseRelease(buttons);
		} catch (AWTException e) {
			e.printStackTrace();
		}	
		

	}

}
