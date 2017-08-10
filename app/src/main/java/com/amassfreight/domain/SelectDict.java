package com.amassfreight.domain;


public class SelectDict {

	public SelectDict() {
	}

	// @Override
	// public void parserDataList(JSONArray response) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void parserData(JSONObject response) throws JSONException {
	// // TODO Auto-generated method stub
	// id = response.getString("id");
	// name = response.getString("name");
	// }
	//
	// @Override
	// public void parserBinaryData(byte[] binaryData) {
	// // TODO Auto-generated method stub
	//
	// }

	private String id;
	private String name;
	private int flgType;
	private Double numProperty;
	private Boolean flgProperty;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		SelectDict other = (SelectDict) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public int getFlgType() {
		return flgType;
	}

	public void setFlgType(int flgType) {
		this.flgType = flgType;
	}

	public Double getNumProperty() {
		return numProperty;
	}

	public void setNumProperty(Double numProperty) {
		this.numProperty = numProperty;
	}

	public Boolean getFlgProperty() {
		return flgProperty;
	}

	public void setFlgProperty(Boolean flgProperty) {
		this.flgProperty = flgProperty;
	}

}
