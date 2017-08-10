package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class DepotPP012 implements Serializable {
	public DepotPP012() {
	}

	private String noBox; // 集箱号
	private String cdContainer; // 箱号
	private String cdBoxTitle; // 封号
	private String nmBox; // 箱型箱量
	private String sealRemarks; // 铅封备注
	private List<FileManageData> FileList;
	private Double totalWeight;	//总重量

	public String getNoBox() {
		return noBox;
	}

	public void setNoBox(String noBox) {
		this.noBox = noBox;
	}

	public String getCdContainer() {
		return cdContainer;
	}

	public void setCdContainer(String cdContainer) {
		this.cdContainer = cdContainer;
	}

	public String getCdBoxTitle() {
		return cdBoxTitle;
	}

	public void setCdBoxTitle(String cdBoxTitle) {
		this.cdBoxTitle = cdBoxTitle;
	}

	public String getNmBox() {
		return nmBox;
	}

	public void setNmBox(String nmBox) {
		this.nmBox = nmBox;
	}

	public String getSealRemarks() {
		return sealRemarks;
	}

	public void setSealRemarks(String sealRemarks) {
		this.sealRemarks = sealRemarks;
	}

	public List<FileManageData> getFileList() {
		return FileList;
	}

	public void setFileList(List<FileManageData> FileList) {
		this.FileList = FileList;
	}

	public Double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(Double totalWeight) {
		this.totalWeight = totalWeight;
	}
}
