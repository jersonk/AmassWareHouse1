package com.amassfreight.domain;

import java.io.Serializable;
import java.util.List;

import javax.security.auth.PrivateCredentialPermission;

public class DepotPP010 implements Serializable {

	public DepotPP010() {
	}

	private String cdOrder; // 进仓编号
	private int noBatch;// 批次
	private String noPilecard;// 桩脚牌
	private Double noLen;// 长
	private Double noWidth;// 宽
	private Double noHeight;// 高
	private String depotPackage;// 包装单位
	private int stockNum;// 库存件数
	private Boolean bolLenhColor;// 显示颜色
	private Boolean bolWidthColor;// 显示颜色
	private Boolean bolHeighthColor;
	private Boolean bolIsPacked;

	public Boolean getBolIsPacked() {
		return bolIsPacked;
	}

	public void setBolIsPacked(Boolean bolIsPacked) {
		this.bolIsPacked = bolIsPacked;
	}

	public Boolean getBolLenhColor() {
		return bolLenhColor;
	}

	public void setBolLenhColor(Boolean bolLenhColor) {
		this.bolLenhColor = bolLenhColor;
	}

	public Boolean getBolWidthColor() {
		return bolWidthColor;
	}

	public void setBolWidthColor(Boolean bolWidthColor) {
		this.bolWidthColor = bolWidthColor;
	}

	public Boolean getBolHeighthColor() {
		return bolHeighthColor;
	}

	public void setBolHeighthColor(Boolean bolHeighthColor) {
		this.bolHeighthColor = bolHeighthColor;
	}

	public String getCdOrder() {
		return cdOrder;
	}

	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}

	public int getNoBatch() {
		return noBatch;
	}

	public void setNoBatch(int noBatch) {
		this.noBatch = noBatch;
	}

	public String getNoPilecard() {
		return noPilecard;
	}

	public void setNoPilecard(String noPilecard) {
		this.noPilecard = noPilecard;
	}

	public Double getNoLen() {
		return noLen;
	}

	public void setNoLen(Double noLen) {
		this.noLen = noLen;
	}

	public Double getNoWidth() {
		return noWidth;
	}

	public void setNoWidth(Double noWidth) {
		this.noWidth = noWidth;
	}

	public Double getNoHeight() {
		return noHeight;
	}

	public void setNoHeight(Double noHeight) {
		this.noHeight = noHeight;
	}

	public String getDepotPackage() {
		return depotPackage;
	}

	public void setDepotPackage(String depotPackage) {
		this.depotPackage = depotPackage;
	}

	public int getStockNum() {
		return stockNum;
	}

	public void setStockNum(int stockNum) {
		this.stockNum = stockNum;
	}
}
