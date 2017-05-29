package vdsl.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import vdsl.exception.GotNullException;
import vdsl.interpreter.Variable;
import vdsl.interpreter.core.Core;
import vdsl.xml.XmlEditor;

@SuppressWarnings("unchecked")
public class Task implements Structure {
	
	
	public Task(List<String> values,String key) {
		
	}


	public static String getTagValue(List<Object> param){
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
	
	public static String setTagValue(List<Object> param){
		String message = (String) param.get(0);
		Map<String,String> tagMap = (Map<String, String>) param.get(1);
		Iterator<String> mapIt = tagMap.keySet().iterator();
		while (mapIt.hasNext()) {
			String tagName = (String) mapIt.next();
			message = new XmlEditor().setTagValue(tagName, Core.processHyperString((tagMap.get(tagName)), null), message);
		}
		return message;
	}

	public static String compareTagValue(List<Object> param){
		String message = (String) param.get(0);
		String responseMessage = "PASS";
		String expectedTagHyperString;
		List<String> response = new ArrayList<>();
		if(message == null){
			return "FAIL , NO_RESPONSE_RECIEVED";
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

		
	private static List<String> getTagCompareResult(String tagNames, String expectedTagsHyperString, String message) {
		List<String> response = new ArrayList<>();
		boolean passStatus = true, isTagList = false;
		String responseString = "";
//		List<String> expectedTagValue = Core.processHyperString(expectedTagHyperString, null);
//		String[] expectedTagArr = ;
		List<String> expectedTagValueListHS = new ArrayList<>();
		List<String> expectedTagValueList = new ArrayList<>();
		if(Core.isTagList(tagNames)){
			expectedTagValueListHS = Arrays.asList(Core.simpleSubString(expectedTagsHyperString,"[","]").split("\\s*,\\s*"));			
		} else {
			expectedTagValueList.add(Core.processHyperString(expectedTagsHyperString, null));
		}
		List<String> tagNameList = new ArrayList<>();
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
				responseString = tagNames + " - expected \""+ expectedTagValueList.get(0).trim() + "\" found \"" + tagValue.trim() +"\"";
				passStatus = false;
			}
			else if(!(expectedTagValueList.contains(tagValue.trim()))){
				responseString = responseString + (responseString.equals("")?"":", ") +  tagName + " - expected \""+ tagValue.trim() + "\" Not found in the List " + Core.printList(expectedTagValueList);
				passStatus = false;
			}
		}
		response.add(String.valueOf(passStatus));
		response.add(" " + responseString+ " ");
		return response;
	}

	@Override
	public String get(List<String> value) throws GotNullException {
		return null;
	}

	@Override
	public void set(List<String> message) {
	}

}
