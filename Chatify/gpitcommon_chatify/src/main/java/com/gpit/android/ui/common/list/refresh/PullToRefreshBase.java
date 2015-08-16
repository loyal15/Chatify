package com.gpit.android.ui.common.list.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;

import com.gpit.android.library.R;

public abstract class PullToRefreshBase<T extends View> extends LinearLayout {

	public static enum Mode {
		NONE(0x0),
		
		PULL_DOWN_TO_REFRESH(0x1),

		PULL_UP_TO_REFRESH(0x2),

		BOTH(0x3);

		private int mIntValue;

		// The modeInt values need to match those from attrs.xml
		Mode(int modeInt) {
			mIntValue = modeInt;
		}

		int getIntValue() {
			return mIntValue;
		}

		boolean canPullDown() {
			return this == PULL_DOWN_TO_REFRESH || this == BOTH;
		}

		boolean canPullUp() {
			return this == PULL_UP_TO_REFRESH || this == BOTH;
		}

		public static Mode mapIntToMode(int modeInt) {
			switch (modeInt) {
			case 0x1:
			default:
				return PULL_DOWN_TO_REFRESH;
			case 0x2:
				return PULL_UP_TO_REFRESH;
			case 0x3:
				return BOTH;
			}
		}

	}

	final class SmoothScrollRunnable implements Runnable {

		static final int ANIMATION_DURATION_MS = 190;
		static final int ANIMATION_FPS = 1000 / 60;

		private final Interpolator mInterpolator;
		private final int mScrollToY;
		private final int mScrollFromY;
		private final Handler mHandler;

		private boolean mContinueRunning = true;
		private long mStartTime = -1;
		private int mCurrentY = -1;

		public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
			mHandler = handler;
			mScrollFromY = fromY;
			mScrollToY = toY;
			mInterpolator = new AccelerateDecelerateInterpolator();
		}

		public void run() {

			if (mStartTime == -1) {
				mStartTime = System.currentTimeMillis();
			} else {

				long normalizedTime = (1000 * (System.currentTimeMillis() - mStartTime))
						/ ANIMATION_DURATION_MS;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math.round((mScrollFromY - mScrollToY)
						* mInterpolator
								.getInterpolation(normalizedTime / 1000f));
				mCurrentY = mScrollFromY - deltaY;
				setHeaderScroll(mCurrentY);
			}

			// If we're not at the target Y, keep going...
			if (mContinueRunning && mScrollToY != mCurrentY) {
				mHandler.postDelayed(this, ANIMATION_FPS);
			}
		}

