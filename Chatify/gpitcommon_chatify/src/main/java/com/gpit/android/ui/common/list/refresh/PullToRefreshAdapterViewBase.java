	package com.gpit.android.ui.common.list.refresh;
	
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

	
public abstract class PullToRefreshAdapterViewBase<T extends AbsListView> extends PullToRefreshBase<T> implements
	OnScrollListener {
	
	private int mSavedLastVisibleIndex = -1;
	private OnScrollListener mOnScrollListener;
	private OnLastItemVisibleListener mOnLastItemVisibleListener;
	private View mEmptyView;
	private FrameLayout mRefreshableViewHolder;
	
	public PullToRefreshAdapterViewBase(Context context) {
	super(context);
	mRefreshableView.setOnScrollListener(this);
	}
	
	public PullToRefreshAdapterViewBase(Context context, Mode mode) {
	super(context, mode);
	mRefreshableView.setOnScrollListener(this);
	}
	
	public PullToRefreshAdapterViewBase(Context context, AttributeSet attrs) {
	super(context, attrs);
	mRefreshableView.setOnScrollListener(this);
	}
	
	abstract public ContextMenuInfo getContextMenuInfo();
	
	public final void onScroll(final AbsListView view, final int firstVisibleItem, final int visibleItemCount,
	final int totalItemCount) {
	
	if (null != mOnLastItemVisibleListener) {
	// detect if last item is visible
	int lastVisibleItemIndex = firstVisibleItem + visibleItemCount;
	
	if (visibleItemCount > 0 && (lastVisibleItemIndex + 1) == totalItemCount) {
	
	// only process first event
	if (lastVisibleItemIndex != mSavedLastVisibleIndex) {
	mSavedLastVisibleIndex = lastVisibleItemIndex;
	mOnLastItemVisibleListener.onLastItemVisible();
	}
	}
	}
	
	if (null != mOnScrollListener) {
	mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
	}
	}
	
	public final void onScrollStateChanged(final AbsListView view, final int scrollState) {
	if (null != mOnScrollListener) {
	mOnScrollListener.onScrollStateChanged(view, scrollState);
	}
	}
	
	public final void setEmptyView(View newEmptyView) {
	// If we already have an Empty View, remove it
	if (null != mEmptyView) {
	mRefreshableViewHolder.removeView(mEmptyView);
	}
	
	if (null != newEmptyView) {
	// New view needs to be clickable so that Android recognizes it as a
	// target for Touch Events
	newEmptyView.setClickable(true);
	
	ViewParent newEmptyViewParent = newEmptyView.getParent();
	if (null != newEmptyViewParent && newEmptyViewParent instanceof ViewGroup) {
	((ViewGroup) newEmptyViewParent).removeView(newEmptyView);
	}
	
	mRefreshableViewHolder.addView(newEmptyView, ViewGroup.LayoutParams.MATCH_PARENT,
	ViewGroup.LayoutParams.MATCH_PARENT);
	
	if (mRefreshableView instanceof EmptyViewMethodAccessor) {
	((EmptyViewMethodAccessor) mRefreshableView).setEmptyViewInternal(newEmptyView);
	} else {
	mRefreshableView.setEmptyView(newEmptyView);
	}
	}
	}
	
	public final void setOnLastItemVisibleListener(OnLastItemVisibleListener listener) {
	mOnLastItemVisibleListener = listener;
	}
	
	public final void setOnScrollListener(OnScrollListener listener) {
	mOnScrollListener = listener;
	}
	
	protected void addRefreshableView(Context context, T refreshableView) {
	mRefreshableViewHolder = new FrameLayout(context);
	mRefreshableViewHolder.addView(refreshableView, ViewGroup.LayoutParams.MATCH_PARENT,
	ViewGroup.LayoutParams.MATCH_PARENT);
	addView(mRefreshableViewHolder, new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));
	};
	
	protected boolean isReadyForPullDown() {
	return isFirstItemVisible();
	}
	
	protected boolean isReadyForPullUp() {
	return isLastItemVisible();
	}
	
	private boolean isFirstItemVisible() {
	if (mRefreshableView.getCount() <= getNumberInternalViews()) {
	return true;
	} else if (mRefreshableView.getFirstVisiblePosition() == 0) {
	
	final View firstVisibleChild = mRefreshableView.getChildAt(0);
	
	if (firstVisibleChild != null) {
	return firstVisibleChild.getTop() >= mRefreshableView.getTop();
	}
	}
	
	return false;
	}
	
	private boolean isLastItemVisible() {
	final int count = mRefreshableView.getCount();
	final int lastVisiblePosition = mRefreshableView.getLastVisiblePosition();
	
	if (DEBUG) {
	Log.d(LOG_TAG, "isLastItemVisible. Count: " + count + " Last Visible Pos: " + lastVisiblePosition);
	}
	
	if (count <= getNumberInternalViews()) {
	return true;
	} else if (lastVisiblePosition == count - 1) {
	
	final int childIndex = lastVisiblePosition - mRefreshableView.getFirstVisiblePosition();
	final View lastVisibleChild = mRefreshableView.getChildAt(childIndex);
	
	if (lastVisibleChild != null) {
	return lastVisibleChild.getBottom() <= mRefreshableView.getBottom();
	}
	}
	
	return false;
	}
	
	protected int getNumberInternalViews() {
	return getNumberInternalHeaderViews() + getNumberInternalFooterViews();
	}
	
	protected int getNumberInternalHeaderViews() {
	return 0;
	}
	
	protected int getNumberInternalFooterViews() {
	return 0;
	}
	}
	
	
