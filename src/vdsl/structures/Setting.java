package vdsl.structures;

import java.util.List;

public class Setting implements Structure{

	public String value;

	public Setting(String value) {
		this.value = value;
	}

	public Setting(List<String> values,String key) {
		this(values.get(0));
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
