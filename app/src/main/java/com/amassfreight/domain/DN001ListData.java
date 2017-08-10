package com.amassfreight.domain;

import java.io.Serializable;

public class DN001ListData implements Serializable {

	public DN001ListData() {
	}

	private String noBatch;
	private String nmWorker;
	private int sumNum;
	private double sumKgs;
	private double sumCbm;
	private boolean status;
	private boolean bolIsTally;

	public String getNoBatch() {
		return noBatch;
	}

	public void setNoBatch(String noBatch) {
		this.noBatch = noBatch;
	}

	public String getNmWorker() {
		return nmWorker;
	}

	public void setNmWorker(String nmWorker) {
		this.nmWorker = nmWorker;
	}

	public int getSumNum() {
		return sumNum;
	}

	public void setSumNum(int sumNum) {
		this.sumNum = sumNum;
	}

	public double getSumKgs() {
		return sumKgs;
	}

	public void setSumKgs(double sumKgs) {
		this.sumKgs = sumKgs;
	}

	public double getSumCbm() {
		return sumCbm;
	}

	public void setSumCbm(double sumCbm) {
		this.sumCbm = sumCbm;
	}

	public boolean getStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public boolean isBolIsTally() {
		return bolIsTally;
	}

	public void setBolIsTally(boolean bolIsTally) {
		this.bolIsTally = bolIsTally;
	}
}
