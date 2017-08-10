package com.amassfreight.domain;

import java.io.Serializable;

import javax.security.auth.PrivateCredentialPermission;

@SuppressWarnings("serial")
public class PP001ResponseData implements Serializable {

	public PP001ResponseData() {
	}

	private String noBox; // 集箱号
	private String contaType; // 装箱类型
	private String stsPacking; // 装箱状态
	private String dtPackingDeadLine; // 装箱最晚时间
	private String nmPacking; // 装箱员
	private String dtETD;
	private String airLine;
	private String statusNow;
	private int flgPresent;
	
	public String getDtETD() {
		return dtETD;
	}
	public void setDtETD(String dtETD) {
		this.dtETD = dtETD;
	}
	public String getAirLine() {
		return airLine;
	}
	public void setAirLine(String airLine) {
		this.airLine = airLine;
	}
	public String getNoBox() {
		return noBox;
	}
	public void setNoBox(String noBox) {
		this.noBox = noBox;
	}
	public String getContaType() {
		return contaType;
	}
	public void setContaType(String contaType) {
		this.contaType = contaType;
	}
	public String getStsPacking() {
		return stsPacking;
	}
	public void setStsPacking(String stsPacking) {
		this.stsPacking = stsPacking;
	}
	public String getDtPackingDeadLine() {
		return dtPackingDeadLine;
	}
	public void setDtPackingDeadLine(String dtPackingDeadLine) {
		this.dtPackingDeadLine = dtPackingDeadLine;
	}
	public String getNmPacking() {
		return nmPacking;
	}
	public void setNmPacking(String nmPacking) {
		this.nmPacking = nmPacking;
	}
	public String getStatusNow() {
		return statusNow;
	}
	public void setStatusNow(String statusNow) {
		this.statusNow = statusNow;
	}
	public int getFlgPresent() {
		return flgPresent;
	}
	public void setFlgPresent(int flgPresent) {
		this.flgPresent = flgPresent;
	}

}
