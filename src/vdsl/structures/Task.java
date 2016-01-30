package vdsl.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vdsl.exception.GotNullException;
import vdsl.interpreter.ComparePayload;
import vdsl.interpreter.Variable;
import vdsl.interpreter.core.Core;
import vdsl.xml.XmlEditor;

@SuppressWarnings("unchecked")
public class Task implements Structure {
	
	public static final String PASS = "PASS";
	public static final String FAIL = "FAIL";
	public static final String FAIL_NO_RESPONSE = "FAIL , NO_RESPONSE_RECIEVED";
	public static final String COMMAND_TAG_NOT_FOUND = "TAG_NOT_FOUND";
	
	public Task(List<String> values,String key) {
		
	}

	public String getTagValue(List<Object> param){
		String message = (String) param.get(0);
		Map<String,String> tagMap = (Map<String, String>) param.get(1);
		Iterator<String> mapIt = tagMap.keySet().iterator();
		while (mapIt.hasNext()) {
			String variableName = (String) mapIt.next();
			String value = new XmlEditor().getTagValue(tagMap.get(variableName),message);
			Variable.loadVariable(variableName, value);
		}
		return message;
	}
	
	public String setTagValue(List<Object> param){
		String message = (String) param.get(0);
		Map<String,String> tagMap = (Map<String, String>) param.get(1);
		Iterator<String> mapIt = tagMap.keySet().iterator();
		while (mapIt.hasNext()) {
			String tagName = (String) mapIt.next();
			message = new XmlEditor().setTagValue(tagName, Core.processHyperString((tagMap.get(tagName)), null), message);
		}
		return message;
	}

	public String compareTagValue(List<Object> param){
		String message = (String) param.get(0);
		String responseMessage = PASS;
		String expectedTagHyperString;
		List<String> response = new ArrayList<>();
		if(message == null){
			return FAIL_NO_RESPONSE;
		}
		Boolean hasFailed = false;
		Map<String,String> tagMap = (Map<String, String>) param.get(1);
		Iterator<String> mapIt = tagMap.keySet().iterator();
		while (mapIt.hasNext()) {
			String tagName = (String) mapIt.next();
			expectedTagHyperString = tagMap.get(tagName);
			response = getTagCompareResult(tagName , expectedTagHyperString, message);				
			if(!Boolean.parseBoolean(response.get(0)) && !hasFailed){
				hasFailed = true;
				responseMessage = "FAIL, {" + response.get(1);
			}else if(!Boolean.parseBoolean(response.get(0))){
				responseMessage = responseMessage + ","+ response.get(1);
			}
		}
		return responseMessage + (hasFailed?"}":"");
	}

	private String compare(List<ComparePayload> payload){
		Iterator lstIt = payload.iterator();
		boolean failed = false;
		StringBuilder reasonsForFailure = new StringBuilder("{");
		
		while (lstIt.hasNext()) {
			ComparePayload payloadInstance = (ComparePayload) lstIt.next();
			String outcome = payloadInstance.compare();
			if(outcome != null){
				if(failed)
					reasonsForFailure.append(", "+outcome);
				else{
					reasonsForFailure.append(" "+outcome);
					failed = true;
				}
					
			}
		}
		
		if(failed)
			return FAIL + ", " + reasonsForFailure.toString();
		else
			return PASS;
	}
		
	@Deprecated
	private static List<String> getTagCompareResult(String tagNames, String expectedTagsHyperString, String message) {
		List<String> response = new ArrayList<>();
		boolean passStatus = true, isTagList = false;
		String responseString = "";
//		List<String> expectedTagValue = Core.processHyperString(expectedTagHyperString, null);
//		String[] expectedTagArr = ;
		List<String> expectedTagValueListHS = Arrays.asList(Core.simpleSubString(expectedTagsHyperString,"[","]").split("\\s*,\\s*"));
		List<String> tagNameList = new ArrayList<>();
		List<String> expectedTagValueList = new ArrayList<>();
		for (String expectedHS : expectedTagValueListHS) {
			expectedTagValueList.add(Core.processHyperString(expectedHS, null));
		}
		if(Core.isTagList(tagNames)){
			isTagList = true;
			int startIndex = 0, endIndex = 1;
			try{
				startIndex = Integer.parseInt(Core.simpleSubString(tagNames, "[", "..").trim());
				endIndex = Integer.parseInt(Core.simpleSubString(tagNames, "..", "]").trim());
			} catch (Exception e) {
				System.out.println("Not a proper Number Entry -> "+ tagNames);
			}
			for (int i = startIndex; i <= endIndex; i++) {
				tagNameList.add(tagNames.replaceFirst("\\[.*\\]", "[" + i +"]"));
			}
		}else{
			tagNameList.add(tagNames);
		}
		Iterator<String> tagValueIt = tagNameList.iterator();

		while (tagValueIt.hasNext()) {
			String tagName = (String) tagValueIt.next();
			String tagValue = new XmlEditor().getTagValue(tagName,message);
			
			if(tagValue == null){
				responseString = tagNames + " - tag Not Found";
				passStatus = false;
			}
			else if(!isTagList && !(expectedTagValueList.get(0).trim().equalsIgnoreCase(tagValue.trim()))){
				if(specialScenarios(tagValue,expectedTagValueList.get(0))){
					
				}else{
					responseString = tagNames + " - expected \""+ expectedTagValueList.get(0).trim() + "\" found \"" + tagValue.trim() +"\"";
					passStatus = false;					
				}
			}
			else if(!(expectedTagValueList.contains(tagValue.trim()))){
				responseString = responseString + (responseString.equals("")?"":", ") +  tagName + " - found \""+ tagValue.trim() + "\" Not present in the expectation list " + Core.printList(expectedTagValueList);
				passStatus = false;
			}
		}
		response.add(String.valueOf(passStatus));
		response.add(" " + responseString+ " ");
		return response;
	}

	private static boolean specialScenarios(String tagValue, String expectedTagValueList) {
		if (expectedTagValueList.trim().equalsIgnoreCase("#NotEmpty()") && tagValue.trim().length()>0)
			return true;
		else 
			return false;
	}

	
	@Override
	public String get(List<String> value) throws GotNullException {
		return null;
	}

	@Override
	public void set(List<String> message) {
	}

}
