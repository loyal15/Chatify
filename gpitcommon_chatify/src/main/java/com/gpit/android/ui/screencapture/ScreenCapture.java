package com.gpit.android.ui.screencapture;

import android.graphics.Bitmap;
import android.view.View;

public class ScreenCapture {
	public Bitmap capture(View rootView) {
		Bitmap bitmap;
		
		if (rootView == null)
			return null;
		
		View v1 = rootView.getRootView();
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);
		
		return bitmap;
	}
}