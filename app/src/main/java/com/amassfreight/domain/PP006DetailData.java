package com.amassfreight.domain;

import java.util.Date;
import java.util.List;

public class PP006DetailData {

	public PP006DetailData(){
		
	}
	private String boxNo;
	private Integer packingStatus;
	private Date packingDeadline;
	private String packingRemarks;
	private Boolean containerLockFlg;
	private String containerCd;
	private Boolean unPackingFlg;
	private Boolean allPackedFlg;
	private Boolean cancelPackedFlg;
	private String planner;
	private String statusNow;
	private Boolean packCompelFlg;
	private int flgPresent;
	private List<OrderListResponseData> orderList;
	
	public String getBoxNo() {
		return boxNo;
	}

	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}

	public Integer getPackingStatus() {
		return packingStatus;
	}

	public void setPackingStatus(Integer packingStatus) {
		this.packingStatus = packingStatus;
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

	public Boolean getContainerLockFlg() {
		return containerLockFlg;
	}

	public void setContainerLockFlg(Boolean containerLockFlg) {
		this.containerLockFlg = containerLockFlg;
	}

	public String getContainerCd() {
		return containerCd;
	}

	public void setContainerCd(String containerCd) {
		this.containerCd = containerCd;
	}

	public Boolean getUnPackingFlg() {
		return unPackingFlg;
	}

	public void setUnPackingFlg(Boolean unPackingFlg) {
		this.unPackingFlg = unPackingFlg;
	}

	public Boolean getAllPackedFlg() {
		return allPackedFlg;
	}

	public void setAllPackedFlg(Boolean allPackedFlg) {
		this.allPackedFlg = allPackedFlg;
	}

	public List<OrderListResponseData> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<OrderListResponseData> orderList) {
		this.orderList = orderList;
	}

	public Boolean getCancelPackedFlg() {
		return cancelPackedFlg;
	}

	public void setCancelPackedFlg(Boolean cancelPackedFlg) {
		this.cancelPackedFlg = cancelPackedFlg;
	}

	public String getPlanner() {
		return planner;
	}

	public void setPlanner(String planner) {
		this.planner = planner;
	}

	public String getStatusNow() {
		return statusNow;
	}

	public void setStatusNow(String statusNow) {
		this.statusNow = statusNow;
	}

	public Boolean getPackCompelFlg() {
		return packCompelFlg;
	}

	public void setPackCompelFlg(Boolean packCompelFlg) {
		this.packCompelFlg = packCompelFlg;
	}

	public int getFlgPresent() {
		return flgPresent;
	}

	public void setFlgPresent(int flgPresent) {
		this.flgPresent = flgPresent;
	}

}
