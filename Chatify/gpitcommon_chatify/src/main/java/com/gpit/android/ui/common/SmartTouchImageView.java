package com.gpit.android.ui.common;

/*
 * SmartSmartTouchImageView.java
 * By: Michael Ortiz
 * Updated By: Patrick Lackemacher
 * Updated By: Babay88
 * Updated By: @ipsilondev
 * Updated By: hank-cp
 * Updated By: singpolyma
 * -------------------
 * Extends Android ImageView to include pinch zooming, panning, fling and double tap zoom.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SmartTouchImageView extends ImageView {

	private float x_down = 0;
	private float y_down = 0;
	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;
	private float oldRotation = 0;
	private Matrix matrix = new Matrix();
	private Matrix matrix1 = new Matrix();
	private Matrix savedMatrix = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	private boolean matrixCheck = false;

	private int widthScreen;
	private int heightScreen;
	
	private boolean isInit = true;

	private Bitmap mBitmap;

	public SmartTouchImageView(Context context) {
        super(context);

        init();
    }

    public SmartTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }
    
    public SmartTouchImageView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	
    	init();
    }
    
	public void init() {
		matrix = new Matrix();
	}

	public void setImageBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}
	
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		
		if (isInit) {
			if (width > 0 && height > 0) {
				isInit = false;
				
				widthScreen = width;
				heightScreen = height;
				
				// Adjust to center
				float scaleX = (float)widthScreen / mBitmap.getWidth();
				float scaleY = (float)heightScreen / mBitmap.getHeight();
				float scale = Math.max(scaleX, scaleY);
				
				int offsetX = (int)(widthScreen - (mBitmap.getWidth() * scale)) / 2;
				int offsetY = (int)(heightScreen - (mBitmap.getHeight() * scale)) / 2;
				
				matrix.postScale(scale, scale);
				matrix.postTranslate(offsetX, offsetY);
				savedMatrix.set(matrix);
				
				invalidate();
			}
		}
	}
	
	protected void onDraw(Canvas canvas) {
		canvas.save();
		canvas.drawBitmap(mBitmap, matrix, null);
		canvas.restore();
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (isInit)
			return false;
		
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mode = DRAG;
			x_down = event.getX();
			y_down = event.getY();
			savedMatrix.set(matrix);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			mode = ZOOM;
			oldDist = spacing(event);
			oldRotation = rotation(event);
			savedMatrix.set(matrix);
			midPoint(mid, event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == ZOOM) {
				matrix1.set(savedMatrix);
				float rotation = rotation(event) - oldRotation;
				float newDist = spacing(event);
				float scale = newDist / oldDist;
				matrix1.postScale(scale, scale, mid.x, mid.y);// 
				matrix1.postRotate(rotation, mid.x, mid.y);// 
				matrixCheck = matrixCheck();
				if (matrixCheck == false) {
					matrix.set(matrix1);
					invalidate();
				}
			} else if (mode == DRAG) {
				matrix1.set(savedMatrix);
				matrix1.postTranslate(event.getX() - x_down, event.getY()
						- y_down);// 
				matrixCheck = matrixCheck();
				matrixCheck = matrixCheck();
				if (matrixCheck == false) {
					matrix.set(matrix1);
					invalidate();
				}
			}
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		}
		return true;
	}

	private boolean matrixCheck() {
		return false;
	}
	
	/*
	private boolean matrixCheck() {
		float[] f = new float[9];
		matrix1.getValues(f);
		// 4
		float x1 = f[0] * 0 + f[1] * 0 + f[2];
		float y1 = f[3] * 0 + f[4] * 0 + f[5];
		float x2 = f[0] * mBitmap.getWidth() + f[1] * 0 + f[2];
		float y2 = f[3] * mBitmap.getWidth() + f[4] * 0 + f[5];
		float x3 = f[0] * 0 + f[1] * mBitmap.getHeight() + f[2];
		float y3 = f[3] * 0 + f[4] * mBitmap.getHeight() + f[5];
		float x4 = f[0] * mBitmap.getWidth() + f[1] * mBitmap.getHeight() + f[2];
		float y4 = f[3] * mBitmap.getWidth() + f[4] * mBitmap.getHeight() + f[5];
		// 
		double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		// 
		if (width < widthScreen / 3 || width > widthScreen * 3) {
			return true;
		}
		// 
		if ((x1 < widthScreen / 3 && x2 < widthScreen / 3
				&& x3 < widthScreen / 3 && x4 < widthScreen / 3)
				|| (x1 > widthScreen * 2 / 3 && x2 > widthScreen * 2 / 3
						&& x3 > widthScreen * 2 / 3 && x4 > widthScreen * 2 / 3)
				|| (y1 < heightScreen / 3 && y2 < heightScreen / 3
						&& y3 < heightScreen / 3 && y4 < heightScreen / 3)
				|| (y1 > heightScreen * 2 / 3 && y2 > heightScreen * 2 / 3
						&& y3 > heightScreen * 2 / 3 && y4 > heightScreen * 2 / 3)) {
			return true;
		}
		return false;
	}
	*/
	
	// 
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}
	
	// 
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 
	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(0) - event.getX(1));
		double delta_y = (event.getY(0) - event.getY(1));
		double radians = Math.atan2(delta_y, delta_x);
		return (float) Math.toDegrees(radians);
	}

}