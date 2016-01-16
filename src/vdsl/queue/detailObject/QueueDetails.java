package vdsl.queue.detailObject;

public class QueueDetails{
	public String queueManager;
	@Override
	public String toString() {
		return "QueueDetails [queueManager=" + queueManager + ", hostName="
				+ hostName + ", port=" + port + ", queueName=" + queueName
				+ "]";
	}

	public String hostName;
	public int port;
	public String queueName;
	
	public QueueDetails(String queueManager, String hostName, int port,
			String queueName) {
		super();
		this.queueManager = queueManager;
		this.hostName = hostName;
		this.port = port;
		this.queueName = queueName;
	}
	
	
}
