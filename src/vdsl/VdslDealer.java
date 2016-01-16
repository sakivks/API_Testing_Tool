package vdsl;

import java.io.File;
import java.io.FilenameFilter;

import org.json.JSONArray;
import org.json.JSONException;


import vdsl.fileIO.FileDealer;
import vdsl.interpreter.MinifyJson;

public class VdslDealer {

	static FileDealer fd = new FileDealer();
	public static void main(String[] args) {
//		System.out.println(readBuildFile());


		
	}

	public static String readStructureLoadingSeq(){
		String str = fd.readFromFile(new File("ConfigAndData/internalConfig/StructureLoadingOrder.properties"));
		return str;
	}
	
	public static String readBuildFile(){
		File buildFiles[] = getAllBuildFiles();
		StringBuilder str = new StringBuilder();
		String buildStr;
		str.append("[");
		for (int i = 0; i < buildFiles.length; i++) {
			
			buildStr = MinifyJson.minify(fd.readFromFile(buildFiles[i]));
			
			try {
				@SuppressWarnings("unused")
				JSONArray processArray = new JSONArray(buildStr);
			} catch (JSONException e) {
				System.out.println("Json parsing Errors in build file -> " + buildFiles[i].getName());
			}
			
			str.append(buildStr);
			
			if(i < buildFiles.length - 1)
				str.append(", ");
		
		}
		str.append("]");
		return str.toString();
	}

	private static File[] getAllBuildFiles() {
		File dir = new File("ConfigAndData");
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith("build.json");
			}
		});
		return files;
	}

	public static String readPropertyFile(){
		String str = MinifyJson.minify(fd.readFromFile(new File("ConfigAndData/properties.json")));
		return str;
	}

	public static void editPropertyFile(String type, String objName, String newValue , String oldValue){
		String oldStr = fd.readFromFile(new File("ConfigAndData/properties.json"));
		String newStr = oldStr.replaceAll("\""+objName+"\" *: *\""+oldValue+"\" *", "\"" + objName +"\" : \""+newValue +"\"");
		fd.writeToFile(new File("ConfigAndData/properties.json"), newStr);
	}

}
