package com.amassfreight.domain;

import java.util.Collection;
import java.util.List;

public class NormalUser implements IUser {

//	@Override
//	public Collection<String> getRoles() {
//		return roles;
//	}
//
//	public void setRoles(Collection<String> roles) {
//		this.roles = roles;
//	}
//
//	@Override
//	public boolean hasRole(String roleId) {
//		return roles.contains(roleId);
//	}

	@Override
	public List<TreeFunMenu> getMenus() {
		return menus;
	}

	private List<TreeFunMenu> menus;

	public void setMenus(List<TreeFunMenu> menus) {
		this.menus = menus;
	}

	private String userId;
	private String userName;
//	private Collection<String> roles;

	@Override
	public String getUserId() {
		return userId;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
