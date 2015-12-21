package com.gpit.android.ui.components.zoomablegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;


public class ZoomableImageView extends ImageView {
	@SuppressWarnings("unused")
	private static final String TAG = "MyImageView";
	// This is the base transformation which is used to show the image
	// initially. The current computation for this shows the image in
	// it's entirety, letterboxing as needed. One could choose to
	// show the image as cropped instead.
	//
	// This matrix is recomputed when we go from the thumbnail image to
	// the full size image.
	protected Matrix mBaseMatrix = new Matrix();
	// This is the supplementary transformation which reflects what
	// the user has done in terms of zooming and panning.
	//
	// This matrix remains the same when we go from the thumbnail image
	// to the full size image.
	protected Matrix mSuppMatrix = new Matrix();
	// This is the final matrix which is computed as the concatentation
	// of the base matrix and the supplementary matrix.
	private final Matrix mDisplayMatrix = new Matrix();
	// Temporary buffer used for getting the values out of a matrix.
	private final float[] mMatrixValues = new float[9];
	// The current bitmap being displayed.
	// protected final RotateBitmap mBitmapDisplayed = new RotateBitmap(null);
	protected Bitmap image = null;
	int mThisWidth = -1, mThisHeight = -1;
	float mMaxZoom = 5.0f;
	float mMinZoom ;
	private int parentWidth;
	private int parentHeight;
	private int imageWidth;
	private int imageHeight;
	private float scaleRate;
	
	public ZoomableImageView(Context context, int parentWidth, int parentHeight, int imageWidth, int imageHeight) {
		super(context);
		this.parentWidth = parentWidth;
		this.parentHeight = parentHeight;
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		init();
	}
	
