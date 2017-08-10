package com.amassfreight.domain;

import java.util.List;

public class TreeFunMenu {
	private String groupTitle;
	private String groupId;
	private List<FunctionMenu> menus;
	public String getGroupTitle() {
		return groupTitle;
	}
	public void setGroupTitle(String groupTitle) {
		this.groupTitle = groupTitle;
	}
	public List<FunctionMenu> getMenus() {
		return menus;
	}
	public void setMenus(List<FunctionMenu> menus) {
		this.menus = menus;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
}
