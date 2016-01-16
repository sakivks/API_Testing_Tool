package vdsl.structures;
 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import vdsl.csv.CsvEditor;
import vdsl.fileIO.FileDealer;
import vdsl.interpreter.Variable;
 
public class File implements Structure{
 
    public java.io.File file;
    FileDealer fd = new FileDealer();
     
    public File(java.io.File file) {
        if(file.isFile())
            this.file = file;
    }
     
    public File(String file) {
        java.io.File tmpFile = new java.io.File(file);
        if(tmpFile.isFile())
            this.file = tmpFile;
    }
 
    public File(List<String> values,String key){
        this(values.get(0));
    }
 
    @Override
    public String get(List<String> value) {
        return fd.readFromFile(this.file);
    }
 
    @Override
    public void set(List<String> message) {
        fd.writeToFile(this.file, message.get(0));
    }
 
	public String variableGet(List<String> inputParam){
		return get(inputParam);
	}

    public void appendToFile(List<String> inputParam) {
    	if(Variable.getBoolean("FRESH_RUN", false)){
    		initializeAndAppendToFile(inputParam);
    		Variable.setVariable("FRESH_RUN", "false");
    	}else{
    		fd.appendToFile(this.file,inputParam.get(0)+'\n' );
    	}
    }

    public void initializeAndAppendToFile(List<String> inputParam) {
		String timeStmp = new SimpleDateFormat(Variable.getString("LogPrefix")).format(new Date());
		java.io.File backUpFile = new java.io.File(this.file.getParentFile()+ "/" + timeStmp + this.file.getName());

    	fd.writeToFile(backUpFile, fd.readFromFile(this.file));
    	
        fd.writeToFile(this.file,"TEST CASE NAME, STATUS, REASON FOR FAILURE"+'\n' );
        fd.appendToFile(this.file,inputParam.get(0)+'\n' );
    }

    
	public String readProcessList(List<String> inputParam){
    	StringBuilder automatedList = new StringBuilder();
    	String str = this.get(null);
    	String[][] csvData = CsvEditor.csvToArray(str);
    	if(csvData.length > 0){
	    	for (int i = 1; i < csvData.length; i++) {
	    		automatedList.append(csvData[i][0]+", ");
			}
    	}
    	return automatedList.toString();
    }

	public String readVariableList(List<String> inputParam){
    	StringBuilder variableList = new StringBuilder();
    	String str = this.get(null);
    	String[][] csvData = CsvEditor.csvToArray(str);
    	if(csvData.length > 0){
	    	for (int i = 1; i < csvData.length; i++) {
	    		variableList.append(csvData[i][0]+" : ");
	    		if(csvData[i].length > 1 )
	    			variableList.append(csvData[i][1]+", ");
	    		else
	    			variableList.append(" "+", ");	    			
	    	}
    	}
    	return variableList.toString();
    }

}
