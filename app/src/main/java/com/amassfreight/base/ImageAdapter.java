package com.amassfreight.base;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amassfreight.base.net.AmassHttpResponseHandler;
import com.amassfreight.base.net.NetworkHelper;
import com.amassfreight.domain.ImageData;
import com.amassfreight.domain.MoreData;
import com.amassfreight.warehouse.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class ImageAdapter extends ArrayAdapter<MoreData> {
	private boolean showAddDesc;
	
	public ImageAdapter(Context context, int resource) {
		super(context, resource);
		this.showAddDesc = true;
	}

	public void setShowAddDesc(boolean showAddDesc){
		this.showAddDesc = showAddDesc; 
	}
//	private static DisplayImageOptions forPage = new DisplayImageOptions.Builder()
//			.showImageOnLoading(R.drawable.app_bg_img).cacheInMemory(false)// ���浽�ڴ�
//			.cacheOnDisc(true)// ���浽����
//			// .decodingOptions(new BitmapFactory.Options())// ͼƬ��С������ȵ�
//			.bitmapConfig(Bitmap.Config.RGB_565)// �������ARGB_8888ʡ�ܶ��ڴ�
//			.build();

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout = new LinearLayout(getContext());

		ImageView imageView;// (ImageView)convertView;
		// LayoutInflater mInflater
		((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.grid_photo_item, layout, true);

		imageView = (ImageView) layout.findViewById(R.id.imageView_menu);
		TextView textImage = (TextView) layout
				.findViewById(R.id.textView_fileName);
		MoreData item = getItem(position);
		if (item.getDataType() == MoreData.DATA_TYPE) {
			ImageData imagedata = (ImageData) item.getData();
			if(showAddDesc){
				textImage.setText(imagedata.getImageDesc());
			}
			imageView.setScaleType(ScaleType.FIT_XY);
			if (imagedata.getData() == null) {
				if (!imagedata.isDownloading()) {
					imagedata.setDownloading(true);
					File dir = new File(
							Environment.getExternalStorageDirectory(),
							"amass/pics/download/cache");
					File imageFile = new File(dir, imagedata.getImageId());
					if (!imageFile.exists()) {
						// Download Image File
						// imageView.setImageResource(R.drawable.widget_icon_add_img);
						DownLoadPicture(imagedata.getImageId(), imageFile,
								imagedata, imageView);
					} else {
						String displyPath = "file://"
								+ imageFile.getAbsolutePath();
						ImageSize size = new ImageSize(100, 100);
						Bitmap thumbnail = ImageLoader.getInstance()
								.loadImageSync(displyPath, size);

						imagedata.setData(thumbnail);
						imagedata.setPath(imageFile.getAbsolutePath());
						imagedata.setUrl(displyPath);
						imageView.setImageBitmap(imagedata.getData());
						notifyDataSetChanged();
					}
				}
			} else {
				imageView.setImageBitmap(imagedata.getData());
			}

			// if (item.isChecked()) {
			// BadgeView bv = new BadgeView(this.getContext(), imageView);
			// bv.setBackgroundResource(R.drawable.ic_action_cancel);
			// bv.show();
			// }
		} else {
			imageView.setImageResource(R.drawable.widget_icon_add_img);
			if(showAddDesc){
				textImage.setText("拍摄\n照片");
			}
		}
		// LinearLayout.LayoutParams layoutParams= new
		// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// layoutParams.gravity=Gravity.CENTER;
		// imageView.setLayoutParams(layoutParams);
		// if (data.nValue > 0) {

		// }
		// layout.setTag(data);
		// layout.addView(imageView);//, layoutParams);
		return layout;
		// return super.getView(position, convertView, parent);
	}

	private void DownLoadPicture(String fileId, final File f,
			final ImageData imagedata, final ImageView imageView) {
		Map<String, Object> p = new HashMap<String, Object>();
		p.put("strFileId", fileId);
		NetworkHelper.getInstance().postJsonReturnBinary(this.getContext(),
				"VA003_GetFileData", p, new AmassHttpResponseHandler() {
					@Override
					protected void OnSuccess(byte[] response) {
						// TODO Auto-generated method stub
						super.OnSuccess(response);
						try {
							// 下载图片
							FileOutputStream fs = new FileOutputStream(f);
							fs.write(response);
							fs.close();

							// 增值服务
							String displyPath = "file://" + f.getAbsolutePath();
							ImageSize size = new ImageSize(100, 100);
							Bitmap thumbnail = ImageLoader.getInstance()
									.loadImageSync(displyPath, size);
							imagedata.setData(thumbnail);
							imagedata.setPath(f.getAbsolutePath());
							imagedata.setUrl(displyPath);
							imageView.setImageBitmap(imagedata.getData());
							notifyDataSetChanged();
						} catch (Exception e) {
							// e.printStackTrace();
						}

					}
				}, false);
	}

}
