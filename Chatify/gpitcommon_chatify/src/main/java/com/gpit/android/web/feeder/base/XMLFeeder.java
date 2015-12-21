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
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Assert;

import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.gpit.android.io.UnicodeBOMInputStream;
import com.gpit.android.web.cache.WebCacheConstant;

import android.content.Context;
import android.util.Log;

public class XMLFeeder extends CommonFeeder implements HttpFeederListener {
	public class XMLFeederTag {
		public HttpFeederListener listener;
		public Object tag;
		
		public XMLFeederTag (HttpFeederListener listener, Object tag) {
			this.listener = listener;
			this.tag = tag;
		}
	}
	// Define singleton class
	public static XMLFeeder FEEDER;
	public final static int XML_MAXIMUM_SIZE = HttpFeeder.HTTP_MAXIMUM_RESPONSE_SIZE;
	
	/**
	 * Init Module
	 * @param context
	 */
	static {
		FEEDER = new XMLFeeder();
	}
	
	private XMLFeeder() {
		super();
	}

	/**
	 * Load xml from file or network storage
	 * @param url
	 * @return
	 */
	public Document getResponseForGetMethod(Context context, URL getURL, 
			HttpFeederListener listener, Object tag, boolean useCache) throws Exception {
		return getResponseForGetMethod(context, getURL, listener, tag, useCache, null);
	}
	
	public Document getResponseForGetMethod(Context context, URL getURL, 
			HttpFeederListener listener, Object tag, boolean useCache, UsernamePasswordCredentials upc) throws Exception {
		Document root = null;
		File result;
		
		XMLFeederTag xmlFeederTag = new XMLFeederTag(listener, tag);
		
		// Retrieve html page from url
		result = HttpFeeder.FEEDER.getResponseForGetMethod(context, 
				getURL, ((listener != null) ? this : null), xmlFeederTag, useCache, upc);
		if (result != null) {
			root = parseXML(result);
		}
		
		return root;
	}
	
	/**
	 * Retrieve response xml from file or network storage
	 * @param getURL
	 * @param listener
	 * @param isBlocking
	 * @return
	 */
	public Document getResponseForPostMethod(Context context, URL postURL, List<NameValuePair> parms, 
			HttpFeederListener listener, Object tag, boolean useCache, boolean priority) throws Exception {
		return getResponseForPostMethod(context, postURL, parms, listener, tag, useCache, priority, null);
	}
	
	public Document getResponseForPostMethod(Context context, URL postURL, List<NameValuePair> parms, 
			HttpFeederListener listener, Object tag, boolean useCache, boolean priority, UsernamePasswordCredentials upc) throws Exception {
		Document root = null;
		File result;
		
		XMLFeederTag xmlFeederTag = new XMLFeederTag(listener, tag);
		
		// Retrieve html page from url
		result = HttpFeeder.FEEDER.getResponseForPostMethod(context, 
				postURL, parms, ((listener != null) ? this : null), xmlFeederTag, useCache, priority);
		if (result != null) {
			root = parseXML(result);
		}
		
		return root;
	}
	
	/**
	 * Parse xml entry from buffer of string
	 * @param xml
	 * @return
	 */
	public static Document parseXML(File xmlFile) {
		Document doc = null;
		
		try {
			FileInputStream fin = new FileInputStream(xmlFile);
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			UnicodeBOMInputStream ubin = new UnicodeBOMInputStream(fin);
			ubin.skipBOM();
			BufferedInputStream bin = new BufferedInputStream(ubin);
			
			// skip special characters to avoid parse-error at xml parser
			int c = 0;
			while (c <= 32) {
				bin.mark(XML_MAXIMUM_SIZE);
				c = bin.read();
			}
			bin.reset();
			
			doc = db.parse(bin);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(WebCacheConstant.WEB_CACHE_MODULE_NAME, "parse xml erro: xml = " + xmlFile);
		}
		
		return doc;
	}

	/**
	 * Prase Soap result
	 */
	public static Document parseXMLFromStr(String buffer) {
		Document doc = null;
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(buffer.getBytes("UTF-8"));
			
			doc = db.parse(is);
			
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
			Log.e(WebCacheConstant.WEB_CACHE_MODULE_NAME, "parse soap error: soap = " + buffer);
		}
		
		return doc;
	}
	
	@Override
	public void onSuccess(HttpQueueItem item) {
		File htmlFile = (File)item.result;
		XMLFeederTag xmlFeederTag = (XMLFeederTag)item.tag;
		HttpFeederListener listener = (HttpFeederListener)xmlFeederTag.listener;
		
		// Parse xml from buffer of string
		item.result = parseXML(htmlFile);
		if (!item.useCache)
			htmlFile.deleteOnExit();
		item.tag = xmlFeederTag.tag;
		Assert.assertTrue(listener != null);
		if (item.result == null)
			listener.onFailed(item);
		else
			listener.onSuccess(item);
	}

	@Override
	public void onFailed(HttpQueueItem item) {
		// Must be checked
		XMLFeederTag xmlFeederTag = (XMLFeederTag)item.tag;
		HttpFeederListener listener = (HttpFeederListener)xmlFeederTag.listener;
		
		item.tag = xmlFeederTag.tag;
		Assert.assertTrue(listener != null);
		listener.onFailed(item);
	}
	
	public static String getNodeValue(NodeList list, int index) {
		String nodeValue = "";

		try {
			nodeValue = list.item(index).getChildNodes().item(0).getNodeValue();
		} catch (Exception e) {}
		
		return nodeValue;
	}
}
