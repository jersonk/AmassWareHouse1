package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class PP009ResponseData implements Serializable {

	public PP009ResponseData() {
	}

	private String cdBusinessPublic; // 进仓编号
	private String cdBusiness; // 进仓编号
	private Boolean flgQuit; // 退关标志
	private int qtNum; // 原件数
	private int counts; // 本箱件数
	private Double weight; // 重量
	private Double volume; // 体积
	private String remark; // 计划备注
	private String matchId;
	private String cdBooking;

	public String getCdBooking() {
		return cdBooking;
	}

	public void setCdBooking(String cdBooking) {
		this.cdBooking = cdBooking;
	}

	public String getMatchId() {
		return matchId;
	}

	public void setMatchId(String matchId) {
		this.matchId = matchId;
	}

	private List<PP009DepotData> pp009DepList;

	public List<PP009DepotData> getPp009DepList() {
		return pp009DepList;
	}

	public void setPp009DepList(List<PP009DepotData> pp009DepList) {
		this.pp009DepList = pp009DepList;
	}

	private List<FileManageData> fileList;

	public List<FileManageData> getFileList() {
		return fileList;
	}

	public void setFileList(List<FileManageData> fileList) {
		this.fileList = fileList;
	}

	public String getCdBusinessPublic() {
		return cdBusinessPublic;
	}

	public void setCdBusinessPublic(String cdBusinessPublic) {
		this.cdBusinessPublic = cdBusinessPublic;
	}

	public String getCdBusiness() {
		return cdBusiness;
	}

	public void setCdBusiness(String cdBusiness) {
		this.cdBusiness = cdBusiness;
	}

	public Boolean getFlgQuit() {
		return flgQuit;
	}

	public void setFlgQuit(Boolean flgQuit) {
		this.flgQuit = flgQuit;
	}

	public int getQtNum() {
		return qtNum;
	}

	public void setQtNum(int qtNum) {
		this.qtNum = qtNum;
	}

	public int getCounts() {
		return counts;
	}

	public void setCounts(int counts) {
		this.counts = counts;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Double getVolume() {
		return volume;
	}

	public void setVolume(Double volume) {
		this.volume = volume;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
