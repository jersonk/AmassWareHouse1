package com.amassfreight.domain;

import java.math.BigDecimal;
import java.util.List;

public class PP005ResponseData {
	
	public PP005ResponseData(){
		
	}

	private String orderCdPrivate;           //进仓编号private
	private String orderCd;           //进仓编号public
	private String shutoutFlag;       //是否退关
	private String preconfigureNum;   //预配件数
	private String paidinNum;         //实收件数
	private BigDecimal preconfigureCube;  //预配立方
	private BigDecimal paidinCube;        //实收立方
	private String numRemark;         //件数备注
	private String preconfigureWeight;//预配重量
	private String planRemark;        //计划备注
	private String packingRequire;    //装箱要求
	private String workStatus;        //单票状态
	private Boolean auth;             //主管权限
	private int realPackingNum;       //实际配置的装箱件数
	
	private List<PileCardResponseData> pileCardList; //桩脚牌一览
	private List<DepotVas> depotVasList;

	public String getOrderCd() {
		return orderCd;
	}

	public void setOrderCd(String orderCd) {
		this.orderCd = orderCd;
	}

	public String getShutoutFlag() {
		return shutoutFlag;
	}

	public void setShutoutFlag(String shutoutFlag) {
		this.shutoutFlag = shutoutFlag;
	}

	public String getPreconfigureNum() {
		return preconfigureNum;
	}

	public void setPreconfigureNum(String preconfigureNum) {
		this.preconfigureNum = preconfigureNum;
	}

	public String getPaidinNum() {
		return paidinNum;
	}

	public void setPaidinNum(String paidinNum) {
		this.paidinNum = paidinNum;
	}

	public BigDecimal getPreconfigureCube() {
		return preconfigureCube;
	}

	public void setPreconfigureCube(BigDecimal preconfigureCube) {
		this.preconfigureCube = preconfigureCube;
	}

	public BigDecimal getPaidinCube() {
		return paidinCube;
	}

	public void setPaidinCube(BigDecimal paidinCube) {
		this.paidinCube = paidinCube;
	}

	public String getNumRemark() {
		return numRemark;
	}

	public void setNumRemark(String numRemark) {
		this.numRemark = numRemark;
	}

	public String getPreconfigureWeight() {
		return preconfigureWeight;
	}

	public void setPreconfigureWeight(String preconfigureWeight) {
		this.preconfigureWeight = preconfigureWeight;
	}

	public String getPlanRemark() {
		return planRemark;
	}

	public void setPlanRemark(String planRemark) {
		this.planRemark = planRemark;
	}

	public String getPackingRequire() {
		return packingRequire;
	}

	public void setPackingRequire(String packingRequire) {
		this.packingRequire = packingRequire;
	}

	public List<PileCardResponseData> getPileCardList() {
		return pileCardList;
	}

	public void setPileCardList(List<PileCardResponseData> pileCardList) {
		this.pileCardList = pileCardList;
	}

	public String getOrderCdPrivate() {
		return orderCdPrivate;
	}

	public void setOrderCdPrivate(String orderCdPrivate) {
		this.orderCdPrivate = orderCdPrivate;
	}

	public String getWorkStatus() {
		return workStatus;
	}

	public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}

	public Boolean getAuth() {
		return auth;
	}

	public void setAuth(Boolean auth) {
		this.auth = auth;
	}

	public List<DepotVas> getDepotVasList() {
		return depotVasList;
	}

	public void setDepotVasList(List<DepotVas> depotVasList) {
		this.depotVasList = depotVasList;
	}

	public int getRealPackingNum() {
		return realPackingNum;
	}

	public void setRealPackingNum(int realPackingNum) {
		this.realPackingNum = realPackingNum;
	}

}