		public void stop() {
			mContinueRunning = false;
			mHandler.removeCallbacks(this);
		}
	}

	static final boolean DEBUG = false;
	static final String LOG_TAG = "PullToRefresh";

	static final float FRICTION = 2.0f;

	static final int PULL_TO_REFRESH = 0x0;
	static final int RELEASE_TO_REFRESH = 0x1;
	static final int REFRESHING = 0x2;
	static final int MANUAL_REFRESHING = 0x3;

	static final Mode DEFAULT_MODE = Mode.PULL_DOWN_TO_REFRESH;

	static final String STATE_STATE = "ptr_state";
	static final String STATE_MODE = "ptr_mode";
	static final String STATE_CURRENT_MODE = "ptr_current_mode";
	static final String STATE_DISABLE_SCROLLING_REFRESHING = "ptr_disable_scrolling";
	static final String STATE_SHOW_REFRESHING_VIEW = "ptr_show_refreshing_view";
	static final String STATE_SUPER = "ptr_super";

	private int mTouchSlop;

	private float mInitialMotionY;
	private float mLastMotionX;
	private float mLastMotionY;
	private boolean mIsBeingDragged = false;

	private int mState = PULL_TO_REFRESH;
	private Mode mMode = DEFAULT_MODE;
	private Mode mCurrentMode;

	T mRefreshableView;
	private boolean mPullToRefreshEnabled = true;
	private boolean mShowViewWhileRefreshing = true;
	private boolean mDisableScrollingWhileRefreshing = true;
	private boolean mFilterTouchEvents = true;

	private LoadLayout mHeaderLayout;
	private LoadLayout mFooterLayout;
	private int mHeaderHeight;

	private final Handler mHandler = new Handler();

	private OnRefreshListener mOnRefreshListener;
	private OnRefreshListener2 mOnRefreshListener2;

	private SmoothScrollRunnable mCurrentSmoothScrollRunnable;

	public PullToRefreshBase(Context context) {
		super(context);
		init(context, null);
	}

	public PullToRefreshBase(Context context, Mode mode) {
		super(context);
		mMode = mode;
		init(context, null);
	}

	public PullToRefreshBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public final T getRefreshableView() {
		return mRefreshableView;
	}

	public final boolean getShowViewWhileRefreshing() {
		return mShowViewWhileRefreshing;
	}

	public final boolean isPullToRefreshEnabled() {
		return mPullToRefreshEnabled;
	}

	public final Mode getCurrentMode() {
		return mCurrentMode;
	}

	public final Mode getMode() {
		return mMode;
	}

	public final void setMode(Mode mode) {
		if (mode != mMode) {
			if (DEBUG) {
				Log.d(LOG_TAG, "Setting mode to: " + mode);
			}
			mMode = mode;
			updateUIForMode();
		}
	}

	public final boolean isDisableScrollingWhileRefreshing() {
		return mDisableScrollingWhileRefreshing;
	}

	public final boolean isRefreshing() {
		return mState == REFRESHING || mState == MANUAL_REFRESHING;
	}

	public final void setDisableScrollingWhileRefreshing(
			boolean disableScrollingWhileRefreshing) {
		mDisableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
	}

	public final void onRefreshComplete() {
		if (mState != PULL_TO_REFRESH) {
			resetHeader();
		}
	}

	public final void setOnRefreshListener(OnRefreshListener listener) {
		mOnRefreshListener = listener;
	}

	public final void setOnRefreshListener2(OnRefreshListener2 listener) {
		mOnRefreshListener2 = listener;
	}

	public final boolean getFilterTouchEvents() {
		return mFilterTouchEvents;
	}

	public final void setFilterTouchEvents(boolean filterEvents) {
		mFilterTouchEvents = filterEvents;
	}

	public final void setPullToRefreshEnabled(boolean enable) {
		mPullToRefreshEnabled = enable;
	}

	public final void setShowViewWhileRefreshing(boolean showView) {
		mShowViewWhileRefreshing = showView;
	}

	public void setReleaseLabel(String releaseLabel) {
		setReleaseLabel(releaseLabel, Mode.BOTH);
	}

	public void setPullLabel(String pullLabel) {
		setPullLabel(pullLabel, Mode.BOTH);
	}

	public void setRefreshingLabel(String refreshingLabel) {
		setRefreshingLabel(refreshingLabel, Mode.BOTH);
	}

	public void setReleaseLabel(String releaseLabel, Mode mode) {
		if (null != mHeaderLayout && mMode.canPullDown()) {
			mHeaderLayout.setReleaseLabel(releaseLabel);
		}
		if (null != mFooterLayout && mMode.canPullUp()) {
			mFooterLayout.setReleaseLabel(releaseLabel);
		}
	}

	public void setPullLabel(String pullLabel, Mode mode) {
		if (null != mHeaderLayout && mMode.canPullDown()) {
			mHeaderLayout.setPullLabel(pullLabel);
		}
		if (null != mFooterLayout && mMode.canPullUp()) {
			mFooterLayout.setPullLabel(pullLabel);
		}
	}

	public void setRefreshingLabel(String refreshingLabel, Mode mode) {
		if (null != mHeaderLayout && mMode.canPullDown()) {
			mHeaderLayout.setRefreshingLabel(refreshingLabel);
		}
		if (null != mFooterLayout && mMode.canPullUp()) {
			mFooterLayout.setRefreshingLabel(refreshingLabel);
		}
	}

	public final void setRefreshing() {
		setRefreshing(true);
	}

	public final void setRefreshing(boolean doScroll) {
		if (!isRefreshing()) {
			setRefreshingInternal(doScroll);
			mState = MANUAL_REFRESHING;
		}
	}

	public void setLastUpdatedLabel(CharSequence label) {
		if (null != mHeaderLayout) {
			mHeaderLayout.setSubHeaderText(label);
		}
		if (null != mFooterLayout) {
			mFooterLayout.setSubHeaderText(label);
		}
	}

	public final boolean hasPullFromTop() {
		return mCurrentMode == Mode.PULL_DOWN_TO_REFRESH;
	}

	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		if (!mPullToRefreshEnabled) {
			return false;
		}

		if (isRefreshing() && mDisableScrollingWhileRefreshing) {
			return true;
		}

		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& event.getEdgeFlags() != 0) {
			return false;
		}

		switch (event.getAction()) {

		case MotionEvent.ACTION_MOVE: {
			if (mIsBeingDragged) {
				mLastMotionY = event.getY();
				pullEvent();
				return true;
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			if (isReadyForPull()) {
				mLastMotionY = mInitialMotionY = event.getY();
				return true;
			}
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			if (mIsBeingDragged) {
				mIsBeingDragged = false;

				if (mState == RELEASE_TO_REFRESH) {

					if (null != mOnRefreshListener) {
						setRefreshingInternal(true);
						mOnRefreshListener.onRefresh();
						return true;

					} else if (null != mOnRefreshListener2) {
						setRefreshingInternal(true);
						if (mCurrentMode == Mode.PULL_DOWN_TO_REFRESH) {
							mOnRefreshListener2.onPullDownToRefresh();
						} else if (mCurrentMode == Mode.PULL_UP_TO_REFRESH) {
							mOnRefreshListener2.onPullUpToRefresh();
						}
						return true;
					}

					return true;
				}

				smoothScrollTo(0);
				return true;
			}
			break;
		}
		}

		return false;
	}

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event) {

		if (!mPullToRefreshEnabled) {
			return false;
		}

		if (isRefreshing() && mDisableScrollingWhileRefreshing) {
			return true;
		}

		final int action = event.getAction();

		if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			mIsBeingDragged = false;
			return false;
		}

		if (action != MotionEvent.ACTION_DOWN && mIsBeingDragged) {
			return true;
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE: {
			if (isReadyForPull()) {

				final float y = event.getY();
				final float dy = y - mLastMotionY;
				final float yDiff = Math.abs(dy);
				final float xDiff = Math.abs(event.getX() - mLastMotionX);

				if (yDiff > mTouchSlop
						&& (!mFilterTouchEvents || yDiff > xDiff)) {
					if (mMode.canPullDown() && dy >= 1f && isReadyForPullDown()) {
						mLastMotionY = y;
						mIsBeingDragged = true;
						if (mMode == Mode.BOTH) {
							mCurrentMode = Mode.PULL_DOWN_TO_REFRESH;
						}
					} else if (mMode.canPullUp() && dy <= -1f
							&& isReadyForPullUp()) {
						mLastMotionY = y;
						mIsBeingDragged = true;
						if (mMode == Mode.BOTH) {
							mCurrentMode = Mode.PULL_UP_TO_REFRESH;
						}
					}
				}
			}
			break;
		}
		case MotionEvent.ACTION_DOWN: {
			if (isReadyForPull()) {
				mLastMotionY = mInitialMotionY = event.getY();
				mLastMotionX = event.getX();
				mIsBeingDragged = false;
			}
			break;
		}
		}

		return mIsBeingDragged;
	}

	protected void addRefreshableView(Context context, T refreshableView) {
		addView(refreshableView, new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, 0, 1.0f));
	}

	protected abstract T createRefreshableView(Context context,
			AttributeSet attrs);

	protected final LoadLayout getFooterLayout() {
		return mFooterLayout;
	}

	protected final LoadLayout getHeaderLayout() {
		return mHeaderLayout;
	}

	protected final int getHeaderHeight() {
		return mHeaderHeight;
	}

	protected final int getState() {
		return mState;
	}

	protected abstract boolean isReadyForPullDown();

	protected abstract boolean isReadyForPullUp();

	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		bundle.putInt(STATE_STATE, mState);
		bundle.putInt(STATE_MODE, mMode.getIntValue());
		bundle.putInt(STATE_CURRENT_MODE, mCurrentMode.getIntValue());
		bundle.putBoolean(STATE_DISABLE_SCROLLING_REFRESHING,
				mDisableScrollingWhileRefreshing);
		bundle.putBoolean(STATE_SHOW_REFRESHING_VIEW, mShowViewWhileRefreshing);
		bundle.putParcelable(STATE_SUPER, super.onSaveInstanceState());
		return bundle;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;

			mMode = Mode.mapIntToMode(bundle.getInt(STATE_MODE, 0));
			mCurrentMode = Mode.mapIntToMode(bundle.getInt(STATE_CURRENT_MODE,
					0));

			mDisableScrollingWhileRefreshing = bundle.getBoolean(
					STATE_DISABLE_SCROLLING_REFRESHING, true);
			mShowViewWhileRefreshing = bundle.getBoolean(
					STATE_SHOW_REFRESHING_VIEW, true);

			// Let super Restore Itself
			super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER));

			final int viewState = bundle.getInt(STATE_STATE, PULL_TO_REFRESH);
			if (viewState == REFRESHING) {
				setRefreshingInternal(true);
				mState = viewState;
			}
			return;
		}

		super.onRestoreInstanceState(state);
	}

	protected void resetHeader() {
		mState = PULL_TO_REFRESH;
		mIsBeingDragged = false;

		if (mMode.canPullDown()) {
			mHeaderLayout.reset();
		}
		if (mMode.canPullUp()) {
			mFooterLayout.reset();
		}

		smoothScrollTo(0);
	}

	protected void setRefreshingInternal(boolean doScroll) {
		mState = REFRESHING;

		if (mMode.canPullDown()) {
			mHeaderLayout.refreshing();
		}
		if (mMode.canPullUp()) {
			mFooterLayout.refreshing();
		}

		if (doScroll) {
			if (mShowViewWhileRefreshing) {
				smoothScrollTo(mCurrentMode == Mode.PULL_DOWN_TO_REFRESH ? -mHeaderHeight
						: mHeaderHeight);
			} else {
				smoothScrollTo(0);
			}
		}
	}

	protected final void setHeaderScroll(int y) {
		scrollTo(0, y);
	}

	protected final void smoothScrollTo(int y) {
		if (null != mCurrentSmoothScrollRunnable) {
			mCurrentSmoothScrollRunnable.stop();
		}

		if (getScrollY() != y) {
			mCurrentSmoothScrollRunnable = new SmoothScrollRunnable(mHandler,
					getScrollY(), y);
			mHandler.post(mCurrentSmoothScrollRunnable);
		}
	}

	protected void updateUIForMode() {
		// Remove Header, and then add Header Loading View again if needed
		if (this == mHeaderLayout.getParent()) {
			removeView(mHeaderLayout);
		}
		if (mMode.canPullDown()) {
			addView(mHeaderLayout, 0, new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			measureView(mHeaderLayout);
			mHeaderHeight = mHeaderLayout.getMeasuredHeight();
		}

		// Remove Footer, and then add Footer Loading View again if needed
		if (this == mFooterLayout.getParent()) {
			removeView(mFooterLayout);
		}
		if (mMode.canPullUp()) {
			addView(mFooterLayout, new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			measureView(mFooterLayout);
			mHeaderHeight = mFooterLayout.getMeasuredHeight();
		}

		// Hide Loading Views
		switch (mMode) {
		case BOTH:
			setPadding(0, -mHeaderHeight, 0, -mHeaderHeight);
			break;
		case PULL_UP_TO_REFRESH:
			setPadding(0, 0, 0, -mHeaderHeight);
			break;
		case PULL_DOWN_TO_REFRESH:
		default:
			setPadding(0, -mHeaderHeight, 0, 0);
			break;
		}

		// If we're not using Mode.BOTH, set mCurrentMode to mMode, otherwise
		// set it to pull down
		mCurrentMode = (mMode != Mode.BOTH) ? mMode : Mode.PULL_DOWN_TO_REFRESH;
	}

	private void init(Context context, AttributeSet attrs) {

		setOrientation(LinearLayout.VERTICAL);

		ViewConfiguration config = ViewConfiguration.get(context);
		mTouchSlop = config.getScaledTouchSlop();

		// Styleables from XML
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PullToRefresh);
		if (a.hasValue(R.styleable.PullToRefresh_ptrMode)) {
			mMode = Mode.mapIntToMode(a.getInteger(
					R.styleable.PullToRefresh_ptrMode, 0));
		}

		// Refreshable View
		// By passing the attrs, we can add ListView/GridView params via XML
		mRefreshableView = createRefreshableView(context, attrs);
		addRefreshableView(context, mRefreshableView);

		// We need to create now layouts now
		mHeaderLayout = new LoadLayout(context, Mode.PULL_DOWN_TO_REFRESH, a);
		mFooterLayout = new LoadLayout(context, Mode.PULL_UP_TO_REFRESH, a);

		// Add Header/Footer Views
		updateUIForMode();

		// Styleables from XML
		if (a.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
			Drawable background = a
					.getDrawable(R.styleable.PullToRefresh_ptrHeaderBackground);
			if (null != background) {
				setBackground(background);
			}
		}
		if (a.hasValue(R.styleable.PullToRefresh_ptrAdapterViewBackground)) {
			Drawable background = a
					.getDrawable(R.styleable.PullToRefresh_ptrAdapterViewBackground);
			if (null != background) {
				mRefreshableView.setBackground(background);
			}
		}
		a.recycle();
		a = null;
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	private boolean pullEvent() {

		final int newHeight;
		final int oldHeight = getScrollY();

		switch (mCurrentMode) {
		case PULL_UP_TO_REFRESH:
			newHeight = Math.round(Math.max(mInitialMotionY - mLastMotionY, 0)
					/ FRICTION);
			break;
		case PULL_DOWN_TO_REFRESH:
		default:
			newHeight = Math.round(Math.min(mInitialMotionY - mLastMotionY, 0)
					/ FRICTION);
			break;
		}

		setHeaderScroll(newHeight);

		if (newHeight != 0) {
			if (mState == PULL_TO_REFRESH
					&& mHeaderHeight < Math.abs(newHeight)) {
				mState = RELEASE_TO_REFRESH;

				switch (mCurrentMode) {
				case PULL_UP_TO_REFRESH:
					mFooterLayout.releaseToRefresh();
					break;
				case PULL_DOWN_TO_REFRESH:
					mHeaderLayout.releaseToRefresh();
					break;
				default:
					break;
				}

				return true;

			} else if (mState == RELEASE_TO_REFRESH
					&& mHeaderHeight >= Math.abs(newHeight)) {
				mState = PULL_TO_REFRESH;

				switch (mCurrentMode) {
				case PULL_UP_TO_REFRESH:
					mFooterLayout.pullToRefresh();
					break;
				case PULL_DOWN_TO_REFRESH:
					mHeaderLayout.pullToRefresh();
					break;
				default:
					break;
				}

				return true;
			}
		}

		return oldHeight != newHeight;
	}

	private boolean isReadyForPull() {
		switch (mMode) {
		case PULL_DOWN_TO_REFRESH:
			return isReadyForPullDown();
		case PULL_UP_TO_REFRESH:
			return isReadyForPullUp();
		case BOTH:
			return isReadyForPullUp() || isReadyForPullDown();
		}
		return false;
	}

	public static interface OnRefreshListener {

		public void onRefresh();

	}

	public static interface OnRefreshListener2 {

		public void onPullDownToRefresh();

		public void onPullUpToRefresh();

	}

	public static interface OnLastItemVisibleListener {

		public void onLastItemVisible();

	}

	@Override
	public void setLongClickable(boolean longClickable) {
		getRefreshableView().setLongClickable(longClickable);
	}

}