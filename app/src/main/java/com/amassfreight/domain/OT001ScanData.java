package com.amassfreight.domain;

import java.io.Serializable;

// 扫描的信息
public class OT001ScanData implements Serializable {
    private String depotDtId;   // 货物明细Id
    private String cdOrder;   	// 进仓编号
    private String depotNum;	// 件数
    private String posCd;	  	// 库区编号
    private String location;  	// 库位
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
	public String getDepotNum() {
		return depotNum;
	}
	public void setDepotNum(String depotNum) {
		this.depotNum = depotNum;
	}
	public String getPosCd() {
		return posCd;
	}
	public void setPosCd(String posCd) {
		this.posCd = posCd;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
    
}
