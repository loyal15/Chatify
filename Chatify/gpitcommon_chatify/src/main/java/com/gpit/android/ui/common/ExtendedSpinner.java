package com.gpit.android.ui.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gpit.android.library.R;

public class ExtendedSpinner extends LinearLayout {
	class ASSpinnerTag {
		public int index = 0;
		public boolean isSelected = false;
	}
	
	private final static int DEFAULT_ITEM_VIEW_BG_COLOR = 0;
	private final static int DEFAULT_ITEM_VIEW_SELECTED_BG_COLOR = R.color.spinner_item_selected_color;
	private int mHeaderViewBGResID;
	private int mItemViewBGColor;
	private int mItemViewSelectedBGColor;
	private int mArrowUpResID;
	private int mArrowDownResID;
	
	private ViewGroup mVGRoot;
	private ViewGroup mHeaderViewContainer;
	private ViewGroup mItemsViewContainer;
	private ImageView mIVArrow;
	
	private ExtendedSpinnerAdapter mAdapter;
	private OnItemSelectedListener mItemSelectedLIstener;
	private int mSelectedIndex = -1;
	private boolean mIsOpened = false;
	private boolean mIsOnUpdating = false;
	
	public ExtendedSpinner(Context context) {
		this(context, null);
	}

	public ExtendedSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initAttrs(attrs);
		initData();
		initUI();
		
