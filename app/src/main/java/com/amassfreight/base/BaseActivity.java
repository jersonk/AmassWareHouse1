package com.amassfreight.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.amassfreight.domain.IUser;
import com.amassfreight.utils.SessionHelper;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;
import com.amassfreight.warehouse.R.string;

public class BaseActivity extends Activity {

	protected BaseActivity _thisActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		_thisActivity = this;
		Log.e("login_Activity:", _thisActivity.getClass().getSimpleName());

		IUser user = SessionHelper.getInstance().getUser();
		CharSequence strTitle = getTitle();
		if (user != null
				&& !"LoginActivity".equals(_thisActivity.getLocalClassName())) {
			strTitle = strTitle + "-" + user.getUserName();
		}
		String strSysRun = SessionHelper.getInstance().getSysRun();
		if (strSysRun != null
				&& "LoginActivity".equals(_thisActivity.getLocalClassName())) {
			strTitle = strTitle + "(" + strSysRun + ")";
		}
		this.setTitle(strTitle);

		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 * 添加标题栏
	 */
	protected void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setIcon(R.drawable.ic_main);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (getCurrentFocus() != null && imm.isActive()) {
			imm.hideSoftInputFromWindow(
					this.getCurrentFocus().getWindowToken(), 0);
		}
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			Utils.gotoHomeActivity(this);
			this.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
