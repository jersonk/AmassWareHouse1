package com.amassfreight.domain;

import java.io.Serializable;

import com.amassfreight.warehouse.R.string;

public class FileManageData implements Serializable {
	public FileManageData() {
	}

	private String fileId; // 文件Id
	private String fileName; // 文件名称

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
