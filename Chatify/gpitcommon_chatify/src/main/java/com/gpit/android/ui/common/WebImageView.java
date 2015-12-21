package com.gpit.android.ui.common;

// import java.io.FileNotFoundException;

import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.gpit.android.library.R;
import com.gpit.android.util.ExtendedRunnable;
import com.gpit.android.web.feeder.base.ImageDownloader;
import com.gpit.android.web.feeder.base.ImageFeeder;
import com.gpit.android.web.feeder.base.ImageFeederListener;
import com.gpit.android.web.feeder.base.ImageQueueItem;

public class WebImageView extends FrameLayout implements ImageFeederListener {
	private int mDefaultImgResID = -1;
	private String mWebImgPath;
	private Bitmap mWebImg;
	
	private boolean mbStaticMode = false;
	private int mCornerPixel = 0;
	private OnWebImageUpdatedListener mListener;
	private boolean mIsLoading = false;
	private boolean mbApplied = false;
	
	// UI Components
	private ViewGroup mLayout;
	private RoundedImageView mIvImgViewer;
	private ProgressBar mPbLoading;
	
	public WebImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayout = (ViewGroup)li.inflate(R.layout.web_imgview_layout, this);
		
		mCornerPixel = attrs.getAttributeIntValue(null, "roundedPixel", 0);
	}
	
	protected void onFinishInflate () {
		super.onFinishInflate();
		
		if (mLayout != null) {
			mIvImgViewer = (RoundedImageView)mLayout.findViewById(R.id.ivWebImageView);
			mIvImgViewer.setImageBitmap(null);
			mIvImgViewer.setCornerPixel(mCornerPixel);
			
			mPbLoading = (ProgressBar)findViewById(R.id.pbLoading);
			
			if (mDefaultImgResID == -1)
				mIvImgViewer.setImageBitmap(null);
			else
				mIvImgViewer.setImageResource(mDefaultImgResID);
		}
	}
	
	@Override
	protected void onLayout (boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	
		setWebImagePath(mWebImgPath, mbStaticMode, mCornerPixel);
	}
	
	@Override
	protected void finalize() {
		try {
			super.finalize();
		} catch (Throwable e) {
		}
		
		if (mWebImg != null && !mWebImg.isRecycled())
			mWebImg.recycle();
	}
	
	public void setDefaultImage(int resID) {
		mDefaultImgResID = resID;
		mIvImgViewer.setImageResource(mDefaultImgResID);
		
		onUpdateUI();
	}
	
	public void setWebImagePath(String path) {
		setWebImagePath(path, false, 0);
	}
	
	public void setWebImagePath(String path, int cornerPixel) {
		setWebImagePath(path, false, cornerPixel);
	}
	
	public void setWebImagePath(String path, boolean bStaticMode) {
		setWebImagePath(path, bStaticMode, 0);
	}
	
	public void setWebImagePath(String path, boolean bStaticMode, int cornerPixel) {
		mbStaticMode = bStaticMode;
		mCornerPixel = cornerPixel;
		mIvImgViewer.setCornerPixel(mCornerPixel);
		
		if (mbApplied) {
			if (bStaticMode) {
				if (mWebImg != null) {
					if (mWebImgPath == path)
						return;
					if (mWebImgPath != null && path != null) {
						if (mWebImgPath.equals(path)) {
							onUpdateUI();
							return;
						}
					}
				}
			} else {
				if (mWebImgPath == path) {
					onUpdateUI();
					return;
				}
				if (mWebImgPath != null && path != null) {
					if (mWebImgPath.equals(path)) {
						onUpdateUI();
						return;
					}
				}
			}
		}
		
		if (mWebImg != null && !mWebImg.isRecycled()) {
			mWebImg.recycle();
		}
		mWebImg = null;
		
		mbApplied = false;
		mWebImgPath = path;
		if (path != null && !path.trim().equals("")) {
			// mPbLoading.setVisibility(View.VISIBLE);
			// in other case, just wait until view loaded
			if (getMeasuredWidth() != 0 && getMeasuredHeight() != 0) {
				mbApplied = true;
				
				try {
					if (mDefaultImgResID != 0)
						mIvImgViewer.setImageResource(mDefaultImgResID);
					if (mbStaticMode) {
						// mIsLoading = true;
						ImageFeeder.FEEDER.getImage(getContext(), mWebImgPath, getMeasuredWidth(), getMeasuredHeight(), true, this, null);
					} else {
						ImageDownloader.getInstance(getContext()).download(mWebImgPath, mIvImgViewer);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		onUpdateUI();
	}
	
	/*
	public void setOnUpdatedImageListener(OnWebImageUpdatedListener listener) {
		mListener = listener;
	}
	*/
	
	public String getWebImagePath() {
		return mWebImgPath;
	}
	
	public Bitmap getWebImage() {
		return mWebImg;
	}
	
	/************************************************************************
	 * Static Loading
	 ***********************************************************************/
	private void onUpdateUI() {
		if (mbStaticMode) {
			if (mWebImg == null) {
				if (mDefaultImgResID != -1)
					mIvImgViewer.setImageResource(mDefaultImgResID);
				else
					mIvImgViewer.setImageBitmap(null);
			} else {
				mIvImgViewer.setImageBitmap(mWebImg);
			}
		
			if (mIsLoading) {
				mPbLoading.setVisibility(View.VISIBLE);
			} else {
				mPbLoading.setVisibility(View.GONE);
			}
		}
		
		invalidate();
	}
	
	@Override
	public void onSuccess(ImageQueueItem item) {
		// check pending request
		if (!item.reqURL.equals(mWebImgPath))
			return;
		
		mIsLoading = false;
		mWebImg = item.result;
		
		
		mPbLoading.setVisibility(View.GONE);
		onUpdateUI();
		
		if (mListener != null)
			mListener.onUpdated(this, mWebImg);
	}

	@Override
	public void onFailed(ImageQueueItem item) {
		if (mListener != null)
			mListener.onFailed(this, item.e);

		if (item.e != null && (item.e instanceof FileNotFoundException)) {
			mPbLoading.setVisibility(View.GONE);
		} else {
			Handler handler = new Handler();
			handler.postDelayed(new ExtendedRunnable(item) {
				@Override
				public void run() {
					ImageQueueItem queueItem = (ImageQueueItem)item;
					try {
						mIsLoading = true;
						ImageFeeder.FEEDER.getImage(WebImageView.this.getContext(), queueItem.reqURL, 
								getMeasuredWidth(), getMeasuredHeight(), true, WebImageView.this, null);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 500);
		}
		onUpdateUI();
	}
}
