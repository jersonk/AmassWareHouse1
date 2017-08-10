package com.amassfreight.warehouse.ui.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.amassfreight.utils.Utils;

public class DN006Data implements java.io.Serializable, Cloneable {
	private String depotDtID;


	public String getDepotDtID() {
		return depotDtID;
	}

	public void setDepotDtID(String depotDtID) {
		this.depotDtID = depotDtID;
	}

	public JSONObject getJsonObject() {

		JSONObject obj = new JSONObject();

		try {
			obj.put("depotDtID", depotDtID);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return obj;
	}
	

	public DN006Data clone()  {
		// TODO Auto-generated method stub
		try {
			return (DN006Data) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
}
