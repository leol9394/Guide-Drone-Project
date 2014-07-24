package sim.app.drones;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileOutput {
	
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
	
		public static int readFile(String filename){
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
