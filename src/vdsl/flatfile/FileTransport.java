package vdsl.flatfile;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FileTransport {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FileTransport ft = new FileTransport();
		ft.copyFile(new File("C:/Users/vikkuma2/Desktop/MPM/test.xml"), new File("C:/Users/vikkuma2/Desktop/MPM/test1.xml"));
		ft.copyFile(new File("C:/Users/vikkuma2/Desktop/MPM/test1.xml"), new File("C:/Users/vikkuma2/Desktop/MPM/internal/test1.xml"));
		ft.moveFile(new File("C:/Users/vikkuma2/Desktop/MPM/test1.xml"), new File("C:/Users/vikkuma2/Desktop/MPM/internal/test3.xml"));
		ft.deleteFile(new File("C:/Users/vikkuma2/Desktop/MPM/test.xml"));
		ft.deleteDirectory(new File("C:/Users/vikkuma2/Desktop/MPM/internal"));
		ft.createFile(new File("C:/Users/vikkuma2/Desktop/MPM/test10.xml"));
		
		
		System.out.println("done");
	}
	
	public boolean copyFile(File srcFile,File destFile) {
		
		try {
			FileUtils.copyFile(srcFile, destFile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean moveFile(File srcFile,File destFile) {
		
		try {
			FileUtils.moveFile(srcFile, destFile);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteDirectory(File srcDirectory) {
		
		try {
			FileUtils.deleteDirectory(srcDirectory);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean deleteFile(File file) {
		
		try {
			return file.delete();
		} catch (SecurityException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean createFile(File srcFile) {
		
		try {
			if (!srcFile.exists()) {
				srcFile.createNewFile();
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

}
