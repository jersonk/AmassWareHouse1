package com.amassfreight.domain;

public class RC002TLoadWorkers {

	public RC002TLoadWorkers(){
		
	}
	private String cargador_LoadId;
	private String forklift_LoadId;
	private String tallyman_LoadId;
	private Boolean flag;
	private String errorCd;
	public String getCargador_LoadId() {
		return cargador_LoadId;
	}
	public void setCargador_LoadId(String cargador_LoadId) {
		this.cargador_LoadId = cargador_LoadId;
	}
	public String getForklift_LoadId() {
		return forklift_LoadId;
	}
	public void setForklift_LoadId(String forklift_LoadId) {
		this.forklift_LoadId = forklift_LoadId;
	}
	public String getTallyman_LoadId() {
		return tallyman_LoadId;
	}
	public void setTallyman_LoadId(String tallyman_LoadId) {
		this.tallyman_LoadId = tallyman_LoadId;
	}
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
	

}
