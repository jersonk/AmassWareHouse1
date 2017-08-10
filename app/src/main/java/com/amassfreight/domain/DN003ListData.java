package com.amassfreight.domain;

import java.io.Serializable;
import java.util.Date;

public class DN003ListData implements Serializable {
	public DN003ListData() {
	}

	private String depotId;
	private String cdException;
	private String nmTallyman;
	private Date dtInsert;
	private String flgStatus;
	private int bolStatus;
	private String nmApproval;
	private Date dtApproval;

	private boolean flgGuarLost; // 保函缺失标志
	private boolean flgGoodsDamage; // 严重破损标志
	private boolean flgDamp; // 受潮标志
	private boolean flgOilStained; // 漏油/油污标志
	private boolean flgOdors; // 有异味标记
	private boolean flgBark; // 有树皮标记
	private boolean flgDanger; // 危险品/化学品/易燃品标记
	private boolean flgUltrasize; // 超长/超宽/超高标记
	private boolean flgOther; // 其他异常标记
	private String descOther; // 其他异常描述
	private boolean flgLogNo;	//原木无章
	private boolean flgMarksPortDif; //唛头目的港不符
	private boolean flgCdOrderDif;	//进仓编号不符

	public String getDepotId() {
		return depotId;
	}

	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}

	public String getCdException() {
		return cdException;
	}

	public void setCdException(String cdException) {
		this.cdException = cdException;
	}

	public String getNmTallyman() {
		return nmTallyman;
	}

	public void setNmTallyman(String nmTallyman) {
		this.nmTallyman = nmTallyman;
	}

	public Date getDtInsert() {
		return dtInsert;
	}

	public void setDtInsert(Date dtInsert) {
		this.dtInsert = dtInsert;
	}

	public String getFlgStatus() {
		return flgStatus;
	}

	public void setFlgStatus(String flgStatus) {
		this.flgStatus = flgStatus;
	}

	public String getNmApproval() {
		return nmApproval;
	}

	public void setNmApproval(String nmApproval) {
		this.nmApproval = nmApproval;
	}

	public Date getDtApproval() {
		return dtApproval;
	}

	public void setDtApproval(Date dtApproval) {
		this.dtApproval = dtApproval;
	}

	public boolean isFlgGuarLost() {
		return flgGuarLost;
	}

	public void setFlgGuarLost(boolean flgGuarLost) {
		this.flgGuarLost = flgGuarLost;
	}

	public boolean isFlgGoodsDamage() {
		return flgGoodsDamage;
	}

	public void setFlgGoodsDamage(boolean flgGoodsDamage) {
		this.flgGoodsDamage = flgGoodsDamage;
	}

	public boolean isFlgDamp() {
		return flgDamp;
	}

	public void setFlgDamp(boolean flgDamp) {
		this.flgDamp = flgDamp;
	}

	public boolean isFlgOilStained() {
		return flgOilStained;
	}

	public void setFlgOilStained(boolean flgOilStained) {
		this.flgOilStained = flgOilStained;
	}

	public boolean isFlgOdors() {
		return flgOdors;
	}

	public void setFlgOdors(boolean flgOdors) {
		this.flgOdors = flgOdors;
	}

	public boolean isFlgBark() {
		return flgBark;
	}

	public void setFlgBark(boolean flgBark) {
		this.flgBark = flgBark;
	}

	public boolean isFlgDanger() {
		return flgDanger;
	}

	public void setFlgDanger(boolean flgDanger) {
		this.flgDanger = flgDanger;
	}

	public boolean isFlgUltrasize() {
		return flgUltrasize;
	}

	public void setFlgUltrasize(boolean flgUltrasize) {
		this.flgUltrasize = flgUltrasize;
	}

	public boolean isFlgOther() {
		return flgOther;
	}

	public void setFlgOther(boolean flgOther) {
		this.flgOther = flgOther;
	}

	public String getDescOther() {
		return descOther;
	}

	public void setDescOther(String descOther) {
		this.descOther = descOther;
	}

	public boolean isFlgLogNo() {
		return flgLogNo;
	}

	public void setFlgLogNo(boolean flgLogNo) {
		this.flgLogNo = flgLogNo;
	}

	public boolean isFlgMarksPortDif() {
		return flgMarksPortDif;
	}

	public void setFlgMarksPortDif(boolean flgMarksPortDif) {
		this.flgMarksPortDif = flgMarksPortDif;
	}

	public boolean isFlgCdOrderDif() {
		return flgCdOrderDif;
	}

	public void setFlgCdOrderDif(boolean flgCdOrderDif) {
		this.flgCdOrderDif = flgCdOrderDif;
	}

	public int getBolStatus() {
		return bolStatus;
	}

	public void setBolStatus(int bolStatus) {
		this.bolStatus = bolStatus;
	}
}
