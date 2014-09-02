package sim.app.drones;

import java.util.ArrayList;

public class DataConverter {
	
	public static ArrayList<ArrayList<Double>> stringToDouble(ArrayList<ArrayList<String>> string){
		ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		for(int i=0; i<string.size(); i++){
			ArrayList<Double> arrayDouble = new ArrayList<Double>();
			for(int j=0; j<string.get(i).size(); j++){
				arrayDouble.add(Double.parseDouble(string.get(i).get(j)));
			}
			result.add(arrayDouble);
		}
		return result;
	}
	
	public static ArrayList<ArrayList<Integer>> stringToInteger(ArrayList<ArrayList<String>> string){
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		for(int i=0; i<string.size(); i++){
			ArrayList<Integer> arrayInteger = new ArrayList<Integer>();
			for(int j=0; j<string.get(i).size(); j++){
				arrayInteger.add(Integer.parseInt((string.get(i).get(j))));
			}
			result.add(arrayInteger);
		}
		return result;
	}
	
	public static ArrayList<double[]> stringToDoubleArray(ArrayList<ArrayList<String>> string){
		ArrayList<double[]> result = new ArrayList<double[]>();
		for(int i=0; i<string.size(); i++){
			double[] arrayDouble = new double[string.get(i).size()];
			for(int j=0; j<string.get(i).size(); j++){
				arrayDouble[j] = Double.parseDouble(string.get(i).get(j));
			}
			result.add(arrayDouble);
		}
		return result;
	}

}
