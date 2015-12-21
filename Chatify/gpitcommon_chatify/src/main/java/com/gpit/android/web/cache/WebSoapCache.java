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
import java.util.List;

import junit.framework.Assert;

import org.apache.http.NameValuePair;

import com.gpit.android.util.Utils;

import android.content.Context;

public class WebSoapCache extends WebCache {
	// Define singleton class
	public static WebSoapCache CACHE;
	
	// Buffer to get cacheable path
	private StringBuffer mBuffer = new StringBuffer(WebCacheConstant.MAX_URL_PATH);
	
	/**
	 * Init Module
	 * @param context
	 */
	public static void init(Context context) {
		CACHE = new WebSoapCache(context);
	}
	
	protected WebSoapCache(Context context) {
		super(context);
		
		Utils.ensureDir(mContext, WebCacheConstant.APPLICATION_RES_SOAP_FOLDER);
	}
	
	/**
	 * Load cached html from file stroage
	 * @param url
	 * @param isGet
	 * @param parms
	 * @param buffer
	 * @return
	 */
	public File loadCachedHtml(String url, String method, String body, List<NameValuePair> parms, StringBuffer buffer) {
		buffer.setLength(0);
		
		File cachedFile = getCachedFile(url, method, body, parms);
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
	public File putHtmlToCacheStroage(String url, String method, String body, List<NameValuePair> parms, StringBuffer buffer) {
		File cachedFile = getCachedFile(url, method, body, parms);
		
		try {
			// Create new file
			cachedFile.createNewFile();
			
			FileOutputStream fout = new FileOutputStream(cachedFile);
			
			String htmlStr;
			
			Assert.assertTrue(buffer != null);
			htmlStr = buffer.toString();
			fout.write(htmlStr.getBytes("UTF-8"));
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
	public boolean isCached(String url, String method, String body, List<NameValuePair> parms) {
		File cachedFile;
		
		cachedFile = getCachedFile(url, method, body, parms);
		
		return cachedFile.exists();
	}

	/**
	 * Retrieve cached file
	 * @param url
	 * @param isGet
	 * @param parms
	 * @return
	 */
	private File getCachedFile(String url, String method, String body, List<NameValuePair> parms) {
		String cachedPath = getCacheablePath(url, method, body, parms);
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
	public synchronized String getCacheablePath(String url, String method, String body, List<NameValuePair> parms) {
		String cacheablePath;
		
		mBuffer.setLength(0);
		// Add url
		mBuffer.append(url);
		
		// Add method
		if (method != null)
			mBuffer.append(method);
		
		// Add body
		if (body != null)
			mBuffer.append(body);
		
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
				WebCacheableResourceType.WEB_CACHEABLE_RESOURCE_SOAP,
				mBuffer.toString());
		
		return cacheablePath;
	}

	public void clearAll() {
		Utils.deleteRecursive(new File(WebCacheConstant.APPLICATION_RES_SOAP_FOLDER));
		Utils.ensureDir(mContext, WebCacheConstant.APPLICATION_RES_SOAP_FOLDER);
	}
}