	public ZoomableImageView(Context context, AttributeSet attrs, int imageWidth, int imageHeight) {
		super(context, attrs);
		this.imageHeight = imageHeight;
		this.imageWidth = imageWidth;
		init();
	}
	/**
	 * 计�??��?�?�?应�?幕�?�?缩?��?比�?
	 */
	private void arithScaleRate() {
		float scaleWidth = parentWidth / (float) imageWidth;
		float scaleHeight = parentHeight / (float) imageHeight;
		Log.e(TAG, "arithScaleRate " + scaleWidth + ", " + scaleHeight);
		scaleRate = Math.min(scaleWidth, scaleHeight);
	}
	public float getScaleRate() {
		return scaleRate;
	}
	public int getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}
	public int getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()) {
			if (getScale() > 1.0f) {
				// If we're zoomed in, pressing Back jumps out to show the
				// entire image, otherwise Back returns the user to the gallery.
				zoomTo(1.0f);
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}
	protected Handler mHandler = new Handler();
	@Override
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		image = bitmap;
		// 计�??��?�?�??��?�?
		arithScaleRate();
		//缩放?��?幕大�?
		zoomTo(getScaleRate(),parentWidth / 2f, parentHeight / 2f);
		//居中
		//layoutToCenter();


		//		imageView.zoomTo(scaleRate, parentWidth / 2, parentHeight / 2
		//		center(true, true);
		//zoomTo(getScaleRate(),parentWidth / 2f, parentHeight / 2f, 200);
	}
	// Center as much as possible in one or both axis. Centering is
	// defined as follows: if the image is scaled down below the
	// view's dimensions then center it (literally). If the image
	// is scaled larger than the view and is translated out of view
	// then translate it back into view (i.e. eliminate black bars).

	int centerCounter = 0;
	protected void center(boolean horizontal, boolean vertical) {
		// if (mBitmapDisplayed.getBitmap() == null) {
		// return;
		// }
		boolean callLayoutToCenter = false; 

		if (image == null) {
			return;
		}
		Matrix m = getImageViewMatrix();
		RectF rect = new RectF(0, 0, image.getWidth(), image.getHeight());
		//		RectF rect = new RectF(0, 0, imageWidth*getScale(), imageHeight*getScale());
		m.mapRect(rect);
		float height = rect.height();
		float width = rect.width();
		float deltaX = 0, deltaY = 0;

		if (centerCounter != 0)
		{
			if (vertical) {
				int viewHeight = getHeight();
				if (height < viewHeight) { 				
					deltaY = (viewHeight - height) / 2 - rect.top; 			
				} 
				else if (rect.top > 0) {
					deltaY = -rect.top;
				} else if (rect.bottom < viewHeight) {
					deltaY = getHeight() - rect.bottom;
				} else if (rect.top < 0 && centerCounter == 0) {
					deltaY = -rect.top;
				}


			}
		}
		else
		{
			if (centerCounter == 0)
			{
					deltaY = deltaY - rect.top;
				
				deltaY = deltaY + ((float)parentHeight - ((float)image.getHeight() * getScaleRate())) / 2f;
			}
		}

		if (horizontal) {
			int viewWidth = getWidth();
			if (width < viewWidth) { 				
				deltaX = (viewWidth - width) / 2 - rect.left; 			
			} 
			else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < viewWidth) { 		
				deltaX = viewWidth - rect.right; 			
			} 		
			if (rect.left < 0 && centerCounter == 0 )
			{
				deltaX = -rect.left;
			}
		} 		
		centerCounter++;

		Log.e(TAG, "in center() " + rect.top+ ", " + getHeight() + ", " + deltaY
		);

		postTranslate(deltaX, deltaY ); 		
		setImageMatrix(getImageViewMatrix()); 	

		if (callLayoutToCenter)
			layoutToCenter();
	} 	

	private void init() { 		
		setScaleType(ImageView.ScaleType.MATRIX); 
	} 	
	/** 	 * 设置?��?居中?�示 	 */ 	
	public void layoutToCenter() 	{ 

		//�?��?�示?�图?��??�宽�?		
		float width = imageWidth*getScale(); 		
		float height = imageHeight*getScale(); 		
		//空白?��?宽�? 		
		float fill_width = parentWidth - width; 		
		float fill_height = parentHeight - height; 		
		//???移动?��?�?		
		float tran_width = 0f; 		
		float tran_height = 0f; 		

		Log.e(TAG, "in layoutToCenter()1 " + width +", " +height + ", " +fill_width + ", " + fill_height + ", " + tran_width + ", " + tran_height);


		if(fill_width>0)
			tran_width = fill_width/2f;
		if(fill_height>0)
			tran_height = fill_height/2f;

		Log.e(TAG, "in layoutToCenter()2 " + width +", " +height + ", " +fill_width + ", " + fill_height + ", " + tran_width + ", " + tran_height);


		postTranslate(tran_width, tran_height);
		setImageMatrix(getImageViewMatrix());
	}
	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		mMinZoom =( parentWidth/2f)/imageWidth;
		return mMatrixValues[whichValue];
	}
	// Get the scale factor out of the matrix.
	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}
	protected float getScale() {
		return getScale(mSuppMatrix);
	}
	// Combine the base matrix and the supp matrix to make the final matrix.
	protected Matrix getImageViewMatrix() {
		// The final matrix is computed as the concatentation of the base matrix
		// and the supplementary matrix.
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}
	static final float SCALE_RATE = 1.25F;
	// Sets the maximum zoom, which is a scale relative to the base matrix. It
	// is calculated to show the image at 400% zoom regardless of screen or
	// image orientation. If in the future we decode the full 3 megapixel image,
	// rather than the current 1024x768, this should be changed down to 200%.
	protected float maxZoom() {
		if (image == null) {
			return 1F;
		}
		float fw = (float) image.getWidth() / (float) mThisWidth;
		float fh = (float) image.getHeight() / (float) mThisHeight;
		float max = Math.max(fw, fh) * 4;
		return max;
	}


	protected void zoomTo(float scale, float centerX, float centerY) {
		Log.e(TAG, "in zoomTo1 " + scale + ", " + mMaxZoom + ", " + mMinZoom);

		if (scale > mMaxZoom) {
			scale = mMaxZoom;
		} else if (scale < mMinZoom) {
			scale = mMinZoom;
		}
		Log.e(TAG, "in zoomTo2 " + scale + ", " + centerX + ", " + centerY);


		float oldScale = getScale();
		Log.e(TAG, "oldScale " + oldScale);

		float deltaScale = scale / oldScale;
		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}
	protected void zoomTo(final float scale, final float centerX, final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();

		final float incrementPerMsAlp = (255 - currentAlpha) / durationMs;
		final int oldAlpha = currentAlpha;


		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				float target = oldScale + (incrementPerMs * currentMs);

				int targetAlp = (int) (oldAlpha + (incrementPerMsAlp * currentMs));
				currentAlpha = targetAlp;


				Log.e(TAG, "target = " + target);

				//MyImageView.this.setAlpha(targetAlp);
				zoomTo(target, centerX, centerY);
				if (currentMs < durationMs) { 					
					mHandler.post(this); 
				} 		
				else
				{
					//currentAlpha = 0;
					//runAlphaAnimation(100);
				}
			} 		
		}); 	
	} 	

	int currentAlpha = 0;
	protected void runAlphaAnimation(final float durationMs)
	{
		final float incrementPerMs = (255 - currentAlpha) / durationMs;
		final int oldAlpha = currentAlpha;
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				int target = (int) (oldAlpha + (incrementPerMs * currentMs));
				currentAlpha = target;
				if (currentMs < durationMs) { 	
					ZoomableImageView.this.setAlpha(target);
					mHandler.post(this); 
				} 		
				else
				{
					ZoomableImageView.this.setAlpha(255);
				}
			} 		
		}); 
	}
	protected void zoomTo(float scale) { 
		float cx = getWidth() / 2F; 		
		float cy = getHeight() / 2F; 		
		zoomTo(scale, cx, cy); 	
	} 	
	protected void zoomToPoint(float scale, float pointX, float pointY) { 		
		float cx = getWidth() / 2F; 		
		float cy = getHeight() / 2F; 		
		panBy(cx - pointX, cy - pointY); 		
		zoomTo(scale, cx, cy); 	
	} 	
	protected void zoomIn() { 		
		zoomIn(SCALE_RATE); 	
	} 	
	protected void zoomOut() { 		
		zoomOut(SCALE_RATE); 	
	} 	
	protected void zoomIn(float rate) { 	
		if (getScale() >= mMaxZoom) {
			return; // Don't let the user zoom into the molecular level.
		} else if (getScale() <= mMinZoom) {
			return;
		}
		if (image == null) {
			return;
		}
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;
		mSuppMatrix.postScale(rate, rate, cx, cy);
		setImageMatrix(getImageViewMatrix());
	}
	protected void zoomOut(float rate) {
		if (image == null) {
			return;
		}
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;
		// Zoom out to at most 1x.
		Matrix tmp = new Matrix(mSuppMatrix);
		tmp.postScale(1F / rate, 1F / rate, cx, cy);
		if (getScale(tmp) < 1F) {
			mSuppMatrix.setScale(1F, 1F, cx, cy);
		} else {
			mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
		}
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}
	public void postTranslate(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}

	protected void postTranslateDur( final float dy, final float durationMs) {
		_dy=0.0f;
		final float incrementPerMs = dy / durationMs;
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				postTranslate(0, incrementPerMs*currentMs-_dy);
				_dy=incrementPerMs*currentMs;
				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}


	protected void postTranslateXYDur( final float dx, final float dy, final float durationMs) {
		_dx=0.0f;
		_dy=0.0f;
		final float incrementPerMsX = dx / durationMs;
		final float incrementPerMsY = dy / durationMs;
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				postTranslate(0, incrementPerMsX*currentMs-_dx);
				postTranslate(0, incrementPerMsY*currentMs-_dy);
				_dx=incrementPerMsX*currentMs;
				_dy=incrementPerMsY*currentMs;				
				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

	float dx=0.0f;
	float _dy=0.0f;
	float _dx=0.0f;
	boolean stop = false;

	float newDx=0.0f;
	float newDy=0.0f;

	int flingFactor = 2;

	protected void postTranslateXY( final float dx,final float dy) {
		_dx=dx;
		_dy=dy;

		newDx=0.0f;
		newDy=0.0f;

		flingFactor = 2;

		stop = false;

		mHandler.post(new Runnable() {
			public void run() {

				_dx=_dx/flingFactor;
				_dy=_dy/flingFactor;

				Log.e("TEST", "_dx = " + _dx + ", _dy = " + _dy);


				float width = getScale() * getImageWidth();
				float height = getScale() * getImageHeight();

				float v[] = new float[9];
				Matrix m = getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float left = v[Matrix.MTRANS_X];
				float bottom = top + height;
				float right = left + width;

				if (right + _dx < parentWidth )
				{
					Log.e("TEST", "in 1");

					newDx = parentWidth - right;

					flingFactor = 8;
				}
				else if (left + _dx > 0)
				{
					Log.e("TEST", "in 2");

					newDx = -left;

					flingFactor = 8;
				}
				//Log.e("test", "bottom = " + bottom + ",  parentHeight  = " + (parentHeight ));
				if ( bottom + _dy < parentHeight)
				{
					Log.e("TEST", "in 3");

					newDy = parentHeight - bottom;

					flingFactor = 8;
				}
				else if (top + _dy > 0)
				{
					Log.e("TEST", "in 4");

					newDy = -top;

					flingFactor = 8;
				}


				if ( ( Math.abs(_dx) > 1 || Math.abs(_dy) > 1)) {
					postTranslate(_dx, _dy);
					mHandler.postDelayed(this, 50);
				}
				else if (newDx != 0 || newDy != 0)
				{
					Log.e("test", newDx + ", " + newDy);					
					stop = true;
					postTranslateXYDur(newDx, newDy, 200f);
				}
			}
		});
	}

	protected void panBy(float dx, float dy) {
		postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}
}
