package com.amassfreight.domain;


public class FunctionMenu {//implements BaseResponseData {

	public FunctionMenu(){
		
	}
//	@Override
//	public void parserDataList(JSONArray response) {
//
//	}
//
//	@Override
//	public void parserData(JSONObject response) throws JSONException {
//		menuId = response.getString("menuId");
//		menuName = response.getString("menuName");
//		menuClass = response.getString("menuClass");
//	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getMenuClass() {
		return menuClass;
	}

	public void setMenuClass(String menuClass) {
		this.menuClass = menuClass;
	}

//	public int getImage() {
//		return image;
//	}
//
//	public void setImage(int image) {
//		this.image = image;
//	}

	private String menuId;
	private String menuName;
	private String menuClass;
//	private int image;
//	@Override
//	public void parserBinaryData(byte[] binaryData) {
//		// TODO Auto-generated method stub
//		
//	}
}
