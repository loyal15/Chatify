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

// ! NOT IMPLEMETNED YET !
package com.gpit.android.web.cache;

import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;

public class WebXMLCache extends WebCache {
	// Define singleton class
	public static WebXMLCache CACHE;
	
	public static void init(Context context) {
		CACHE = new WebXMLCache(context);
	}
	
	protected WebXMLCache(Context context) {
		super(context);
		
		// Utils.ensureDir(mContext, LMKConstant.RES_XML_FOLDER);
	}
	
	public boolean loadCachedXML(URL url, boolean isGet, List<NameValuePair> parms, StringBuffer buffer) {
		buffer.setLength(0);
		
		return false;
	}

	public boolean isCached(URL url, boolean isGet, List<NameValuePair> parms) {
		return false;
	}

	public static void clearAll() {
		// Utils.deleteRecursive(new File(Utils.getFilePath(mContext, WebCacheConstant.RES_XML_FOLDER)));
	}

}
