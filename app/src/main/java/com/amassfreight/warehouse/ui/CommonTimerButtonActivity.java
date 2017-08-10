package com.amassfreight.warehouse.ui;



import android.R.integer;
import android.app.Activity;
import android.os.CountDownTimer;
import android.widget.Button;

public class CommonTimerButtonActivity extends Activity {

	private TimeCount time;
	private Button checking;
	private String strName;

	public CommonTimerButtonActivity(Button btnTimer) {

		time = new TimeCount(1000 * 60, 1000);// 构造CountDownTimer对象
		checking = btnTimer;
		strName = btnTimer.getText().toString();
		time.start();// 开始计时
	}

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			checking.setText(strName);
			checking.setClickable(true);
			checking.setEnabled(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			checking.setClickable(false);
			checking.setEnabled(false);
			checking.setText("请等待" + millisUntilFinished / 1000 + "秒");
		}
	}

}
