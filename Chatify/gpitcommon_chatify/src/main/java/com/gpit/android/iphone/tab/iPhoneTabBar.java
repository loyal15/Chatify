package com.gpit.android.iphone.tab;

import java.util.HashMap;

import com.gpit.android.library.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class iPhoneTabBar extends LinearLayout {
	// Status
	private boolean mIsFirst = false;
	private int mCurrSelectedTabID = -1;
	
	// Callback
	private iPhoneTabSelectedListener mListener;
	
	// Tab Items
	private HashMap<Integer, iPhoneTabItem> mTabs = new HashMap<Integer, iPhoneTabItem>();
	
	public iPhoneTabBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setOrientation(LinearLayout.HORIZONTAL);
	}

	@Override
	protected void onFinishInflate () {
		super.onFinishInflate();
		
		setBackgroundResource(R.drawable.tab_bg);
		
		// Register all of tabs
		iPhoneTabItem firstTab = null;
		for (int i = 0 ; i < getChildCount() ; i++) {
			iPhoneTabItem tabItem = (iPhoneTabItem) getChildAt(i);
			if (firstTab == null) firstTab = tabItem;
			
			tabItem.setTabBar(this);
			mTabs.put(tabItem.getId(), tabItem);
			
			tabItem.setTabSelected(false);
		}
		
		if (mTabs.size() > 0)
			select(firstTab.getId());
	}
	
	@Override
	protected void onLayout(boolean arg0, int l, int t, int r, int b) {
		super.onLayout(arg0, l, t, r, b);
		if (mIsFirst) {
			mIsFirst = false;
		}
	}
	
	/************************* Public API *******************************/
	public void setOnTabSelectedListener(iPhoneTabSelectedListener listener) {
		mListener = listener;
	}

	// Select specified tab
	public void select(int tabID) {
		if (mCurrSelectedTabID == tabID) {
			return;
		}
		
		// de-select original tab
		if (mCurrSelectedTabID != -1) {
			iPhoneTabItem orgTabItem = mTabs.get(mCurrSelectedTabID);
			if (orgTabItem == null)
				return;
			
			orgTabItem.setTabSelected(false);
		}
		
		mCurrSelectedTabID = tabID;
		
		if (mCurrSelectedTabID != -1) {
			iPhoneTabItem newTabItem = mTabs.get(mCurrSelectedTabID);
			if (newTabItem == null)
				return;
			
			newTabItem.setTabSelected(true);
			
			// call registered callback function
			if (mListener != null) {
				mListener.onTabItemSelected(newTabItem.getId());
			}
		}
	}
	
	public int getSelectedID() {
		return mCurrSelectedTabID;
	}
	
	public boolean onBack() {
		if (getSelectedID() != -1) {
			iPhoneTabItem tabItem = mTabs.get(getSelectedID());
			iPhoneTabViewer tabViewer = tabItem.getTabViewer();
			if (tabViewer != null) {
				return tabViewer.onBack();
			}
		}
		
		return false;
	}
	
	/************************* Package API *******************************/
	public void onTabSelected(int tabID) {
		select(tabID);
	}
	
	public iPhoneTabItem getSelectedTab() {
		int selectedID = getSelectedID();
		iPhoneTabItem tabItem = mTabs.get(selectedID);
		
		return tabItem;
	}
}
