package vdsl.structures;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import vdsl.fileIO.FileDealer;
import vdsl.fileIO.FolderDealer;

public class Folder implements Structure{

	public File file;
	private FolderDealer fldd = new FolderDealer();
	private FileDealer fd = new FileDealer();
	
	public Folder(File file) {
		if(file.isDirectory())
			this.file = file;
	}

	public Folder(String file) {
		File tmpfile = new File(file);
		if(tmpfile.isDirectory())
			this.file = tmpfile;
	}

	public Folder(List<String> values,String key){
		this(values.get(0));
	}

	public String variableGet(List<String> inputParam){
		return file.getAbsolutePath().replace("\\", "/");
	}

	@Override
	public String get(List<String> value) {
		File msgFile = fldd.latestFile(this.file);
		String msg = fd.readFromFile(msgFile);
		return msg;
	}

	
	public String latest(List<String> value){
		return get(value);
	}
	
	@Override
	public void set(List<String> message) {
		String timeStmp = new SimpleDateFormat("_dd-MMM_HH-mm-ss").format(new Date());
		File fileName = new File(file.getAbsolutePath() + File.separatorChar +"Benjamin"+timeStmp+".xml");
		fd.writeToFile(fileName, message.get(0));
	}
}
