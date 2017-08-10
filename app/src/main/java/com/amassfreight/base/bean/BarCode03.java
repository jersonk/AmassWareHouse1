package com.amassfreight.base.bean;

public class BarCode03 {

	private String delivId;           //放货编号

	public boolean paserBarCode(String barCode) {	
		String[] obj=barCode.split(",",-1);
		if("03".equals(obj[0]) && obj.length == 2){
			this.delivId = obj[1];
			return true;
		}
		else{
			return false;
		} 
	}
	
	public String getDelivId() {
		return delivId;
	}

}
