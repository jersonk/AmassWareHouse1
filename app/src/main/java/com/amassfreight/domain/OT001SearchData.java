package com.amassfreight.domain;

import java.io.Serializable;

public class OT001SearchData implements Serializable{

	private static final long serialVersionUID = -8059330644320185450L;
	public OT001SearchData(){}
	private String cdOrderPublic;    // 进仓编号
	private String coLoader;         // 同行编号
	private String batchNo;          // 批次
	private String pilecardNo;       // 桩脚牌Id
	private String depotNum;         // 实收件数
	private String posCd;	  	     // 库区编号
    private String location;  	     // 库位
    public String getCdOrderPublic() {
		return cdOrderPublic;
	}
	public void setCdOrderPublic(String cdOrderPublic) {
		this.cdOrderPublic = cdOrderPublic;
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
	public String getDepotNum() {
		return depotNum;
	}
	public void setDepotNum(String depotNum) {
		this.depotNum = depotNum;
	}
}
