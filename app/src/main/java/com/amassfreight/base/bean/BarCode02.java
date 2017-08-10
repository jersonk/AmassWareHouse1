package com.amassfreight.base.bean;

public class BarCode02 {

	private String depotDtId;	      //货物明细ID
	private String orderCd;           //进仓编号
	private String coLoader;          //同行编号
	private String depotNum;          //件数
	
	public boolean paserBarCode(String barCode) {	
		String[] obj=barCode.split(",",-1);
		//if("02".equals(obj[0]) && obj.length == 5){
		// 解决同行编号中带逗号的情况 modify by sdhuang 2014-12-11
		if("02".equals(obj[0]) && obj.length >= 5){
			this.depotDtId = obj[1];
			this.orderCd = obj[2];
			this.coLoader = obj[3];
			this.depotNum = obj[4];
			return true;
		}
		else{
			return false;
		} 
	}
	
	public String getDepotDtId() {
		return depotDtId;
	}
	public String getOrderCd() {
		return orderCd;
	}

	public String getCoLoader() {
		return coLoader;
	}

	public String getDepotNum() {
		return depotNum;
	}

}
