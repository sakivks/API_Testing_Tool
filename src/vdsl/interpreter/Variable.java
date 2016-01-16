package vdsl.interpreter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import vdsl.VdslDealer;
import vdsl.fileIO.FileDealer;
import vdsl.interpreter.core.Core;

@SuppressWarnings("unchecked")
public class Variable {

	private JSONObject jsonVar = null;
	FileDealer fd = new FileDealer();
	static Map<String,Object> variableList = new HashMap<String,Object>();
//	private Map<String, String> replaceFunctionNameMap = new HashMap<>();
	private List<String> loadingSeq = new ArrayList<>();
	
	public static void main(String[] args) {
		Variable var = new Variable();
		var.initialize();

		
//		System.out.println(Core.processHyperString("#variableGet($(Sample_Input_Folder))",null));
//		System.out.println(Core.processHyperString("#variableGet($(Sample_Folder))",null));
//		List<String> loadingSeq = Arrays.asList(VdslDealer.readStructureLoadingSeq().split(" *\\r?\\n *"));
//
//		for(String str : loadingSeq)
//			System.out.println(str);
	}
	
	public void initialize() {
		variableList.clear();
		String str = VdslDealer.readPropertyFile();
		List<String> typeMetaData = Arrays.asList(VdslDealer.readStructureLoadingSeq().split(" *\\r?\\n *"));
		processTypeMetaData(typeMetaData);
		try {
			jsonVar = new JSONObject(str);
			Iterator<String> strucTypeIt = loadingSeq.iterator();
			while (strucTypeIt.hasNext()) {
				String key = (String)strucTypeIt.next();
				JSONObject someStrucJsonObj = jsonVar.getJSONObject(key);
				addToList(someStrucJsonObj,key);
			}
			loadVariable("Task", "task", new ArrayList<String>());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void processTypeMetaData(List<String> typeMetaData) {
		Iterator<String> typeMetaDataIt = typeMetaData.iterator();
//		String metaData, type;
		String metaData;
		while (typeMetaDataIt.hasNext()) {
			metaData = (String) typeMetaDataIt.next();
//			type = Core.simpleSubString(metaData, "-", false).trim();
			loadingSeq.add(metaData);
//			replaceFunctionNameMap.put(type, Core.simpleSubString(metaData, "-", true).trim());
		}
	}

	private void addToList(JSONObject jsonObj, String type) throws JSONException {
		Iterator<String> jsonIt = jsonObj.keys();
		List<String> values = new ArrayList<String>();
		while (jsonIt.hasNext()) {
			String key = (String) jsonIt.next();
			values = variableParser(jsonObj.getString(key));
			loadVariable(type,key,values);
		}
	}
	
	
    public static void loadVariable(String type, String key, List<String> values) {
    	ClassLoader classLoader = Variable.class.getClassLoader();
    	try {
	        Class<?> varClass = classLoader.loadClass("vdsl.structures."+type);
	        Constructor<?> constructor = varClass.getConstructor(List.class,String.class);
	        Object object = constructor.newInstance(values , key);
	        variableList.put(key, object);
    	}catch (ClassNotFoundException e) {
	        e.printStackTrace();
	    }catch (SecurityException e) {
	        e.printStackTrace();
	    } catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public static List<String> variableParser(String string) {
		List<String> values = new ArrayList<String>();
		if(string.indexOf("[") != -1 && string.indexOf("]") != -1 && string.indexOf("[") < string.indexOf("]")){
			string = Core.simpleSubString(string, "[", "]");
			values = Arrays.asList( string.split(" *[,] *"));
		}
		else{
			values.add(string);
		}
		values = replaceVariableReference(values);
		return values;
	}
	
	private static List<String> replaceVariableReference(List<String> values) {
		String dynamicPortion,preString, replacementString,  postString;
		List<String> newValues = new ArrayList<>();
		for(String value : values){
			if(Core.hasFunctionCall(value)){
				preString = Core.simpleSubString(value, "#" , false);
				dynamicPortion = Core.simpleSubString(value, "#" , "))");
				postString = Core.simpleSubString(value, "))", true);
				replacementString = Core.processHyperString("#"+dynamicPortion + "))", null);
				value = preString + replacementString + postString;
			}
			else if(Core.containsVariable(value)){
				preString = Core.simpleSubString(value, "$" , false);
				dynamicPortion = Core.simpleSubString(value, "(" , ")");
				postString = Core.simpleSubString(value, ")", true);
				replacementString = Core.processHyperString("#variableGet($("+ dynamicPortion + "))", null);
				value = preString + replacementString + postString;
			}
			newValues.add(value);
		}
		return newValues;
	}

	public static Object get(String key) {
		if(variableList.containsKey(key)){
			return variableList.get(key);
		}else{
			return null;
		}
	}

	public static String getString(String key) {
		if(variableList.containsKey(key)){
			return Core.processHyperString("#get($("+key+"))", null);
		}else{
			return null;
		}
	}

	public static Boolean getBoolean(String key,Boolean default_return) {
		if(variableList.containsKey(key)){
			Boolean response = strToBool(Core.processHyperString("#get($("+key+")" , null));
			return (response != null) ? response : default_return;
		}else{
			return default_return;
		}
	}

	private static Boolean strToBool(String str) {
		str = str.trim();
		if(str.equalsIgnoreCase("true") || str.equalsIgnoreCase("t") || str.equalsIgnoreCase("yes") || str.equalsIgnoreCase("y")){
			return true;
		}else if(str.equalsIgnoreCase("false") || str.equalsIgnoreCase("f") || str.equalsIgnoreCase("no") || str.equalsIgnoreCase("n")){
			return false;
		}
		return null;
	}

	public static Object get() {
			return variableList;
	}

	public static void set(String key, Object value) {
		variableList.put(key, value);
	}

	public static void remove(String key) {
		variableList.remove(key);
	}

	public static void setString(String key,String value) {
		if(variableList.containsKey(key)){
			List<Object> inputParam = new ArrayList<>();
			inputParam.add(value);
			Core.processHyperString("#set($("+key+")" , inputParam);
		}
	}

	public static void loadVariable(String variableName, String value) {
		loadVariable("Variable", variableName, Core.makeList(value));
	}

	public static void setVariable(String variableName, String value) {
		loadVariable("Variable", variableName, Core.makeList(value));
	}

}
