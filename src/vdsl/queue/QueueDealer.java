package vdsl.queue;

import vdsl.queue.delete.EmptyQ;
import vdsl.queue.detailObject.QueueDetails;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.jms.BytesMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ibm.mq.jms.MQQueueConnectionFactory;

public class QueueDealer {

	public QueueDetails queueDetails;
	public QueueDealer(vdsl.structures.Queue queue){
		this.queueDetails = new QueueDetails(queue.queueManager.qmName, queue.queueManager.location
				, queue.queueManager.port, queue.qName);
	}
	public QueueDealer(){
		
	}
	public static void main(String[] args) {
		
		QueueDealer qd= new QueueDealer();
		qd.writeToQueue("QM_MPM_VIKAS", "blrd042", 1414 , "FROM.BACK.OFFICE.Q", "Hello");
		System.out.println("Msg Written");
		System.out.println(qd.readFromQueue("QM_MPM_VIKAS", "blrd042", 1414 , "FROM.BACK.OFFICE.Q"));
		qd.deleteFromQueue("QM_MPM_VIKAS", "blrd042", 1414,"FROM.BACK.OFFICE.Q" );
		
	}

	public List<String> readFromQueue() {
		return readFromQueue(queueDetails.queueManager, queueDetails.hostName, queueDetails.port, queueDetails.queueName);
	}

	public List<String> readFromQueue(vdsl.structures.Queue queueDetails) {
		return readFromQueue(queueDetails.queueManager.qmName, queueDetails.queueManager.location, queueDetails.queueManager.port, queueDetails.qName);
	}

	public List<String> readFromQueue(QueueDetails queueDetails) {
		return readFromQueue(queueDetails.queueManager, queueDetails.hostName, queueDetails.port, queueDetails.queueName);
	}

	public boolean writeToQueue(String messageData) {
		return writeToQueue(queueDetails.queueManager, queueDetails.hostName, queueDetails.port, queueDetails.queueName, messageData);
	}

	public boolean writeToQueue(QueueDetails queueDetails, String messageData) {
		return writeToQueue(queueDetails.queueManager, queueDetails.hostName, queueDetails.port, queueDetails.queueName, messageData);
	}
	
	public boolean writeToQueue(String queueManager, String hostName, int port , String queueName, String messageData) {
		
		try {
			QueueConnectionFactory factory = new MQQueueConnectionFactory();
			((MQQueueConnectionFactory) factory).setQueueManager(queueManager);
			((MQQueueConnectionFactory) factory).setHostName(hostName);
			((MQQueueConnectionFactory) factory)
					.setChannel("SYSTEM.ADMIN.SVRCONN");
			((MQQueueConnectionFactory) factory).setPort(port);
			((MQQueueConnectionFactory) factory).setTransportType(1);
			QueueConnection connection = factory.createQueueConnection("", "");
			QueueSession session = connection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);		
	
			Queue queue = session.createQueue(queueName);
			QueueSender sender = session.createSender(queue);			
			TextMessage message = session.createTextMessage();
			
			message.setText(messageData);
			sender.send(message);
			sender.close();
			session.close();
			connection.stop();
			connection.close();
			factory = null;
			return true;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Boolean writeToQueue(String queueManager, String hostName, int port , String queueName, File filePath) {
		return writeToQueue(queueManager, hostName, port, queueName, readMessage(filePath));
	}

	public String readMessage(File filePath) {
		String everything = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(
					filePath));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
			everything = sb.toString();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return everything;
	}

	public List<String> readFromQueue(String queueManager, String hostName, int port,
			String queueName) {
		List<String> queueData = new ArrayList<String>();
		try {
			//reference collection has the set of all the identifier tags
			QueueConnectionFactory factory = new MQQueueConnectionFactory();
			((MQQueueConnectionFactory) factory).setQueueManager(queueManager);
			((MQQueueConnectionFactory) factory).setHostName(hostName);
			((MQQueueConnectionFactory) factory)
					.setChannel("SYSTEM.ADMIN.SVRCONN");
			((MQQueueConnectionFactory) factory).setPort(port);
			((MQQueueConnectionFactory) factory).setTransportType(1);
			QueueConnection connection = factory.createQueueConnection("", "");
			QueueSession session = connection.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue(queueName);
			QueueBrowser browser = session.createBrowser(queue);
			Enumeration<?> e = browser.getEnumeration();
			
			while(e.hasMoreElements()) {
				try{
					TextMessage o = ((TextMessage) e.nextElement());
					queueData.add(o.getText());
				}catch(Exception exc){
					BytesMessage o = ((BytesMessage) e.nextElement());
					queueData.add(o.readUTF());
				}
			}
			
			browser.close();
			session.close();
			connection.stop();
			connection.close();
	
		    return queueData;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean deleteFromQueue(String queueManager, String hostName, int port,
			String queueName) {
		try {
			EmptyQ emptyq = new EmptyQ();
			emptyq.init(queueManager, hostName, port, queueName);
		    emptyq.emptyIt();
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
