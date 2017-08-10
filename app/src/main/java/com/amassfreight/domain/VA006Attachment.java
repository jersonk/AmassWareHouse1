package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class VA006Attachment implements Serializable {
	
	public VA006Attachment() {
	}

	private String cdFile;  	     // 文件编号
	private String idFile;  	     // 文件Id
	private String cdOrder; 	     // 业务编号
	private String nmFile; 		     // 文件名
	private String cdUpdateUser; 	 // 上传者Id
	private String cdUpdateUserName; // 上传者Name
	private Date   dtUpdateDate;     // 上传时间
	private List<FileManageData> fileList;
	public String getCdFile() {
		return cdFile;
	}
	public void setCdFile(String cdFile) {
		this.cdFile = cdFile;
	}
	public String getIdFile() {
		return idFile;
	}
	public void setIdFile(String idFile) {
		this.idFile = idFile;
	}
	public String getCdOrder() {
		return cdOrder;
	}
	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}
	public String getNmFile() {
		return nmFile;
	}
	public void setNmFile(String nmFile) {
		this.nmFile = nmFile;
	}
	public String getCdUpdateUser() {
		return cdUpdateUser;
	}
	public void setCdUpdateUser(String cdUpdateUser) {
		this.cdUpdateUser = cdUpdateUser;
	}
	public Date getDtUpdateDate() {
		return dtUpdateDate;
	}
	public void setDtUpdateDate(Date dtUpdateDate) {
		this.dtUpdateDate = dtUpdateDate;
	}
	public String getCdUpdateUserName() {
		return cdUpdateUserName;
	}
	public void setCdUpdateUserName(String cdUpdateUserName) {
		this.cdUpdateUserName = cdUpdateUserName;
	}
	public List<FileManageData> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileManageData> fileList) {
		this.fileList = fileList;
	}

}
