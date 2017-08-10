package com.amassfreight.base.bean;

public class BarCode05 {

	private String boxNo;             //集箱号
	
	public boolean paserBarCode(String barCode) {	
		String[] obj=barCode.split(",",-1);
		if("05".equals(obj[0]) && obj.length == 2){
			this.boxNo = obj[1];
			return true;
		}
		else{
			return false;
		} 
	}
	
	public String getBoxNo() {
		return boxNo;
	}

}
