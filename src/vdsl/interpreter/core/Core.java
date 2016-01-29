package vdsl.interpreter.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vdsl.interpreter.Variable;


public class Core {

	public static void main(String[] args) {

//		Variable.main(null);
//		List<Object> msg = new ArrayList<>();
//		msg.add("someThing write");
//		System.out.println(Core.processHyperString("ok man #get($(Sample_XML))",null));
//		System.out.println(Core.processHyperString("#set($(Sample_Folder))",msg));
//		System.out.println(Core.processHyperString("asdfa $(Sample_Folder)",msg));
//		System.out.println(Core.processHyperString("$(Sample_Folder)",msg));
//		Core.processHyperString("#get(#latest($(Sample_Input_Folder)))",null);

		Core.processHyperString2("a $(x3_12)sa h() #fn(asdf)asdjl$(xyz)#fn2($(mn))anditends", null);
	}

	public static String processHyperString2(String hyperString,List<Object> context){
		
		// create the pattern joining the keys with '|'
		String regexp = "\\$\\(\\w+\\)|#\\w+\\(.*?\\)+";

		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(hyperString);

		while (m.find())
		{
			m.appendReplacement(sb, processor(m.group()));		
			System.out.println(m.group());
		}

		m.appendTail(sb);
		System.out.println(sb.toString());   
		
		return null;
	}
	
	private static String processor(String inputStr) {
		inputStr = inputStr.trim();
		if(inputStr.startsWith("#")){
			return "FunctioN";
		}else if(inputStr.startsWith("$(")){
			return "VariablE";
		}
		return null;
	}

	@Deprecated	
	public static String processHyperString(String hyperStr,List<Object> inputParam){
		//Code to return fixed String
		if(hyperStr.trim().startsWith("||") && hyperStr.trim().endsWith("||")){
			return hyperStr.substring(2,hyperStr.length()-2);
		}
		
		String prevCommand = null, finalOutput = null;
		Object baseObject = null,returnObj = null;
		Boolean firstRound = true,isVariable = false;
		List<String> commandList = Arrays.asList(hyperStr.split("[(]"));
		java.util.Collections.reverse(commandList);
		ListIterator<String> commandIt = commandList.listIterator();
		//initialising Param List
		if(inputParam == null){
			inputParam = new ArrayList<>();
		}
		
		
		
		//below loop is just to extract the baseObject from Variable list
		while (commandIt.hasNext()) {
			String command = commandIt.next();
			if(command.contains(")"))
				command = Core.simpleSubString(command, ")", false);
			if(firstRound){
				baseObject = command;
				prevCommand = command;
			}else{
				if(command.trim().equalsIgnoreCase("$")){
					baseObject = Variable.get(prevCommand);
					isVariable = true;
				}else{
					commandIt.previous();
					baseObject = prevCommand;
				}
				break;
			}
			firstRound = false;
		}
		//This is to pass variable name
		//as used in increment function
		inputParam.add(prevCommand);
		returnObj = baseObject;
		
		//Handling the default get Case
		if(!commandIt.hasNext() && isVariable){
			returnObj = Core.methodExecuter(returnObj, "get", inputParam);
//			System.out.println("Here");
		}
		
		//below loop is for chain processing
		while (commandIt.hasNext()) {
			String command= (String) commandIt.next();
			command = Core.simpleSubString(command, "#", true);
			returnObj = Core.methodExecuter(returnObj, command, inputParam);				
		}
		
		finalOutput = (String)returnObj;		
		
		return finalOutput;
	}	

	
	
	
	public static Object methodExecuter(Object obj,String methodName,List<Object> inputParam){
		Class<? extends Object> cls = obj.getClass();
		Method method = null;
		Object msg = null;
		try {
			method = cls.getMethod(methodName, List.class);
			msg = method.invoke(obj,inputParam);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	
	public static String simpleSubString(String str,
			String startStr, String endStr , Boolean upperCase) {
		if(str.contains(startStr) && str.contains(endStr))
			if(str.indexOf(startStr) <= str.indexOf(endStr))
				return simpleSubString(str,str.indexOf(startStr) +startStr.length(), str.indexOf(endStr), upperCase);
		return null;
	}

	public static String simpleSubString(String str,
			String startStr, String endStr) {
		if(str.indexOf(startStr) == -1 || str.indexOf(endStr) == -1){
			return str;
		}else
			return simpleSubString(str,str.indexOf(startStr) + startStr.length(), str.indexOf(endStr),false);
	}

	public static String simpleSubString(String str,
			int startIndex, int endIndex, Boolean upperCase) {
		if(upperCase)
			return (str.substring(startIndex, endIndex)).trim().toUpperCase();
		else
			return (str.substring(startIndex, endIndex)).trim();
	}

	public static String simpleSubString(String str,
			 String decisionStr, Boolean lastHalf) {
		return simpleSubString(str, decisionStr, lastHalf, false);
	}

	public static String simpleSubString(String str,
			 String decisionStr, Boolean lastHalf, Boolean upperCase) {
		if(str.indexOf(decisionStr) == -1){
			return str;
		}
		if(lastHalf)
				return simpleSubString(str,str.indexOf(decisionStr)+decisionStr.length(), str.length() ,upperCase);
		else
			return simpleSubString(str,0, str.indexOf(decisionStr),upperCase);
	}

	public static boolean hasFunctionCall(String output) {
		if(output.contains("#"))
			return true;
		else 
			return false;
	}

	public static boolean containsVariable(String str) {
		if(str.contains("$"))
			return true;
		else 
			return false;
	}
	
	public static List<String> makeList(String str1){
		List<String> values = new ArrayList<String>();
		values.add(str1);
		return values;
	}

	public static List<String> makeList(String str1, String str2){
		List<String> values = new ArrayList<String>();
		values.add(str1);
		values.add(str2);
		return values;
	}

	public static List<String> makeList(String str1, String str2, String str3){
		List<String> values = new
				ArrayList<String>();
		values.add(str1);
		values.add(str2);
		values.add(str3);
		return values;
	}

	public static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static void delay(int delay_in_ms){
		try {
			Thread.sleep(delay_in_ms);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}

	public static boolean isTagList(String tagName) {
		if(tagName.contains("[") && tagName.contains("..")){
			return true;
		}
		return false;
	}

	public static String printList(List<String> list) {
		StringBuilder strBld = new StringBuilder("[ ");
		for(int i=0;i<list.size();i++){
		    strBld.append(list.get(i)+ ((i == list.size() - 1 )?"":"; " ));
		}
		strBld.append(" ]");
		return strBld.toString();
	}

	

}
