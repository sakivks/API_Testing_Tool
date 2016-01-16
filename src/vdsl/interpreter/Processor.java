package vdsl.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import vdsl.interpreter.core.Core;

@SuppressWarnings("unchecked")
public class Processor {

	JSONObject processorJson = null;
	private Map<String,String> tagMap = null;
//	private String message = null;
	
	public Processor(JSONObject jsonObject) {
		processorJson = jsonObject;
	}
	
	public String doProcessing(String inputMessage) {
		String message = inputMessage;
		String[] tasks = {"getTagValue","setTagValue","compareTagValue"};
		boolean[] taskPresent = {false,false,false};
		Iterator<String> jsonIt = processorJson.keys();
		while (jsonIt.hasNext()) {
			String task = (String) jsonIt.next();
			if(task.equalsIgnoreCase(tasks[0])){
				taskPresent[0] = true;
			}
			else if(task.equalsIgnoreCase(tasks[1])){
				taskPresent[1] = true;
			}
			else if(task.equalsIgnoreCase(tasks[2])){
				taskPresent[2] = true;
			}
		}

		for(int i = 0; i < 3; i++){
			if(taskPresent[i]){
				JSONObject parameters;
				try {
					parameters = processorJson.getJSONObject(tasks[i]);
					message = executetask(message,tasks[i],parameters);
				} catch (JSONException e) {
					e.printStackTrace();
				}				
			}
		}
//		while (jsonIt.hasNext()) {
//			String task = (String) jsonIt.next();
//		}
		return message;
	}

	private String executetask(String inputMessage,String task, JSONObject parameters) {
		Iterator<String> jsonIt = parameters.sortedKeys();
		tagMap = new HashMap<String,String>();
		String value = null,
			   key = null;
		while (jsonIt.hasNext()) {
			try {
				key = (String) jsonIt.next();
				value = parameters.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			tagMap.put(key, value);
		}
		List<Object> inputParam = new ArrayList<>();
		inputParam.add(inputMessage);
		inputParam.add(tagMap);
		return Core.processHyperString("#"+task+"($(task))" , inputParam);
	}
	
}
