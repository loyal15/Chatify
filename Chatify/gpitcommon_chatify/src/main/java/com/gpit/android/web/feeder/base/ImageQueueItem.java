package com.gpit.android.web.feeder.base;

import android.content.Context;
import android.graphics.Bitmap;

public class ImageQueueItem {
	public Context context;
	public String reqURL;
	public Bitmap result;
	public int width, height;
	public boolean useCache;
	
	public ImageFeederListener listener;
	public Object tag;
	
	public Exception e;
	
	public ImageQueueItem() {}
	
	public ImageQueueItem(Context context, String reqURL, int width, int height, boolean useCache, 
			ImageFeederListener listener, Object tag) {
		this.context = context;
		this.reqURL = reqURL;
		this.width = width;
		this.height = height;
		this.useCache = useCache;
		this.listener = listener;
		this.tag = tag;
	}
}
