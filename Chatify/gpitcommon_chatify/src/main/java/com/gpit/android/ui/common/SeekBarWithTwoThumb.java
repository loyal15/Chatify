package com.gpit.android.ui.common;

import junit.framework.Assert;

import com.gpit.android.library.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class SeekBarWithTwoThumb extends View {

	private String TAG = this.getClass().getSimpleName();

	// Configuration
	private static int TEXT_PADDING_TOP = 3;
	private static int TOUCH_PLUS_RANGE = 30;

	private Bitmap mBG;
	private Bitmap mThumb, mThumbNoTouched, mThumbTouched;
	private String mUnitFormat;
	private int mMinValue, mMaxValue, mScope;
	private int mStep = 10;
	private int mThumbStartValue, mThumbEndValue;

	private int mThumbStartX, mThumbEndX;
	private int mThumbY;
	private int mSliderBarY;
	private int mTextY;
	private int mThumbHalfWidth;

	private int mSelectedThumb;
	private SeekBarChangeListener scl;
	private Paint mPaint = new Paint();

	public SeekBarWithTwoThumb(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		init();
	}

	public SeekBarWithTwoThumb(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		init();
	}

	public SeekBarWithTwoThumb(Context context) {
		super(context);
		
		init();
	}

	public void init() {
		TEXT_PADDING_TOP = getContext().getResources().getDimensionPixelSize(R.dimen.seekbar_text_padding_top);
		TOUCH_PLUS_RANGE = getContext().getResources().getDimensionPixelSize(R.dimen.seekbar_touch_plus_range);
		setImage(R.drawable.seekbar_bg, R.drawable.seekbar_thumb, R.drawable.seekbar_thumb);
		setUnitSymbol("%s");
		setRange(0, 100);
		setStep(10);
		setPos(0, 100, false);
	}
	
	public void setImage(int bgResID, int thumbResID, int thumbTouchedResID) {
		if (bgResID != -1) {
			mBG = BitmapFactory.decodeResource(getResources(), bgResID);
		}

		if (thumbResID != -1) {
			mThumb = mThumbNoTouched = BitmapFactory.decodeResource(getResources(), thumbResID);
		}
		
		if (thumbTouchedResID != -1) {
			mThumbTouched = BitmapFactory.decodeResource(getResources(), thumbTouchedResID);
		}

		requestLayout();
		invalidate();
	}

	public void setUnitSymbol(String unitFormat) {
		mUnitFormat = unitFormat;

		invalidate();
	}
	
	public void setFontSize(int size) {
		mPaint.setTextSize(size);

		invalidate();
	}
	
	public void setTextColor(int color) {
		mPaint.setColor(color);
	}

	public void setStep(int step) {
		mStep = step;

		setPos(getStartPos(), getEndPos());
	}
	
	// Set thumb pos
	public void setPos(int startValue, int endValue) {
		setPos(startValue, endValue, true);
	}
	
	private void setPos(int startValue, int endValue, boolean reDraw) {
		float width = getMeasuredWidth() - (getPaddingLeft() + getPaddingRight());
		
		Assert.assertTrue(mMinValue < mMaxValue);
		
		// Round-up
		startValue = (int)Math.round((float)startValue / mStep) * mStep; 
		endValue = (int)Math.round((float)endValue / mStep) * mStep;
		
		if (startValue < mMinValue)
			startValue = mMinValue;
		if (endValue > mMaxValue)
			endValue = mMaxValue;
		
		mThumbStartValue = (int)startValue;
		mThumbEndValue = (int)endValue;
		
		// Get position from percent
		mThumbStartX = getPaddingLeft()
				+ (int) (((float)width / (mScope)) * (startValue - mMinValue));
		mThumbEndX = getPaddingLeft()
				+ (int) (((float)width / (mScope)) * (endValue - mMinValue));

		if (reDraw)
			invalidate();
	}

	public int getStartPos() { return mThumbStartValue; }
	public int getEndPos() { return mThumbEndValue; }
	
	public int getMax() { return mMaxValue; }
	public int getMin() { return mMinValue; }
	
	public void setRange(int minValue, int maxValue) {
		setRange(minValue, maxValue, true);
	}
	
	public void setRange(int minValue, int maxValue, boolean reDraw) {
		int startValue = getStartPos();
		int endValue = getEndPos();
		
		if (minValue > maxValue)
			maxValue = minValue + 1;
		mMinValue = minValue;
		mMaxValue = maxValue;
		mScope = mMaxValue - mMinValue;
		
		if (startValue < mMinValue)
			startValue = mMinValue;
		if (endValue > mMaxValue)
			endValue = mMaxValue;
		
		setPos(startValue, endValue, reDraw);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getMeasuredHeight() > 0) {
			_onMeansure();
		}
	}

	private void _onMeansure() {
		int height, contentHeight;
		int padding;
		
		height = getMeasuredHeight() - (getPaddingTop() + getPaddingBottom());
		// calculate padding size
		contentHeight = mBG.getHeight();
		contentHeight += mThumb.getHeight();
		contentHeight += mPaint.getTextSize();

		padding = (height - contentHeight) / 2;

		mThumbHalfWidth = mThumb.getWidth() / 2;

		// calculate all positions of sub component
		mThumbY = getPaddingTop() + padding;
		mSliderBarY = mThumbY + mThumb.getHeight();
		mTextY = mSliderBarY + mBG.getHeight() + TEXT_PADDING_TOP;
		setPos(mThumbStartValue, mThumbEndValue, false);
		invalidate();
	}

	public void setSeekBarChangeListener(SeekBarChangeListener scl) {
		this.scl = scl;
	}

	private int rearrangePosX(int x) {
		if (x < getPaddingLeft())
			x = getPaddingLeft();
		
		if (x > (getMeasuredWidth() - getPaddingRight()))
			x = getMeasuredWidth() - getPaddingRight();
		
		return x;
	}
	
	@SuppressWarnings("unused")
	private int rearrangePosY(int y) {
		if (y < getPaddingTop())
			y = getPaddingTop();
		
		if (y > (getMeasuredHeight() - getPaddingBottom()))
			y = getMeasuredHeight() - getPaddingBottom();
		
		return y;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// super.onDraw(canvas);
		// draw thumb
		int drawX;
		
		drawX = rearrangePosX(mThumbStartX - mThumbHalfWidth);
		if (mSelectedThumb == 1)
			canvas.drawBitmap(mThumb, drawX, mThumbY, mPaint);
		else
			canvas.drawBitmap(mThumbNoTouched, drawX, mThumbY, mPaint);
		
		drawX = rearrangePosX(mThumbEndX - mThumbHalfWidth);
		if (mSelectedThumb == 2)
			canvas.drawBitmap(mThumb, drawX, mThumbY, mPaint);
		else
			canvas.drawBitmap(mThumbNoTouched, drawX, mThumbY, mPaint);

		// draw slider
		canvas.drawBitmap(
				mBG,
				new Rect(0, 0, mBG.getWidth(), mBG.getHeight()),
				new Rect(getPaddingLeft(), mSliderBarY,
						(getMeasuredWidth() - getPaddingRight()), (mSliderBarY + mBG
								.getHeight())), mPaint);

		// draw symbol
		String symbol;
		int symbolX;
		symbol = String.format(mUnitFormat, mThumbStartValue);
		symbolX = rearrangePosX(mThumbStartX - (int) mPaint.measureText(symbol) / 2);
		canvas.drawText(symbol, symbolX, mTextY + mPaint.getTextSize(), mPaint);
		
		symbol = String.format(mUnitFormat, mThumbEndValue);
		symbolX = rearrangePosX(mThumbEndX - (int)(mPaint.measureText(symbol) / 2));
		canvas.drawText(symbol, symbolX, mTextY + mPaint.getTextSize(), mPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Backup thumb's position
		// int mThumbOrgStartX = mThumbStartX;
		// int mThumbOrgEndX = mThumbEndX;

		int mx = (int) event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			int mGapStartX, mGapEndX;
			mGapStartX = Math.abs(mThumbStartX - mx);
			mGapEndX = Math.abs(mThumbEndX - mx);
			if (mx >= (mThumbStartX - mThumbHalfWidth - TOUCH_PLUS_RANGE)
					&& mx <= (mThumbStartX + mThumbHalfWidth + TOUCH_PLUS_RANGE)) {
				mSelectedThumb = 1;
				mThumb = mThumbTouched;
			}
			
			if (mx >= (mThumbEndX - mThumbHalfWidth - TOUCH_PLUS_RANGE)
					&& mx <= (mThumbEndX + mThumbHalfWidth + TOUCH_PLUS_RANGE)) {
				if (!(mSelectedThumb == 1 && mGapStartX < mGapEndX))
					mSelectedThumb = 2;
				mThumb = mThumbTouched;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			printLog("Mouse Move : " + mSelectedThumb);

			if (mSelectedThumb == 1) {
				mThumbStartX = mx;
				printLog("Move Thumb 1");
			} else if (mSelectedThumb == 2) {
				mThumbEndX = mx;
				printLog("Move Thumb 2");
			}
			break;
		case MotionEvent.ACTION_UP:
			mThumb = mThumbNoTouched;
			mSelectedThumb = 0;
			break;
		}

		// re-arrange all positions
		if (mSelectedThumb != 0) {
			mThumbStartX = rearrangePosX(mThumbStartX);
			mThumbEndX = rearrangePosX(mThumbEndX);
	
			if (mThumbStartX > mThumbEndX) {
				if (mSelectedThumb == 1)
					mThumbStartX = mThumbEndX;
				else if (mSelectedThumb == 2)
					mThumbEndX = mThumbStartX;
			}
	
			invalidate();
			
			calculateThumbValue();
			if (scl != null) {
				scl.SeekBarValueChanged(mThumbStartValue, mThumbEndValue);
			}
		}
		return true;
	}

	private void calculateThumbValue() {
		int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
		int startValue = (int)(mMinValue + mScope * ((float)(mThumbStartX - getPaddingLeft()) / (float)width));
		int endValue = (int)(mMinValue + mScope * ((float)(mThumbEndX - getPaddingLeft()) / (float)width));
		
		setPos(startValue, endValue, false);
	}

	private void printLog(String log) {
		Log.i(TAG, log);
	}

	interface SeekBarChangeListener {
		void SeekBarValueChanged(int Thumb1Value, int Thumb2Value);
	}

}
