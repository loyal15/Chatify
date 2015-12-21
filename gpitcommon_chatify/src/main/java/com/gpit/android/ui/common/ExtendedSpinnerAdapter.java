package com.gpit.android.ui.common;

import android.view.View;

public interface ExtendedSpinnerAdapter {
	public int getCount();
	
	public View getHeaderView();
	public View getItemView(int index);
}
