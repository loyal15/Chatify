package com.gpit.android.ui.common;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.gpit.android.library.R;
import com.gpit.android.util.ExtendedRunnable;

import junit.framework.Assert;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;

public class HorizontalWheel extends FrameLayout implements OnTouchListener {
	private final static String TAG = "HorizontalWheel";
	
	// Volume
	public final static int DEFAULT_MAX_VOLUME = 100;
	private int mMax = DEFAULT_MAX_VOLUME;
	
	private final static int WHEEL_SCROLL_GAP = 40;
	private float mVolume = 0;

	// Drawable
	private Drawable[] mWheelImages = new Drawable[ANIM_COUNT];

	// Listener
	private OnWheelVolumeChangeListener mWheelVolumeChangeListener = null;

	// UI Components
	private ImageView mIVWheel;

	// Animation
	private final static int ANIM_FRAME_MSECONDS = 30;
	private final static int ANIM_COUNT = 3;
	private int mAnimIdx = 0;

	// Touch position
	private float TOUCH_SENSE = 1;
	private float mTouchX, mTouchY;

	// Thread processing
	private Thread mThread = null;
	private ConcurrentLinkedQueue<Integer> mAnimQueue = new ConcurrentLinkedQueue<Integer>();

	public HorizontalWheel(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup mLayout = (ViewGroup) li.inflate(R.layout.cust_obj_wheel,
				null);
		addView(mLayout);

		// add touch events
		setOnTouchListener(this);

		// load wheel images
		for (int i = 0; i < ANIM_COUNT; i++) {
			Log.d(TAG, "animation index = " + i);

			String animImg = "thm_black_wheel_animation_" + i;

			Field field;
			int resID = 0;
			try {
				field = R.drawable.class.getField(animImg);
				resID = ((Integer) field.get(R.drawable.class)).intValue();
			} catch (Exception e) {
				Assert.assertTrue(e == null);
			}

			mWheelImages[i] = getContext().getResources().getDrawable(resID);
		}

		// start animation handling thread
		// mThread = new Thread(mAnimationRunnable);
		// mThread.start();
	}

	protected void onFinishInflate() {
		super.onFinishInflate();

		mIVWheel = (ImageView) findViewById(R.id.ivWheel);
		onUpdateUI();
	}

	// Set maximum volume
	public void setMax(int max) {
		Assert.assertTrue(mMax > 0);

		mMax = max;

		setVolume(0);
	}

	// Set wheel position
	public void setVolume(int volume) {
		mVolume = volume;

		onUpdateUI();
	}

	public float getVolume() {
		return mVolume;
	}

	public float getVolumePercent() {
		return (mVolume / mMax);
	}

	public void setVolumePercent(float percent) {
		Assert.assertTrue(percent >= 0 && percent <= 1);
		setVolume((int)(mMax * percent));
	}
	
	// Set volume change listener
	public void setOnWheelVolumeChangeListener(
			OnWheelVolumeChangeListener listener) {
		mWheelVolumeChangeListener = listener;
	}

	private void onUpdateUI() {
	}
	
	static int _count = 0;
	private void onUpdateAnimation(int animIdx) {
		if (mIVWheel == null)
			return;

		// show animation image
		Log.d(TAG, "count = " + (_count++) + ", animation index = " + animIdx);
		mIVWheel.setImageDrawable(mWheelImages[animIdx]);
	}

	/*
	 * protected void onLayout (boolean changed, int left, int top, int right,
	 * int bottom) { super.onLayout(changed, left, top, right, bottom);
	 * 
	 * if (getWidth() > 0 && getHeight() > 0) {
	 * 
	 * } }
	 */

	public boolean onTouch(View v, MotionEvent event) {
		float touchX = event.getX();
		float touchY = event.getY();
		float gapVolume;

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			mTouchX = touchX;
			mTouchY = touchY;
		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
		}

		float gapX = touchX - mTouchX;

		// Re-calculate wheel position
		gapVolume = gapX / ((float) (getWidth() - WHEEL_SCROLL_GAP * 2) / mMax);
		mVolume += gapVolume;
		if (mVolume < 0) {
			mVolume = 0;
			gapVolume = 0;
		} else if (mVolume > mMax) {
			mVolume = mMax;
			gapVolume = 0;
		}

		// Log.d(TAG, "gapX = " + gapX);
		if (Math.abs(gapX) < TOUCH_SENSE) {
			mAnimQueue.clear();
			
			return true;
		}
		
		// Add animation event
		for (int i = 1; i <= Math.abs(gapVolume); i++) {
			int volume = i * (int)(gapVolume / Math.abs(gapVolume));
			if (volume < 0) {
				mAnimIdx--;
				if (mAnimIdx < 0)
					mAnimIdx = ANIM_COUNT + mAnimIdx;
			} else if (volume > 0) {
				mAnimIdx++;
			}
			mAnimIdx = mAnimIdx % ANIM_COUNT;
			// mAnimQueue.add(mAnimIdx);
			
			Activity activity = (Activity)getContext();
			activity.runOnUiThread(new ExtendedRunnable(mAnimIdx) {
				public void run() {
					int animIdx = (Integer)item;
					onUpdateAnimation(animIdx);
				}
			});
			
			break;
		}

		if (mWheelVolumeChangeListener != null)
			mWheelVolumeChangeListener.onVolumeChanged(mVolume);

		mTouchX = touchX;
		mTouchY = touchY;

		// Update view
		onUpdateUI();

		return true;
	}

	/********************************** Animation Handle *******************************/
	// static int _index = 0;
	private Runnable mAnimationRunnable = new Runnable() {
		public void run() {
			android.os.Process.setThreadPriority(-20);
			
			while (!mThread.isInterrupted()) {
				Integer animID = mAnimQueue.poll();

				// animID = 2;
				if (animID != null) {
					Activity activity = (Activity)getContext();
					
					/*
					_index--;
					if (_index < 0)
						_index = ANIM_COUNT - 1;
					_index = _index % ANIM_COUNT;
					*/
					activity.runOnUiThread(new ExtendedRunnable(animID) {
						public void run() {
							int animIdx = (Integer)item;
							onUpdateAnimation(animIdx);
						}
					});
					
					try {
						Thread.sleep(ANIM_FRAME_MSECONDS);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

	};

}
