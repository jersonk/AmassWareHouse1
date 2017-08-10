package com.amassfreight.warehouse.ui;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.PP005StatusData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;

public class PP015Activity extends BaseActivity {

	private String strNoBox;
	private Boolean bolIsFclOrScl; // True:整箱； False:自拼箱
	private TextView tvOpr;
	private TextView tvNoBoxContext;
	private EditText tvConext;
	private Button btnSend;
	private TextView tvNoBox;
	private String oprUserName;

	/** 初始化事件 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = this.getIntent().getExtras();

		bolIsFclOrScl = true;
		strNoBox = "";
		// 判断页面传值是否为空
		if (bundle != null) {
			bolIsFclOrScl = (Boolean) bundle.get("isFclOrScl"); // 判断是否为整箱或者自拼箱
			strNoBox = bundle.get("noBox").toString(); // 集箱号
			oprUserName = bundle.get("oprUser").toString(); // 操作人
		}

		setContentView(R.layout.activity_pp015);
		// 设置标题栏是左边是否有返回事件
		setupActionBar();

		tvOpr = (TextView) findViewById(R.id.tvOpr); // 页面操作

		tvConext = (EditText) findViewById(R.id.tvConext); // 输入内容

		btnSend = (Button) findViewById(R.id.btnSend); // 发送

		tvNoBoxContext = (TextView) findViewById(R.id.tvNoBoxContext);

		tvNoBox = (TextView) findViewById(R.id.tvNoBox);

		tvNoBox.setText(strNoBox);

		tvOpr.setText(oprUserName);

		InitData();
		// 发送信息
		btnSend.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onClick(View v) {
				Map<String, Object> pp015Map = new HashMap<String, Object>();
				pp015Map.put("bolFclOrScl", bolIsFclOrScl);// 是否是整箱或者自拼箱
				pp015Map.put("strNoBox", strNoBox);// 集箱号
				pp015Map.put("strOtherContext", tvConext.getText().toString());// 内容
				NetworkHelper.getInstance().postJsonData(_thisActivity,
						"PP010_SendMail", pp015Map, PP005StatusData.class,
						new AmassHttpResponseHandler<PP005StatusData>() {
							@Override
							protected void OnSuccess(PP005StatusData response) {
								super.OnSuccess(response);
								// 获取对应的数据
								PP005StatusData statusData = (PP005StatusData) response;
								if (statusData.getUpdatePickStatus()) {
									closeActivity();
								} else {
									Utils.showAlertDialog(_thisActivity,
											getString(R.string.PP015_MSG_001));
								}

							}
						}, true);

			}
		});
	}

	public void InitData() {
		Map<String, Object> pp015Map = new HashMap<String, Object>();
		pp015Map.put("bolFclOrScl", bolIsFclOrScl);// 是否是整箱或者自拼箱
		pp015Map.put("strNoBox", strNoBox);// 集箱号
		NetworkHelper.getInstance().postJsonData(_thisActivity, "MailContext",
				pp015Map, String.class, new AmassHttpResponseHandler<String>() {
					@Override
					protected void OnSuccess(String response) {
						super.OnSuccess(response);
						tvNoBoxContext.setText(response);
					}
				}, true);
	}

	/* 关闭页面 */
	public void closeActivity() {
		setResult(Activity.RESULT_OK);
		this.finish();
	}

	/*
	 * 返回事件
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 后退
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// 按下的如果是BACK，同时没有重复
			setResult(Activity.RESULT_OK);
			this.finish();

		}
		return super.onKeyDown(keyCode, event);
	}

}
