package com.amassfreight.warehouse.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.FileManageData;
import com.amassfreight.utils.Utils;
import com.amassfreight.warehouse.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressWarnings("rawtypes")
public class FileDataAdapter extends ArrayAdapter implements
		View.OnClickListener {

	private LayoutInflater inflater;
	/**
	 * 屏幕宽度,由于我们用的是HorizontalScrollView,所以按钮选项应该在屏幕外
	 */
	@SuppressWarnings("unused")
	private Context context;
	private Activity _activity;

	@SuppressWarnings("unchecked")
	public FileDataAdapter(Context context, List<FileManageData> datas) {
		super(context, 0, datas);
		inflater = LayoutInflater.from(context);
		this.context = context;
		this._activity = (Activity) context;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		FileManageData model = (FileManageData) getItem(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.activity_pp008_fclpick_file_item, null);

			holder.tvfileName = (TextView) convertView
					.findViewById(R.id.tvfileName);
			holder.tvfileName.setText(model.getFileName());// 文件名称
			holder.tvfileName.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
			holder.tvfileName.setTextColor(Color.BLUE);

			// 文件名称的监听事件
			holder.tvfileName.setOnClickListener(new OnClickListener() {

				@SuppressLint("DefaultLocale")
				@Override
				public void onClick(View v) {

					final FileManageData fileMan = (FileManageData) getItem(position);

					final String dirName = Environment
							.getExternalStorageDirectory() + "/amass/Download";
					final File dir = new File(dirName);
					
					/* 取得扩展名 */
					String end = fileMan
							.getFileName()
							.substring(fileMan.getFileName().lastIndexOf("."),
									fileMan.getFileName().length())
							.toLowerCase();
					final String fileNewName = fileMan.getFileId() + end;
					final File f = new File(dir, fileNewName);
					if (f.exists()) {
						Utils.openFile(_activity, dirName + "/" + fileNewName);
					} else {

						Map<String, Object> p = new HashMap<String, Object>();
						p.put("strFileId", fileMan.getFileId());
						NetworkHelper.getInstance().postJsonReturnBinary(
								getContext(), "PP008_GetFileData", p,
								new AmassHttpResponseHandler() {
									@Override
									protected void OnSuccess(byte[] response) {
										super.OnSuccess(response);

										try {
											FileOutputStream fs = new FileOutputStream(
													f);
											fs.write(response);
											fs.close();

											Utils.openFile(_activity, dirName
													+ "/" + fileNewName);
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}, true);
					}
				}
			});

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}

	public final class ViewHolder {
		public TextView tvfileName;

	}

	@Override
	public void onClick(View arg0) {

	}

}
