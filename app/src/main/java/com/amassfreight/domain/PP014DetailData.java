package com.amassfreight.domain;

import java.util.Date;
import java.util.List;

public class PP014DetailData {

	public PP014DetailData(){
		
	}
	private String boxNo;
	private Date packingDeadline;
	private String packingRemarks;
	private Boolean containerLockFlg;
	private List<UnPackingList> unPackingList;
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public Date getPackingDeadline() {
		return packingDeadline;
	}
	public void setPackingDeadline(Date packingDeadline) {
		this.packingDeadline = packingDeadline;
	}
	public String getPackingRemarks() {
		return packingRemarks;
	}
	public void setPackingRemarks(String packingRemarks) {
		this.packingRemarks = packingRemarks;
	}
	public List<UnPackingList> getUnPackingList() {
		return unPackingList;
	}
	public void setUnPackingList(List<UnPackingList> unPackingList) {
		this.unPackingList = unPackingList;
	}
	public Boolean getContainerLockFlg() {
		return containerLockFlg;
	}
	public void setContainerLockFlg(Boolean containerLockFlg) {
		this.containerLockFlg = containerLockFlg;
	}
}
