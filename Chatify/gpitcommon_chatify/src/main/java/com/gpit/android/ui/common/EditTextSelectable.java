package com.gpit.android.ui.common;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextSelectable extends EditText {
	public interface onSelectionChangedListener {
		public void onSelectionChanged(int selStart, int selEnd);
	}

	private List<onSelectionChangedListener> listeners;

	public EditTextSelectable(Context context) {
		super(context);
		listeners = new ArrayList<onSelectionChangedListener>();
	}

	public EditTextSelectable(Context context, AttributeSet attrs) {
		super(context, attrs);
		listeners = new ArrayList<onSelectionChangedListener>();
	}

	public EditTextSelectable(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		listeners = new ArrayList<onSelectionChangedListener>();
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
}