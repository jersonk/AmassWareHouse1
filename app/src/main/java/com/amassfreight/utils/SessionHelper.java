package com.amassfreight.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.IUser;
import com.amassfreight.domain.LogonFormData;
import com.amassfreight.domain.SelectDict;
import com.google.gson.reflect.TypeToken;

public class SessionHelper {
	private static SessionHelper _session;

	private long _startTime;
	private Date _curDate;
	private String _sysRun;

	public static SessionHelper getInstance() {
		if (_session == null) {
			_session = new SessionHelper();
		}
		return _session;
	}

	public void initSysTime(Date curDate) {
		_curDate = curDate;
		_startTime = System.nanoTime();
	}

	public void setSysRun(String strRun) {
		_sysRun = strRun;
	}

	public String getSysRun() {
		return _sysRun;
	}

	public Date getCurSysTime() {
		long n = (System.nanoTime() - _startTime) / 1000000;
		long rc = _curDate.getTime() + n;
		Date d = new Date(rc);
		return d;
	}

	protected SessionHelper() {
		objMap = new HashMap<String, Object>();
	}

	public Object getObject(String key) {
		return objMap.get(key);
	}

	public void setObject(String key, Object obj) {
		objMap.put(key, obj);
	}

	public void clearObject(String key) {
		objMap.remove(key);
	}

	private Map<String, Object> objMap;
	private IUser user;

	public IUser getUser() {
		return user;
	}

	public void setUser(IUser user) {
		this.user = user;
	}

	public LogonFormData getAuthenUser() {
		return authenUser;
	}

	public void setAuthenUser(LogonFormData authenUser) {
		this.authenUser = authenUser;
	}

	private LogonFormData authenUser;

	private static Map<String, SelectDict> _serviceTypeDictMap;
	private static List<SelectDict> _serviceTypeDictList;
	private static Map<String, SelectDict> _serviceFlgMap;
	private static List<SelectDict> _serviceFlgList;

	public void initDictList(Context context) {
		getServiceTypeList(context);
	}

	private void getServiceTypeList(Context context) {
		if (_serviceTypeDictMap == null) {

			// // _serviceTypeDictList = new BaseDataList<SelectDict>() {
			//
			// /**
			// *
			// */
			// private static final long serialVersionUID =
			// 2115548458208532299L;
			//
			// @Override
			// protected SelectDict createBaseData() {
			// // TODO Auto-generated method stub
			// return new SelectDict();
			// }
			// };

			Map<String, Object> p = new HashMap<String, Object>();
			p.put("cdType", "DEPOT_SER_ADD_LIST");
			NetworkHelper.getInstance().postJsonData(context,
					"VA001_GetDictList", p, new TypeToken<List<SelectDict>>() {
					}.getType(),
					new AmassHttpResponseHandler<List<SelectDict>>() {

						@Override
						protected void OnSuccess(List<SelectDict> response) {
							super.OnSuccess(response);
							_serviceTypeDictList = response;
							_serviceTypeDictMap = new HashMap<String, SelectDict>();
							Iterator<SelectDict> it = _serviceTypeDictList
									.iterator();
							while (it.hasNext()) {
								SelectDict item = it.next();
								_serviceTypeDictMap.put(item.getId(), item);
							}
						}

					}, false);
		}

		if (_serviceFlgMap == null) {
			Map<String, Object> p = new HashMap<String, Object>();
			p.put("cdType", "DEPOT_ADD_STATUS");
			NetworkHelper.getInstance().postJsonData(context,
					"Sys_GetDictList", p, new TypeToken<List<SelectDict>>() {
					}.getType(),
					new AmassHttpResponseHandler<List<SelectDict>>() {
						@Override
						protected void OnSuccess(List<SelectDict> response) {
							super.OnSuccess(response);
							_serviceFlgList = response;
							_serviceFlgMap = new HashMap<String, SelectDict>();
							Iterator<SelectDict> it = _serviceFlgList
									.iterator();
							while (it.hasNext()) {
								SelectDict item = it.next();
								_serviceFlgMap.put(item.getId(), item);
							}
						}
					}, false);
		}

	}

	public List<SelectDict> getServiceTypeList() {
		return _serviceTypeDictList;
	}

	public String getServiceType(String serviceType) {
		SelectDict dict = _serviceTypeDictMap.get(serviceType);
		if (dict != null) {
			return dict.getName();
		}
		return null;
	}

	public List<SelectDict> getServiceFlgList() {
		return _serviceFlgList;
	}

	public String getServiceFlg(String serviceFlg) {
		SelectDict dict = _serviceFlgMap.get(serviceFlg);
		if (dict != null) {
			return dict.getName();
		}
		return null;
	}

}
