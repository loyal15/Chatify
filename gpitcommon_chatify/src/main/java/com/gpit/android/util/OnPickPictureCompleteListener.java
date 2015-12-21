package com.gpit.android.util;

public interface OnPickPictureCompleteListener {
	public void onCompleted(String path);
	public void onFailed(Exception exception);
}
