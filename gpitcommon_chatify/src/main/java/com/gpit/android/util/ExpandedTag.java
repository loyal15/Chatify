package com.gpit.android.util;

public class ExpandedTag {
	public Object tag;
	public ExpandedTag more;
	
	public ExpandedTag(Object tag, ExpandedTag more) {
		this.tag = tag;
		this.more = more;
	}
}
