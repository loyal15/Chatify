package com.gpit.android.ui.common.list;


import com.gpit.android.library.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class PullToSearchListView extends ListView implements OnScrollListener {

    private static final int TAP_TO_REFRESH = 1;
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;

    private static final String TAG = "PullToRefreshListView";

    private OnRefreshListener mOnRefreshListener;

    /**
     * Listener that will receive notifications every time the list scrolls.
     */
    private OnScrollListener mOnScrollListener;
    private LayoutInflater mInflater;

    private RelativeLayout mSearchView;

    private int mCurrentScrollState;
    private int mRefreshState;

    private int mRefreshViewHeight;
    @SuppressWarnings("unused")
	private int mLastMotionY;

    private boolean mBounceHack;

    public PullToSearchListView(Context context) {
        super(context);
        init(context);
    }

    public PullToSearchListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToSearchListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

		mSearchView = (RelativeLayout) mInflater.inflate(
				R.layout.pull_to_search_header, this, false);

        mRefreshState = TAP_TO_REFRESH;

        addHeaderView(mSearchView);

        super.setOnScrollListener(this);

        measureView(mSearchView);
        mRefreshViewHeight = mSearchView.getMeasuredHeight();
        mSearchView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
        //mSearchView.setVisibility(View.GONE);
    }

    @Override
    protected void onAttachedToWindow() {
        setSelection(1);//scrollTo(0, mRefreshViewHeight);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        setSelection(1);//scrollTo(0, mRefreshViewHeight);
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     * 
     * @param l The scroll listener. 
     */
    @Override
    public void setOnScrollListener(AbsListView.OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Register a callback to be invoked when this list should be refreshed.
     * 
     * @param onRefreshListener The callback to run.
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int y = (int) event.getY();
        mBounceHack = false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (!isVerticalScrollBarEnabled()) {
                    setVerticalScrollBarEnabled(true);
                }
                if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
                    if ((mSearchView.getBottom() > mRefreshViewHeight
                            || mSearchView.getTop() >= 0)
                            && mRefreshState == RELEASE_TO_REFRESH) {
                        // Initiate the refresh
                        mRefreshState = REFRESHING;
                        prepareForRefresh();
                        onRefresh();
                    } else if (mSearchView.getBottom() < mRefreshViewHeight
                            || mSearchView.getTop() < 0) {
                        // Abort refresh and scroll down below the refresh view
                        setSelection(1);//scrollTo(0, mRefreshViewHeight);
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
        }
        return super.onTouchEvent(event);
    }


    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        // When the refresh view is completely visible, change the text to say
        // "Release to refresh..." and flip the arrow drawable.
        if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
                && mRefreshState != REFRESHING) {
            if (firstVisibleItem == 0) {
                if ((mSearchView.getBottom() > mRefreshViewHeight + 20
                        || mSearchView.getTop() >= 0)
                        && mRefreshState != RELEASE_TO_REFRESH) {
//                    mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
//                    mRefreshViewImage.startAnimation(mFlipAnimation);
                    mRefreshState = RELEASE_TO_REFRESH;
                } else if (mSearchView.getBottom() < mRefreshViewHeight + 20
                        && mRefreshState != PULL_TO_REFRESH) {
//                    mRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
                    if (mRefreshState != TAP_TO_REFRESH) {
//                        mRefreshViewImage.startAnimation(mReverseFlipAnimation);
                    }
                    mRefreshState = PULL_TO_REFRESH;
                }
            } else {
            }
        } else if (mCurrentScrollState == SCROLL_STATE_FLING
                && firstVisibleItem == 0
                && mRefreshState != REFRESHING) {
            setSelection(1);//scrollTo(0, mRefreshViewHeight);
            mBounceHack = true;
        } else if (mBounceHack && mCurrentScrollState == SCROLL_STATE_FLING) {
            setSelection(1);//scrollTo(0, mRefreshViewHeight);
        }

        if (mOnScrollListener != null) {
            mOnScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;

        if (mCurrentScrollState == SCROLL_STATE_IDLE) {
        	mSearchView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
            mBounceHack = false;
        }
        else {
        	mSearchView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, mRefreshViewHeight));
        }

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    public void prepareForRefresh() {
        mRefreshState = REFRESHING;
        mSearchView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, mRefreshViewHeight));
    }

    public void onRefresh() {
        Log.d(TAG, "onRefresh");

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * Resets the list to a normal state after a refresh.
     * @param lastUpdated Last updated at.
     */
    public void onRefreshComplete(CharSequence lastUpdated) {
        onRefreshComplete();
    }

    /**
     * Resets the list to a normal state after a refresh.
     */
    public void onRefreshComplete() {        
        Log.d(TAG, "onRefreshComplete");

        mRefreshState = PULL_TO_REFRESH;
        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        //if (mSearchView.getBottom() > 0) 
        {
            invalidateViews();
            setSelection(1);//scrollTo(0, mRefreshViewHeight);
            
        }
    }

    /**
     * Interface definition for a callback to be invoked when list should be
     * refreshed.
     */
    public interface OnRefreshListener {
        /**
         * Called when the list should be refreshed.
         * <p>
         * A call to {@link PullToSearchListView #onRefreshComplete()} is
         * expected to indicate that the refresh has completed.
         */
        public void onRefresh();
    }
}
