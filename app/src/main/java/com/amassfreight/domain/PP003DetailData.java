package com.amassfreight.domain;

import java.util.Date;

public class PP003DetailData {

	public PP003DetailData(){
		
	}
	private String vesselNm;
	private String fdestNm;
	private String boxNo;
	private String boxNm;
	private String mblCd;
	private String containerCd;
	private String operatorNm;
	private String portarea;
	private Date packingDeadLine;
	private Date etd;
	
	public String getVesselNm() {
		return vesselNm;
	}
	public void setVesselNm(String vesselNm) {
		this.vesselNm = vesselNm;
	}
	public String getFdestNm() {
		return fdestNm;
	}
	public void setFdestNm(String fdestNm) {
		this.fdestNm = fdestNm;
	}
	public String getBoxNo() {
		return boxNo;
	}
	public void setBoxNo(String boxNo) {
		this.boxNo = boxNo;
	}
	public String getBoxNm() {
		return boxNm;
	}
	public void setBoxNm(String boxNm) {
		this.boxNm = boxNm;
	}
	public String getMblCd() {
		return mblCd;
	}
	public void setMblCd(String mblCd) {
		this.mblCd = mblCd;
	}
	public String getContainerCd() {
		return containerCd;
	}
	public void setContainerCd(String containerCd) {
		this.containerCd = containerCd;
	}
	public String getOperatorNm() {
		return operatorNm;
	}
	public void setOperatorNm(String operatorNm) {
		this.operatorNm = operatorNm;
	}
	public String getPortarea() {
		return portarea;
	}
	public void setPortarea(String portarea) {
		this.portarea = portarea;
	}
	public Date getPackingDeadLine() {
		return packingDeadLine;
	}
	public void setPackingDeadLine(Date packingDeadLine) {
		this.packingDeadLine = packingDeadLine;
	}
	public Date getEtd() {
		return etd;
	}
	public void setEtd(Date etd) {
		this.etd = etd;
	}

}
