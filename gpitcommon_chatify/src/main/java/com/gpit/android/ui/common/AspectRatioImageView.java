package com.gpit.android.ui.common;

import com.gpit.android.library.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AspectRatioImageView extends ImageView {
	private boolean mRatioByWidth;
	
    public AspectRatioImageView(Context context) {
        super(context);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initAttrs(attrs);
    }

    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initAttrs(attrs);
    }

    public void initAttrs(AttributeSet attrs) {
    	TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AspectRatioImageView);
        mRatioByWidth = a.getBoolean(R.styleable.AspectRatioImageView_ratio_by_width, true);
		a.recycle();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        
        if (width != 0 && height != 0) {
        	if (getDrawable() != null) {
		        if (mRatioByWidth) {
		        	height = (int)(width * ((float)getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth()));
		        } else {
		        	width = (int)(height * ((float)getDrawable().getIntrinsicWidth() / getDrawable().getIntrinsicHeight()));
		        }
        	}
        }
        
       	setMeasuredDimension(width, height);
    	
    	/*
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		
		// Log.d("+++mRatioByWidth", String.valueOf(mRatioByWidth));
		// Log.d("+++width", String.valueOf(width));
		// Log.d("+++height", String.valueOf(height));
		
		if (mRatioByWidth)
			height = width * (getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth());
		else
			width = height * (getDrawable().getIntrinsicWidth() / getDrawable().getIntrinsicHeight());
		
		// Log.d("---width", String.valueOf(width));
		// Log.d("---height", String.valueOf(height));
		
		setMeasuredDimension(width, height);
		*/
    }
}
