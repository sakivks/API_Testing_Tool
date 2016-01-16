package vdsl.structures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import vdsl.exception.GotNullException;
import vdsl.fileIO.FileDealer;
import vdsl.interpreter.Logger;
import vdsl.interpreter.Variable;

public class SoapService implements Structure{

	public String url;
	private FileDealer fd = new FileDealer();
	private Logger logger = new Logger();
	
	public SoapService(String url){
		this.url = url;
	}
	
	public SoapService(List<String> values,String key){
		this(values.get(0));
	}

	@Override
	public String get(List<String> value) throws GotNullException {

		return null;
	}

	@Override
	public void set(List<String> message) {
		SOAPMessage request,response = null;				
		try {
			request = createRequest(message.get(0));
			response = callWebService(url,request);
			
			String timeStmp = new SimpleDateFormat("_dd-MMM_HH-mm-ss").format(new Date());
			File fileName = new File(getResponseFolderName() +"Benjamin"+timeStmp+".xml");
			fd.writeToFile(fileName, soapMessageToString(response));
			logger.log("output", soapMessageToString(response),"SoapUIResponse");
		} catch (SOAPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getResponseFolderName() {
		String folderLocation = Variable.getString("Soap_WS_Response_Folder");
		if(!folderLocation.isEmpty() && folderLocation != null){
			folderLocation = (folderLocation.endsWith("/") ? folderLocation : (folderLocation  + File.separatorChar));
			return folderLocation;
		}else{
			return "soapResponse/";
		}
	}

	private SOAPMessage createRequest(String message) throws SOAPException, IOException {
        InputStream inputStream = new ByteArrayInputStream(message.getBytes());        
        SOAPMessage request = null;
		request = MessageFactory.newInstance().createMessage(null, inputStream);
        return request;
	}
	
	private SOAPMessage callWebService(String url,SOAPMessage request) throws UnsupportedOperationException, SOAPException {
        // Create SOAP Connection
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
		SOAPMessage response = soapConnection.call(request, url);
        soapConnection.close();
		return response;
	}


    public String soapMessageToString(SOAPMessage message) 
    {
        String result = null;

        if (message != null) 
        {
            ByteArrayOutputStream baos = null;
            try 
            {
                baos = new ByteArrayOutputStream();
                message.writeTo(baos); 
                result = baos.toString();
            } 
            catch (Exception e) 
            {
            } 
            finally 
            {
                if (baos != null) 
                {
                    try 
                    {
                        baos.close();
                    } 
                    catch (IOException ioe) 
                    {
                    }
                }
            }
        }
        return result;
    }   


}
