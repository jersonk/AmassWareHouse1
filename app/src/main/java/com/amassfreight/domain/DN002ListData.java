package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;

public class DN002ListData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DN002ListData() {
	}

	private String depotDtID;
	private String noPilecard;
	private int depotNum;
	private double kgs;
	private double cbm;
	private boolean status;
	private String noCarLice; // 车牌号
	private String typeCar; // 车辆类型
	private String noMultipleOrder; // 分票号
	private String noCarNum; // 同车编号
	private String noMultipleDepot; // 进仓分票号  add by yxq 2014/09/03
	private Date depotDate;  //进仓日期
	private String noMultipleColo; // 同行分号
	private String marksRealPort;   // 唛头港口
	

	public String getDepotDtID() {
		return depotDtID;
	}

	public void setDepotDtID(String depotDtID) {
		this.depotDtID = depotDtID;
	}

	public String getNoPilecard() {
		return noPilecard;
	}

	public void setNoPilecard(String noPilecard) {
		this.noPilecard = noPilecard;
	}

	public int getDepotNum() {
		return depotNum;
	}

	public void setDepotNum(int depotNum) {
		this.depotNum = depotNum;
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

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getNoCarLice() {
		return noCarLice;
	}

	public void setNoCarLice(String noCarLice) {
		this.noCarLice = noCarLice;
	}

	public String getTypeCar() {
		return typeCar;
	}

	public void setTypeCar(String typeCar) {
		this.typeCar = typeCar;
	}

	public String getNoMultipleOrder() {
		return noMultipleOrder;
	}

	public void setNoMultipleOrder(String noMultipleOrder) {
		this.noMultipleOrder = noMultipleOrder;
	}

	public String getNoCarNum() {
		return noCarNum;
	}

	public void setNoCarNum(String noCarNum) {
		this.noCarNum = noCarNum;
	}

	public String getNoMultipleDepot() {
		return noMultipleDepot;
	}

	public void setNoMultipleDepot(String noMultipleDepot) {
		this.noMultipleDepot = noMultipleDepot;
	}

	public Date getDepotDate() {
		return depotDate;
	}

	public void setDepotDate(Date depotDate) {
		this.depotDate = depotDate;
	}

	public String getNoMultipleColo() {
		return noMultipleColo;
	}

	public void setNoMultipleColo(String noMultipleColo) {
		this.noMultipleColo = noMultipleColo;
	}

	public String getMarksRealPort() {
		return marksRealPort;
	}

	public void setMarksRealPort(String marksRealPort) {
		this.marksRealPort = marksRealPort;
	}
}
