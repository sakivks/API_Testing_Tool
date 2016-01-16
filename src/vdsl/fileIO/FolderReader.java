package vdsl.fileIO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FolderReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File folder = new File(
				"//blrvswasubdev14/SEPA/Sender/BulkPosting/Processed");
		FileOps fOps = new FileOps();
		// File latestFile=fOps.latestFile(folder);
//		folder = new File("SEPAMessages");
		// System.out.println("latest file:"+latestFile.getName());
		String regex = "([^\\s]+(\\.(?i)(xml))$)";
		List<File> matchedFiles = fOps.filesOnRegex(folder, regex);
		System.out.println(matchedFiles.toString());
	}

}

class FileOps {
	public File latestFile(File folder) throws IOException {
		long modifTime = 0;
		File latestFile = null;
		for (File fileIterator : folder.listFiles()) {
			System.out.println(fileIterator.lastModified());
			if (modifTime < (fileIterator.lastModified())) {
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