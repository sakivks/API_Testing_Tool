package vdsl.interpreter;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import vdsl.interpreter.core.Core;

public class Step {

	public String name = null;
	public String input = null;
	public String output = null;
	private String message = null;
	public Boolean isTestStep = false;
	public Processor processor = null;
	private JSONObject jsonObject = null;
	private Logger logger = new Logger();
	
	public Step(JSONObject jsonObj, Boolean isTest) throws JSONException {
		jsonObject = jsonObj;
		name = getString("name");
		input = getString("input");
		output = getString("output");
		isTestStep = isTest;
		if(getJSON("processing") != null)
			processor = new Processor(getJSON("processing"));
	}

	public Step(JSONObject jsonObj) throws JSONException {
		this(jsonObj, false);
	}

	public void getInput() {
		if(input != null){
			if(Core.hasFunctionCall(input)){
				message = Core.processHyperString(input, null);
			}else{
				if(Core.containsVariable(input))
					message = Core.processHyperString("#get("+input+")", null);
				else
					message = Core.processHyperString("#get($("+input+"))", null);	
			}
			logger.log("input", message,name);
		}
	}
	
	public void setOutput() {
		List<Object> inputParam = new ArrayList<>();
		if (output != null) {
			logger.log("output", message, name);
			if (isTestStep) {
				inputParam.add(name + " , " + message);
			} else {
				inputParam.add(message);
			}
			if(Core.hasFunctionCall(output)){
				Core.processHyperString(output, inputParam);				
			}else{
				if(Core.containsVariable(output))
					Core.processHyperString("#set("+output+")", inputParam);
				else
					Core.processHyperString("#set($("+output+"))", inputParam);					
			}
		}
	}

	public void doProcessing() {
		if(processor != null)
			message = processor.doProcessing(message);
	}

	private String getString(String key){
		String str = null;
		try{
			str = jsonObject.getString(key);			
		} catch (JSONException e) {
		} finally {
		}
		return str;
	}

	private JSONObject getJSON(String key){
		JSONObject jsonObj = null;
		try{
			jsonObj = jsonObject.getJSONObject(key);			
		} catch (JSONException e) {
		} finally {
		}
		return jsonObj;
	}
}
