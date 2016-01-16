package vdsl.interpreter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import vdsl.fileIO.FileDealer;

public class Logger {

	FileDealer fd = new FileDealer();
	
	public void log(String state,String message,String stepName) {

		String settings = Variable.getString("printMsgInConsole");
		if(checkConfig(state,settings))
			System.out.println(message);

		settings = Variable.getString("LogMessages");
		if(checkConfig(state,settings)){
			String logFolder = Variable.getString("LogFolder");
			logFolder = (logFolder.endsWith("/") ? logFolder : (logFolder  + File.separatorChar));
			
			String timeStmp = new SimpleDateFormat(Variable.getString("LogPrefix")).format(new Date());
			if("input".equalsIgnoreCase(state))
				state = "_beforeStep";
			else if("output".equalsIgnoreCase(state))
				state = "_afterStep";
			File fileName = new File( logFolder +timeStmp + stepName + state +".xml");
			fd.writeToFile(fileName, message);
		}
	}
	

	private boolean checkConfig(String state, String settings){
		if(state.equalsIgnoreCase("input")){
			if(settings != null)
				if(settings.trim().equalsIgnoreCase("true") || settings.trim().equalsIgnoreCase("input") ||settings.trim().equalsIgnoreCase("both") )
					return true;
		}
		if(state.equalsIgnoreCase("output")){
			if(settings != null)
				if(settings.trim().equalsIgnoreCase("both") || settings.trim().equalsIgnoreCase("output"))
					return true;
		}
		return false;
	}

//
//	public static void log(String string, String readFromFile) {
//		String setting = Variable.getString("LogMessages");
//		if(checkConfig(state,setting)){
//			String logFolder = Variable.getString("LogFolder");
//			logFolder = (logFolder.endsWith("/") ? logFolder : (logFolder  + File.separatorChar));
//			
//			String timeStmp = new SimpleDateFormat(Variable.getString("LogPrefix")).format(new Date());
//			File fileName = new File( logFolder +timeStmp + stepName + state +".xml");
//			fd.writeToFile(fileName, message);
//		}		
//	}
	
}
