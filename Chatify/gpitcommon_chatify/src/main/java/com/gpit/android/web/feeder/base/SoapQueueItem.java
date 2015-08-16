package com.gpit.android.web.feeder.base;

import java.util.List;

import org.apache.http.NameValuePair;

import android.content.Context;

public class SoapQueueItem {
	public Context context;
	public String url;
	public boolean isNativeMode = false;
	// native mode: false
	public String xmlns;
	public String method;
	
	// native mode: false
	public String header;
	public String footer;
	public String body;
	
	public List<NameValuePair> postParms;
	public SoapFeederListener listener;
	public Exception e;
	
	public boolean useCache;
	public Object tag;
	public Object result;
	
	public SoapQueueItem(Object result) {
		this.result = result;
	}
	
	public SoapQueueItem(Context context, String url, String xmlns, String method, List<NameValuePair> parms, 
			SoapFeederListener listener, boolean useCache, Object result, Object tag) {
		this.context = context;
		this.url = url;
		this.xmlns = xmlns;
		this.method = method;
		this.postParms = parms;
		this.listener = listener;
		
		this.useCache = useCache;
		this.result = result;
		this.tag = tag;
	}
	
	public SoapQueueItem(String url, String method, String header, String footer, String body, List<NameValuePair> parms, 
			SoapFeederListener listener, boolean useCache, Object result, Object tag) {
		isNativeMode = true;
		this.url = url;
		this.method = method;
		this.header = header;
		this.footer = footer;
		this.body = body;
		this.postParms = parms;
		this.listener = listener;
		
		this.useCache = useCache;
		this.result = result;
		this.tag = tag;
	}
}
