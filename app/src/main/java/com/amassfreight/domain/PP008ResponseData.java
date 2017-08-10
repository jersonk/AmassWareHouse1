package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class PP008ResponseData implements Serializable {

	public PP008ResponseData() {
	}

	private String noBox; // 集箱号
	private String stspacking; // 装箱状态
	private String dtPackingDeadline; // 装箱最晚完成时间
	private String opWhRemark; // 装箱规定
	private int totalCounts; // 合计件数
	private Double totalWeight; // 合计重量
	private Double totalVolume; // 合计体积
	private int totalRealCounts; // 实装总件数
	private boolean flgLockContainer; // 配柜完成标志
	private boolean flgLockBox; // 装箱锁定标志
	private boolean bolShowComplete;
	private boolean bolRoleShow;
	private int flgCompulsory;
	private String oprUserName;
	private String planner;//计划员
	private int flgPresent;
	
	public String getOprUserName() {
		return oprUserName;
	}

	public void setOprUserName(String oprUserName) {
		this.oprUserName = oprUserName;
	}

	public int getFlgCompulsory() {
		return flgCompulsory;
	}

	public void setFlgCompulsory(int flgCompulsory) {
		this.flgCompulsory = flgCompulsory;
	}

	private boolean bolRoleCancelShow;

	public boolean isBolRoleCancelShow() {
		return bolRoleCancelShow;
	}

	public void setBolRoleCancelShow(boolean bolRoleCancelShow) {
		this.bolRoleCancelShow = bolRoleCancelShow;
	}

	public boolean isBolRoleShow() {
		return bolRoleShow;
	}

	public void setBolRoleShow(boolean bolRoleShow) {
		this.bolRoleShow = bolRoleShow;
	}

	public boolean isBolShowComplete() {
		return bolShowComplete;
	}

	public void setBolShowComplete(boolean bolShowComplete) {
		this.bolShowComplete = bolShowComplete;
	}

	private List<PP008FclData> pp008FclList;

	private List<FileManageData> fileList;

	public List<FileManageData> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileManageData> fileList) {
		this.fileList = fileList;
	}

	public List<PP008FclData> getPp008FclList() {
		return pp008FclList;
	}

	public void setPp008FclList(List<PP008FclData> pp008FclList) {
		this.pp008FclList = pp008FclList;
	}

	public String getNoBox() {
		return noBox;
	}

	public void setNoBox(String noBox) {
		this.noBox = noBox;
	}

	public String getStspacking() {
		return stspacking;
	}

	public void setStspacking(String stspacking) {
		this.stspacking = stspacking;
	}

	public String getDtPackingDeadline() {
		return dtPackingDeadline;
	}

	public void setDtPackingDeadline(String dtPackingDeadline) {
		this.dtPackingDeadline = dtPackingDeadline;
	}

	public String getOpWhRemark() {
		return opWhRemark;
	}

	public void setOpWhRemark(String opWhRemark) {
		this.opWhRemark = opWhRemark;
	}

	public int getTotalCounts() {
		return totalCounts;
	}

	public void setTotalCounts(int totalCounts) {
		this.totalCounts = totalCounts;
	}

	public Double getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(Double totalWeight) {
		this.totalWeight = totalWeight;
	}

	public Double getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
	}

	public int getTotalRealCounts() {
		return totalRealCounts;
	}

	public void setTotalRealCounts(int totalRealCounts) {
		this.totalRealCounts = totalRealCounts;
	}

	public boolean isFlgLockContainer() {
		return flgLockContainer;
	}

	public void setFlgLockContainer(boolean flgLockContainer) {
		this.flgLockContainer = flgLockContainer;
	}

	public boolean isFlgLockBox() {
		return flgLockBox;
	}

	public void setFlgLockBox(boolean flgLockBox) {
		this.flgLockBox = flgLockBox;
	}

	public String getPlanner() {
		return planner;
	}

	public void setPlanner(String planner) {
		this.planner = planner;
	}

	public int getFlgPresent() {
		return flgPresent;
	}

	public void setFlgPresent(int flgPresent) {
		this.flgPresent = flgPresent;
	}

}
