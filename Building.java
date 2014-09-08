package sim.app.drones;

import java.awt.Color;
import java.util.ArrayList;

import sim.portrayal.simple.ShapePortrayal2D;

public class Building extends ShapePortrayal2D {
	
	private static final long serialVersionUID = 1;
	protected ArrayList<Double> X = new ArrayList<Double>();
	protected ArrayList<Double> Y = new ArrayList<Double>();
	protected double[] buildingX = new double[X.size()];
	protected double[] buildingY = new double[Y.size()];
	
	public Building(double[] xpoints, double[] ypoints, boolean filled) {
		super(xpoints, ypoints, Color.blue, 1.0, false);
		this.buildingX = xpoints;
		this.buildingY = ypoints;
	}
	
}
