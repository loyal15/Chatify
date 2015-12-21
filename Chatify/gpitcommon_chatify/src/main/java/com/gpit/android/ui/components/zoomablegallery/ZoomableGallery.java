package com.gpit.android.ui.components.zoomablegallery;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Gallery;

public class ZoomableGallery extends Gallery {
	private GestureDetector gestureScanner;
	private ZoomableImageView imageView;

	private int mWidth;
	private int mHeight;
	
	public ZoomableGallery(Context context) {
		super(context);
	}

	public ZoomableGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ZoomableGallery(Context context, AttributeSet attrs) {
		super(context, attrs);

		gestureScanner = new GestureDetector(new MySimpleGesture());
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = ZoomableGallery.this.getSelectedView();
				if (view instanceof ZoomableImageView) {
					imageView = (ZoomableImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = imageView.getScale();
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);
							// System.out.println("value:" + value);
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;
								// scale the image
								
								if (originalScale*scale > imageView.getScaleRate())
								imageView.zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1));

							}
						}
					}
				}
				return false;
			}

		});
	}
	
	@Override
	public void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		View view = ZoomableGallery.this.getSelectedView();
		if (view instanceof ZoomableImageView) {
			imageView = (ZoomableImageView) view;

			float v[] = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);

			float left, right;

			float width, height;
			width = imageView.getScale() * imageView.getImageWidth();
			height = imageView.getScale() * imageView.getImageHeight();

			if ((int) width <= mWidth && (int) height <= mHeight)
			{
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				imageView.getGlobalVisibleRect(r);

				if (distanceX > 0)// �椰�??��?
				{
					if (r.left > 0) {// ��?敶?��ImageView�臬?��?�內?�??
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (right < mWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0)// �?��??��?
				{
					if (r.right < mWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (left > 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				}

			}

		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}


	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2){ 
		return e2.getX() > e1.getX(); 
	}

	
	boolean isFling = false;
	float flingVelocityX = 0;
	float flingVelocityY = 0;
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		//return false;
		Log.e("tset", "in onFling");
		int kEvent;
		if(isScrollingLeft(e1, e2)){ //Check if scrolling left
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		}
		else{ //Otherwise scrolling right
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		//return onKeyDown(kEvent, null);


		//return super.onFling(e1, e2, velocityX,velocityY);
		
		flingVelocityX = velocityX;
		flingVelocityY = velocityY;
		
		View view = ZoomableGallery.this.getSelectedView();
		if (view instanceof ZoomableImageView) {
			imageView = (ZoomableImageView) view;
			float width = imageView.getScale() * imageView.getImageWidth();
			float height = imageView.getScale() * imageView.getImageHeight();
			
			float v[] = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);
			float top = v[Matrix.MTRANS_Y];
			float left = v[Matrix.MTRANS_X];
			float bottom = top + height;
			float right = left + width;
			
			//Log.e("TEST", "left = " + left + ", top = " + top+ ", right = " + right+ ", bottom = " + bottom + ", height = " + height);
			
			if ((int) width <= mWidth && (int) height <= mHeight)
			{
				return onKeyDown(kEvent, null);
			}
			
			
			float startY = ((mHeight - (height))/2);
			
			
			if ((int) width > mWidth && (int) height > mHeight)
			{
				
				if (top > 0) {
					imageView.postTranslateDur(-top, 200f);
				}
				else if (bottom < mHeight) {
					imageView.postTranslateDur(mHeight - bottom, 200f);
					//imageView.postTranslateDur(30, 200f);

				}
				else
				{
					//Log.e("test", "velocityX = " + velocityX + ", velocityY = " +velocityY);
					
		
					float dx = velocityX/6;
					float dy = velocityY/6;
					
					
					
					
					if ( ((right + dx < mWidth) || (left + dx > 0)) && Math.abs(velocityX) > 100 )
					{
						return onKeyDown(kEvent, null);
					}
					
					
					
			
					
					/*//Log.e("test", "bottom = " + bottom + ",  mHeight  = " + (mHeight ));
					if ( bottom + dy < mHeight)
					{
						//Log.e("TEST", "in 1");
						dy = mHeight - bottom;
					}
					else if (top + dy > 0)
					{
						//Log.e("TEST", "in 2");
						dy = -top;
					}*/
					
					
					
					
					//Log.e("test", left + ", " + top+ ", "  + newX+ ", "  + newY);
					
					imageView.postTranslateXY(dx ,dy );
				}
			}
			else
			{
				
				float dx = velocityX/6;
				
				if ( ((right + dx < mWidth) || (left + dx > 0)) && Math.abs(velocityX) > 100 )
				{
					return onKeyDown(kEvent, null);
				}
				
				
				if (top < startY) {
					imageView.postTranslateDur(startY - top, 200f);
				}
				
				else if (bottom > mHeight - startY) {
					imageView.postTranslateDur(startY - top, 200f);

				}
				
				
				
			}
			
			//imageView.postTranslateDur(-top, 200f);
			
			
		}
		
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			//Log.e("tset", "in ACTION_UP");
			
			if (isFling)
				return true;
			
			
			// ��?�??��颲寧��?��??��?�?
			View view = ZoomableGallery.this.getSelectedView();
			if (view instanceof ZoomableImageView) {
				imageView = (ZoomableImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale() * imageView.getImageHeight();
				if ((int) width <= mWidth && (int) height <= mHeight)
				{
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;


				if ((int) width > mWidth && (int) height > mHeight)
				{
					
					if (top > 0) {
						imageView.postTranslateDur(-top, 200f);
					}
					Log.i("manga", "bottom:" + bottom);
					if (bottom < mHeight) {
						imageView.postTranslateDur(mHeight - bottom, 200f);
						//imageView.postTranslateDur(30, 200f);

					}
				}
				else
				{
					float startY = ((mHeight - (height))/2);
					
					if (top < startY) {
						imageView.postTranslateDur(startY - top, 200f);
					}
					
					else if (bottom > mHeight - startY) {
						imageView.postTranslateDur(startY - top, 200f);

					}
					
					
					
				}

			}
			break;
		case MotionEvent.ACTION_DOWN:
			isFling = false;
			//Log.e("tset", "isFling to false");
			break;
		}
		return super.onTouchEvent(event);
	}

	private class MySimpleGesture extends SimpleOnGestureListener {

		public boolean onDoubleTap(MotionEvent e) {
			View view = ZoomableGallery.this.getSelectedView();
			if (view instanceof ZoomableImageView) {
				imageView = (ZoomableImageView) view;
				if (imageView.getScale() > imageView.getScaleRate()) {
					imageView.zoomTo(imageView.getScaleRate(), mWidth / 2, mHeight / 2, 200f);
				} else {
					imageView.zoomTo(2.0f, mWidth / 2, mHeight / 2, 200f);
				}

			} else {

			}
			// return super.onDoubleTap(e);
			return true;
		}
	}
}
