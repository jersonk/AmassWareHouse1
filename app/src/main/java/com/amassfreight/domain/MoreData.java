package com.amassfreight.domain;

public class MoreData {
	public static int DATA_TYPE= 1;
	public static int MORE_TYPE = 2;
	
	private int dataType;
	private Object data;
	private boolean checked;
	
	public MoreData(){
		dataType = MORE_TYPE;
	}
	public MoreData(Object data){
		dataType = DATA_TYPE;
		this.data = data;
	}
	public int getDataType() {
		return dataType;
	}
	public Object getData() {
		return data;
	}
	
	private boolean getDataing;

	public boolean isGetDataing() {
		return getDataing;
	}
	public void setGetDataing(boolean getDataing) {
		this.getDataing = getDataing;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
}
