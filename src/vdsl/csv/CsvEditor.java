package vdsl.csv;

import java.io.File;

import vdsl.fileIO.FileDealer;

public class CsvEditor {

	private final static String CSV_LINE_DELIM = " *\\r?\\n *";
	private final static String CSV_ENTITY_DELIM = ",";
	
	public static void main(String[] args) {
		String str = new FileDealer().readFromFile(new File("ConfigAndData/csv/Variable.csv"));
		String[][] csvData = csvToArray(str);
		for (int i = 0; i < csvData.length; i++) {
			for (int j = 0; j < csvData[i].length; j++) {
				System.out.print(csvData[i][j]+"\t");
			}
			System.out.println();
		}
		
	}
	
	public static String[][] csvToArray(String csvString){
		String[][] csvData;
		String[] csvRowData = null;
		csvRowData = csvString.split(CSV_LINE_DELIM);
		csvData = new String[csvRowData.length][];
		for (int i = 0; i < csvRowData.length; i++) {
			csvData[i] = csvRowData[i].trim().split(CSV_ENTITY_DELIM);
			for (int j = 0; j < csvData[i].length; j++) {
				csvData[i][j] = csvData[i][j].trim();
			}
		}
		return csvData;
	}
}
