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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.auth.NTLMEngine;
import org.apache.http.impl.auth.NTLMEngineException;
import org.apache.http.impl.auth.NTLMScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import com.gpit.android.util.ExtendedRunnable;
import com.gpit.android.web.cache.WebCacheConstant;
import com.gpit.android.web.cache.WebHtmlCache;

import junit.framework.Assert;

import android.app.Activity;
import android.content.Context;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.params.AuthPNames;

import android.util.Base64;
import android.util.Log;

public class HttpFeeder extends CommonFeeder implements Runnable {
	// Define singleton class
	public static HttpFeeder FEEDER;
	
	private static Header[] headers = new BasicHeader[1];
	
	private final static int SLEEP_MTIME = 100;
	public final static int HTTP_CONNECTION_MTIMEOUT = 1000 * 20;
	public final static int HTTP_MAXIMUM_RESPONSE_SIZE = 4096;
	
	/**
	 * Init Module
	 * @param context
	 */
	static {
		FEEDER = new HttpFeeder();
	}
	
	// Dispatch thread to send http request
	private Thread httpHandler;
	
	// Request queue
	private ConcurrentLinkedQueue<HttpQueueItem> requestQueue = new ConcurrentLinkedQueue<HttpQueueItem>(); 
	private ConcurrentLinkedQueue<HttpQueueItem> priorQueue = new ConcurrentLinkedQueue<HttpQueueItem>();
	
	private HttpFeeder() {
		super();
		
		headers[0] = new BasicHeader("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 5.0; Windows XP; DigExt)");
		
		// Start dispatch thread
		httpHandler = new Thread(this);
		httpHandler.start();
	}

	/**
	 * Retrieve response html from HTTP-GET Method
	 * @param getURL
	 * @param listener
	 * @param isBlocking
	 * Ignore useCache option
	 * @return
	 */
	public File getResponseForGetMethod(Context context, URL getURL, 
			HttpFeederListener listener, Object tag, boolean useCache, UsernamePasswordCredentials upc)  throws Exception {
		StringBuffer response = new StringBuffer(HTTP_MAXIMUM_RESPONSE_SIZE);
		response.setLength(0);
		
		HttpQueueItem newItem = new HttpQueueItem(context, true, getURL, null, listener, useCache, 
				response, tag, upc);
		File result = null;
		
		if (listener == null) {
			result = postRequestItem(newItem, useCache);
		} else {
			requestQueue.offer(newItem);
		}
		
		return result;
	}
	
	public File getResponseForGetMethod(Context context, URL getURL, 
			HttpFeederListener listener, Object tag, boolean useCache)  throws Exception {
		return getResponseForGetMethod(context, getURL, listener, tag, useCache, null);
	}
	
