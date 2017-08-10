package com.amassfreight.domain;

import java.io.Serializable;

public class PP013Data implements Serializable {
	public PP013Data() {
	}

	private String noBox; // 集箱号
	private boolean flgCheckOk; // 验箱正常标志
	private boolean flgCheckPollution; // 验箱污染标志
	private boolean flgCheckDamaged; // 验箱破损标志
	private boolean flgCheckDeform; // 验箱变形标志
	private String checkRemarks; // 验箱备注
	private String place;        // 位置
	private Double qtVgmKgsLeather;		//箱皮重

	public String getNoBox() {
		return noBox;
	}

	public void setNoBox(String noBox) {
		this.noBox = noBox;
	}

	public boolean isFlgCheckOk() {
		return flgCheckOk;
	}

	public void setFlgCheckOk(boolean flgCheckOk) {
		this.flgCheckOk = flgCheckOk;
	}

	public boolean isFlgCheckPollution() {
		return flgCheckPollution;
	}

	public void setFlgCheckPollution(boolean flgCheckPollution) {
		this.flgCheckPollution = flgCheckPollution;
	}

	public boolean isFlgCheckDamaged() {
		return flgCheckDamaged;
	}

	public void setFlgCheckDamaged(boolean flgCheckDamaged) {
		this.flgCheckDamaged = flgCheckDamaged;
	}

	public boolean isFlgCheckDeform() {
		return flgCheckDeform;
	}

	public void setFlgCheckDeform(boolean flgCheckDeform) {
		this.flgCheckDeform = flgCheckDeform;
	}

	public String getCheckRemarks() {
		return checkRemarks;
	}

	public void setCheckRemarks(String checkRemarks) {
		this.checkRemarks = checkRemarks;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public Double getQtVgmKgsLeather() {
		return qtVgmKgsLeather;
	}

	public void setQtVgmKgsLeather(Double qtVgmKgsLeather) {
		this.qtVgmKgsLeather = qtVgmKgsLeather;
	}

}
