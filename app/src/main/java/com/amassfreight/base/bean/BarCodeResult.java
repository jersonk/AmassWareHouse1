package com.amassfreight.base.bean;

public class BarCodeResult {
	private Boolean success;          //扫描是否正确
	private String type;              //条码类型
	private Object barCode;           //条码内容

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Object getBarCode() {
		return barCode;
	}
	public void setBarCode(Object barCode) {
		this.barCode = barCode;
	}
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	
	
}
