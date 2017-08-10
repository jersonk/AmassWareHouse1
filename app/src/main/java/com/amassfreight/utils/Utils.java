package com.amassfreight.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import com.amassfreight.domain.ImageData;
import com.amassfreight.warehouse.LoginActivity;
import com.amassfreight.warehouse.MainScreenActivity;
import com.amassfreight.warehouse.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.widget.TextView;

public class Utils {

	private static int DEFAULT_MENU_IMAGE = R.drawable.launcher_icon;
	private static Map<String, java.lang.Integer> menuImageMap;
	static {
		menuImageMap = new HashMap<String, java.lang.Integer>();
		menuImageMap.put("ui.VA001Activity", R.drawable.ic_va001);
		menuImageMap.put("ui.DN001Activity", R.drawable.ic_dn001);
		menuImageMap.put("ui.DN006Activity", R.drawable.ic_dn006);
		menuImageMap.put("ui.PP004Activity", R.drawable.ic_pp004);
		menuImageMap.put("ui.PP006Activity", R.drawable.ic_pp006);
		menuImageMap.put("ui.PP008Activity_FCL", R.drawable.ic_pp008_fcl);
		menuImageMap.put("ui.PP008Activity_SCL", R.drawable.ic_pp008_scl);
		menuImageMap.put("ui.PP010Activity_FCL", R.drawable.ic_pp010_fcl);
		menuImageMap.put("ui.PP010Activity_SCL", R.drawable.ic_pp010_scl);
		menuImageMap.put("ui.PP012Activity", R.drawable.ic_pp012);
		menuImageMap.put("ui.PP013Activity", R.drawable.ic_pp013);
		menuImageMap.put("ui.RC001Activity", R.drawable.ic_rc001);
		menuImageMap.put("ui.PP001Activity", R.drawable.ic_pp001);
		menuImageMap.put("ui.OT001Activity", R.drawable.ic_ot001_new);
		// TODO
		menuImageMap.put("ui.DN001Activity_Single", R.drawable.ic_dn001_single);
		menuImageMap.put("ui.OT003Activity", R.drawable.ic_ot003);
		menuImageMap.put("ui.OT001_InBatchActivity", R.drawable.ic_ot001_batch);
	}

	public static Dialog createConnectDialg(Context ctx, String msg) {
		Dialog dlg = new Dialog(ctx);
		// dlg.setTitle(R.string.app_name);
		dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dlg.setCancelable(false);
		dlg.setContentView(R.layout.progress);

		if (msg != null && !msg.isEmpty()) {
			TextView progressText = (TextView) dlg
					.findViewById(R.id.progressText);
			// progressText.setText(msg);
		}
		return dlg;
	}

	public static String CALL_FROM_SET = "CALL_FROM_ACTIVITY";

	public static Class<?> GetClassByName(String name) {

		try {
			return Class.forName("com.amassfreight.warehouse." + name);
		} catch (ClassNotFoundException e) {
			Log.d("Utlis", e.getLocalizedMessage());
		}
		return null;
	}

