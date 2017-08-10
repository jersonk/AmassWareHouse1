package com.amassfreight.domain;

import java.util.List;

public class RC002ListData {

	public RC002ListData(){
		
	}
	private List<RC002MeasureMode> measureMode;
	private List<SelectDict> depotMan;
	private List<Packing_type> packingType;
	private List<FileManageData> fileList;
	
	public List<RC002MeasureMode> getMeasureMode() {
		return measureMode;
	}
	public void setMeasureMode(List<RC002MeasureMode> measureMode) {
		this.measureMode = measureMode;
	}
	public List<SelectDict> getDepotMan() {
		return depotMan;
	}
	public void setDepotMan(List<SelectDict> depotMan) {
		this.depotMan = depotMan;
	}
	public List<Packing_type> getPackingType() {
		return packingType;
	}
	public void setPackingType(List<Packing_type> packingType) {
		this.packingType = packingType;
	}
	public List<FileManageData> getFileList() {
		return fileList;
	}
	public void setFileList(List<FileManageData> fileList) {
		this.fileList = fileList;
	}
	
}
