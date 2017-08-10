package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

public class RC001Deliver_batch implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5370407908789854717L;
	private String rownum;
	private String no_batch;// 批次
	private String cd_order_public;// 进仓编号
	private String depot_id;       //进仓ID
	private String fumigationStatus = "已放货";// 放货状态
	private String kucunCount;// 库存件数
	private List<RC001PileCard> pilecards;

	private boolean isChecked;

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}	

	public String getRownum() {
		return rownum;
	}

	public void setRownum(String rownum) {
		this.rownum = rownum;
	}

	public RC001Deliver_batch() {
		// TODO Auto-generated constructor stub
	}

	public String getNo_batch() {
		return no_batch;
	}

	public void setNo_batch(String no_batch) {
		this.no_batch = no_batch;
	}

	public String getCd_order_public() {
		return cd_order_public;
	}

	public void setCd_order_public(String cd_order_public) {
		this.cd_order_public = cd_order_public;
	}

	public List<RC001PileCard> getPilecards() {
		return pilecards;
	}

	public void setPilecards(List<RC001PileCard> pilecards) {
		this.pilecards = pilecards;
	}

	public String getFumigationStatus() {
		return fumigationStatus;
	}

	public void setFumigationStatus(String fumigationStatus) {
		this.fumigationStatus = fumigationStatus;
	}

	public String getKucunCount() {
		return kucunCount;
	}

	public void setKucunCount(String kucunCount) {
		this.kucunCount = kucunCount;
	}

	public String getDepot_id() {
		return depot_id;
	}

	public void setDepot_id(String depot_id) {
		this.depot_id = depot_id;
	}

}
