package vdsl.fileIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileDealer {

	public static void main(String[] args) {

		FileDealer fd= new FileDealer();
		fd.writeToFile(new File("C:/Users/vikkuma2/Desktop/MPM/test1.xml"),"<sometag>SomeValue</sometag>");
		System.out.println("Mission Success");
		System.out.println( fd.readFromFile(new File("C:/Users/vikkuma2/Desktop/MPM/test3.xml")) ); 
	}

	public boolean writeToFile(File file, String message) {

		try {

			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter writer = new FileWriter(file);

			writer.write(message);
			writer.flush();
			writer.close();

			return true;
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found -> "+ file);
			return false;
		} catch (IOException e) {
			System.out.println("IO Exception in file -> "+ file);
			return false;
		} catch (Exception e){
			System.out.println(e.getClass().getName()+" while dealing with file -> "+ file);			
			return false;
		}
	}

	public String readFromFile(File file) {
		BufferedReader br = null;
	    try {
	    	br= new BufferedReader(new FileReader(file));
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
	        try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }	
	}

	public boolean writeToFile(String file, String message) {
		return writeToFile(new File(file), message);
	}

	public String readFromFile(String fileName) {
		return readFromFile(new File(fileName));
	}

	public void appendToFile(File file, String message) {
		try {
			Files.write(Paths.get(file.getAbsolutePath()), message.getBytes(), StandardOpenOption.APPEND);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
