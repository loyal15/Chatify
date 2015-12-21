package com.gpit.android.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.gpit.android.library.R;

public class SquareFrameLayout extends FrameLayout {
	private boolean mKeepWidth;
	
    public SquareFrameLayout(Context context) {
        super(context);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        
        initAttrs(attrs);
    }

    public SquareFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initAttrs(attrs);
    }

    public void initAttrs(AttributeSet attrs) {
    	TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SquareFrameLayout);
        mKeepWidth = a.getBoolean(R.styleable.SquareFrameLayout_keep_width, true);
		a.recycle();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        
        if (mKeepWidth)
        	height = width;
        else
        	width = height;
        
        setMeasuredDimension(width, height);
    }
}
