package com.amassfreight.domain;

public class RC002MeasureMode {
	private String rownum;
	private String measureModeId;
	private String measureModeName;

	public RC002MeasureMode() {
		super();
	}

	public String getRownum() {
		return rownum;
	}

	public void setRownum(String rownum) {
		this.rownum = rownum;
	}

	public String getMeasureModeId() {
		return measureModeId;
	}

	public void setMeasureModeId(String measureModeId) {
		this.measureModeId = measureModeId;
	}

	public String getMeasureModeName() {
		return measureModeName;
	}

	public void setMeasureModeName(String measureModeName) {
		this.measureModeName = measureModeName;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((measureModeId == null) ? 0 : measureModeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RC002MeasureMode other = (RC002MeasureMode) obj;
		if (measureModeId == null) {
			if (other.measureModeId != null)
				return false;
		} else if (!measureModeId.equals(other.measureModeId))
			return false;
		return true;
	}
}
