package vdsl.structures;

import java.util.List;

public class QueueManager implements Structure{
	public String qmName = null;
	public String location = null;
	public int port;

	public QueueManager(String qmName, String location, int port) {
		this.qmName = qmName;
		this.location = location;
		this.port = port;
	}
	
	public QueueManager(List<String> values,String key) {
		this(values.get(0),values.get(1),Integer.parseInt(values.get(2)));
	}

	@Override
	public String get(List<String> value) {
		return null;
	}

	@Override
	public void set(List<String> value) {
		
	}

}
