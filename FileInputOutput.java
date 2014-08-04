package sim.app.drones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public class FileInputOutput {
	
	public static String lineSep = System.getProperty("line.separator");
	
	public static void deleteFile(String filename){
		File file = new File(filename);
		if(file.exists()){
			file.delete();
		}
	}
	
	public static void insertFile(String fileName, String[] contents){
		try{
			File file=new File(fileName);
			if(!file.exists()){
				file.createNewFile();
			}
			String output = "";
			for(int i=0;i<contents.length;i++){
				if(contents[i]==null){
					contents[i]="";
				}
				output+=contents[i];
				output+=(i==contents.length-1?"":",");
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
	
	public static ArrayList<ArrayList<String>> readFile(String filename){
		ArrayList<ArrayList<String>> contents=new ArrayList<ArrayList<String>>();
		File file=new File(filename);		
		
		if(file.exists()){
			try{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String thisLine;
				int i=0;
				while ((thisLine = reader.readLine()) != null) {
					String[] line=thisLine.split(",",0);
					contents.add(i,new ArrayList<String>());
					for(int j=0;j<line.length;j++){
						contents.get(i).add(line[j]);
					}
					i++;
				}
				reader.close();
			}			
			
			catch(Exception e){
				e.printStackTrace();
			}		
		}
		
		return contents;
	}
	
	public static int lineCount(String filename){
		int count = 0;
		File file=new File(filename);		
			
		if(file.exists()){
			try{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				while (reader.readLine() != null) {
					count += 1;
				}
				reader.close();
			}			
			
			catch(Exception e){
				e.printStackTrace();
			}		
		}
		return count;
	}
}
