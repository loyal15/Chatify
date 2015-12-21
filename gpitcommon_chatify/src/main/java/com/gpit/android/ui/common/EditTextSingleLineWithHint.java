package com.gpit.android.ui.common;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;

public class EditTextSingleLineWithHint extends EditText implements OnKeyListener {
	private OnKeyListener mKeyListener;
	private OnTextChangedListener mTextChangedListener;
	private String mPrevText = "";
	
	public EditTextSingleLineWithHint(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		super.setOnKeyListener(this);
	}

	public boolean onKey(View v, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			if (mKeyListener != null)
				mKeyListener.onKey(v, keyCode, event);
			return true;
		}
		
		return false;
	}
	
	public void setOnTextChangedListener(OnTextChangedListener listener) {
		mTextChangedListener = listener;
	}
	
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		super.onTextChanged(arg0, arg1, arg2, arg3);
		
		String currText = arg0.toString();
		if (arg0 == null ||
			mPrevText == currText || 
			(arg0.toString() != null && mPrevText != null && mPrevText.equals(currText)))
			return;
		
		mPrevText = currText;
		
		if (mTextChangedListener != null) {
			mTextChangedListener.onTextChanged(arg0, arg1, arg2, arg3);
		}
	}
}
