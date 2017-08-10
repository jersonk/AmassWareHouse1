package com.amassfreight.domain;

import java.util.List;


public class LogonUser{// implements BaseResponseData, Serializable{

//	@Override
//	public void parserDataList(JSONArray response) {
//		// TODO Auto-generated method stub
//		
//	}
//
//	public LogonUser(){}
//	
//	@Override
//	public void parserData(JSONObject response) throws JSONException {
//		userId = response.getString("userId");
//		userName = response.getString("userName");
//		menus = new BaseDataList<FunctionMenu>() {
//
//			/**
//			 * 
//			 */
//			private static final long serialVersionUID = 5019651895620627970L;
//
//			@Override
//			protected FunctionMenu createBaseData() {
//				// TODO Auto-generated method stub
//				return new FunctionMenu();
//			}
//
//			
//		};
//		JSONArray menusJson = response.getJSONArray("menus");
//		menus.parserDataList(menusJson);
//
//	
//	}

	private String userId;
	private String userName;
//	private List<String> roles;

	
	private List<TreeFunMenu> menus;
	
	public List<TreeFunMenu> getMenus(){
		return menus;
	}
	
	public String getUserId() {
		// TODO Auto-generated method stub
		return userId;
	}


	public String getUserName() {
		// TODO Auto-generated method stub
		return userName;
	}

//	@Override
//	public void parserBinaryData(byte[] binaryData) {
//		// TODO Auto-generated method stub
//		
//	}

//	public List<String> getRoles() {
//		return roles;
//	}
//
//	public void setRoles(List<String> roles) {
//		this.roles = roles;
//	}


}
