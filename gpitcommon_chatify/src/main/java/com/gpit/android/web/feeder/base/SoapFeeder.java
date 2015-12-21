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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.gpit.android.util.ExtendedRunnable;
import com.gpit.android.web.cache.WebCacheConstant;
import com.gpit.android.web.cache.WebSoapCache;

import junit.framework.Assert;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class SoapFeeder extends CommonFeeder implements Runnable {
	// Define singleton class
	public static SoapFeeder FEEDER;
	
	private final static int SLEEP_MTIME = 50;
	public final static int HTTP_CONNECTION_MTIMEOUT = 1000 * 60;
	public final static int HTTP_MAXIMUM_RESPONSE_SIZE = 4096;
	
	/**
	 * Init Module
	 * @param context
	 */
	static {
		FEEDER = new SoapFeeder();
	}
	
	// Dispatch thread to send http request
	private Thread httpHandler;
	
	// Request queue
	private ConcurrentLinkedQueue<SoapQueueItem> requestQueue = new ConcurrentLinkedQueue<SoapQueueItem>(); 
	private ConcurrentLinkedQueue<SoapQueueItem> priorQueue = new ConcurrentLinkedQueue<SoapQueueItem>();
	
	private SoapFeeder() {
		super();
		
		// Start dispatch thread
		httpHandler = new Thread(this);
		httpHandler.start();
	}
	
	/**
	 * Retrieve response html from HTTP-POST Method
	 * @param getURL
	 * @param listener
	 * @param isBlocking
	 * Ignore useCache option
	 * @return
	 */
	public Node getSoapResponse(Context context, String url, String xmlns, String method,
			List<NameValuePair> parms, SoapFeederListener listener, 
			Object tag, boolean useCache, boolean isPriorier) throws Exception {
		StringBuffer response = new StringBuffer(HTTP_MAXIMUM_RESPONSE_SIZE);
		SoapQueueItem newItem = new SoapQueueItem(context, url, xmlns, method, parms, listener, useCache, 
				response, tag);
		
		Node result = null;
		
		if (listener == null) {
			result = postRequestItem(newItem, useCache);
		} else {
			if(isPriorier)
				priorQueue.offer(newItem);
			else
				requestQueue.offer(newItem);
		}
		
		return result;
	}
	
	/**
	 * Retrieve response html from HTTP-POST Method
	 * @param getURL
	 * @param listener
	 * @param isBlocking
	 * Ignore useCache option
	 * @return
	 */
	public Node getSoapResponse(String url, String method, String header, String footer, String body, 
			List<NameValuePair> parms, SoapFeederListener listener, 
			Object tag, boolean useCache, boolean isPriorier) throws Exception {
		StringBuffer response = new StringBuffer(HTTP_MAXIMUM_RESPONSE_SIZE);
		SoapQueueItem newItem = new SoapQueueItem(url, method, header, footer, body, parms, listener, useCache, 
				response, tag);
		
		Node result = null;
		
		if (listener == null) {
			result = postRequestItem(newItem, useCache);
		} else {
			if(isPriorier)
				priorQueue.offer(newItem);
			else
				requestQueue.offer(newItem);
		}
		
		return result;
	}

	/**
	 * Execute request item directly
	 * @param item
	 * @return
	 */
	public Node postRequestItem(SoapQueueItem item, boolean useCache) throws Exception {
		File responseFile = null;
		Node result = null;
		
		Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "SOAP: " + item.url + " METHOD: " + item.method);
		
		StringBuffer response = (StringBuffer)item.result;
		// Get response from cache
		if (useCache) {
			Thread.sleep(100);
			Assert.assertTrue(item.body != null);
			responseFile = WebSoapCache.CACHE.loadCachedHtml(item.url, item.method, 
					item.body, item.postParms, response);
		}
		
		if (responseFile == null) {
			ResponseType resultType;
			resultType = postSoapItem(item);
			
			if (resultType == ResponseType.RESPONSE_SUCCESS) {
				// Put response to cache stroage
				responseFile = WebSoapCache.CACHE.putHtmlToCacheStroage(item.url, item.method, 
						item.body, item.postParms, response);
			}
		}
		
		if (responseFile != null) {
			response.setLength(0);
			
			FileInputStream fio = new FileInputStream(responseFile);
			DataInputStream dio = new DataInputStream(fio);
			String line;
			
			while ((line = dio.readLine()) != null) {
				response.append(line);
			}
			dio.close();
			
			// Parse soap result
			result = parseSoapResult(item.method, response.toString());
		}
		
		Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "SOAP-RESULT: " + response);
		
		return result;
	}
	
	/**
	 * Execute SOAP request item directly
	 * @param item
	 * @return
	 */
	public ResponseType postSoapItem(SoapQueueItem item) throws Exception {
		Assert.assertTrue(item.method != null && !item.method.equals(""));
		String soapMsg = null;
		
		Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "SOAP: " + item.method);
		if (item.postParms != null) {
			for (int i = 0 ; i < item.postParms.size() ; i++) {
				NameValuePair pair = item.postParms.get(i);
				Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "\n\t\t" + pair.getName() + " = " + pair.getValue());
			}
		}
		soapMsg = SoapMessageHandler.createSoapMessage(item);
		
		// Create a new HttpClient and Post Header
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_CONNECTION_MTIMEOUT);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, HTTP_CONNECTION_MTIMEOUT);

	    HttpClient httpclient = new DefaultHttpClient(httpParameters);
	    HttpPost httppost = new HttpPost(item.url);
		StringEntity se = new StringEntity(soapMsg, HTTP.UTF_8);

		se.setContentType("text/xml");  
		httppost.setHeader("Content-Type", "application/soap+xml;charset=UTF-8");
		httppost.setEntity(se);  

        // Execute HTTP Post Request
        HttpResponse resp = httpclient.execute(httppost);
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(
        		resp.getEntity().getContent(), "UTF-8"));
		String line;

		StringBuffer response = (StringBuffer)item.result;
		Assert.assertTrue(response != null);
		// Clear buffer
		response.setLength(0);
		while ((line = reader.readLine()) != null) {
			response.append(line);
		}
		reader.close();
		
	    Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "SOAP-POST-RESULT: " + response);
	    
	    if (response.length() == 0)
			return ResponseType.RESPONSE_FAILED;
		else
			return ResponseType.RESPONSE_SUCCESS;
	}
	
	/**
	 * Prase Soap result
	 */
	public Node parseSoapResult(String method, String buffer) {
		Document doc = null;
		Node result = null;
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = new ByteArrayInputStream(buffer.getBytes("UTF-8"));
			
			doc = db.parse(is);
			
			doc.getDocumentElement().normalize();
			
			NodeList nodeList = doc.getElementsByTagName(method + "Response");
			result = nodeList.item(0);
		} catch (Exception e) {
			Log.e(WebCacheConstant.WEB_CACHE_MODULE_NAME, "parse soap error: soap = " + buffer);
		}
		
		return result;
	}
	
	/**
	 * Dispatch request items
	 */
	@Override
	public void run() {
		while (!httpHandler.isInterrupted()) {
			SoapQueueItem item = priorQueue.poll();
			if(item == null)
				item = requestQueue.poll();
			if (item != null) {
				try {
					Node result = postRequestItem(item, item.useCache);
					item.result = (Object)result;
					if (item.listener != null && item.result != null) {
						if (item.context instanceof Activity) {
							((Activity)(item.context)).runOnUiThread(new ExtendedRunnable(item) {
								@Override
								public void run() {
									SoapQueueItem queueItem = (SoapQueueItem)item;
									queueItem.listener.onSuccess(queueItem);
								}
							});
						} else {
							SoapQueueItem queueItem = (SoapQueueItem)item;
							queueItem.listener.onSuccess(queueItem);
						}
						continue;
					}
				} catch (Exception e) {
					Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "Soap Error: " + e.getMessage());
					item.e = e;
				}
				if (item.listener != null) {
					if (item.context instanceof Activity) {
						((Activity)(item.context)).runOnUiThread(new ExtendedRunnable(item) {
							@Override
							public void run() {
								SoapQueueItem queueItem = (SoapQueueItem)item;
								queueItem.listener.onFailed(queueItem);
							}
						});
					} else {
						SoapQueueItem queueItem = (SoapQueueItem)item;
						queueItem.listener.onFailed(queueItem);
					}
				}
			}
			try {
				Thread.sleep(SLEEP_MTIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void clearAllPendingItems()
	{
		requestQueue.clear();
	}
}
