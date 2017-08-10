package com.amassfreight.domain;

import java.util.Date;

public class UnPackingList {

	public UnPackingList(){
		
	}

	private String unPackingId;
	private String orderCd;
	private String orderCdPublic;
	private String depotDtId;
	private String pileCardCd;
	private String numRemark;
	private int unPackingFlg;
	private int paidinNum;
	private Date unPackingDate;
	private String boxNo;
	private String depotDtIdOrigin;
	
	public String getOrderCd() {
		return orderCd;
	}
	public void setOrderCd(String orderCd) {
		this.orderCd = orderCd;
	}
	public String getOrderCdPublic() {
		return orderCdPublic;
	}
	public void setOrderCdPublic(String orderCdPublic) {
		this.orderCdPublic = orderCdPublic;
	}
	public String getDepotDtId() {
		return depotDtId;
	}
	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}
	public String getPileCardCd() {
		return pileCardCd;
	}
	public void setPileCardCd(String pileCardCd) {
		this.pileCardCd = pileCardCd;
	}
	public String getNumRemark() {
		return numRemark;
	}
	public void setNumRemark(String numRemark) {
		this.numRemark = numRemark;
	}
	public int getUnPackingFlg() {
		return unPackingFlg;
	}
	public void setUnPackingFlg(int unPackingFlg) {
		this.unPackingFlg = unPackingFlg;
	}
	public int getPaidinNum() {
		return paidinNum;
	}
	public void setPaidinNum(int paidinNum) {
		this.paidinNum = paidinNum;
	}
	public String getUnPackingId() {
		return unPackingId;
	}
	public void setUnPackingId(String unPackingId) {
		this.unPackingId = unPackingId;
	}
	public Date getUnPackingDate() {
		return unPackingDate;
	}
	public void setUnPackingDate(Date unPackingDate) {
		this.unPackingDate = unPackingDate;
	}
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public String getDepotDtIdOrigin() {
		return depotDtIdOrigin;
	}
	public void setDepotDtIdOrigin(String depotDtIdOrigin) {
		this.depotDtIdOrigin = depotDtIdOrigin;
	}
}
