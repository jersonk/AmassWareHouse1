package com.amassfreight.domain;

import java.io.Serializable;

public class VA003FileList implements Serializable {

	public VA003FileList() {
	}

	private String fileId;   // 文件Id
	private String fileName; // 文件名

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
