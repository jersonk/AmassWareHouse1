/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android.camera;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

final class PreviewCallback implements Camera.PreviewCallback {

	private static final String TAG = PreviewCallback.class.getSimpleName();

	private final CameraConfigurationManager configManager;
	private Handler previewHandler;
	private int previewMessage;

	PreviewCallback(CameraConfigurationManager configManager) {
		this.configManager = configManager;
	}

	void setHandler(Handler previewHandler, int previewMessage) {
		this.previewHandler = previewHandler;
		this.previewMessage = previewMessage;
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		byte[] des = new byte[data.length];
	//	Point cameraSrc = camera.get
		Point cameraResolution = configManager.getCameraResolution();
//		YUVRotate90(des, data, cameraResolution.y, cameraResolution.x);
		Handler thePreviewHandler = previewHandler;
		if (cameraResolution != null && thePreviewHandler != null) {
			Message message = thePreviewHandler.obtainMessage(previewMessage,
					cameraResolution.x, cameraResolution.y, data);
			message.sendToTarget();
			previewHandler = null;
		} else {
			Log.d(TAG,
					"Got preview callback, but no handler or resolution available");
		}
	}

	private void YUVRotate90(byte[] des, byte[] src, int width, int height) {
		int i = 0, j = 0, n = 0;
		int hw = width / 2, hh = height / 2;
		for (j = width; j > 0; j--)
			for (i = 0; i < height; i++) {
				des[n++] = src[width * i + j];
			}

		// unsigned char *ptmp = src+width*height;
		for (j = hw; j > 0; j--){
			for (i = 0; i < hh; i++) {
				// des[n++] = ptmp[hw*i+j];
				des[n++] = src[width * height + hw * i + j];
			}
		}
		// ptmp = src+width*height*5/4;
		for (j = hw; j > 0; j--){
			for (i = 0; i < hh; i++) {
				// des[n++] = ptmp[hw*i+j];
				des[n++] = src[width * height * 5 / 4 + hw * i + j];
			}
		}
	}
}
