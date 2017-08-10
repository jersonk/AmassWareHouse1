package com.amassfreight.domain;

import java.io.Serializable;

public class DN004TDepotDt implements Serializable {
	public DN004TDepotDt() {
	}

	private String depotID; // 进仓ID
	private int noBatch; // 批次
	private double noLen; // 长
	private double noWidth; // 宽
	private double noHeight; // 高
	private int depotNum; // 进仓件数
	private int billingNum; // 计费件数
	private String packing; // 包装
	private double kgs; // 重量
	private double cbm; // 体积
	private String noCarNum; // 同车编号
	private String noCarLice; // 车牌号
	private String depotRemark; // 进仓备注
	private String noMultipleOrder; // 分票号
	private int typeCar; // 车辆类型
	private int modeMeasure; // 测量模式
	private String pos; // 库区
	private String location; // 库位
	private String packType; // 包装类型
	private String modeRemark;
	// add by yxq 2014/09/03 begin
	private String noMultipleDepot; // 进仓分票号
	// add by yxq 2014/09/03 end
	private String noMultipleColo; // 进仓分票号

	public String getModeRemark() {
		return modeRemark;
	}

	public void setModeRemark(String modeRemark) {
		this.modeRemark = modeRemark;
	}

	public String getDepotID() {
		return depotID;
	}

	public void setDepotID(String depotID) {
		this.depotID = depotID;
	}

	public int getNoBatch() {
		return noBatch;
	}

	public void setNoBatch(int noBatch) {
		this.noBatch = noBatch;
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

	public String getPacking() {
		return packing;
	}

	public void setPacking(String packing) {
		this.packing = packing;
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

	public String getNoCarNum() {
		return noCarNum;
	}

	public void setNoCarNum(String noCarNum) {
		this.noCarNum = noCarNum;
	}

	public String getNoCarLice() {
		return noCarLice;
	}

	public void setNoCarLice(String noCarLice) {
		this.noCarLice = noCarLice;
	}

	public String getDepotRemark() {
		return depotRemark;
	}

	public void setDepotRemark(String depotRemark) {
		this.depotRemark = depotRemark;
	}

	public String getNoMultipleOrder() {
		return noMultipleOrder;
	}

	public void setNoMultipleOrder(String noMultipleOrder) {
		this.noMultipleOrder = noMultipleOrder;
	}

	public int getTypeCar() {
		return typeCar;
	}

	public void setTypeCar(int typeCar) {
		this.typeCar = typeCar;
	}

	public int getModeMeasure() {
		return modeMeasure;
	}

	public void setModeMeasure(int modeMeasure) {
		this.modeMeasure = modeMeasure;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getPackType() {
		return packType;
	}

	public void setPackType(String packType) {
		this.packType = packType;
	}

	public String getNoMultipleDepot() {
		return noMultipleDepot;
	}

	public void setNoMultipleDepot(String noMultipleDepot) {
		this.noMultipleDepot = noMultipleDepot;
	}

	public String getNoMultipleColo() {
		return noMultipleColo;
	}

	public void setNoMultipleColo(String noMultipleColo) {
		this.noMultipleColo = noMultipleColo;
	}

}
