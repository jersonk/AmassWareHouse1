package com.amassfreight.domain;

import java.util.Date;

public class RC001TDelivDetail {
	private String deliv_id;
	private String rownum;
	private String depotDtId;
	private String depotId;
	private String cdOrder;
	private String coloader;
	private String cdOrderPublic;
	private int noBatch;
	private String noPilecard;
	private String idDelivman;
	private String nmDelivman;
	private Date dtDeliv;
	private int countsDeliv;
	private boolean flgDeliv;
	private int delivnum;    //已放货件数
	private int delivCounts; //发货指示件数

	private double deliv_kgs;
	private double deliv_cbm;
	private String marks;

	public String getDeliv_id() {
		return deliv_id;
	}

	public void setDeliv_id(String deliv_id) {
		this.deliv_id = deliv_id;
	}

	public int getDelivnum() {
		return delivnum;
	}

	public void setDelivnum(int delivnum) {
		this.delivnum = delivnum;
	}

	public String getColoader() {
		return coloader;
	}

	public void setColoader(String coloader) {
		this.coloader = coloader;
	}

	public String getDepotId() {
		return depotId;
	}

	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}

	public RC001TDelivDetail() {
		// TODO Auto-generated constructor stub
	}

	public String getRownum() {
		return rownum;
	}

	public void setRownum(String rownum) {
		this.rownum = rownum;
	}

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

	public String getCdOrderPublic() {
		return cdOrderPublic;
	}

	public void setCdOrderPublic(String cdOrderPublic) {
		this.cdOrderPublic = cdOrderPublic;
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

	public String getIdDelivman() {
		return idDelivman;
	}

	public void setIdDelivman(String idDelivman) {
		this.idDelivman = idDelivman;
	}

	public String getNmDelivman() {
		return nmDelivman;
	}

	public void setNmDelivman(String nmDelivman) {
		this.nmDelivman = nmDelivman;
	}

	public Date getDtDeliv() {
		return dtDeliv;
	}

	public void setDtDeliv(Date dtDeliv) {
		this.dtDeliv = dtDeliv;
	}

	public int getCountsDeliv() {
		return countsDeliv;
	}

	public void setCountsDeliv(int countsDeliv) {
		this.countsDeliv = countsDeliv;
	}

	public boolean isFlgDeliv() {
		return flgDeliv;
	}

	public void setFlgDeliv(boolean flgDeliv) {
		this.flgDeliv = flgDeliv;
	}

	public double getDeliv_kgs() {
		return deliv_kgs;
	}

	public void setDeliv_kgs(double deliv_kgs) {
		this.deliv_kgs = deliv_kgs;
	}

	public double getDeliv_cbm() {
		return deliv_cbm;
	}

	public void setDeliv_cbm(double deliv_cbm) {
		this.deliv_cbm = deliv_cbm;
	}

	public String getMarks() {
		return marks;
	}

	public void setMarks(String marks) {
		this.marks = marks;
	}

	public int getDelivCounts() {
		return delivCounts;
	}

	public void setDelivCounts(int delivCounts) {
		this.delivCounts = delivCounts;
	}

}
