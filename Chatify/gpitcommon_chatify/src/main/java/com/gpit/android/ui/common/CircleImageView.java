package com.gpit.android.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.gpit.android.library.R;

public class CircleImageView extends ImageView {
	private static String TAG = "CircleImageView";
	
	private boolean mFitByWidth;
	
	private Paint mPaint;
	private Paint mPaintEraser;
	private Drawable mBackgroundDrawable;
	private Bitmap mBackgroundBitmap;
	private Bitmap mCircleBitmap;
	
	private int mOrgWidth;
	private int mOrgHeight;
	
    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        
        initUI();
        initAttrs(attrs);
    }

    @Deprecated
    public void setBitmap(Bitmap bitmap) {
    	setImageBitmap(bitmap);
    }
    
    @Override
    public void setImageBitmap(Bitmap bitmap) {
    	super.setImageBitmap(bitmap);
    	
    	buildCircleBitmap();
    }
    
    @Override
    public void setImageDrawable(Drawable drawable) {
    	super.setImageDrawable(drawable);
    	
    	buildCircleBitmap();
    }
    
    @Override
    public void setImageResource(int resId) {
    	super.setImageResource(resId);
    	
    	buildCircleBitmap();
    }
    
    @SuppressLint("NewApi") @Override
    public void setBackground(Drawable background) {
    	super.setBackground(background);
    	
    	mBackgroundDrawable = background;
    	loadBackgroundBitmap(true);
    	
    	super.setBackground(null);
    }
    
    @Override
    public void setBackgroundColor(int color) {
    	super.setBackgroundColor(color);
    	
    	mBackgroundDrawable = new ColorDrawable(color);
    	loadBackgroundBitmap(true);
    }
    
    @Override
    public void setBackgroundResource(int resId) {
    	super.setBackgroundResource(resId);
    	
    	mBackgroundDrawable = getResources().getDrawable(resId);
    	loadBackgroundBitmap(true);
    	
    	super.setBackgroundResource(0);
    }
    
    private void initUI() {
    	mPaint = new Paint();
    	mPaint.setAntiAlias(true);
    	mPaint.setDither(true);
    	mPaint.setFilterBitmap(true);
    }
    
    private void initAttrs(AttributeSet attrs) {
    	TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleImageView);
    	mFitByWidth = a.getBoolean(R.styleable.CircleImageView_fitByWidth, true);
    	
		a.recycle();
    }
    
    public Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
    	if (drawable == null)
    		return null;
    	
    	if (widthPixels == 0 || heightPixels == 0)
    		return null;
    	
        Bitmap mutableBitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        if (mutableBitmap == null) {
        	return null;
        }
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);

        return mutableBitmap;
    }
    
    private void loadBackgroundBitmap(boolean reload) {
    	// Retrieve background bitmap
		if (mBackgroundBitmap == null || reload) {
			mBackgroundBitmap = convertToBitmap(mBackgroundDrawable, getMeasuredWidth(), getMeasuredHeight());
			if (mBackgroundBitmap == null) {
				return;
			}
			
			Canvas canvas = new Canvas(mBackgroundBitmap);
			
			mPaintEraser = new Paint();
			mPaintEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
			mPaintEraser.setColor(Color.TRANSPARENT);
			mPaintEraser.setAntiAlias(true);
			canvas.drawCircle(mBackgroundBitmap.getWidth() / 2, mBackgroundBitmap.getHeight() / 2, 
					mBackgroundBitmap.getWidth() / 2, mPaintEraser);
		}
    }
    
	private void buildCircleBitmap() {
		loadBackgroundBitmap(false);
		
    	mCircleBitmap = null;
		
		Drawable drawable = getDrawable();
    	if (drawable == null) {
    		return;
    	}
    	
    	if (getMeasuredWidth() == 0 || getMeasuredHeight() == 0)
    		return;
    	
    	Bitmap sourceBitmap = convertToBitmap(drawable, getMeasuredWidth(), getMeasuredHeight());
    	if (sourceBitmap == null) {
    		return;
    	}
    	
    	try {
    		mCircleBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Config.ARGB_8888);
    	} catch (Exception e) {
    		// Ugh, we don't have memory anymore.
    		System.gc();
    		
    		Log.e(TAG, "width = " + getMeasuredWidth() + ", height = " + getMeasuredHeight());
    		mCircleBitmap = null;
    		return;
    	}
    	
    	if (mCircleBitmap == null) {
    		return;
    	}
    	
    	Canvas canvas = new Canvas(mCircleBitmap);
    	
		final Paint paint = new Paint();
		final Rect srcRect = new Rect(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight());
		final Rect destRect = new Rect(0, 0, mCircleBitmap.getWidth(), mCircleBitmap.getHeight());

		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(mCircleBitmap.getWidth() / 2 + 0.7f, mCircleBitmap.getHeight() / 2 + 0.7f, mCircleBitmap.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(sourceBitmap, srcRect, destRect, paint);
		
		invalidate();
    }
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    	
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        
        if (width == 0 && height == 0) {
        	return;
        }
        
        if (mFitByWidth) {
        	height = width;
        } else {
        	width = height;
        }

        setMeasuredDimension(width, height);
        if (width != mOrgWidth || height != mOrgHeight) {
        	mOrgWidth = width;
        	mOrgHeight = height;
        	
        	buildCircleBitmap();
        }
    }
    
    @Override
    public void onDraw(Canvas canvas) {
    	if (canvas == null)
    		return;
    	
    	// Draw background first
    	if (mBackgroundBitmap != null) {
    		canvas.drawBitmap(mBackgroundBitmap, 0, 0, mPaint);
    	}
    	
    	// Draw circle image
    	if (mCircleBitmap != null) {
    		canvas.drawBitmap(mCircleBitmap, 0, 0, mPaint);
    	}
    }
}
