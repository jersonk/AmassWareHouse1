package com.amassfreight.widget;

import uk.co.senab.photoview.PhotoView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.amassfreight.warehouse.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class GalleryAct extends FragmentActivity {

	private static String extraUrl = "url";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent in = getIntent();
		Bundle extras = in.getExtras();
		if (extras != null) {
			String url = extras.getString(extraUrl);
			PhotoView photoView = new PhotoView(this);
			ImageLoader.getInstance().displayImage(url, photoView, forPage, new SimpleImageLoadingListener() {

				@Override
				public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
					
				}

				@Override
				public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
					
				}

				@Override
				public void onLoadingCancelled(String arg0, View arg1) {
					
				}
			});
			setContentView(photoView);
		} else {
			
		}
	}

	public static void start(Activity from, String url) {
		Intent in = new Intent(from, GalleryAct.class);
		in.putExtra(extraUrl, url);
		from.startActivity(in);
	}

	private static DisplayImageOptions forPage = new DisplayImageOptions.Builder()
			.showImageOnLoading(R.drawable.app_bg_img)
			.cacheInMemory(false)// 缓存到内�?
//			.cacheOnDisc(true)// 缓存到磁�?
			// .decodingOptions(new BitmapFactory.Options())// 图片大小，清晰度�?
			.bitmapConfig(Bitmap.Config.RGB_565)// 用这个比ARGB_8888省很多内�?
			.build();

}
