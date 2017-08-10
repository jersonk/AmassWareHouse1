package com.amassfreight.domain;

import java.util.Collection;
import java.util.List;

public interface IUser {
//	Collection<String> getRoles();
//	boolean hasRole(String roleId);
	List<TreeFunMenu> getMenus();
	String getUserId();
	String getUserName();
}
