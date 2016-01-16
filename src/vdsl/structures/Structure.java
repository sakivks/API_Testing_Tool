package vdsl.structures;

import java.util.List;

import vdsl.exception.GotNullException;

public interface Structure {

	public String get(List<String> value) throws GotNullException;
	public void set(List<String> message);
}
