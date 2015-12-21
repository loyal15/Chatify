package com.gpit.android.web.feeder.base;

import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;

import android.content.Context;

public class HttpQueueItem {
	public Context context;
	public boolean isGet;
	public URL reqURL;
	public List<NameValuePair> postParms;
	public UsernamePasswordCredentials upc;
	public HttpFeederListener listener;
	public Exception e;
	
	public boolean useCache;
	public Object tag;
	public Object result;
	public StringBuffer resultStr;
	
	public HttpQueueItem() {}
	
	public HttpQueueItem(Context context, boolean isGet, URL reqURL, List<NameValuePair> parms, 
			HttpFeederListener listener, boolean useCache, Object result, Object tag) {
		this(context, isGet, reqURL, parms, listener, useCache, result, tag, null);
	}
	
	public HttpQueueItem(Context context, boolean isGet, URL reqURL, List<NameValuePair> parms, 
			HttpFeederListener listener, boolean useCache, Object result, Object tag, UsernamePasswordCredentials upc) {
		this.context = context;
		this.isGet = isGet;
		this.reqURL = reqURL;
		this.upc = upc;
		this.postParms = parms;
		this.listener = listener;
		
		this.useCache = useCache;
		this.result = result;
		this.tag = tag;
	}
}
