package com.gpit.android.ui.common;

import android.content.Context;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

public class AutoResizeTextView extends TextView {
	public static final float MIN_TEXT_SIZE = 22;
	private static final int MIN_PADDING_LEFT = 0; // temp value for padding :
													// default 3
	private static final int MIN_PADDING_RIGHT = 0; // temp value for padding :
													// default 2
	private static final int MAX_PADDING_LEFT = 10; // 10PX
	private static final int MAX_PADDING_RIGHT = 10; // 10PX

	// Interface for resize notifications
	public interface OnTextResizeListener {
		public void onTextResize(TextView textView, float oldSize, float newSize);
	}

	/**
     */
	public void resizeText(int width, int height) {
		CharSequence text = getText();

		if (text == null || text.length() == 0 || height <= 0 || width <= 0
				|| mTextSize == 0) {
			return;
		}

		TextPaint textPaint = getPaint();

		// Store the current text size
		float oldTextSize = textPaint.getTextSize();
		// If there is a max text size set, use the lesser of that and the
		// default text size
		float targetTextSize = mMaxTextSize > 0 ? Math.min(mTextSize,
				mMaxTextSize) : mTextSize;

		// Get the required text height
		int textHeight = getTextHeight(text, textPaint, width, targetTextSize);

		// Until we either fit within our text view or we had reached our min
		// text size, incrementally try smaller sizes
		while (textHeight > height && targetTextSize > mMinTextSize) {
			targetTextSize = Math.max(targetTextSize - 2, mMinTextSize);
			textHeight = getTextHeight(text, textPaint, width, targetTextSize);
		}

		// If we had reached our minimum text size and still don't fit, append
		// an ellipsis
		if (mAddEllipsis && targetTextSize == mMinTextSize
				&& textHeight > height) {
			// Draw using a static layout
			StaticLayout layout = new StaticLayout(text, textPaint, width,
					Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, false);
			// Check that we have a least one line of rendered text
			if (layout.getLineCount() > 0) {
				// Since the line at the specific vertical position would be cut
				// off,
				// we must trim up to the previous line
				int lastLine = layout.getLineForVertical(height) - 1;
				// If the text would not even fit on a single line, clear it
				if (lastLine < 0) {
					setText("");
				}
				// Otherwise, trim to the previous line and add an ellipsis
				else {
					int start = layout.getLineStart(lastLine);
					int end = layout.getLineEnd(lastLine);
					float lineWidth = layout.getLineWidth(lastLine);
					float ellipseWidth = textPaint.measureText(mEllipsis);

					// Trim characters off until we have enough room to draw the
					// ellipsis
					while (width < lineWidth + ellipseWidth) {
						if (start < end)
							lineWidth = textPaint.measureText(text.subSequence(
									start, --end + 1).toString());
						else
							break;
					}

					if (end > 0)
						setText(text.subSequence(0, end) + mEllipsis);
				}
			}
		}

		// Some devices try to auto adjust line spacing, so force default line
		// spacing
		// and invalidate the layout as a side effect
		// textPaint.density = 1.0f;

		textPaint.setTextSize(targetTextSize);
		setLineSpacing(mSpacingAdd, mSpacingMult);

		// Notify the listener if registered
		if (mTextResizeListener != null) {
			mTextResizeListener.onTextResize(this, oldTextSize, targetTextSize);
		}

		// Reset force resize flag
		mNeedsResize = false;
	}

	// Default constructor override
	public AutoResizeTextView(Context context) {
		this(context, null);
	}

	// Default constructor when inflating from XML file
	public AutoResizeTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	// Default constructor override
	public AutoResizeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTextSize = getTextSize();

	}

	@Override
	protected void onTextChanged(final CharSequence text, final int start,
			final int before, final int after) {
		mNeedsResize = true;
		// Since this view may be reused, it is good to reset the text size
		resetTextSize();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (w != oldw || h != oldh) {
			mNeedsResize = true;
		}
	}

	public void setOnResizeListener(OnTextResizeListener listener) {
		mTextResizeListener = listener;
	}

	public float getMaxTextSize() {
		return mMaxTextSize;
	}

	public float getMinTextSize() {
		return mMinTextSize;
	}

	public boolean getAddEllipsis() {
		return mAddEllipsis;
	}

	public void setMinTextSize(float minTextSize) {
		mMinTextSize = minTextSize;
		requestLayout();
		invalidate();
	}

	@Override
	public void setTextSize(float size) {
		super.setTextSize(size);
		mTextSize = getTextSize();
	}

	@Override
	public void setTextSize(int unit, float size) {
		super.setTextSize(unit, size);
		mTextSize = getTextSize();
	}

	@Override
	public void setLineSpacing(float add, float mult) {
		super.setLineSpacing(add, mult);
		mSpacingMult = mult;
		mSpacingAdd = add;
	}

	public void setMaxTextSize(float maxTextSize) {
		mMaxTextSize = maxTextSize;
		requestLayout();
		invalidate();
	}

	public void setAddEllipsis(boolean addEllipsis) {
		mAddEllipsis = addEllipsis;
	}

	public void resetTextSize() {
		if (mTextSize > 0) {
			super.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
			mMaxTextSize = mTextSize;
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		if (changed || mNeedsResize) {
			int widthLimit = (right - left) - getCompoundPaddingLeft()
					- getCompoundPaddingRight();
			int heightLimit = (bottom - top) - getCompoundPaddingBottom()
					- getCompoundPaddingTop();
			resizeText(widthLimit, heightLimit);

		}

		super.onLayout(changed, left + MIN_PADDING_LEFT, top, right
				+ MIN_PADDING_RIGHT, bottom);

	}

	public void resizeText() {
		int heightLimit = getHeight() - getPaddingBottom() - getPaddingTop();
		int widthLimit = getWidth() - getPaddingLeft() - getPaddingRight();
		resizeText(widthLimit, heightLimit);
	}

	// Set the text size of the text paint object and use a static layout to
	// render text off screen before measuring
	private int getTextHeight(CharSequence source, TextPaint paint, int width,
			float textSize) {
		// Update the text paint object
		paint.setTextSize(textSize);
		// Measure using a static layout
		StaticLayout layout = new StaticLayout(source, paint, width,
				Alignment.ALIGN_NORMAL, mSpacingMult, mSpacingAdd, true);
		return layout.getHeight();
	}

	private static final String mEllipsis = "...";
	private OnTextResizeListener mTextResizeListener;
	private boolean mNeedsResize = false;
	private float mTextSize;
	private float mMaxTextSize = 0;
	private float mMinTextSize = MIN_TEXT_SIZE;
	private float mSpacingMult = 0.99f;
	private float mSpacingAdd = 0.0f;
	private boolean mAddEllipsis = true;
}
