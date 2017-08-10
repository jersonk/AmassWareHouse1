package com.amassfreight.base.net;

import org.apache.http.Header;

import com.amassfreight.domain.MobileError;

public class AmassHttpResponseHandler<T> {
	protected void OnSuccess(T response){}
	protected void OnSuccess(byte[] response){}
	protected void onFailure(int statusCode, Header[] headers,
			String responseBody, Throwable e){}
	public void onFailure(int statusCode, Header[] headers, byte[] binaryData,
			Throwable e) {
		
	}
	public void onErrMsg(MobileError err) {
		
	}
}
