package com.gpit.android.gesture;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View.OnLongClickListener;

import com.gpit.android.util.Utils;

public class LongTouchGesture {
	// Long Touch Handler
	private final static int LONG_TOUCH_DELAY = 3000;
	// private final static float TOUCH_MOVE_SENSITIVE = 0.75f;
	private int mTouchDelay = LONG_TOUCH_DELAY;
	
	private Activity mActivity;
	
	private OnLongClickListener mListener;
	
	private TouchManager mTouchManager = new TouchManager(2);
	
	private int mLastEventAction;
	private Thread mLongClickDetecter;
	private long mTouchLastTime;
	private boolean mIsReqDie = false;
	
	public LongTouchGesture(Activity activity) {
		mActivity = activity;
		
		// Start long-click detector
        mLongClickDetecter = new Thread(mLongClickDetectRunnable);
        mLongClickDetecter.start();
	}
	
	public void stop() {
		mIsReqDie = true;
    	if (mLongClickDetecter != null && mLongClickDetecter.isAlive()) {
    		mLongClickDetecter.interrupt();
    		try {
				mLongClickDetecter.join();
			} catch (InterruptedException e) {
			}
    	}
	}
	
	public void setTouchDelay(int delay) {
		mTouchDelay = delay;
	}
	
	public void onTouch(MotionEvent event) {
		mLastEventAction = event.getAction();
		mTouchManager.update(event);
		
		int touchDiff = 0;
		for (int i = 0 ; i < mTouchManager.getPressCount() ; i++) {
			touchDiff += mTouchManager.moveDelta(i).getLength();
		}
		
		if (event.getAction() != MotionEvent.ACTION_MOVE || touchDiff != 0) {
			mTouchLastTime = Utils.getTimeMilis();
		}
	}
	
	public void setOnLongTouchListener(OnLongClickListener listener) {
		mListener = listener;
	}
	
    private Runnable mLongClickDetectRunnable = new Runnable() {
		@Override
		public void run() {
			mTouchLastTime = Utils.getTimeMilis();
			long nowTime = Utils.getTimeMilis();
			
			while(!mLongClickDetecter.isInterrupted() && !mIsReqDie) {
				nowTime = Utils.getTimeMilis();
				// if (mLastEvent != null)
				// 	Log.d(FacePlantApp.LOG_TAG, "last touch action: " + String.valueOf(mLastEvent.getAction()));
				if (mLastEventAction == MotionEvent.ACTION_DOWN || mLastEventAction == MotionEvent.ACTION_MOVE) {
					long diff = nowTime - mTouchLastTime;
					if (diff > mTouchDelay) {
						if (mListener != null) {
							mActivity.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									mListener.onLongClick(null);
								}
								
							});
						}
						
						mTouchLastTime = Utils.getTimeMilis();
					}
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
		}
    };
}
