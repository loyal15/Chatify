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

package com.gpit.android.web.cache;

import java.io.File;
import java.io.FileOutputStream;

import com.gpit.android.util.ImageScaleType;
import com.gpit.android.util.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.util.Log;

public class WebImageCache extends WebCache {
	public static WebImageCache CACHE;
	
	// Buffer to get cacheable path
	private StringBuffer mBuffer = new StringBuffer(WebCacheConstant.MAX_URL_PATH);
	
	public static void init(Context context) {
		CACHE = new WebImageCache(context);
	}
	
	
	private WebImageCache(Context context) {
		super(context);
		
		Utils.ensureDir(mContext, getResourcePath(WebCacheableResourceType.WEB_CACHEABLE_RESOURCE_IMAGE, ""), false);
	}

	/**
	 * Load cached image from stroage
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	public Bitmap loadCachedImage(String path, int width, int height) {
		String cachedPath = getCacheablePath(path);
		
		Bitmap bmp = null;
		
		try {
			bmp = Utils.makeThumb(cachedPath, width, height, 0, ImageScaleType.SCALE_FIT_PROPER, false);
		} catch (OutOfMemoryError error) {
			// retry again
			System.gc();
			
			try {
				bmp = Utils.makeThumb(cachedPath, width, height, 0, ImageScaleType.SCALE_FIT_PROPER, false);
			} catch (OutOfMemoryError ofe) {
				Log.w(WebCacheConstant.WEB_CACHE_MODULE_NAME, ofe.getMessage());
				bmp = null;
			}
		} catch (Exception e) {
		}
		
		return bmp;
	}
	
	/**
	 * Put html contents to file stroage
	 * @param path
	 * @param bmp
	 * @return
	 */
	public File putImageToCacheStroage(String path, Bitmap bmp) {
		File cachedFile = getCachedFile(path);
		
		try {
			// Create new file
			cachedFile.createNewFile();

			FileOutputStream out = new FileOutputStream(cachedFile);
			bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
		} catch (Exception e) {
			return null;
		}

		return cachedFile;
	}
	
	/**
	 * Is cached?
	 * @param url
	 * @param isGet
	 * @param parms
	 * @return
	 */
	public boolean isCached(String path) {
		File cachedFile;
		
		cachedFile = getCachedFile(path);
		
		return cachedFile.exists();
	}

	/**
	 * Retrieve cached file
	 * @param url
	 * @param isGet
	 * @param parms
	 * @return
	 */
	private File getCachedFile(String path) {
		String cachedPath = getCacheablePath(path);
		File cachedFile = new File(cachedPath);
		
		return cachedFile;
	}
	
	/**
	 * Retrieve cache-able path from url & params
	 * @param url
	 * @param isGet
	 * @param parms
	 * @return
	 */
	private synchronized String getCacheablePath(String path) {
		String cacheablePath;
		
		mBuffer.setLength(0);
		// Add image path
		mBuffer.append(path);
		
		// Normalize
		Utils.normalizePath(mBuffer);
		
		// Append base path
		cacheablePath = getResourcePath(
				WebCacheableResourceType.WEB_CACHEABLE_RESOURCE_IMAGE,
				mBuffer.toString());
		
		return cacheablePath;
	}


	public void clearAll() {
		Utils.deleteRecursive(new File(Utils.getFilePath(mContext, WebCacheConstant.APPLICATION_RES_IMAGE_FOLDER)));
		Utils.ensureDir(mContext, WebCacheConstant.APPLICATION_RES_IMAGE_FOLDER);
	}
}
