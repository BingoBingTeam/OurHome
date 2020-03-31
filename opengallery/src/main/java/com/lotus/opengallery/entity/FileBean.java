package com.lotus.opengallery.entity;

public class FileBean {
	private String topFilePath;
	private String folderName;
	private int fileCounts;
	
	private String filePath;
	private String fileName;
	private long fileSize;

	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getTopFilePath() {
		return topFilePath;
	}
	public void setTopFilePath(String topFilePath) {
		this.topFilePath = topFilePath;
	}
	public String getFolderName() {
		return folderName;
	}
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}
	public int getFileCounts() {
		return fileCounts;
	}
	public void setFileCounts(int fileCounts) {
		this.fileCounts = fileCounts;
	}
	
	
}
