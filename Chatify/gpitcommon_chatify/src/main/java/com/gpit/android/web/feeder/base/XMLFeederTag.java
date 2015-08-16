package com.gpit.android.web.feeder.base;

public class XMLFeederTag {
	public HttpFeederListener listener;
	public Object tag;
	
	public XMLFeederTag (HttpFeederListener listener, Object tag) {
		this.listener = listener;
		this.tag = tag;
	}
}