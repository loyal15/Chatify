package com.gpit.android.ui.common;

import com.gpit.android.util.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		String fontName = attrs.getAttributeValue(null, "font");
		if (fontName != null && !fontName.equals(""))
			Utils.setAssetFont(context, this, fontName);
	}

	private OnSizeChangedListener sizeChangedListener;
	private OnLayoutChangedListener layoutChangedListener;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// TODO Auto-generated method stub
		super.onSizeChanged(w, h, oldw, oldh);
		
		if(sizeChangedListener != null)
			sizeChangedListener.onSizeChanged(w, h, oldw, oldh);
	}

	public void setOnSizeChangedListener(OnSizeChangedListener listener) {
		sizeChangedListener = listener;
	}

	public interface OnSizeChangedListener {
		void onSizeChanged(int w, int h, int oldw, int oldh);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (layoutChangedListener != null)
			layoutChangedListener.onLayout(changed, left, top, right, bottom);
	}

	public void setLayoutChangedListener(
			OnLayoutChangedListener layoutChangedListener) {
		this.layoutChangedListener = layoutChangedListener;
	}

	public interface OnLayoutChangedListener {
		void onLayout(boolean changed, int l, int t, int r, int b);
	}

}
