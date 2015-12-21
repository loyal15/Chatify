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

import com.gpit.android.util.Utils;
import com.gpit.android.web.cache.WebCacheConstant;
import com.gpit.android.web.cache.WebHtmlCache;
import com.gpit.android.web.cache.WebImageCache;
import com.gpit.android.web.cache.WebSoapCache;
import com.gpit.android.web.cache.WebXMLCache;

import android.content.Context;

public class FeederManager { 
	// singleton object
	public static FeederManager MANAGER;
	
	public static boolean isInitialized() {
		return MANAGER != null;
	}
	
	public static void init(Context context) {
		MANAGER = new FeederManager(context);
	}
	
	// defines some various feeder
	public HttpFeeder httpFeeder;
	
	// Application context
	private Context mContext;
	
	private FeederManager(Context context) {
		mContext = context;
		
		// ensure cache directory
		ensureCacheDir();
		
		// load all cache module
		WebImageCache.init(context);
		WebHtmlCache.init(context);
		WebXMLCache.init(context);
		WebSoapCache.init(context);
		
	}
	
	/**
	 * Ensure cache directory
	 */
	private void ensureCacheDir() {
		// Ensure root directory
		Utils.ensureDir(mContext, WebCacheConstant.APPLICATION_RES_CACHE_FOLDER);
		
	}
}
