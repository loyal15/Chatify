package com.gpit.android.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gpit.android.library.R;

public class CustomTextViewNoDependOnBackgroundSize extends TextView {
	private int mNoDependBackground;
	
	public CustomTextViewNoDependOnBackgroundSize(Context context) {
		super(context);
		
		initUI();
	}
	
	public CustomTextViewNoDependOnBackgroundSize(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initAttrs(attrs);
		initUI();
	}
	
	public CustomTextViewNoDependOnBackgroundSize(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		initAttrs(attrs);
		initUI();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void setBackgroundResource(int resId) {
		BitmapDrawableNoMinimumSize bitmapDrawable = new BitmapDrawableNoMinimumSize(getResources(), resId);
		setBackgroundDrawable(bitmapDrawable);
	}

	private void initAttrs(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.CustomTextViewNoDependOnBackgroundSize);
		mNoDependBackground = a.getResourceId(R.styleable.CustomTextViewNoDependOnBackgroundSize_backgroundColor, 0);
		
		a.recycle();
	}
	
	@SuppressWarnings("deprecation")
	private void initUI() {
		if (mNoDependBackground != 0) {
			BitmapDrawableNoMinimumSize bitmapDrawable = new BitmapDrawableNoMinimumSize(getResources(), mNoDependBackground);
			setBackgroundDrawable(bitmapDrawable);
		}
	}
}
