package vdsl.structures;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import vdsl.VdslDealer;
import vdsl.interpreter.core.Core;

public class Variable implements Structure{

	public String value;
	private final static String VARIABLE_LIST_DELIM = ",";
	private final static String VARIABLE_KEY_VALUE_DELIM = ":";

	public static void main(String[] args) {
//		vdsl.interpreter.Variable var = new vdsl.interpreter.Variable();
//		var.initialize("properties.vdsl");
//
//		CountingVariables cntVar1 = new CountingVariables("alpha");
//		System.out.println(Variable.get());
//		Map<String,Object> varList = (Map<String, Object>) Variable.get();
//		System.out.println("hello world");
//		cntVar1.set("1");
	}

	public Variable(String value) {
		this.value = value;
	}

	public Variable(List<String> values) {
		this(values.get(0));
	}

	public Variable(List<String> values,String key) {
		this(values.get(0));
//		if the key is loadVariableList which implies special meaning
		if(key.trim().startsWith("loadVariableList")){
			vdsl.interpreter.Variable.remove("loadVariableList");
			loadVariableList(values.get(0));
		}
	}

	private void loadVariableList(String str) {
		List<String> variableList = Arrays.asList( str.split(VARIABLE_LIST_DELIM));
		Iterator<String> vListIt = variableList.iterator();
		while (vListIt.hasNext()) {
			String kvPair = ((String) vListIt.next()).trim();
			if(!("".equalsIgnoreCase(kvPair) || !kvPair.contains(VARIABLE_KEY_VALUE_DELIM))){
				String key = Core.simpleSubString(kvPair, VARIABLE_KEY_VALUE_DELIM, false);
				String value = Core.simpleSubString(kvPair, VARIABLE_KEY_VALUE_DELIM, true);
//				System.out.println(key +"\t"+value);
				vdsl.interpreter.Variable.loadVariable("Variable", key, Core.makeList(value));				
			}
		}
	}

	public String increment(List<String> inputParam) {
		String name = inputParam.get(0);
		String value = vdsl.interpreter.Variable.getString(name);
		int character, 
			counterStartPos = value.length()-1;
		String counter;
		for(int j=value.length()-1 ; j >= 0 ; j--)
		{
			character=value.charAt(j);
			if((character<48)||(character>57)){
				counterStartPos = j;
				break;
			}
		}
		counter = value.substring(counterStartPos + 1, value.length());
		if(Core.isInteger(counter)){
			counter = String.format("%0"+(value.length() - counterStartPos -1) +"d", Integer.parseInt(counter) + 1 );
			this.value = value.substring(0,counterStartPos + 1) + counter;
		}
		vdsl.interpreter.Variable.set(name, this);
		VdslDealer.editPropertyFile("Variable", name, this.value,value);
		return (String) this.value;
	}

	public String variableGet(List<String> inputParam){
		return value;
	}

	
	@Override
	public String get(List<String> name) {
		return (String) value;
	}

	@Override
	public void set(List<String> value) {
		this.value = value.get(0);
	}
	
}