	/**
	 * Retrieve response html from HTTP-POST Method
	 * @param getURL
	 * @param listener
	 * @param isBlocking
	 * Ignore useCache option
	 * @return
	 */
	public File getResponseForPostMethod(Context context, URL postURL,
			List<NameValuePair> parms, HttpFeederListener listener, 
			Object tag, boolean useCache, boolean isPriorier, UsernamePasswordCredentials upc) throws Exception {
		StringBuffer response = new StringBuffer(HTTP_MAXIMUM_RESPONSE_SIZE);
		HttpQueueItem newItem = new HttpQueueItem(context, false, postURL, parms, listener, useCache, 
				response, tag, upc);
		
		File result = null;
		
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
	
	public File getResponseForPostMethod(Context context, URL postURL,
			List<NameValuePair> parms, HttpFeederListener listener, 
			Object tag, boolean useCache, boolean isPriorier) throws Exception {
		return getResponseForPostMethod(context, postURL, parms, listener, tag, useCache, isPriorier, null);
	}

	/**
	 * Retrieve response html from HTTP-GET Method
	 * @param getURL
	 * @param listener
	 * @param isBlocking
	 * Ignore useCache option
	 * @return
	 */
	public StringBuffer getResponseStrForGetMethod(Context context, URL getURL, 
			HttpFeederListener listener, Object tag, boolean useCache)  throws Exception {
		return getResponseStrForGetMethod(context, getURL, listener, tag, useCache, null);
	}
	
	public StringBuffer getResponseStrForGetMethod(Context context, URL getURL, 
			HttpFeederListener listener, Object tag, boolean useCache, UsernamePasswordCredentials upc)  throws Exception {
		StringBuffer response = new StringBuffer(HTTP_MAXIMUM_RESPONSE_SIZE);
		HttpQueueItem newItem = new HttpQueueItem(context, true, getURL, null, listener, useCache, 
				response, tag, upc);
		
		if (listener == null) {
			response = postRequestItemToGetStr(newItem, useCache);
		} else {
			response = null;
			requestQueue.offer(newItem);
		}
		
		return response;
	}
	
	/**
	 * Retrieve response html from HTTP-POST Method
	 * @param getURL
	 * @param listener
	 * @param isBlocking
	 * Ignore useCache option
	 * @return
	 */
	public StringBuffer getResponseStrForPostMethod(Context context, URL postURL,
			List<NameValuePair> parms, HttpFeederListener listener, 
			Object tag, boolean useCache, boolean isPriorier) throws Exception {
		return getResponseStrForPostMethod(context, postURL, parms, listener, tag, useCache, isPriorier, null);
	}
	
	public StringBuffer getResponseStrForPostMethod(Context context, URL postURL,
			List<NameValuePair> parms, HttpFeederListener listener, 
			Object tag, boolean useCache, boolean isPriorier, UsernamePasswordCredentials upc) throws Exception {
		StringBuffer response = new StringBuffer(HTTP_MAXIMUM_RESPONSE_SIZE);
		HttpQueueItem newItem = new HttpQueueItem(context, false, postURL, parms, listener, useCache, 
				response, tag, upc);
		
		if (listener == null) {
			response = postRequestItemToGetStr(newItem, useCache);
		} else {
			response = null;
			
			if(isPriorier)
				priorQueue.offer(newItem);
			else
				requestQueue.offer(newItem);
		}
		
		return response;
	}
	
	/**
	 * Execute request item directly
	 * @param item
	 * @return
	 */
	public File postRequestItem(HttpQueueItem item, boolean useCache) throws Exception {
		File responseFile = null;
		
		StringBuffer response = (StringBuffer)item.result;
		// Get response from cache
		if (useCache) {
			// Thread.sleep(100);
			responseFile = WebHtmlCache.CACHE.loadCachedHtml(item.reqURL, item.isGet, 
					item.postParms, response);
		}
		
		if (responseFile == null) {
			ResponseType result;
			if (item.isGet)
				result = postHttpGetItem(item);
			else
				result = postHttpPostItem(item);
			
			if (result == ResponseType.RESPONSE_SUCCESS) {
				// Put response to cache storage
				responseFile = WebHtmlCache.CACHE.putHtmlToCacheStroage(item.reqURL, item.isGet, item.postParms, response);
			}
		}
		
		// Set response to the queue item
		item.result = (Object)responseFile;
		
		return responseFile;
	}
	
	
	/**
	 * Execute request item directly
	 * @param item
	 * @return
	 */
	public StringBuffer postRequestItemToGetStr(HttpQueueItem item, boolean useCache) throws Exception {
		StringBuffer response = (StringBuffer)item.result;
		File responseFile = null;
		
		Assert.assertTrue(useCache == false);
		
		// Get response from cache
		if (useCache) {
			// Thread.sleep(100);
			responseFile = WebHtmlCache.CACHE.loadCachedHtml(item.reqURL, item.isGet, 
					item.postParms, response);
		}
		
		if (responseFile == null) {
			@SuppressWarnings("unused")
			ResponseType result;
			
			if (item.isGet)
				result = postHttpGetItem(item);
			else
				result = postHttpPostItem(item);
			
			return (StringBuffer)item.result; 
		}
		
		Assert.assertTrue(false);
		
		// Set response to the queue item
		item.result = (Object)responseFile;
		
		return null;
	}
	 
	public void setCredential(final UsernamePasswordCredentials aUpc, HttpClient client, HttpRequestBase request, String host) {
		if (aUpc == null)
			return;
		
		/*
		String credentials = Base64.encodeToString((aUpc.getUserName()+":"+aUpc.getPassword()).getBytes(), Base64.DEFAULT);
		request.addHeader("Authorization", "Basic "+credentials);
		*/
		
		/*
        AuthScope as = new AuthScope(host, 80);
        ((AbstractHttpClient) client).getCredentialsProvider()
                .setCredentials(as, aUpc);
		*/
		
		/*
        request.setHeader("Authorization", "Basic " + 
        		 BasicAuth.encode(aUpc.getUserName(), aUpc.getPassword()));
        */
		
		/*
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(aUpc.getUserName(), aUpc.getPassword().toCharArray());
			}
		});
		*/
		
		// /*
		try {
			Header header = new BasicScheme().authenticate(aUpc,
					request);
			request.addHeader(header);
		} catch (AuthenticationException e) {
			e.printStackTrace();
		}
		// */
		
		/*
		((DefaultHttpClient) httpclient).getCredentialsProvider()
				.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
						new UsernamePasswordCredentials(item.upc.getUserName(), item.upc.getPassword()));

		httpRequest.getParams().setParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
		 */
		
		/*
		Credentials creds = new UsernamePasswordCredentials(aUpc.getUserName(), aUpc.getPassword());        
        ((AbstractHttpClient) client).getCredentialsProvider().setCredentials(
                        new AuthScope(host, 80), creds);
        */
	}
	
	/**
	 * Execute HTTP-GET request item directly
	 * @param item
	 * @return
	 */
	public ResponseType postHttpGetItem(HttpQueueItem item) throws Exception {
		Assert.assertTrue(item.isGet);
		Assert.assertTrue(item.reqURL != null && !item.reqURL.equals(""));
		
		Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "HTTP-GET: " + item.reqURL);
		
		String line;
		
		HttpGet httpRequest = null;

		HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_CONNECTION_MTIMEOUT);// Set the default socket timeout (SO_TIMEOUT) // in milliseconds which is the timeout for waiting for data.  
        HttpConnectionParams.setSoTimeout(httpParameters, HTTP_CONNECTION_MTIMEOUT * 2);  
        
