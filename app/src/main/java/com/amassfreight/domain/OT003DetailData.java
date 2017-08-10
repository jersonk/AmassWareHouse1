package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;

// 显示的列表信息
public class OT003DetailData implements Serializable {
	private String depotDtId;  		// 货物明细Id
	private String depotId;  		// 进仓Id
	private String cdOrder;	  		// 进仓编号个别
	private String cdOrderPublic;	// 进仓编号共通
	private String coLoader;   		// 同行编号
	private String batchNo;    		// 批次
	private String pilecardNo; 		// 桩脚牌Id
	private String pos;             // 库区
	private String location;        // 库位
	private String num;			    // 实际库存
	private String kgs;				// 重量
	private String cbm;				// 体积
	private String createDate;      // 创建日期   add by yxq 2014/10/22
	private int stockDiffNum;       // 盘点差异表记录数
	private String idStocktaking;	// 盘点人ID
	private String nmStocktaking;	// 盘点人姓名
	private String dtStocktaking;		// 盘点时间
	
	private double length;
	private double width;
	private double height;
	
	public String getDepotDtId() {
		return depotDtId;
	}
	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}
	public String getCdOrder() {
		return cdOrder;
	}
	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}
	public String getCoLoader() {
		return coLoader;
	}
	public void setCoLoader(String coLoader) {
		this.coLoader = coLoader;
	}
	public String getBatchNo() {
		return batchNo;
	}
	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}
	public String getPilecardNo() {
		return pilecardNo;
	}
	public void setPilecardNo(String pilecardNo) {
		this.pilecardNo = pilecardNo;
	}
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getKgs() {
		return kgs;
	}
	public void setKgs(String kgs) {
		this.kgs = kgs;
	}
	public String getCbm() {
		return cbm;
	}
	public void setCbm(String cbm) {
		this.cbm = cbm;
	}
	public String getDepotId() {
		return depotId;
	}
	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}
	public String getCdOrderPublic() {
		return cdOrderPublic;
	}
	public void setCdOrderPublic(String cdOrderPublic) {
		this.cdOrderPublic = cdOrderPublic;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public int getStockDiffNum() {
		return stockDiffNum;
	}
	public void setStockDiffNum(int stockDiffNum) {
		this.stockDiffNum = stockDiffNum;
	}
	public String getIdStocktaking() {
		return idStocktaking;
	}
	public void setIdStocktaking(String idStocktaking) {
		this.idStocktaking = idStocktaking;
	}
	public String getNmStocktaking() {
		return nmStocktaking;
	}
	public void setNmStocktaking(String nmStocktaking) {
		this.nmStocktaking = nmStocktaking;
	}
	public String getDtStocktaking() {
		return dtStocktaking;
	}
	public void setDtStocktaking(String dtStocktaking) {
		this.dtStocktaking = dtStocktaking;
	}
}