	public static void showAlertDialog(Context context, String alertMsg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						android.R.style.Theme_Holo_Light));
		builder.setTitle(R.string.app_name);
		builder.setCancelable(false);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(R.string.button_ok, null);
		builder.show();
	}

	public static void showAlertDialogWithClose(Context context,
			String alertMsg, DialogInterface.OnClickListener clickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						android.R.style.Theme_Holo_Light));
		builder.setTitle(R.string.app_name);
		builder.setCancelable(false);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(R.string.button_ok, clickListener);
		builder.show();
	}

	public static void showAlertDialogRestart(final Context context,
			String alertMsg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						android.R.style.Theme_Holo_Light));
		builder.setTitle(R.string.app_name);
		builder.setCancelable(false);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(R.string.button_ok,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						restartActivity(context);
					}
				});
		builder.show();
	}

	private static List<String> pathList;
	static {
		pathList = new ArrayList<String>();
		pathList.add("amass/pics/download/cache/");
		pathList.add("amass/Download/");
		pathList.add("amass/pics/pp011/");
		pathList.add("amass/pics/dn002/");
		pathList.add("amass/pics/dn003/");
		pathList.add("amass/pics/dn005/");
		pathList.add("amass/pics/pp012/");
		pathList.add("amass/pics/pp013/");
		pathList.add("amass/pics/PP007/");
		pathList.add("amass/pics/rc002/");
	}

	public static void createCacheDir() {
		File dir;
		for (String path : pathList) {
			dir = new File(Environment.getExternalStorageDirectory(), path);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
	}

	public static void deleteCacheDir() {
		File dir;
		for (String path : pathList) {
			dir = new File(Environment.getExternalStorageDirectory(), path);
			if (dir.exists()) {
				deleteFileAndPath(dir);
			}
		}
	}

	/* 创建备份照片 */
	public static void createBackUp() {
		File dir;
		dir = new File(Environment.getExternalStorageDirectory(),
				"amass/backup/");
		if (!dir.exists()) {
			dir.mkdirs();
		}

	}

	/* 删除备份照片 */
	public static void deleteBackUp() {
		File dir;
		dir = new File(Environment.getExternalStorageDirectory(),
				"amass/backup/");
		if (dir.exists()) {
			deleteFileAndPath(dir);
		}
	}

	public static void showAlertDialogIntMsg(Context context, int alertMsg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						android.R.style.Theme_Holo_Light));
		builder.setTitle(R.string.app_name);
		builder.setCancelable(false);
		builder.setMessage(alertMsg);
		builder.setPositiveButton(R.string.button_ok, null);
		builder.show();
	}

	private static void restartActivity(Context context) {
		Intent restartIntent = context.getPackageManager()
				.getLaunchIntentForPackage(LAUNCH_PACKAGE_NAME);
		restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		restartIntent.putExtra(LoginActivity.LOGIN_EXTRA_DATA, SessionHelper
				.getInstance().getAuthenUser());
		context.startActivity(restartIntent);
	}

	public static void gotoHomeActivity(Context context) {
		Intent intent = new Intent(context, MainScreenActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_CLEAR_TASK);
		// restartIntent.putExtra(LoginActivity.LOGIN_EXTRA_DATA,
		// SessionHelper.getInstance().getAuthenUser());
		context.startActivity(intent);
	}

	public static Date getCurrentDate(boolean bStart) {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		if (bStart) {
			cal.set(Calendar.HOUR_OF_DAY, 0);
		} else {
			cal.set(Calendar.HOUR_OF_DAY, 24);
		}
		cal.set(Calendar.MINUTE, 0);
		return cal.getTime();
	}

	private static String LAUNCH_PACKAGE_NAME = "com.amassfreight.warehouse";

	// public static void restartApp(Context context) {
	// Intent restartIntent = context.getPackageManager()
	// .getLaunchIntentForPackage(LAUNCH_PACKAGE_NAME);
	// restartIntent.putExtra(LoginActivity.LOGIN_EXTRA_DATA,
	// SessionHelper.getInstance().getAuthenUser());
	//
	// PendingIntent intent = PendingIntent.getActivity(context, 0,
	// restartIntent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
	// AlarmManager manager = (AlarmManager) context
	// .getSystemService(Context.ALARM_SERVICE);
	// manager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, intent);
	// System.exit(1);
	// }

	/*
	 * 解析扫描的条码 create by sdhuang 2014-05-19
	 */
	/*
	 * public static BarCodeResult analysisBarCode(String barCodeStr,String
	 * type) { BarCodeResult result = new BarCodeResult(); //拆分逗号分隔的条码 String[]
	 * obj=barCodeStr.split(","); if(!type.equals(obj[0])){
	 * result.setSuccess(false); }else { if("05".equals(obj[0])){ BarCode05
	 * code05 =new BarCode05(); code05.setBoxNo(obj[1]);
	 * result.setSuccess(true); result.setType(obj[0]);
	 * result.setBarCode(code05); } else if("04".equals(obj[0])){ BarCode04
	 * code04 =new BarCode04(); code04.setContainerCd(obj[1]);
	 * result.setSuccess(true); result.setType(obj[0]);
	 * result.setBarCode(code04); } else if("03".equals(obj[0])){ BarCode03
	 * code03 =new BarCode03(); code03.setDelivId(obj[1]);
	 * result.setSuccess(true); result.setType(obj[0]);
	 * result.setBarCode(code03); } else if("02".equals(obj[0])){ BarCode02
	 * code02 =new BarCode02(); code02.setDepotDtId(obj[1]);
	 * code02.setOrderCd(obj[2]); code02.setCoLoader(obj[3]);
	 * code02.setDepotNum(obj[4]); result.setSuccess(true);
	 * result.setType(obj[0]); result.setBarCode(code02); } else
	 * if("01".equals(obj[0])){ BarCode01 code01 =new BarCode01();
	 * code01.setDepotId(obj[1]); code01.setOrderCd(obj[2]);
	 * code01.setCoLoader(obj[3]); result.setSuccess(true);
	 * result.setType(obj[0]); result.setBarCode(code01); }else{
	 * result.setSuccess(false); result.setType("error");
	 * result.setBarCode(null); } }
	 * 
	 * return result; }
	 */

	public static String completeOrderId(String orderId) {
		if (orderId == null || orderId.trim().isEmpty()) {
			return null;
		}
		orderId = orderId.trim();
		if (orderId.length() <= 7) {
			Calendar cal = Calendar.getInstance(Locale.getDefault());
			cal.setTime(new Date());
			return String.format("%02d%s%s", cal.get(Calendar.YEAR) % 100,
					"0000000".substring(orderId.length()), orderId);
		}
		return orderId;
	}

	public static int getVerCode(Context context) {
		int verCode = 1;
		try {
			verCode = context.getPackageManager().getPackageInfo(
					"com.amassfreight.warehouse", 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e("Utils", e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(
					"com.amassfreight.warehouse", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e("Utils", e.getMessage());
		}
		return verName;
	}

	public static void compressImage(String filePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		options.inJustDecodeBounds = false;
		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;
		int reqHeight = 1600;
		int reqWidth = 900;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			options.inSampleSize = inSampleSize;
		}
		bitmap = BitmapFactory.decodeFile(filePath, options);
		try {
			String tempImagePath = filePath + "_01";
			FileOutputStream out = new FileOutputStream(tempImagePath);

			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out)) {
				out.flush();
				out.close();
			}

			// ADD 2014-09-10 ZXX START 
			// 照片备份
			File dir = new File(Environment.getExternalStorageDirectory(),
					"amass/backup");

			File cameraTempFile = new File(dir, UUID.randomUUID() + ".jpg");

			FileOutputStream outBack = new FileOutputStream(
					cameraTempFile.getAbsolutePath());

			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outBack)) {
				outBack.flush();
				outBack.close();
			}
			// ADD 2014-09-10 ZXX END  备份照片
			
			File f = new File(filePath);
			f.delete();
			File tempF = new File(tempImagePath);
			tempF.renameTo(f);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void openFile(Activity activity, String filePath) {
		Intent intent = getFileIntent(filePath);
		try {
			activity.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			Utils.showAlertDialog(activity, "没有相应的应用程序打开该文件！");
		}
	}

	private static Intent getFileIntent(String filePath) {

		File file = new File(filePath);
		if (!file.exists())
			return null;
		/* 取得扩展名 */
		String end = file
				.getName()
				.substring(file.getName().lastIndexOf(".") + 1,
						file.getName().length()).toLowerCase();
		/* 依扩展名的类型决定MimeType */
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
			return getAudioFileIntent(filePath);
		} else if (end.equals("3gp") || end.equals("mp4")) {
			return getAudioFileIntent(filePath);
		} else if (end.equals("jpg") || end.equals("gif") || end.equals("png")
				|| end.equals("jpeg") || end.equals("bmp")) {
			return getImageFileIntent(filePath);
		} else if (end.equals("apk")) {
			return getApkFileIntent(filePath);
		} else if (end.equals("ppt") || end.equals("pptx")) {
			return getPptFileIntent(filePath);
		} else if (end.equals("xls") || end.equals("xlsx")) {
			return getExcelFileIntent(filePath);
		} else if (end.equals("doc") || end.equals("docx")) {
			return getWordFileIntent(filePath);
		} else if (end.equals("pdf")) {
			return getPdfFileIntent(filePath);
		} else if (end.equals("chm")) {
			return getChmFileIntent(filePath);
		} else if (end.equals("txt")) {
			return getTextFileIntent(filePath, false);
		} else if (end.equals("rar")) {
			return getRarFileIntent(filePath);
		} else if (end.equals("zip")) {
			return getZipFileIntent(filePath);
		} else {
			return getAllIntent(filePath);
		}
	}

	// Android获取一个用于打开APK文件的intent
	private static Intent getAllIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "*/*");
		return intent;
	}

	// Android获取一个用于打开APK文件的intent
	private static Intent getApkFileIntent(String param) {

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.android.package-archive");
		return intent;
	}

	// Android获取一个用于打开VIDEO文件的intent
	private static Intent getVideoFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "video/*");
		return intent;
	}

	// Android获取一个用于打开AUDIO文件的intent
	private static Intent getAudioFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("oneshot", 0);
		intent.putExtra("configchange", 0);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "audio/*");
		return intent;
	}

	// Android获取一个用于打开Html文件的intent
	private static Intent getHtmlFileIntent(String param) {

		Uri uri = Uri.parse(param).buildUpon()
				.encodedAuthority("com.android.htmlfileprovider")
				.scheme("content").encodedPath(param).build();
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.setDataAndType(uri, "text/html");
		return intent;
	}

	// Android获取一个用于打开图片文件的intent
	private static Intent getImageFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "image/*");
		return intent;
	}

	// Android获取一个用于打开PPT文件的intent
	private static Intent getPptFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
		return intent;
	}

	// Android获取一个用于打开Excel文件的intent
	private static Intent getExcelFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		return intent;
	}

	// Android获取一个用于打开Word文件的intent
	private static Intent getWordFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/msword");
		return intent;
	}

	// Android获取一个用于打开CHM文件的intent
	private static Intent getChmFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-chm");
		return intent;
	}

	// Android获取一个用于打开文本文件的intent
	private static Intent getTextFileIntent(String param, boolean paramBoolean) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		if (paramBoolean) {
			Uri uri1 = Uri.parse(param);
			intent.setDataAndType(uri1, "text/plain");
		} else {
			Uri uri2 = Uri.fromFile(new File(param));
			intent.setDataAndType(uri2, "text/plain");
		}
		return intent;
	}

	// Android获取一个用于打开PDF文件的intent
	private static Intent getPdfFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/pdf");
		return intent;
	}

	private static Intent getRarFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/x-rar-compressed");
		return intent;
	}

	private static Intent getZipFileIntent(String param) {

		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Uri uri = Uri.fromFile(new File(param));
		intent.setDataAndType(uri, "application/zip");
		return intent;
	}

	public static void deleteFileAndPath(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			if (files != null) {
				for (File f : files) {
					deleteFileAndPath(f);
				}
			}
		} else {
			dir.delete();
		}
	}

	public static void setImageInfo(ImageData image, String imageId) {
		String[] images = imageId.split(",");
		image.setImageId(images[0]);
		if (images.length > 1) {
			image.setImageDesc(images[1]);
		}
	}

	public static int getMenuImage(String menuClass) {
		Integer n = menuImageMap.get(menuClass);
		if (n != null) {
			return n.intValue();
		}
		return DEFAULT_MENU_IMAGE;
	}
}
