package vdsl.main;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import vdsl.fileIO.FileDealer;
import vdsl.fileIO.FolderDealer;
import vdsl.flatfile.FileTransport;
import vdsl.queue.QueueDealer;
import vdsl.queue.detailObject.QueueDetails;
import vdsl.xml.XmlEditor;

public class DoStuff {

	QueueDealer qd = new QueueDealer();
	FileDealer fd = new FileDealer();
	FileTransport ft = new FileTransport();
	FolderDealer fod = new FolderDealer();
	XmlEditor xe = new XmlEditor();
	QueueDetails UB_FBO = new QueueDetails("QM_MPM_VIKAS", "blrd042", 1414 , "FROM.BACK.OFFICE.Q");
	QueueDetails MPM_FBO = new QueueDetails("QM_MPM_QATCH", "blrvswasubdev13", 1414 , "FROM.BACK.OFFICE.Q");
	QueueDetails UB_TBO = new QueueDetails("QM_MPM_VIKAS", "blrd042", 1414 , "TO.BACK.OFFICE.Q");
	QueueDetails MPM_TBO = new QueueDetails("QM_MPM_QATCH", "blrvswasubdev13", 1414 , "TO.BACK.OFFICE.Q");
	QueueDetails UB_PCX = new QueueDetails("QM_MPM_VIKAS", "blrd042", 1414 , "PAYMENTCAPTUREXML.Q");
	QueueDetails MPM_PCX = new QueueDetails("QM_MPM_QATCH", "blrvswasubdev13", 1414 , "PAYMENTCAPTUREXML.Q");
	File inward_CT = new File("SampleMsg/SEPAMessages/STEP2_Inward_CT.xml");
	File sepaReciever = new File("//blrvswasubdev14/SEPA/Receiver/");
	File sepaSenderBulk = new File("//blrvswasubdev14/SEPA/Sender/BulkPosting/Processed");
	File sepaReceiverBulk = new File("//blrvswasubdev14/SEPA/Receiver/BulkPosting/Processed");
	File bfmInput = new File("C:/IBM/WebSphere/AppServer/profiles/AppSrv03/MPMInput/bulk.xml");
	File msgBackup = new File("D:/Interfaces/VdslOld/msgBackup/MPMtoUB/recall_26-Mar_15-17.xml");
	String sepaRecieverS = "//blrvswasubdev14/SEPA/Receiver/";
	String msgBackupS	= "msgBackup/";
	
	
	public static void main(String[] args) {
		DoStuff ds= new DoStuff();

//		ds.ubToMPMinward();
//		System.out.println(ds.xe.getTagValue("CdtTrfTxInf|TxId", ds.inward_CT));
		
//		ds.intiateInwardCT("msgid_inward_vks_0312_t2_", "txid_inward_vks_0312_t2_", 10);
		
//		ds.MPMToUBinward("_inward2_");

//		ds.UBToMPMinward("_inward2_");
		
		ds.moveMsg(ds.UB_PCX, ds.MPM_PCX, "PI/qatch");
		ds.moveMsg(ds.sepaSenderBulk, ds.bfmInput, "MPMtoUB/bulk");    //For PI Bulk uncomment this
//		ds.moveMsg(ds.sepaReceiverBulk, ds.bfmInput, "MPMtoUB/bulk");
		
//		ds.moveMsg(ds.MPM_TBO, ds.UB_TBO, "MPMtoUB/recall");
		
//		ds.moveMsg(ds.UB_FBO, ds.MPM_FBO, "UBtoMPM/response");

//		ds.moveMsg(ds.MPM_TBO, ds.UB_TBO, "MPMtoUB/return",6);
		
//		ds.moveMsg(ds.MPM_TBO, ds.UB_TBO, "MPMtoUB/return");
		
//		ds.showMsg(ds.MPM_TBO,6);
		
//		ds.putMsg(ds.msgBackup,ds.UB_TBO);

	}

