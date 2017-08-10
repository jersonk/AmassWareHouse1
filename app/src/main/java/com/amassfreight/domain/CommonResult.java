package com.amassfreight.domain;

public class CommonResult {

	public CommonResult(){
		
	}
	private Boolean flag;
	private String errorCd;
	private String depotDtId;
	private String remark;
	
	public Boolean getFlag() {
		return flag;
	}
	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	public String getErrorCd() {
		return errorCd;
	}
	public void setErrorCd(String errorCd) {
		this.errorCd = errorCd;
	}
	public String getDepotDtId() {
		return depotDtId;
	}
	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
}
