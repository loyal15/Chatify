package com.gpit.android.ui.common;

import android.graphics.Bitmap;

public interface OnWebImageUpdatedListener {
	public void onUpdated(WebImageView imageView, Bitmap bitmap);
	public void onFailed(WebImageView imageView, Exception e);
}
