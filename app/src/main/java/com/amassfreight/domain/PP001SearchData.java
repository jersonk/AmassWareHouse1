package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class PP001SearchData implements Serializable {

	public PP001SearchData() {

	}

	private String noBox; // 集箱号
	private String packingName; // 装箱员
	private String cdBooking; // 航线
	private String stsPacking; // 装箱状态
	private String contaType; // 集箱类型
	private Date deadLineStart; // 最晚装箱时间
	private Date deadLineEnd; // 最晚装箱时间
	private String sort; // 排序
	private Date etdStart; // ETD
	private Date etdEnd; // ETD

	public Date getEtdStart() {
		return etdStart;
	}

	public void setEtdStart(Date etdStart) {
		this.etdStart = etdStart;
	}

	public Date getEtdEnd() {
		return etdEnd;
	}

	public void setEtdEnd(Date etdEnd) {
		this.etdEnd = etdEnd;
	}

	public Date getDeadLineStart() {
		return deadLineStart;
	}

	public void setDeadLineStart(Date deadLineStart) {
		this.deadLineStart = deadLineStart;
	}

	public Date getDeadLineEnd() {
		return deadLineEnd;
	}

	public void setDeadLineEnd(Date deadLineEnd) {
		this.deadLineEnd = deadLineEnd;
	}

	public String getNoBox() {
		return noBox;
	}

	public void setNoBox(String noBox) {
		this.noBox = noBox;
	}

	public String getPackingName() {
		return packingName;
	}

	public void setPackingName(String packingName) {
		this.packingName = packingName;
	}

	public String getCdBooking() {
		return cdBooking;
	}

	public void setCdBooking(String cdBooking) {
		this.cdBooking = cdBooking;
	}

	public String getStsPacking() {
		return stsPacking;
	}

	public void setStsPacking(String stsPacking) {
		this.stsPacking = stsPacking;
	}

	public String getContaType() {
		return contaType;
	}

	public void setContaType(String contaType) {
		this.contaType = contaType;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
}
