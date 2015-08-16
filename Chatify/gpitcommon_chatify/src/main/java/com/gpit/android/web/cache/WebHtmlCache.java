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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.List;

import junit.framework.Assert;

import org.apache.http.NameValuePair;

import com.gpit.android.util.Utils;

import android.content.Context;
import android.util.Log;

public class WebHtmlCache extends WebCache {
	// Define singleton class
	public static WebHtmlCache CACHE;
	
	// Buffer to get cacheable path
	private StringBuffer mBuffer = new StringBuffer(WebCacheConstant.MAX_URL_PATH);
	
	/**
	 * Init Module
	 * @param context
	 */
	public static void init(Context context) {
		CACHE = new WebHtmlCache(context);
	}
	
	protected WebHtmlCache(Context context) {
		super(context);
		
		Utils.ensureDir(mContext, getResourcePath(WebCacheableResourceType.WEB_CACHEABLE_RESOURCE_IMAGE, ""), false);
	}
	
	/**
	 * Load cached html from file stroage
	 * @param url
	 * @param isGet
	 * @param parms
	 * @param buffer
	 * @return
	 */
	public File loadCachedHtml(URL url, boolean isGet, List<NameValuePair> parms, StringBuffer buffer) {
		buffer.setLength(0);
		
		File cachedFile = getCachedFile(url, isGet, parms);
		if (!cachedFile.exists())
			return null;
		
		// Load html from cached file
		Assert.assertTrue(buffer != null);
		try {
			FileInputStream fin = new FileInputStream(cachedFile);
			byte[] readBuffer = new byte[256];
			
			int ret = 0;
			while (ret != -1){
				ret = fin.read(readBuffer, 0, readBuffer.length);
				buffer.append(new String(readBuffer));
			};
			
			fin.close();
		} catch (Exception e) {
			buffer.setLength(0);
			return null;
		}
		
		return cachedFile;
	}

	/**
	 * Put html contents to file stroage
	 * @param url
	 * @param isGet
	 * @param parms
	 * @param buffer
	 * @return
	 */
	public File putHtmlToCacheStroage(URL url, boolean isGet, List<NameValuePair> parms, StringBuffer buffer) {
		File cachedFile = getCachedFile(url, isGet, parms);
		
		try {
			// Create new file
			cachedFile.deleteOnExit();
			cachedFile.createNewFile();
			
			FileOutputStream fout = new FileOutputStream(cachedFile);
			
			String htmlStr;
			byte[] htmlBytes;
			
			Assert.assertTrue(buffer != null);
			htmlStr = buffer.toString();
			// MUST BE CHECKED ON ANOTHER PROJECTS
			htmlBytes = htmlStr.getBytes("UTF-8");
			// htmlBytes = htmlStr.getBytes("ISO-8859-1");
			fout.write(htmlBytes);
			fout.close();
		} catch (Exception e) {
			buffer.setLength(0);
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
	public boolean isCached(URL url, boolean isGet, List<NameValuePair> parms) {
		File cachedFile;
		
		cachedFile = getCachedFile(url, isGet, parms);
		
		return cachedFile.exists();
	}

	/**
	 * Retrieve cached file
	 * @param url
	 * @param isGet
	 * @param parms
	 * @return
	 */
	private File getCachedFile(URL url, boolean isGet, List<NameValuePair> parms) {
		String cachedPath = getCacheablePath(url, isGet, parms);
		File cachedFile = new File(cachedPath);
		
		return cachedFile;
	}
	
	/**
	 * Retrieve cacheable path from url & params
	 * @param url
	 * @param isGet
	 * @param parms
	 * @return
	 */
	public synchronized String getCacheablePath(URL url, boolean isGet, List<NameValuePair> parms) {
		String cacheablePath;
		
		mBuffer.setLength(0);
		// Add method type first
		mBuffer.append(isGet);
		mBuffer.append("_");
		// Add url
		mBuffer.append(url.toString());
		// Add params
		if (parms != null) {
			for (int i = 0 ; i < parms.size() ; i++) {
				NameValuePair pair = parms.get(i);
				mBuffer.append(",");
				mBuffer.append(pair.getName());
				mBuffer.append("=");
				mBuffer.append(pair.getValue());
			}
		}
		
		// Normalize
		Utils.normalizePath(mBuffer);
		
		// Append base path
		cacheablePath = getResourcePath(
				WebCacheableResourceType.WEB_CACHEABLE_RESOURCE_HTML,
				mBuffer.toString());
		
		return cacheablePath;
	}

	public void clearAll() {
		Utils.deleteRecursive(new File(Utils.getFilePath(mContext, WebCacheConstant.APPLICATION_RES_HTML_FOLDER)));
		Utils.ensureDir(mContext, WebCacheConstant.APPLICATION_RES_HTML_FOLDER);
	}
}
