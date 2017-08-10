package com.amassfreight.domain;

public class PP005StatusData {

	public PP005StatusData(){
		
	}

	public Boolean updatePickStatus;     //拣货状态是否更新成功标志
	public String errorCd;               //错误编号
	public String orderCd;               //进仓编号
	public String depotDtId;
	
	private boolean flgLockScan;
	private String lockScanRemark;
    
	public Boolean getUpdatePickStatus() {
		return updatePickStatus;
	}
	public void setUpdatePickStatus(Boolean updatePickStatus) {
		this.updatePickStatus = updatePickStatus;
	}
	public String getErrorCd() {
		return errorCd;
	}
	public void setErrorCd(String errorCd) {
		this.errorCd = errorCd;
	}
	public String getOrderCd() {
		return orderCd;
	}
	public void setOrderCd(String orderCd) {
		this.orderCd = orderCd;
	}
	public String getDepotDtId() {
		return depotDtId;
	}
	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}
	public boolean isFlgLockScan() {
		return flgLockScan;
	}
	public void setFlgLockScan(boolean flgLockScan) {
		this.flgLockScan = flgLockScan;
	}
	public String getLockScanRemark() {
		return lockScanRemark;
	}
	public void setLockScanRemark(String lockScanRemark) {
		this.lockScanRemark = lockScanRemark;
	}


}
