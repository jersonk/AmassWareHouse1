package com.amassfreight.base.bean;

public class BarCode04 {

	private String containerCd;       //箱号

	public boolean paserBarCode(String barCode) {	
		String[] obj=barCode.split(",",-1);
		if("04".equals(obj[0]) && obj.length == 2){
			this.containerCd = obj[1];
			return true;
		}
		else{
			return false;
		} 
	}
	
	public String getContainerCd() {
		return containerCd;
	}
}