        httpRequest = new HttpGet(item.reqURL.toURI());
        // httpRequest.setHeaders(headers);
        
        HttpClient httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Custom user agent");
        setCredential(item.upc, httpclient, httpRequest, item.reqURL.getHost());
        
        HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);

        HttpEntity entity = response.getEntity();
        BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
        InputStream in = bufHttpEntity.getContent();
        
        /*
		URLConnection conn = item.reqURL.openConnection();
		conn.setConnectTimeout(HTTP_CONNECTION_MTIMEOUT);
		//sendin the base64 encoded credentials thru d header
		if (item.upc != null) {
		    conn.setRequestProperty(
		        "Authorization",
		        "Basic "+ BasicAuth.encode(item.upc.getUserName(), item.upc.getPassword()));
		}

		InputStream in = conn.getInputStream();
		*/
        
		BufferedReader reader = new BufferedReader(new InputStreamReader(in,
				"UTF-8"));

		// Clear buffer
		StringBuffer resposeStr = (StringBuffer)item.result;
		Assert.assertTrue(resposeStr != null);
		resposeStr.setLength(0);
		while ((line = reader.readLine()) != null) {
			resposeStr.append(line);
		}
		in.close();
		
		Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "HTTP-GET-RESULT: " + resposeStr);
		
		if (resposeStr.length() == 0) {
			return ResponseType.RESPONSE_FAILED;
		} else {
			item.resultStr = resposeStr;
			return ResponseType.RESPONSE_SUCCESS;
		}
		
	}
	
	/**
	 * Execute HTTP-POST request item directly
	 * @param item
	 * @return
	 */
	public ResponseType postHttpPostItem(HttpQueueItem item) throws Exception {
		Assert.assertTrue(!item.isGet);
		Assert.assertTrue(item.reqURL != null && !item.reqURL.equals(""));
		
		Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "HTTP-POST: " + item.reqURL);
		if (item.postParms != null) {
			for (int i = 0 ; i < item.postParms.size() ; i++) {
				NameValuePair pair = item.postParms.get(i);
				Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "\n\t\t" + pair.getName() + " = " + pair.getValue());
			}
		}
		
		// Create a new HttpClient and Post Header
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		HttpConnectionParams.setConnectionTimeout(httpParameters, HTTP_CONNECTION_MTIMEOUT);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		HttpConnectionParams.setSoTimeout(httpParameters, HTTP_CONNECTION_MTIMEOUT * 2);

	    HttpClient httpclient = new DefaultHttpClient(httpParameters);
	    httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Custom user agent");
	    
	    HttpPost httppost = new HttpPost(item.reqURL.toString());
	    httppost.setHeaders(headers);
	    
	    setCredential(item.upc, httpclient, httppost, item.reqURL.getHost());

	    // Set connection timeout. MUST BE CHECKED
	    // conn.setConnectTimeout(HTTP_CONNECTION_MTIMEOUT);
	    
        // Add your data
	    if (item.postParms != null)
	    	httppost.setEntity(new UrlEncodedFormEntity(item.postParms));

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
		
	    Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "HTTP-POST-RESULT: " + response);
	    
	    if (response.length() == 0) {
			return ResponseType.RESPONSE_FAILED;	
	    } else {
	    	item.resultStr = response;
			return ResponseType.RESPONSE_SUCCESS;
	    }
	}
	
	/**
	 * Dispatch request items
	 */
	@Override
	public void run() {
		while (!httpHandler.isInterrupted()) {
			HttpQueueItem item = priorQueue.poll();
			if(item == null)
				item = requestQueue.poll();
			if (item != null) {
				try {
					File htmlFile = postRequestItem(item, item.useCache);
					item.result = (Object)htmlFile;
					if (item.listener != null && item.result != null) {
						if (item.context instanceof Activity) {
							((Activity)(item.context)).runOnUiThread(new ExtendedRunnable(item) {
								@Override
								public void run() {
									HttpQueueItem queueItem = (HttpQueueItem)item;
									queueItem.listener.onSuccess(queueItem);
								}
							});
						} else {
							HttpQueueItem queueItem = (HttpQueueItem)item;
							queueItem.listener.onSuccess(queueItem);
						}
						continue;
					}
				} catch (Exception e) {
					Log.v(WebCacheConstant.WEB_CACHE_MODULE_NAME, "HttpFeeding Error: " + e.getMessage());
					item.e = e;
				}
				if (item.listener != null) {
					if (item.context instanceof Activity) {
						((Activity)(item.context)).runOnUiThread(new ExtendedRunnable(item) {
							@Override
							public void run() {
								HttpQueueItem queueItem = (HttpQueueItem)item;
								queueItem.listener.onFailed(queueItem);
							}
						});
					} else {
						HttpQueueItem queueItem = (HttpQueueItem)item;
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
