package com.amassfreight.domain;


import java.io.Serializable;

public class VATVasExpensesDt implements Serializable {

	private String vasExpensesDtId;  //增值服务费用明细ID
	private String cdOrder; //进仓编号
	private String serviceId; // 增值服务ID
	private String vasId; // 增值服务费用ID
	private String vasChaName; // 增值服务科目(中文名)
	private Double varPrice; // 单价
	private Double varQuantity; // 数量
	private Double varWeight;	//重量
	private Double varAmount; // 总价
	private String varUit; // 单位
	private Integer vasBelong; // 耗材所属

	public String getCdOrder() {
		return cdOrder;
	}

	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getVasId() {
		return vasId;
	}

	public void setVasId(String vasId) {
		this.vasId = vasId;
	}

	public String getVasChaName() {
		return vasChaName;
	}

	public void setVasChaName(String vasChaName) {
		this.vasChaName = vasChaName;
	}

	public Double getVarPrice() {
		return varPrice;
	}

	public void setVarPrice(Double varPrice) {
		this.varPrice = varPrice;
	}

	public Double getVarQuantity() {
		return varQuantity;
	}

	public void setVarQuantity(Double varQuantity) {
		this.varQuantity = varQuantity;
	}

	public Double getVarAmount() {
		return varAmount;
	}

	public void setVarAmount(Double varAmount) {
		this.varAmount = varAmount;
	}

	public String getVarUit() {
		return varUit;
	}

	public void setVarUit(String varUit) {
		this.varUit = varUit;
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

	public Double getVarWeight() {
		return varWeight;
	}

	public void setVarWeight(Double varWeight) {
		this.varWeight = varWeight;
	}

}