		setSelection(mSelectedIndex);
		openList(mIsOpened);
	}
	
	public void setAdapter(ExtendedSpinnerAdapter adapter) {
		if (mIsOnUpdating)
			return;
		
		mAdapter = adapter;
		
		rebuildHeaderView();
		rebuildItemsView();
	}
	
	public int getSelectedIndex() {
		return mSelectedIndex;
	}
	
	public void setSelection(int index) {
		if (mIsOnUpdating)
			return;
		
		if (index >= mAdapter.getCount() || index < 0) {
			return;
		}
		
		// Disable all selection first.
		for (int i = 0 ; i < mAdapter.getCount() ; i++) {
			ViewGroup itemViewContainer = (ViewGroup) mItemsViewContainer.getChildAt(i);
			
			ASSpinnerTag tag = (ASSpinnerTag) itemViewContainer.getTag();
			tag.isSelected = (i == index);
			mSelectedIndex = index;
			
		}
		
		updateItemsView();
		rebuildHeaderView();
	}
	
	public void openList(boolean open) {
		if (mIsOnUpdating)
			return;
		
		if (open && mAdapter.getCount() == 0)
			return;
		
		if (open) {
			mItemsViewContainer.setVisibility(View.VISIBLE);
			
			// Update header view
			mHeaderViewContainer.setBackgroundResource(0);
						
			// Update arrow item
			mIVArrow.setImageResource(mArrowUpResID);
		} else {
			mItemsViewContainer.setVisibility(View.GONE);
			
			// Update header view
			mHeaderViewContainer.setBackgroundResource(mHeaderViewBGResID);
			
			// Update arrow item
			mIVArrow.setImageResource(mArrowDownResID);
		}
		
		mIsOpened = open;
	}
	
	public void notifyDataSetChanged() {
		if (mIsOnUpdating)
			return;
		
		rebuildHeaderView();
		rebuildItemsView();
	}
	
	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mItemSelectedLIstener = listener;
	}
	
	/************************ INITIALIZATION ***************************/
	protected void initAttrs(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.ExtendedSpinner);
		
		mHeaderViewBGResID = a.getResourceId(R.styleable.ExtendedSpinner_headerViewBG, R.drawable.sp_header_bg);
		mItemViewBGColor = a.getColor(R.styleable.ExtendedSpinner_itemViewBGColor, DEFAULT_ITEM_VIEW_BG_COLOR);
		mItemViewSelectedBGColor = a.getColor(R.styleable.ExtendedSpinner_itemViewSelectedBGColor, getResources().getColor(DEFAULT_ITEM_VIEW_SELECTED_BG_COLOR));
		mArrowUpResID = a.getResourceId(R.styleable.ExtendedSpinner_arrowUp, R.drawable.ic_spinner_arrow_up);
		mArrowDownResID = a.getResourceId(R.styleable.ExtendedSpinner_arrowDown, R.drawable.ic_spinner_arrow_down);
		
		a.recycle();
	}

	protected void initData() {
		mAdapter = new ExtendedSimpleTextSpinnerAdapter(getContext(), "") {
			@Override
			public int getCount() {
				return 0;
			}
		};
	}
	
	protected void initUI() {
		// Create spinner viewer
		mVGRoot = (ViewGroup) View.inflate(getContext(), R.layout.common_subview_spinner, null);
		mVGRoot.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mVGRoot);
		mVGRoot = (ViewGroup) mVGRoot.findViewById(R.id.llRoot);
		
		mHeaderViewContainer = (ViewGroup) mVGRoot.findViewById(R.id.flHeaderViewContainer);
		mIVArrow = (ImageView) mVGRoot.findViewById(R.id.ivArrow);
		mHeaderViewContainer.setOnClickListener(mHeaderClickListener);
		
		mItemsViewContainer = (ViewGroup) findViewById(R.id.llItemsViewContainer);
		
		rebuildHeaderView();
		rebuildItemsView();
		
		updateUI();
	}

	private void rebuildHeaderView() {
		if (mHeaderViewContainer.getChildCount() == 2)
			mHeaderViewContainer.removeViewAt(0);
		
		View headerView;
		
		if (mSelectedIndex == -1)
			headerView = mAdapter.getHeaderView();
		else
			headerView = mAdapter.getItemView(mSelectedIndex);
		
		headerView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
		// Add header view to header container;
		mHeaderViewContainer.addView(headerView, 0);
	}
	
	private void rebuildItemsView() {
		// Remove all of sub views
		mItemsViewContainer.removeAllViews();
		
		for (int i = 0 ; i < mAdapter.getCount() ; i++) {
			ViewGroup itemViewContainer = (ViewGroup) View.inflate(getContext(), R.layout.common_subview_spinner_item_container, null);
			itemViewContainer.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			
			View itemView = mAdapter.getItemView(i);
			itemView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			itemViewContainer.addView(itemView);
			
			// Set item's meta data
			ASSpinnerTag tag = new ASSpinnerTag();
			tag.index = i;
			itemViewContainer.setTag(tag);
			
			// Set item's appearance
			itemViewContainer.setBackgroundColor(mItemViewBGColor);
			
			// Set item's event listener
			itemViewContainer.setOnClickListener(mItemClickListener);
			
			mItemsViewContainer.addView(itemViewContainer);
		}
	}

	/************************ UPDATE UI ***************************/
	private void updateUI() {
		mVGRoot.setBackgroundResource(mHeaderViewBGResID);
		
		// updateItemsView();
	}
	
	private void updateItemsView() {
		for (int i = 0 ; i < mAdapter.getCount() ; i++) {
			ViewGroup itemViewContainer = (ViewGroup) mItemsViewContainer.getChildAt(i);
			ASSpinnerTag tag = (ASSpinnerTag) itemViewContainer.getTag();
			
			if (tag.isSelected)
				itemViewContainer.setBackgroundColor(mItemViewSelectedBGColor);
			else
				itemViewContainer.setBackgroundColor(mItemViewBGColor);
		}
	}
	
	/************************ EVENT *****************************/
	private OnClickListener mHeaderClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			openList(!mIsOpened);
		}
	};
	
	private OnClickListener mItemClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			final ViewGroup itemViewContainer = (ViewGroup) v;
			final View itemView = itemViewContainer.getChildAt(0);
			final ASSpinnerTag tag = (ASSpinnerTag) itemViewContainer.getTag();

			setSelection(tag.index);
			mIsOnUpdating = true;
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mIsOnUpdating = false;
					
					openList(false);
				}
			}, 200);
			
			if (mItemSelectedLIstener != null)
				mItemSelectedLIstener.onItemSelected(null, itemView, tag.index, tag.index);
		}
	};
}
