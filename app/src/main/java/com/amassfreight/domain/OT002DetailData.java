package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;

// 显示的列表信息
public class OT002DetailData implements Serializable {
	private String depotDtId;  // 货物明细Id
	private String cdOrder;	   // 进仓编号
	private String coLoader;   // 同行编号
	private String batchNo;    // 批次
	private String pilecardNo; // 桩脚牌Id
	private String posName;    // 库区名字
	private String location;   // 库位名字
	private String num;  	   // 数据库中的数量
	public String getDepotDtId() {
		return depotDtId;
	}
	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}
	public String getCdOrder() {
		return cdOrder;
	}
	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}
	public String getCoLoader() {
		return coLoader;
	}
	public void setCoLoader(String coLoader) {
		this.coLoader = coLoader;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getPilecardNo() {
		return pilecardNo;
	}
	public void setPilecardNo(String pilecardNo) {
		this.pilecardNo = pilecardNo;
	}
	public String getPosName() {
		return posName;
	}
	public void setPosName(String posName) {
		this.posName = posName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
}
