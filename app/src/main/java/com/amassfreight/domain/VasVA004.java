package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.amassfreight.utils.Utils;

public class VasVA004 implements Serializable {

	public VasVA004() {
	}

	private String vasExpensesDtId;	//增值服务费用明细ID
	private String vasId; // 增值服务费用ID
	private String vasNmChn; // 增值服务科目（中文名）
	private Boolean vasdefault; // 默认科目
	private Double quantity; // 数量
	private Boolean isSelect; // 是否选择
	private int flgAppoval; // 审核标识
	private Double vasPrice; // 单价
	private String vasUnit; // 单位
	private Integer vasBelong; // 耗材所属
	private Double weight;//耗材重量
	private Boolean flgWeight;//耗材是否要计算重量
	

	public Double getVasPrice() {
		return vasPrice;
	}

	public void setVasPrice(Double vasPrice) {
		this.vasPrice = vasPrice;
	}

	public String getVasUnit() {
		return vasUnit;
	}

	public void setVasUnit(String vasUnit) {
		this.vasUnit = vasUnit;
	}

	public String getVasId() {
		return vasId;
	}

	public void setVasId(String vasId) {
		this.vasId = vasId;
	}

	public String getVasNmChn() {
		return vasNmChn;
	}

	public void setVasNmChn(String vasNmChn) {
		this.vasNmChn = vasNmChn;
	}

	public Boolean getVasdefault() {
		return vasdefault;
	}

	public void setVasdefault(Boolean vasdefault) {
		this.vasdefault = vasdefault;
	}

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Boolean getIsSelect() {
		return isSelect;
	}

	public void setIsSelect(Boolean isSelect) {
		this.isSelect = isSelect;
	}

	public int getFlgAppoval() {
		return flgAppoval;
	}

	public void setFlgAppoval(int flgAppoval) {
		this.flgAppoval = flgAppoval;
	}

	public String getVasExpensesDtId() {
		return vasExpensesDtId;
	}

	public void setVasExpensesDtId(String vasExpensesDtId) {
		this.vasExpensesDtId = vasExpensesDtId;
	}

	public Integer getVasBelong() {
		return vasBelong;
	}

	public void setVasBelong(Integer vasBelong) {
		this.vasBelong = vasBelong;
	}

	public Double getWeight() {
		return weight;
	}

	public void setWeight(Double weight) {
		this.weight = weight;
	}

	public Boolean getFlgWeight() {
		return flgWeight;
	}

	public void setFlgWeight(Boolean flgWeight) {
		this.flgWeight = flgWeight;
	}

}
