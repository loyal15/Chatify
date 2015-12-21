package com.gpit.android.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.gpit.android.library.R;

public class ExtendedSimpleTextSpinner extends ExtendedSpinner {
	public final static int DEFAULT_ITEM_TEXT_COLOR = Color.WHITE;
	
	private ExtendedSimpleTextSpinnerAdapter mAdapter;
	
	private int mItemTextColor;
	private String[] mContents;
	private String mHeaderHint;
	
	public ExtendedSimpleTextSpinner(Context context) {
		this(context, null);
	}

	public ExtendedSimpleTextSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setAdapter(ExtendedSimpleTextSpinnerAdapter adapter) {
		super.setAdapter(adapter);
	}
	
	public void setContents(String[] contents) {
		mContents = contents;
		mAdapter.setContents(mContents);
		
		notifyDataSetChanged();
	}
	
	public void setSelection(String text) {
		if (text == null)
			return;
		
		for (int i = 0 ; i < mContents.length ; i++) {
			String content = mContents[i];
			if (content.equals(text)) {
				setSelection(i);
				break;
			}
		}
	}
	
	public String getSelection() {
		if (getSelectedIndex() < 0)
			return null;
		
		return mContents[getSelectedIndex()];
	}
	
	/************************ INITIALIZATION ***************************/
	protected void initAttrs(AttributeSet attrs) {
		super.initAttrs(attrs);
		
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.ExtendedSimpleTextSpinner);
		
		mItemTextColor = a.getColor(R.styleable.ExtendedSimpleTextSpinner_itemTextColor, DEFAULT_ITEM_TEXT_COLOR);
		
		int contentArrayId = a.getResourceId(R.styleable.ExtendedSimpleTextSpinner_contents, 0);
		if (contentArrayId != 0) {
			try {
				mContents = getResources().getStringArray(contentArrayId);
			} catch (Exception e) {
				// Ensure to working at the Mockview
			}
		}
		
		mHeaderHint = a.getString(R.styleable.ExtendedSimpleTextSpinner_hint);
		
		a.recycle();
	}
	
	protected void initUI() {
		super.initUI();
		
		mAdapter = new ExtendedSimpleTextSpinnerAdapter();
		mAdapter.setContext(getContext());
		mAdapter.setTextColor(mItemTextColor);
		mAdapter.setHint(mHeaderHint);
		mAdapter.setContents(mContents);
		setAdapter(mAdapter);
	}
}
