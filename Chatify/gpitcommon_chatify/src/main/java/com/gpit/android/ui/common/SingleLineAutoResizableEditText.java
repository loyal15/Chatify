package com.gpit.android.ui.common;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.EditText;

public class SingleLineAutoResizableEditText extends EditText {
	public interface onSelectionChangedListener {
		public void onSelectionChanged(int selStart, int selEnd);
	}

	private List<onSelectionChangedListener> listeners;
	private int mMinWidth = -1;
	
	public SingleLineAutoResizableEditText(Context context) {
		super(context);
		listeners = new ArrayList<onSelectionChangedListener>();
		
		setMaxLines(1);
		addTextChangedListener(mTextWatcher);
	}

	public SingleLineAutoResizableEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		listeners = new ArrayList<onSelectionChangedListener>();
		
		setMaxLines(1);
		addTextChangedListener(mTextWatcher);
	}

	public SingleLineAutoResizableEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		listeners = new ArrayList<onSelectionChangedListener>();
		
		setMaxLines(1);
		addTextChangedListener(mTextWatcher);
	}

	public void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
		if (mMinWidth < 0) {
			mMinWidth = right - left;
		}
	}
	public void addOnSelectionChangedListener(onSelectionChangedListener o) {
		listeners.add(o);
	}

	protected void onSelectionChanged(int selStart, int selEnd) {
		if (listeners == null)
			return;
		
		for (onSelectionChangedListener l : listeners)
			l.onSelectionChanged(selStart, selEnd);
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (getLayoutParams() != null) {
				ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) getLayoutParams();
				params.width = calcTextWidth(s.toString());
			
				if (params.width > 0)
					setLayoutParams(params);
			}
		}
	};
	
	private int calcTextWidth(String s) {
		Paint paint = getPaint();
		int width = (int) paint.measureText(s);
		width += getPaddingLeft() + getPaddingRight();
		
		try {
			if (width < mMinWidth)
				width = mMinWidth;
		} catch (Exception e) {
			width = -1;
		}
		
		return width;
	}
} 