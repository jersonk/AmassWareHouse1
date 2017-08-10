package com.amassfreight.domain;

import java.util.List;

public class VA003TransferData{

	public VA003TransferData(){
		
	}
	private String cdOrder;
	private String depotDtId;
	private String cdOrderTransfer;
	private String cdOrderPublicTransfer;
	private String depotDtIdTransfer;
	private String noBatchTransfer;
	private String pileCardTransfer;
	private String coLoaderTransfer;
	private String depotIdTransfer;
	
	private List<FileManageData> fileList;
	
	public String getCdOrder() {
		return cdOrder;
	}
	public void setCdOrder(String cdOrder) {
		this.cdOrder = cdOrder;
	}
	public String getDepotDtId() {
		return depotDtId;
	}
	public void setDepotDtId(String depotDtId) {
		this.depotDtId = depotDtId;
	}
	public String getCdOrderTransfer() {
		return cdOrderTransfer;
	}
	public void setCdOrderTransfer(String cdOrderTransfer) {
		this.cdOrderTransfer = cdOrderTransfer;
	}
	public String getCdOrderPublicTransfer() {
		return cdOrderPublicTransfer;
	}
	public void setCdOrderPublicTransfer(String cdOrderPublicTransfer) {
		this.cdOrderPublicTransfer = cdOrderPublicTransfer;
	}
	public String getDepotDtIdTransfer() {
		return depotDtIdTransfer;
	}
	public void setDepotDtIdTransfer(String depotDtIdTransfer) {
		this.depotDtIdTransfer = depotDtIdTransfer;
	}
	public String getNoBatchTransfer() {
		return noBatchTransfer;
	}
	public void setNoBatchTransfer(String noBatchTransfer) {
		this.noBatchTransfer = noBatchTransfer;
	}
	public String getPileCardTransfer() {
		return pileCardTransfer;
	}
	public void setPileCardTransfer(String pileCardTransfer) {
		this.pileCardTransfer = pileCardTransfer;
	}
	public String getCoLoaderTransfer() {
		return coLoaderTransfer;
	}
	public void setCoLoaderTransfer(String coLoaderTransfer) {
		this.coLoaderTransfer = coLoaderTransfer;
	}
	public List<FileManageData> getFileList() {
		return fileList;
	}
	public void setFileList(List<FileManageData> fileList) {
		this.fileList = fileList;
	}
	public String getDepotIdTransfer() {
		return depotIdTransfer;
	}
	public void setDepotIdTransfer(String depotIdTransfer) {
		this.depotIdTransfer = depotIdTransfer;
	}
	
}
