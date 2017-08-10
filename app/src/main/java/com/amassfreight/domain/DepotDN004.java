package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class DepotDN004 implements Serializable {
	public DepotDN004() {
	}

	private String depotDtID; // 货物明细ID
	private String depotID; // 进仓ID
	private int noBatch; // 批次
	private String noPilecard; // 桩脚牌
	private String noCarLice; // 车牌号
	private String typeCar; // 车辆类型
	private String noMultipleOrder; // 分票号
	private String noCarNum; // 同车编号
	private String modeMeasure; // 测量模式
	private double noLen; // 长
	private double noWidth; // 宽
	private double noHeight; // 高
	private int depotNum; // 进仓件数
	private int billingNum; // 计费件数
	private String packing; // 包装
	private double kgs; // 重量
	private double cbm; // 体积
	private String depotRemark; // 进仓备注
	private String workerID; // 员工ID
	private String workerNM; // 员工姓名
	private String loadID1; // 装卸ID1
	private String loadID2; // 装卸ID2
	private String truckLoadID; // 装卸ID3
	private String truckID; // 铲车工ID
	private String packType; // 包装类型
	private String truckName; //铲车工名字
	private String pos; // 库区
	private String location; // 库位

	private List<SelectDict> carTypeList; // 车辆类型
	private List<SelectDict> meterModelList; // 测量模式
	private List<SelectDict> dockManList; // 装卸工
	private List<SelectDict> packUnitList; // 包装单位
	private List<SelectDict> posList; // 库区
	private List<SelectDict> packTypeList; // 包装类型
	private List<SelectDict> truckList; // 铲车工
	private String modeRemark;
	// add by yxq 2014/09/03 begin
	private String noMultipleDepot;   // 进仓分票号
	// add by yxq 2014/09/03 end
    // add by yxq 2014/09/03 begin
	private String typeReaMea;       // 复量区分
	// add by yxq 2014/09/03 end
	private String noMultipleColo;   //同行编号分号
	
	public String getModeRemark() {
		return modeRemark;
	}

	public void setModeRemark(String modeRemark) {
		this.modeRemark = modeRemark;
	}
	public String getDepotDtID() {
		return depotDtID;
	}

	public void setDepotDtID(String depotDtID) {
		this.depotDtID = depotDtID;
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

	public String getNoPilecard() {
		return noPilecard;
	}

	public void setNoPilecard(String noPilecard) {
		this.noPilecard = noPilecard;
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

	public String getModeMeasure() {
		return modeMeasure;
	}

	public void setModeMeasure(String modeMeasure) {
		this.modeMeasure = modeMeasure;
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

	public String getDepotRemark() {
		return depotRemark;
	}

	public void setDepotRemark(String depotRemark) {
		this.depotRemark = depotRemark;
	}

	public String getWorkerID() {
		return workerID;
	}

	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}

	public String getWorkerNM() {
		return workerNM;
	}

	public void setWorkerNM(String workerNM) {
		this.workerNM = workerNM;
	}

	public String getLoadID1() {
		return loadID1;
	}

	public void setLoadID1(String loadID1) {
		this.loadID1 = loadID1;
	}

	public String getLoadID2() {
		return loadID2;
	}

	public void setLoadID2(String loadID2) {
		this.loadID2 = loadID2;
	}

	public String getTruckLoadID() {
		return truckLoadID;
	}

	public void setTruckLoadID(String truckLoadID) {
		this.truckLoadID = truckLoadID;
	}

	public String getTruckID() {
		return truckID;
	}

	public void setTruckID(String truckID) {
		this.truckID = truckID;
	}

	public String getPackType() {
		return packType;
	}

	public void setPackType(String packType) {
		this.packType = packType;
	}

	public List<SelectDict> getCarTypeList() {
		return carTypeList;
	}

	public void setCarTypeList(List<SelectDict> carTypeList) {
		this.carTypeList = carTypeList;
	}

	public List<SelectDict> getMeterModelList() {
		return meterModelList;
	}

	public void setMeterModelList(List<SelectDict> meterModelList) {
		this.meterModelList = meterModelList;
	}

	public List<SelectDict> getDockManList() {
		return dockManList;
	}

	public void setDockManList(List<SelectDict> dockManList) {
		this.dockManList = dockManList;
	}

	public List<SelectDict> getPackUnitList() {
		return packUnitList;
	}

	public void setPackUnitList(List<SelectDict> packUnitList) {
		this.packUnitList = packUnitList;
	}

	public List<SelectDict> getPosList() {
		return posList;
	}

	public void setPosList(List<SelectDict> posList) {
		this.posList = posList;
	}

	public List<SelectDict> getPackTypeList() {
		return packTypeList;
	}

	public void setPackTypeList(List<SelectDict> packTypeList) {
		this.packTypeList = packTypeList;
	}

	public List<SelectDict> getTruckList() {
		return truckList;
	}

	public void setTruckList(List<SelectDict> truckList) {
		this.truckList = truckList;
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

	public String getNoMultipleDepot() {
		return noMultipleDepot;
	}

	public void setNoMultipleDepot(String noMultipleDepot) {
		this.noMultipleDepot = noMultipleDepot;
	}

	public String getTypeReaMea() {
		return typeReaMea;
	}

	public void setTypeReaMea(String typeReaMea) {
		this.typeReaMea = typeReaMea;
	}

	public String getTruckName() {
		return truckName;
	}

	public void setTruckName(String truckName) {
		this.truckName = truckName;
	}

	public String getNoMultipleColo() {
		return noMultipleColo;
	}

	public void setNoMultipleColo(String noMultipleColo) {
		this.noMultipleColo = noMultipleColo;
	}

}
