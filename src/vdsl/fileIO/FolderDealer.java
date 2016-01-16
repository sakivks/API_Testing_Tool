package vdsl.fileIO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FolderDealer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File folder = new File(
				"//blrvswasubdev14/SEPA/Sender/BulkPosting/Processed");
		FolderDealer fOps = new FolderDealer();
		File latestFile = fOps.latestFile(folder);
		System.out.println("latest file:" + latestFile.getName());

//		String regex = "([^\\s]+(\\.(?i)(xml))$)";
//		List<File> matchedFiles = fOps.filesOnRegex(folder, regex);
//		System.out.println(matchedFiles.toString());
	}

	public File latestFile(String file){
		return latestFile(new File(file));
	}
	public File latestFile(File folder){
		long modifTime = 0;
		File latestFile = null;
		for (File fileIterator : folder.listFiles()) {
			if (modifTime < (fileIterator.lastModified()) && fileIterator.isFile()) {
				latestFile = fileIterator;
				modifTime = fileIterator.lastModified();
			}
		}
		return latestFile;
	}

	public List<File> filesOnRegex(File folder, String regex) {

		List<File> matchedFiles = new ArrayList<File>();
		Pattern p = Pattern.compile(regex);
		for (File folderIterator : folder.listFiles()) {
			if (p.matcher(folderIterator.toString()).matches())
				matchedFiles.add(folderIterator);
		}
		return matchedFiles;

	}

}