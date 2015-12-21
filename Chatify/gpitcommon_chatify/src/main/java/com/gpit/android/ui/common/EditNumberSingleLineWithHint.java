package com.gpit.android.ui.common;

import android.content.Context;
import android.util.AttributeSet;

public class EditNumberSingleLineWithHint extends EditTextSingleLineWithHint {
	public final static int MIN_NUMBER = 0;
	public final static int MAX_NUMBER = 20;
	
	private int prevValue = 0;
	private int prevCursorIdx = 0;
	public EditNumberSingleLineWithHint(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		int value;
		
		super.onTextChanged(arg0, arg1, arg2, arg3);
		
		try {
			value = Integer.parseInt(arg0.toString());
		} catch (Exception e) {
			value = 0;
		}
		
		if (value < MIN_NUMBER || value > MAX_NUMBER) {
			String setValue = String.valueOf(prevValue); 
			setText(setValue);
			requestFocus();
			setSelection(prevCursorIdx);
		} else {
			prevValue = value;
			prevCursorIdx = getSelectionStart();
		}
	}
	
	protected void onSelectionChanged (int selStart, int selEnd) {
		super.onSelectionChanged(selStart, selEnd);
		prevCursorIdx = selStart;
	}
}
