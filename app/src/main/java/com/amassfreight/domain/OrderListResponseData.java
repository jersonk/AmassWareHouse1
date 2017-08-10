package com.amassfreight.domain;


public class OrderListResponseData {
	
	public OrderListResponseData() {
	}
	private String orderCd;
	private String orderCdPublic;
	private int workStatus;
	private int realNum;
	private String numRemark;
	private String packingRequire;
	private int preconfigureNum;
	
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
	public int getWorkStatus() {
		return workStatus;
	}
	public void setWorkStatus(int workStatus) {
		this.workStatus = workStatus;
	}
	public int getRealNum() {
		return realNum;
	}
	public void setRealNum(int realNum) {
		this.realNum = realNum;
	}
	public String getPackingRequire() {
		return packingRequire;
	}
	public void setPackingRequire(String packingRequire) {
		this.packingRequire = packingRequire;
	}
	public String getNumRemark() {
		return numRemark;
	}
	public void setNumRemark(String numRemark) {
		this.numRemark = numRemark;
	}
	public int getPreconfigureNum() {
		return preconfigureNum;
	}
	public void setPreconfigureNum(int preconfigureNum) {
		this.preconfigureNum = preconfigureNum;
	}
	

}
