package sim.app.drones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class FileOutput {
	
	public static String lineSep = System.getProperty("line.separator");
	
	public static void deleteFile(String filename){
		File file = new File(filename);
		if(file.exists()){
			file.delete();
		}
	}
	
//	public static ArrayList<ArrayList<String>> readFile(String filename){
//		ArrayList<ArrayList<String>> contents=new ArrayList<ArrayList<String>>();
//		File file=new File(filename);		
//		
//		if(file.exists()){
//			try{
//				BufferedReader reader = new BufferedReader(new FileReader(file));
//				String thisLine="";
//				int i=0;
//				while ((thisLine = reader.readLine()) != null) {
//					String[] line=thisLine.split(",",0);
//					contents.add(i,new ArrayList<String>());
//					for(int j=0;j<line.length;j++){
//						contents.get(i).add(line[j]);
//					}
//					i++;
//				}
//				reader.close();
//			}			
//			
//			catch(Exception e){
//				e.printStackTrace();
//			}		
//		}
//		
//		return contents;
//	}
	
	public static void insertFile(String fileName, ArrayList<String> contents){
		try{
			File file=new File(fileName);
			if(!file.exists()){
				file.createNewFile();
			}
			String output = "";
			for(int i=0;i<contents.size();i++){
				output+=contents.get(i);
				output+=(i==contents.size()-1?"":",");
			}
			output+=lineSep;		
			BufferedWriter writer = new BufferedWriter(new FileWriter(file,true));
			writer.write(output);
			writer.flush();
			writer.close();			
			
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
	
//	public static void updateFile(String filename, ArrayList<ArrayList<String>> contents){
//		try{
//			File file=new File(filename);
//			if (file.exists()){
//				file.delete();
//			}
//			file.createNewFile();
//			String output = "";
//			for(int i=0;i<contents.size();i++){
//				for(int j=0;j<contents.get(i).size();j++){
//					output+=contents.get(i).get(j);
//					output+=((i==(contents.size()-1)&&(j==(contents.get(i).size())))?"":",");
//				}
//				output+=lineSep;
//			}		
//			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//			writer.write(output);
//			writer.flush();
//			writer.close();			
//			
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//	}
}
