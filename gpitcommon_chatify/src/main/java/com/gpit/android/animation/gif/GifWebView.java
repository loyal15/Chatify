package com.gpit.android.animation.gif;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;

public class GifWebView extends WebView {
	private final static int DETFAULT_SCALING = 100;
	int mGifWidth;
	
	public GifWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setPath(String path, int width) {
		mGifWidth = width;
		
        getSettings().setSupportZoom(true);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setScrollbarFadingEnabled(false);
        setOnTouchListener(onWebViewTouchListener);
        
        loadUrl(path);
        onUpdateUI();
	}
	
	private void onUpdateUI() {
		float scaling;
		int width = getMeasuredWidth();
		if (width <= 0)
			return;
		
		// Fit scale with gif width
		scaling = (((float)width / mGifWidth) * DETFAULT_SCALING);
		scaling = (int) Math.floor(scaling); 
		setInitialScale((int)scaling);
	}
	
	public void onWindowFocusChanged (boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		
		onUpdateUI();
	}
	
	// Disable touch event
	private OnTouchListener onWebViewTouchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			return true;
		}
	};
}
