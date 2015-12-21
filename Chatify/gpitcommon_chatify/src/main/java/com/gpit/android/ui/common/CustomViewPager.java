package com.gpit.android.ui.common;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class CustomViewPager extends ViewPager{

	public CustomViewPager (Context context) {
		super(context);
	}

	public CustomViewPager (Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;

		final View tab = getChildAt(0);
		
		if ( tab != null ) {

			int width = getMeasuredWidth();

			if (wrapHeight) {
				// Keep the current measured width.
				widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
			}

			int fragmentHeight = measureFragment(((Fragment) getAdapter().instantiateItem(this, getCurrentItem())).getView());
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(fragmentHeight, MeasureSpec.AT_MOST);
			
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public int measureFragment(View view) {
		if (view == null)
			return 0;

		view.measure(0, 0);
		return view.getMeasuredHeight();
	}
}