package vdsl.structures;

import java.util.List;

import vdsl.exception.GotNullException;
import vdsl.interpreter.Variable;
import vdsl.interpreter.core.Core;
import vdsl.queue.QueueDealer;

public class Queue implements Structure{
	
	public String qName= null;
	private QueueDealer qd = null;
	public QueueManager queueManager ;

	public Queue(QueueManager queueManager,String qName) {
		this.qName = qName;
		this.queueManager = queueManager;
		qd = new QueueDealer(this);
	}

	public Queue(String queueManager,String qName) {
		this((QueueManager)Variable.get(queueManager),qName);
	}

	public Queue(List<String> values,String key) {
		this(values.get(0),values.get(1));
	}

	@Override
	public String get(List<String> command) throws GotNullException {

		if(Variable.getBoolean("automatedRun", false)){
			if(Core.isInteger(Variable.getString("queue_read_timeout"))){
				return listenGet(Core.makeList("true"));
			}		
			else{
				return normalListen(null);
			}
		}else{
			if(Variable.getBoolean("pseudoListen_onQueue", false)){
				return listenGet(Core.makeList("false"));				
			}else{
				return normalListen(null);
			}
		}
//		throw new GotNullException(toString() + " is Empty");
	}

	public String normalListen(List<String> command){		
		List<String> lstMsg = qd.readFromQueue();			
		String msg = null;
		if(!lstMsg.isEmpty()){
			msg = lstMsg.get(lstMsg.size() -1);
			if(msg != null){
				return msg;
			}
		}
		return null;
	}
	
	public String listenGet(List<String> command) {
		int rounds = (Integer.parseInt(Variable.getString("queue_read_timeout")))/500;
		List<String> lstMsg = qd.readFromQueue();		
		while(rounds >= 0){
			Core.delay(500);
			lstMsg = qd.readFromQueue();
			if(!lstMsg.isEmpty()){
				if(!command.isEmpty()){
					if(Boolean.parseBoolean(command.get(0))){						
						qd.deleteFromQueue(queueManager.qmName, queueManager.location, queueManager.port, qName);
					}					
				}
				return lstMsg.get(lstMsg.size() -1);
			}
			rounds --;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "Queue [qName=" + qName + ", queueManager="
				+ queueManager + "]";
	}

	@Override
	public void set(List<String> message) {
		if(message == null){
			return;
		}
		qd.writeToQueue(message.get(0));
	}

}