	public void  intiateInwardCT(String msgId,String txnId, int nofMsg) {
		for(int i = 1 ;i <= nofMsg; i++){
			xe.setTagValue("GrpHdr|MsgId",msgId+String.valueOf(i), inward_CT);
			xe.setTagValue("GrpHdr|TtlIntrBkSttlmAmt",String.valueOf(50+i), inward_CT);
			xe.setTagValue("CdtTrfTxInf|TxId",txnId+String.valueOf(i), inward_CT);
			xe.setTagValue("CdtTrfTxInf|EndToEndId",txnId+String.valueOf(i), inward_CT);
			xe.setTagValue("CdtTrfTxInf|InstrId",txnId+String.valueOf(i), inward_CT);
			xe.setTagValue("CdtTrfTxInf|IntrBkSttlmAmt",String.valueOf(50+i), inward_CT);
			ft.copyFile(inward_CT, new File(sepaRecieverS + "inward_vks_"+String.valueOf(i)+".xml"));
		}
		
	}
	
	public void UBToMPMinward(String fileName) {
		List<String> allMsg = qd.readFromQueue(UB_FBO);
		Iterator<String> msgIt = allMsg.iterator();
		
		while (msgIt.hasNext()) {
			String msg = (String) msgIt.next();
			String timeStmp = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss").format(new Date());
			fd.writeToFile(new File("msgBackup/UBToMPM"+fileName+timeStmp+".xml"), msg);
			qd.writeToQueue(MPM_FBO, msg);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void MPMToUBinward(String fileName) {
		List<String> allMsg = qd.readFromQueue(MPM_TBO);
		Iterator<String> msgIt = allMsg.iterator();
		
		while (msgIt.hasNext()) {
			String msg = (String) msgIt.next();
			String timeStmp = new SimpleDateFormat("dd-MMM-yyyy_HH-mm-ss").format(new Date());
			fd.writeToFile(new File("msgBackup/MPMtoUB"+fileName+timeStmp+".xml"), msg);
			qd.writeToQueue(UB_TBO, msg);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void moveMsg(QueueDetails srcQueue,QueueDetails destQueue, String fileName){
		List<String> allMsg = qd.readFromQueue(srcQueue);
		String msg = allMsg.get(allMsg.size() -1);
		String timeStmp = new SimpleDateFormat("_dd-MMM_HH-mm").format(new Date());
		fd.writeToFile(new File(msgBackupS+fileName+timeStmp+".xml"), msg);
		qd.writeToQueue(destQueue, msg);
		System.out.println(msg);
	}

	public void moveMsg(File srcFolder,File dest, String fileName){
		
		File file = fod.latestFile(srcFolder);
		String msg = fd.readFromFile(file);
		String timeStmp = new SimpleDateFormat("_dd-MMM_HH-mm").format(new Date());
		File srcFile = new File(msgBackupS+fileName+timeStmp+".xml");
		fd.writeToFile(srcFile, msg);
		ft.copyFile(srcFile, dest);
		System.out.println(msg);
	}

	public void moveMsg(QueueDetails srcQueue,QueueDetails destQueue, String fileName, int msgNo){
		List<String> allMsg = qd.readFromQueue(srcQueue);
		String msg = allMsg.get(msgNo-1);
		String timeStmp = new SimpleDateFormat("_dd-MMM_HH-mm").format(new Date());
		fd.writeToFile(new File(msgBackupS+fileName+timeStmp+".xml"), msg);
		qd.writeToQueue(destQueue, msg);
		System.out.println(msg);
	}

	public void putMsg(String srcFile, QueueDetails destQueue){
		String msg = fd.readFromFile(new File(srcFile));
		qd.writeToQueue(destQueue, msg);
		System.out.println(msg);
	}

	public void putMsg(File srcFile, QueueDetails destQueue){
		String msg = fd.readFromFile(srcFile);
		qd.writeToQueue(destQueue, msg);
		System.out.println(msg);
	}

	public void showMsg(QueueDetails srcQueue,int msgNo){
		List<String> allMsg = qd.readFromQueue(srcQueue);
		String msg = allMsg.get(msgNo-1);
		System.out.println(msg);
	}

}
