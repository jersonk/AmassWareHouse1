package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class DepotPP013 implements Serializable {
	public DepotPP013() {
	}

	private String noBox; // 集箱号
	private String cdContainer; // 箱号
	private String nmBox; // 箱型箱量
	private String nmStatusNow; //集装箱状态
	private boolean flgCheckOk; // 验箱正常标志
	private boolean flgCheckPollution; // 验箱污染标志
	private boolean flgCheckDamaged; // 验箱破损标志
	private boolean flgCheckDeform; // 验箱变形标志
	private String checkRemarks; // 验箱备注
	private Double qtVgmKgsLeather;		//箱皮重

	private String place;				// 所选位置    add by yxq 2014/10/24
	
	private List<FileManageData> FileList;

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

	public String getNmBox() {
		return nmBox;
	}

	public void setNmBox(String nmBox) {
		this.nmBox = nmBox;
	}

	public boolean isFlgCheckOk() {
		return flgCheckOk;
	}

	public void setFlgCheckOk(boolean flgCheckOk) {
		this.flgCheckOk = flgCheckOk;
	}

	public boolean isFlgCheckPollution() {
		return flgCheckPollution;
	}

	public void setFlgCheckPollution(boolean flgCheckPollution) {
		this.flgCheckPollution = flgCheckPollution;
	}

	public boolean isFlgCheckDamaged() {
		return flgCheckDamaged;
	}

	public void setFlgCheckDamaged(boolean flgCheckDamaged) {
		this.flgCheckDamaged = flgCheckDamaged;
	}

	public boolean isFlgCheckDeform() {
		return flgCheckDeform;
	}

	public void setFlgCheckDeform(boolean flgCheckDeform) {
		this.flgCheckDeform = flgCheckDeform;
	}

	public String getCheckRemarks() {
		return checkRemarks;
	}

	public void setCheckRemarks(String checkRemarks) {
		this.checkRemarks = checkRemarks;
	}

	public List<FileManageData> getFileList() {
		return FileList;
	}

	public void setFileList(List<FileManageData> fileList) {
		FileList = fileList;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Double getQtVgmKgsLeather() {
		return qtVgmKgsLeather;
	}

	public void setQtVgmKgsLeather(Double qtVgmKgsLeather) {
		this.qtVgmKgsLeather = qtVgmKgsLeather;
	}

	public String getNmStatusNow() {
		return nmStatusNow;
	}

	public void setNmStatusNow(String nmStatusNow) {
		this.nmStatusNow = nmStatusNow;
	}

}
