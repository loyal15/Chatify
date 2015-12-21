package com.gpit.android.iphone.tab;

import com.gpit.android.library.R;

import junit.framework.Assert;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class iPhoneTabItem extends LinearLayout {
	private View mTabItemLayout;
	private iPhoneTabBar mTabBar;
	
	private iPhoneTabViewer mTabViewer;
	private int mTextID;
	private int mIconID, mIconTouchedID;
	
	private boolean mIsSelected = false;
	
	// UI Components
	private LinearLayout mLLTabLayout;
	private ImageView mIVTabIcon;
	private TextView mTVName;
	
	public iPhoneTabItem(Context context, AttributeSet attrs) {
		super(context, attrs);
	
		// Init components
		mTextID = attrs.getAttributeResourceValue(null, "text", 0);
		mIconID = attrs.getAttributeResourceValue(null, "icon", 0);
		mIconTouchedID = attrs.getAttributeResourceValue(null, "icon_touched", 0);
		
		setOnClickListener(mTabClickListener);
	}
	
	@Override
	protected void onFinishInflate () {
		super.onFinishInflate();
		
		// Set properties
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTabItemLayout = inflater.inflate(R.layout.tab_item, null);
		if (mTabItemLayout != null) {
			addView(mTabItemLayout);
			
			LayoutParams parms = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			parms.weight = 1;
			setLayoutParams(parms);
			
			setBackgroundDrawable(null);
			
			// Retrieve components
			mLLTabLayout = (LinearLayout) mTabItemLayout.findViewById(R.id.llLayout);
			mIVTabIcon = (ImageView) mTabItemLayout.findViewById(R.id.ivIcon);
			mTVName = (TextView) mTabItemLayout.findViewById(R.id.tvName);
			
			onUpdateUI();
		}
	}
	
	public void setTabViewer(iPhoneTabViewer tabViewer) {
		mTabViewer = tabViewer;
		
		setTabSelected(mIsSelected);
	}
	
	public iPhoneTabViewer getTabViewer() {
		return mTabViewer;
	}
	
	private void onUpdateUI() {
		if (mIsSelected) {
			mLLTabLayout.setBackgroundResource(R.drawable.tab_selected_touchable);
			
			if (mIconTouchedID != 0)
				mIVTabIcon.setImageResource(mIconTouchedID);
			
			mTVName.setTextColor(Color.WHITE);
			if (mTextID != 0)
				mTVName.setText(mTextID);
		} else {
			mLLTabLayout.setBackgroundResource(R.drawable.tab_touchable);
			
			if (mIconID != 0)
				mIVTabIcon.setImageResource(mIconID);
			
			mTVName.setTextColor(Color.GRAY);
			if (mTextID != 0)
				mTVName.setText(mTextID);
		}
	}
	
	private OnClickListener mTabClickListener = new OnClickListener() {
		public void onClick(View v) {
			mTabBar.onTabSelected(getId());
		}
	};
	
	/*********************************** Package API *****************************************/
	void setTabBar(iPhoneTabBar tabBar) {
		mTabBar = tabBar;
	}
	
	void setTabSelected(boolean bSelected) {
		mIsSelected = bSelected;
		
		onUpdateUI();
		
		if (mTabViewer == null)
			return;
		mTabViewer.setVisibility(mIsSelected ? View.VISIBLE : View.GONE);
		if (mIsSelected) {
			mTabViewer.onResume();
		} else {
			mTabViewer.onPause();
		}
	}
}
