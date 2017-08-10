package com.amassfreight.domain;

import java.io.Serializable;

public class RC002TDepotDt implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String depotDtId;
	private double noLen;
	private double noWidth;
	private double noHeight;
	private int depotNum;
	private int delivNum;
	private int billingNum;
	private String pkg;
	private double kgs;
	private double cbm;
	private String marks;// 进仓备注
	private String modeMeasure;

	public RC002TDepotDt() {
		// TODO Auto-generated constructor stub
	}

	public String getDepotDtId() {
		return depotDtId;
	}

	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}

	public double getNoLen() {
		return noLen;
	}

	public void setNoLen(double noLen) {
		this.noLen = noLen;
	}

	public double getNoWidth() {
		return noWidth;
	}

	public void setNoWidth(double noWidth) {
		this.noWidth = noWidth;
	}

	public double getNoHeight() {
		return noHeight;
	}

	public void setNoHeight(double noHeight) {
		this.noHeight = noHeight;
	}

	public int getDepotNum() {
		return depotNum;
	}

	public void setDepotNum(int depotNum) {
		this.depotNum = depotNum;
	}

	public int getBillingNum() {
		return billingNum;
	}

	public void setBillingNum(int billingNum) {
		this.billingNum = billingNum;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public double getKgs() {
		return kgs;
	}

	public void setKgs(double kgs) {
		this.kgs = kgs;
	}

	public double getCbm() {
		return cbm;
	}

	public void setCbm(double cbm) {
		this.cbm = cbm;
	}

	public String getMarks() {
		return marks;
	}

	public void setMarks(String marks) {
		this.marks = marks;
	}

	public String getModeMeasure() {
		return modeMeasure;
	}

	public void setModeMeasure(String modeMeasure) {
		this.modeMeasure = modeMeasure;
	}

	@Override
	public String toString() {
		return "TDepotDt [depotDtId=" + depotDtId + ", noLen=" + noLen 
				+ ", noWidth=" + noWidth + ", noHeight=" + noHeight
				+ ", depotNum=" + depotNum + ", billingNum=" + billingNum
				+ ", pkg=" + pkg + ", kgs=" + kgs + ", cbm=" + cbm + ", marks="
				+ marks + ", modeMeasure=" + modeMeasure + "]";
	}

	public int getDelivNum() {
		return delivNum;
	}

	public void setDelivNum(int delivNum) {
		this.delivNum = delivNum;
	}

}
