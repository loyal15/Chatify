package com.gpit.android.ui.common.list;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

public class HorizontalScrollSpinner extends CustomHorizontalScrollView {

	private ListAdapter mAdapter;

	private int mCenterViewPosition = -1;

	private OnSelectedItemChanged onSelectedItemChanged = new OnSelectedItemChanged() {
		@Override
		public void onSelectedChanged(View view, int newPosition) {

		}
	};

	public HorizontalScrollSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.setHorizontalFadingEdgeEnabled(true);
		this.setHorizontalScrollBarEnabled(false);
		this.setFadingEdgeLength(5);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (getChildCount() == 0)
			return;

		initCenterView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (getChildCount() == 0)
			return;

		ViewGroup parent = (ViewGroup) getChildAt(0);

		if (parent.getChildCount() == 0)
			return;

		View FirstChild = parent.getChildAt(0);

		int LeftPadding = (getWidth() / 2)
				- (FirstChild.getMeasuredWidth() / 2);

		View LastChild = parent.getChildAt(getChildCount() - 1);

		int RightPadding = (getWidth() / 2)
				- (LastChild.getMeasuredWidth() / 2);

		if (parent.getPaddingLeft() != LeftPadding
				&& parent.getPaddingRight() != RightPadding) {
			parent.setPadding(LeftPadding, parent.getPaddingTop(),
					RightPadding, parent.getPaddingBottom());
			requestLayout();
		}
	}

	private int getInternalCenterView() {
		if (getChildCount() == 0)
			return -1;

		int CenterView = 0;
		int CenterX = getScrollX() + (getWidth() / 2);

		ViewGroup parent = (ViewGroup) getChildAt(0);

		if (parent.getChildCount() == 0)
			return -1;

		View child = parent.getChildAt(0);

		while (child != null && child.getRight() <= CenterX
				&& CenterView < parent.getChildCount()) {
			CenterView++;
			child = parent.getChildAt(CenterView);
		}

		if (CenterView >= parent.getChildCount())
			CenterView = parent.getChildCount() - 1;

		return CenterView;
	}

	private int getCenterPositionFromView() {
		int CenterView = getInternalCenterView();

		if (mCenterViewPosition != CenterView) {
			onSelectedItemChanged.onSelectedChanged(this, CenterView);
		}

		mCenterViewPosition = CenterView;

		return mCenterViewPosition;
	}

	public int getCenterViewPosition() {
		return mCenterViewPosition;
	}

	public ListAdapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(ListAdapter mAdapter) {

		this.mAdapter = mAdapter;
		fillViewWithAdapter();
	}

	private void fillViewWithAdapter() {
		if (getChildCount() == 0 || mAdapter == null)
			return;

		ViewGroup parent = (ViewGroup) getChildAt(0);

		parent.removeAllViews();

		for (int i = 0; i < mAdapter.getCount(); i++) {
			parent.addView(mAdapter.getView(i, null, parent));
		}
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		getCenterPositionFromView();

		initCenterView();
	}

	private void initCenterView() {
		if (getChildCount() == 0)
			return;

		ViewGroup parent = (ViewGroup) getChildAt(0);

		if (parent.getChildCount() == 0)
			return;

		int CenterView = getCenterViewPosition();

		if (CenterView == -1) {
			mCenterViewPosition = 0;
			CenterView = 0;
		}

		if (CenterView != -1 && CenterView != getInternalCenterView()
				&& parent.getChildAt(0).getLeft() >= 0) {
			scrollToSelectedIndex();
		}

		if (CenterView < 0 || CenterView > parent.getChildCount())
			return;

		for (int i = 0; i <= parent.getChildCount(); i++) {

			if (!(parent.getChildAt(i) instanceof TextView))
				continue;

			if (i == CenterView) {
				// Start Animation
			} else {
				// Remove Animation for other Views
			}
		}

	}

	public int getSelectedIndex() {
		return getCenterViewPosition();
	}

	public void setSelectedIndex(int index) {
		if (getChildCount() == 0)
			return;

		ViewGroup parent = (ViewGroup) getChildAt(0);

		if (index < 0 || index > parent.getChildCount()) {
			throw new ArrayIndexOutOfBoundsException(index);
		}

		mCenterViewPosition = index;

		onSelectedItemChanged.onSelectedChanged(this, mCenterViewPosition);

		requestLayout();
	}

	protected void scrollToSelectedIndex() {
		ViewGroup parent = (ViewGroup) getChildAt(0);

		View child = parent.getChildAt(mCenterViewPosition);

		int ChildCenterX = child.getLeft() + (child.getMeasuredWidth() / 2);

		int ScreenCenterX = getWidth() / 2;

		int ChildScrollToX = ChildCenterX - ScreenCenterX;

		scrollTo(ChildScrollToX, 0);
	}

	public interface OnSelectedItemChanged {
		public void onSelectedChanged(View view, int newPosition);
	}

	public OnSelectedItemChanged getOnSelectedItemChanged() {
		return onSelectedItemChanged;
	}

	public void setOnSelectedItemChanged(
			OnSelectedItemChanged onSelectedItemChanged) {
		this.onSelectedItemChanged = onSelectedItemChanged;
	}

}