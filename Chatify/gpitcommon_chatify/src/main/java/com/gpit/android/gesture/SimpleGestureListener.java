package com.gpit.android.gesture;

import android.view.MotionEvent;

public interface SimpleGestureListener {
	void onSwipe(int direction);
	void onDoubleTap();
	void onSingleTap(MotionEvent e);
}