package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class VA006Data implements Serializable {
    public List<VA006Attachment> attachmentList;
    public List<FileManageData> fileList;
	public List<VA006Attachment> getAttachmentList() {
		return attachmentList;
	}
	public void setAttachmentList(List<VA006Attachment> attachmentList) {
		this.attachmentList = attachmentList;
	}
	public List<FileManageData> getFileList() {
		return fileList;
	}
	public void setFileList(List<FileManageData> fileList) {
		this.fileList = fileList;
	}
}
