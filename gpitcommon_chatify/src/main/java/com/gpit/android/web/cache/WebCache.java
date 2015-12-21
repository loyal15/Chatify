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

import com.gpit.android.util.Utils;

import junit.framework.Assert;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public abstract class WebCache {
	private final static String PREFS_FIRST_LOADED = "app_first_loaded"; 
	
	public Context mContext;
	
	protected WebCache(Context context) {
		mContext = context;
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		SharedPreferences.Editor prefsEditor = prefs.edit();
		if (prefs.getBoolean(PREFS_FIRST_LOADED, true)) {
			// clear all directory
			String dirPath = getResourcePath(WebCacheableResourceType.WEB_CACHEABLE_RESOURCE_HTML, "");
			Utils.deleteDir(new File(dirPath));
			
			dirPath = getResourcePath(WebCacheableResourceType.WEB_CACHEABLE_RESOURCE_SOAP, "");
			Utils.deleteDir(new File(dirPath));
			
			dirPath = getResourcePath(WebCacheableResourceType.WEB_CACHEABLE_RESOURCE_IMAGE, "");
			Utils.deleteDir(new File(dirPath));
			prefsEditor.putBoolean(PREFS_FIRST_LOADED, false);
			prefsEditor.commit();
		}
	}
	
	public String getResourcePath(WebCacheableResourceType resType, String resName) {
		String resPath = null;
		
		switch (resType) {
		case WEB_CACHEABLE_RESOURCE_HTML:
			resPath = WebCacheConstant.APPLICATION_RES_HTML_FOLDER + resName;
			break;
		case WEB_CACHEABLE_RESOURCE_SOAP:
			resPath = WebCacheConstant.APPLICATION_RES_SOAP_FOLDER + resName;
			break;
		case WEB_CACHEABLE_RESOURCE_IMAGE:
			resPath = WebCacheConstant.APPLICATION_RES_IMAGE_FOLDER + resName;
			break;
		default:
			Assert.assertTrue("Not supported cache type" != null);
		}
		
		File cacheFile;
		String sdState = android.os.Environment.getExternalStorageState();
		if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
			cacheFile = mContext.getExternalCacheDir();    
	    } else {
	    	cacheFile = mContext.getCacheDir();
	    }
		// resPath = cacheFile.getAbsolutePath() + "/" + resPath;
		resPath = cacheFile.getAbsolutePath() + "/" + resName;
		
		return resPath;
	}
}
