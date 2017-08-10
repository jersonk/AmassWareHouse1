package com.amassfreight.base.bean;

/**
 * 封装扫码获取的二维码信息bean
 * @author U11001548
 *
 */
public class BarCode01 {

	private String depotId;           //进仓id
	private String orderCd;           //进仓编号
	private String coLoader;          //同行编号

	public boolean paserBarCode(String barCode) {
		String[] obj=barCode.split(",",-1);
		// 解决同行编号中带逗号的情况 modify by sdhuang 2014-12-11
		//if("01".equals(obj[0]) && obj.length == 4){
		if("01".equals(obj[0]) && obj.length >= 4){
			this.depotId = obj[1];
			this.orderCd = obj[2];
			this.coLoader = obj[3];
			return true;
		}
		else{
			return false;
		}
	}
	public String getDepotId() {
		return depotId;
	}

	public String getOrderCd() {
		return orderCd;
	}

	public String getCoLoader() {
		return coLoader;
	}

}
