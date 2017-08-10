package com.amassfreight.domain;

import java.io.Serializable;

public class RC001PileCard implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rownum; //
	private String depot_dt_id;// 货物明细id
	private String depot_id;// 进仓id
	private String co_loader;// 同行编号
	private String no_batch;// 批次
	private String no_pilecard; // 装脚牌ID
	private int counts_deliv;// 放货件数
	private int flg_deliv;// 放货标记，放货状态
	private String kucunCount;// 库存件数
	private String deliv_num; //已放货件数
	private String cd_order_public;// 进仓编号
	private String cd_order;
	private String posAndLoc;

	private double kgs;
	private double cbm;
	private String marks;
	private double length;
	private double width;
	private double height;
	
	private String workerId;  //搬运工
	private String cargadorId;//装卸工
	
	private boolean printChecked;//是否打印标志

	public String getCo_loader() {
		return co_loader;
	}

	public void setCo_loader(String co_loader) {
		this.co_loader = co_loader;
	}

	public String getRownum() {
		return rownum;
	}

	public void setRownum(String rownum) {
		this.rownum = rownum;
	}

	public String getDepot_dt_id() {
		return depot_dt_id;
	}

	public void setDepot_dt_id(String depot_dt_id) {
		this.depot_dt_id = depot_dt_id;
	}

	public String getDepot_id() {
		return depot_id;
	}

	public void setDepot_id(String depot_id) {
		this.depot_id = depot_id;
	}

	public String getCd_order_public() {
		return cd_order_public;
	}

	public void setCd_order_public(String cd_order_public) {
		this.cd_order_public = cd_order_public;
	}

	public RC001PileCard() {
		// TODO Auto-generated constructor stub
	}

	public String getNo_batch() {
		return no_batch;
	}

	public void setNo_batch(String no_batch) {
		this.no_batch = no_batch;
	}

	public String getNo_pilecard() {
		return no_pilecard;
	}

	public void setNo_pilecard(String no_pilecard) {
		this.no_pilecard = no_pilecard;
	}

	public int getCounts_deliv() {
		return counts_deliv;
	}

	public void setCounts_deliv(int counts_deliv) {
		this.counts_deliv = counts_deliv;
	}

	public int getFlg_deliv() {
		return flg_deliv;
	}

	public void setFlg_deliv(int flg_deliv) {
		this.flg_deliv = flg_deliv;
	}

	public String getKucunCount() {
		return kucunCount;
	}

	public void setKucunCount(String kucunCount) {
		this.kucunCount = kucunCount;
	}

	public double getKgs() {
		return kgs;
	}

	public void setKgs(double kgs) {
		this.kgs = kgs;
	}

	public double getCbm() {
		return cbm;
	}

	public void setCbm(double cbm) {
		this.cbm = cbm;
	}

	public String getMarks() {
		return marks;
	}

	public void setMarks(String marks) {
		this.marks = marks;
	}

	public String getCd_order() {
		return cd_order;
	}

	public void setCd_order(String cd_order) {
		this.cd_order = cd_order;
	}

	public String getDeliv_num() {
		return deliv_num;
	}

	public void setDeliv_num(String deliv_num) {
		this.deliv_num = deliv_num;
	}

	public String getPosAndLoc() {
		return posAndLoc;
	}

	public void setPosAndLoc(String posAndLoc) {
		this.posAndLoc = posAndLoc;
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

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getCargadorId() {
		return cargadorId;
	}

	public void setCargadorId(String cargadorId) {
		this.cargadorId = cargadorId;
	}

	public boolean isPrintChecked() {
		return printChecked;
	}

	public void setPrintChecked(boolean printChecked) {
		this.printChecked = printChecked;
	}


}