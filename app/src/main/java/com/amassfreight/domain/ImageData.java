package com.amassfreight.domain;

import android.graphics.Bitmap;

public class ImageData {
	private Bitmap data;
	private String path;
	private String url;
	private String fileUploadId;
	private String imageDesc;
	public String getImageDesc() {
		return imageDesc;
	}
	public void setImageDesc(String imageDesc) {
		this.imageDesc = imageDesc;
	}
	public String getFileUploadId() {
		return fileUploadId;
	}
	public void setFileUploadId(String fileUploadId) {
		this.fileUploadId = fileUploadId;
	}
	private boolean downloading;
	
	public boolean isDownloading() {
		return downloading;
	}
	public void setDownloading(boolean downloading) {
		this.downloading = downloading;
	}
	private String imageId;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Bitmap getData() {
		return data;
	}
	public void setData(Bitmap data) {
		this.data = data;
	}
	
	public String getImageId() {
		return imageId;
	}
	public void setImageId(String imageId) {
		this.imageId = imageId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
