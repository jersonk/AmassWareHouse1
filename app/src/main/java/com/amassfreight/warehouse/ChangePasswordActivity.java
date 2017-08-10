package com.amassfreight.warehouse;

import java.util.HashMap;
import java.util.Map;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.BaseActivity;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.MobileError;
import com.amassfreight.utils.Utils;
import com.amassfreight.widget.SquaredPassWord;

import android.os.Bundle;
import android.content.DialogInterface;
import android.widget.TextView;
import android.widget.Toast;

public class ChangePasswordActivity extends BaseActivity {

	private TextView passwordLabel;
	private SquaredPassWord passwordView;
	private String oldpassword;
	private String newpassword;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		setupActionBar();
		passwordLabel = (TextView)findViewById(R.id.textViewPasswrd);
		passwordLabel.setText("请输入现在的密码");
		passwordView = (SquaredPassWord)findViewById(R.id.passwordChange);
		passwordView.setOnCompleteListener(new SquaredPassWord.OnCompleteListener() {
			
			@Override
			public void onComplete(String password) {
				passwordView.clearPassword();
				setpasswordProc(password);
			}
		});
	}

	private void setpasswordProc(String password){
		if(oldpassword == null){
			oldpassword = password;
			passwordLabel.setText("请输入现在的密码:OK\n请输入新密码");
		}else if(newpassword == null){
			newpassword = password;
			passwordLabel.setText("请输入现在的密码:OK\n请输入新密码:OK\n请再输入新密码");
		}else {
			if(!password.equals(newpassword)){
				newpassword = null;
				passwordLabel.setText("请输入现在的密码:OK\n请输入新密码");
				Toast.makeText(this, "两次新密码不相同,请重新输入新密码!",
						Toast.LENGTH_SHORT).show();
			}else{
				Map<String, Object> pwdMap = new HashMap<String, Object>();
				pwdMap.put("oldPassword", oldpassword);
				pwdMap.put("newpassword", newpassword);
				
				NetworkHelper.getInstance().postJsonData(_thisActivity, "ChangeMobilePassword", pwdMap, null, new AmassHttpResponseHandler(){

					@Override
					public void onErrMsg(MobileError err) {
						clearAllPassword();
					}

					@Override
					protected void OnSuccess(Object response) {
						Utils.showAlertDialogWithClose(_thisActivity, "密码修改成功。", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								_thisActivity.finish();
							}
						});
					}
					
					
					
				}, true);
			}
		}
	}
	
	private void clearAllPassword()
	{
		passwordLabel.setText("请输入现在的密码");
		oldpassword = null;
		newpassword = null;
	}
	
}
