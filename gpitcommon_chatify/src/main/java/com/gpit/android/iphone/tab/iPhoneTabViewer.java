package com.gpit.android.iphone.tab;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public abstract class iPhoneTabViewer extends LinearLayout {
	protected int mTabBarID;
	
	public iPhoneTabViewer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void setTabBarID(int tabBarID) {
		mTabBarID = tabBarID;
	}
	
	public abstract void onPause();
	public abstract void onResume();
	public abstract boolean onBack();
}
