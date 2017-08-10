package com.amassfreight.warehouse.ui.dialogs;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.amassfreight.utils.Utils;

public class VA001SearchMoreData implements java.io.Serializable, Cloneable{
	private String  cdOrderPublic;
	private String serviceType;
	private String flgService;
	private Date  dtFrom;
	private Date  dtEnd;

	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public String getFlgService() {
		return flgService;
	}
	public void setFlgService(String flgService) {
		this.flgService = flgService;
	}
	public Date getDtFrom() {
		return dtFrom;
	}
	public void setDtFrom(Date dtFrom) {
		this.dtFrom = dtFrom;
	}
	public Date getDtEnd() {
		return dtEnd;
	}
	public void setDtEnd(Date dtEnd) {
		this.dtEnd = dtEnd;
	}
//	public JSONObject getJsonObject() {
//		
//		JSONObject obj = new JSONObject();
//		
//		try {
//			obj.put("serviceType", serviceType);
//			obj.put("flgService", flgService);
//			obj.put("cdOrderPublic", cdOrderPublic);
//			obj.put("serviceType", serviceType);
//			obj.put("dtFrom", Utils.setDateToJson(dtFrom));
//			obj.put("dtEnd", Utils.setDateToJson(dtEnd));
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return obj;
//	}
	public String getCdOrderPublic() {
		return cdOrderPublic;
	}
	public void setCdOrderPublic(String cdOrderPublic) {
		this.cdOrderPublic = cdOrderPublic;
	}

	public VA001SearchMoreData clone()  {
		// TODO Auto-generated method stub
		try {
			return (VA001SearchMoreData) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this;
	}
	
	
}
