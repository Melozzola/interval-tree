package riz.silvano.intervaltree;

public class IntervalFile {
	
	private String fileName;
	private String supplier;
	private String fileDate;
	
	public IntervalFile(String name, String supply, String date) {
		this.fileName = name;
		this.supplier = supply;
		this.fileDate = date;
	}
	
	public String getFileName() {
		return fileName;
	}
	public String getSupplier() {
		return supplier;
	}
	public String getFileDate() {
		return fileDate;
	}	
	
}
