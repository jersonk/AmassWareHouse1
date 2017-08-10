package com.amassfreight.domain;

import java.io.Serializable;

public class DN004TLoad implements Serializable {
	public DN004TLoad() {
	}

	private String depotID; // 进仓ID
	private int noBatch; // 批次
	private String workerID; // 员工编号
	private String truckID; // 铲车工编号
	private String truckNM; // 铲车工名称

	public String getDepotID() {
		return depotID;
	}

	public void setDepotID(String depotID) {
		this.depotID = depotID;
	}

	public int getNoBatch() {
		return noBatch;
	}

	public void setNoBatch(int noBatch) {
		this.noBatch = noBatch;
	}

	public String getWorkerID() {
		return workerID;
	}

	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}

	public String getTruckID() {
		return truckID;
	}

	public void setTruckID(String truckID) {
		this.truckID = truckID;
	}

	public String getTruckNM() {
		return truckNM;
	}

	public void setTruckNM(String truckNM) {
		this.truckNM = truckNM;
	}
}
