package com.amassfreight.domain;


public class PileCardResponseData {//implements BaseResponseData{

	public PileCardResponseData(){}

	private String depotDtId;
	private String pileCardCd;  
	private String number;  
	private String pickStatus;  
	private String packStatus;  
	private String batchNo; 
	private String pileCardNo; 
	private String posAndLoc;      //库区-库位
	private String packingRemark;  //装箱备注
	public String getPileCardCd() {
		return pileCardCd;
	}

	public void setPileCardCd(String pileCardCd) {
		this.pileCardCd = pileCardCd;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getPickStatus() {
		return pickStatus;
	}

	public void setPickStatus(String pickStatus) {
		this.pickStatus = pickStatus;
	}

	public String getPackStatus() {
		return packStatus;
	}

	public void setPackStatus(String packStatus) {
		this.packStatus = packStatus;
	}

	public String getDepotDtId() {
		return depotDtId;
	}

	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getPileCardNo() {
		return pileCardNo;
	}

	public void setPileCardNo(String pileCardNo) {
		this.pileCardNo = pileCardNo;
	}

	public String getPosAndLoc() {
		return posAndLoc;
	}

	public void setPosAndLoc(String posAndLoc) {
		this.posAndLoc = posAndLoc;
	}

	public String getPackingRemark() {
		return packingRemark;
	}

	public void setPackingRemark(String packingRemark) {
		this.packingRemark = packingRemark;
	}

}
