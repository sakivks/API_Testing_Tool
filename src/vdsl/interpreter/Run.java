package vdsl.interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.json.*;

import vdsl.VdslDealer;
import vdsl.fileIO.FileDealer;
import vdsl.interpreter.core.Core;

public class Run {

	FileDealer fd = new FileDealer();
	private final int numberPad = 4;
	private final int shortcutPad = 6;
	
	private static List<String> automatedProcessList = null;
	private static Iterator<String> automatedProcessIt = null;
	private boolean automatedRun = false;
	Variable propertyVariables = new Variable();
	Scanner in = new Scanner(System.in);

	public static void main(String[] args) {
		Run r = new Run();
		try {
			r.run();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void run() throws JSONException {

		propertyVariables.initialize();
		initializeAutomatedProcessList();
		while (true) {
			Object[] prcSelectorResponse = processSelector();
			Process process = (Process) prcSelectorResponse[0];
			if (!(process == null))
				process.execute();
			if((boolean) prcSelectorResponse[1] && !automatedProcessIt.hasNext()){
				break;
			}
		}

	}

	private void initializeAutomatedProcessList() {
		if(Variable.getBoolean("automatedRun",false) && Variable.getBoolean("Create_Fresh_Result_File", false)){
			Variable.loadVariable("FRESH_RUN", "true");
		}
		if(Variable.getString("automatedProcessList")!= null){
			automatedProcessList = Arrays.asList(Variable.getString("automatedProcessList").split(","));
			automatedProcessIt = automatedProcessList.iterator();			
		}
	}

	private Object[] processSelector() throws JSONException {

		Object[] prcSelectorResponse = new Object [2];
		Process prc = null;
		JSONArray processArray = null, buildArray;
		String choice = null;
		boolean reloadFlag = false, exitFlag = false;
		List<JSONObject> objList = new ArrayList<JSONObject>();
		String str = VdslDealer.readBuildFile();
		automatedRun = Boolean.parseBoolean(Variable.getString("automatedRun"));
		exitFlag = automatedRun;
		buildArray =  new JSONArray(str);
		
		for (int i = 0; i < buildArray.length(); i++) {
			processArray = new JSONArray(buildArray.getString(i));
			// Creating the list out of array of Process
			for (int j = 0; j < processArray.length(); j++) {
				objList.add(new JSONObject(processArray.getString(j)));
			}
		}
		
		do {
			reloadFlag = false;
			if(automatedRun){
				if(automatedProcessIt.hasNext())
					choice = automatedProcessIt.next();
				else
					automatedRun = false;
			}
			if(!automatedRun){
				displayProcessList(objList);
				choice = in.nextLine();
			}

			if (choice.trim().equalsIgnoreCase("run")) {
				reloadFlag = true;
				if (Variable.getString("automatedProcessList") != null) {
					automatedProcessList = Arrays.asList(Variable.getString("automatedProcessList").split(","));
					automatedProcessIt = automatedProcessList.iterator();
					Variable.setString("automatedRun","true");
				}
				automatedRun = true;
				break;
			}


			if (choice.trim().equalsIgnoreCase("reload")) {
				reloadFlag = true;
				propertyVariables.initialize();
				break;
			}

			prc = processSelector(objList, choice);

		} while (reloadFlag);
		
		prcSelectorResponse[0] = prc;
		prcSelectorResponse[1] = exitFlag;
//		in.close();
		return prcSelectorResponse;
	}

	private Process processSelector(List<JSONObject> objList, String choice)
			throws JSONException {
		Iterator<JSONObject> objIt = objList.iterator();
		if(choice.trim().equalsIgnoreCase("")){
			return null;
		}
		JSONObject obj = null;
		int listIndex = 1;
		while (objIt.hasNext()) {
			obj = (JSONObject) objIt.next();
			if (Core.isInteger(choice)) {
				if (listIndex == Integer.parseInt(choice))
					break;
			} else if (obj.getString("shortcut").equalsIgnoreCase(choice.trim())) {
				break;
			}
			if (listIndex == objList.size()) {
				System.out.println("Couldn't find a process with this reference");
				return null;
			}
			listIndex++;
		}

		return new Process(obj);
	}

	private void displayProcessList(List<JSONObject> objList)
			throws JSONException {
		System.out.println("\n");
		Iterator<JSONObject> objIt = objList.iterator();
		JSONObject obj = null;
		int listIndex = 1;
		while (objIt.hasNext()) {
			obj = (JSONObject) objIt.next();
			if(Variable.getBoolean("printMenu", true)){
				System.out.println(padRight(listIndex+"",numberPad) + "- " + padRight(obj.getString("shortcut"),shortcutPad)
						+ "- " + obj.getString("name"));
			}
			listIndex++;
		}
	}

	
	public static String padRight(String s, int n) {
	     return String.format("%1$-" + n + "s", s);  
	}
}
