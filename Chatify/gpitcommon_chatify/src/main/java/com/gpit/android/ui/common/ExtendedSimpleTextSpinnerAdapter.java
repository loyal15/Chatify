package com.gpit.android.ui.common;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gpit.android.library.R;

public class ExtendedSimpleTextSpinnerAdapter implements ExtendedSpinnerAdapter {
	private Context mContext;
	
	private int mTextColor = ExtendedSimpleTextSpinner.DEFAULT_ITEM_TEXT_COLOR;
	private String[] mContents = null;
	private String mHeaderHint;
	
	public ExtendedSimpleTextSpinnerAdapter() {
	}
	
	public ExtendedSimpleTextSpinnerAdapter(Context context, String headerHint) {
		setContext(context);
		setHint(headerHint);
	}

	public void setContext(Context context) {
		mContext = context;
	}
	
	public void setTextColor(int color) {
		mTextColor = color;
	}
	
	public void setHint(String headerHint) {
		mHeaderHint = headerHint;
	}
	
	public void setContents(String[] contents) {
		mContents = contents;
	}
	
	@Override
	public View getHeaderView() {
		ViewGroup headerView = (ViewGroup) View.inflate(mContext, R.layout.common_subview_simple_text_spinner_header, null);
		TextView tvHeader = (TextView) headerView.findViewById(R.id.tvHeader);
		tvHeader.setTextColor(mTextColor);
		tvHeader.setHintTextColor(mTextColor);
		tvHeader.setHint(mHeaderHint);
		
		return headerView;
	}

	@Override
	public View getItemView(int index) {
		ViewGroup itemView = (ViewGroup) View.inflate(mContext, R.layout.common_subview_simple_text_spinner_item, null);
		TextView tvValue = (TextView) itemView.findViewById(R.id.tvItemValue);
		tvValue.setTextColor(mTextColor);
		tvValue.setText(mContents[index]);
		
		return itemView;
	}

	@Override
	public int getCount() {
		if (mContents != null)
			return mContents.length;
		else
			return 0;
	}

}
