/*
 * WEB Cache Module
 * Copyright (C) 2011 ZheXue Ding
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
 * 
 */

package com.gpit.android.web.feeder.base;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import junit.framework.Assert;

import com.gpit.android.util.ExtendedRunnable;
import com.gpit.android.web.cache.WebCacheConstant;
import com.gpit.android.web.cache.WebImageCache;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class ImageFeeder extends CommonFeeder {
	public static ImageFeeder FEEDER;
	private final static int SLEEP_MTIME = 50;
	
	public final static int IMAGE_CONNECTION_MTIMEOUT = 1000 * 10;
	public final static int IMAGE_READ_MTIMEOUT = 1000 * 30;
	
	static {
		FEEDER = new ImageFeeder();
	}
	
	// Request queue
	private ConcurrentLinkedQueue<ImageQueueItem> requestQueue = 
		new ConcurrentLinkedQueue<ImageQueueItem>();
	
	private ImageFeeder() {
		super();
		
		// Start dispatch thread
		Timer timer = new Timer();
		timer.schedule(timerTask, SLEEP_MTIME, SLEEP_MTIME);
	}

	/**
	 * Get Image from network resource or cache storage
	 * @param url
	 * @param width
	 * @param height
	 * @param useCache
	 * @param listener
	 * @param tag
	 * @return
	 */
	public Bitmap getImage(Context context, String url, int width, int height, boolean useCache, 
			ImageFeederListener listener, Object tag) throws Exception {
		Assert.assertTrue(url != null && !url.equals(""));

		Bitmap bitmap = null;
		
		try {
			ImageQueueItem newItem = new ImageQueueItem(context, url, width, height, useCache, 
					listener, tag);
			if (listener == null) {
				bitmap = postRequestItem(newItem, useCache);
			} else {
				requestQueue.offer(newItem);
			}
		} catch (Exception e) {
			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}
			Log.w(WebCacheConstant.WEB_CACHE_MODULE_NAME, e.getMessage());
			throw e;
		}
		
		return bitmap;
	}
	
	/**
	 * Execute request item directly
	 * @param item
	 * @return
	 */
	public Bitmap postRequestItem(ImageQueueItem item, boolean useCache) throws Exception {
		Bitmap bitmap;
		
		try {
			bitmap = _postRequestItem(item, useCache);
		} catch (OutOfMemoryError e) {
			System.gc();
			bitmap = _postRequestItem(item, useCache);
		}
		
		return bitmap;
	}

	public Bitmap _postRequestItem(ImageQueueItem item, boolean useCache) throws Exception {
		Bitmap bitmap;
		
		if (useCache) {
			bitmap = WebImageCache.CACHE.loadCachedImage(item.reqURL, item.width, item.height);
			if (bitmap != null)
				return bitmap;
		}
		 
		// Load bitmap from internet
		URL imgURL = new URL(item.reqURL);
		URLConnection conn = imgURL.openConnection();
		conn.setConnectTimeout(IMAGE_CONNECTION_MTIMEOUT);
		conn.setReadTimeout(IMAGE_READ_MTIMEOUT);
		conn.setDoInput(true);
        
		InputStream in = conn.getInputStream();
		BufferedInputStream bi = new BufferedInputStream(in);
		bitmap = BitmapFactory.decodeStream(bi);
		
		// put image to cache storage
		WebImageCache.CACHE.putImageToCacheStroage(item.reqURL, bitmap);
		bitmap.recycle();
		
		// Get bitmap again from cache storage
		bitmap = WebImageCache.CACHE.loadCachedImage(item.reqURL, item.width, item.height);
		
		// Set response to the queue item
		item.result = bitmap;
		
		return bitmap;
	}
	
	/**
	 * Dispatch request items
	 */
	TimerTask timerTask = new TimerTask() {
		@Override
		public void run() {
			ImageQueueItem item = requestQueue.poll();
			
			// Changes the Priority of the calling Thread!
			android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_LOWEST);
			
			if (item != null) {
				try {
					Bitmap bitmap = postRequestItem(item, item.useCache);
					item.result = bitmap;
					((Activity)(item.context)).runOnUiThread(new ExtendedRunnable(item) {
						@Override
						public void run() {
							ImageQueueItem item = (ImageQueueItem)this.item;
							if (item.listener != null)
								item.listener.onSuccess(item);
						}
					});
				} catch (Exception e) {
					item.e = e;
					
					((Activity)(item.context)).runOnUiThread(new ExtendedRunnable(item) {
						@Override
						public void run() {
							ImageQueueItem item = (ImageQueueItem)this.item;
							if (item.listener != null)
								item.listener.onFailed(item);
						}
					});
				}
			}
		}
	};
	
	public void clearAllPendingItems()
	{
		requestQueue.clear();
	}
}
