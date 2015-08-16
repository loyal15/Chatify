package com.gpit.android.ui.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.ScaleGestureDetector;
import android.view.View.OnTouchListener;

@Deprecated
public class TouchImageView extends ImageView implements OnTouchListener {
	private final static int LIMIT_STEP = 50;
	
	private OnTouchMoveListener mTouchMoveListener;
	
	Matrix matrix = new Matrix();
	
	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;
	
	// Remember some things for zooming
	PointF last = new PointF();
	PointF start = new PointF();
	float minScale = 1f;
	float maxScale = 3f;
	float[] m;
	
	float redundantXSpace, redundantYSpace;
	
	float width, height;
	static final int CLICK = 3;
	float saveScale = 0f;
	float right, bottom, origWidth, origHeight, bmWidth = 0, bmHeight;
	
	ScaleGestureDetector mScaleDetector;
	
	Context context;
	
	
	public TouchImageView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    super.setClickable(true);
	    this.context = context;
	    mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
	    matrix.setTranslate(1f, 1f);
	    m = new float[9];
	    setImageMatrix(matrix);
	    setScaleType(ScaleType.MATRIX);
	
	    setOnTouchListener(this);
	}
	
    public void setOnTouchMoveListsner(OnTouchMoveListener listener) {
    	mTouchMoveListener = listener;
    }
    
	@Override
	public void setImageBitmap(Bitmap bm) { 
	    super.setImageBitmap(bm);
	    if (bm == null)
			return;
	    bmWidth = bm.getWidth();
	    bmHeight = bm.getHeight();
	    
	    setImageMatrix(matrix);
	    setScaleType(ScaleType.MATRIX);
	}
	
	public void setMaxZoom(float x)
	{
	    maxScale = x;
	}
	
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    @Override
	    public boolean onScaleBegin(ScaleGestureDetector detector) {
	        mode = ZOOM;
	        return true;
	    }
	
	    @Override
	    public boolean onScale(ScaleGestureDetector detector) {
	        float mScaleFactor = (float)Math.min(Math.max(.95f, detector.getScaleFactor()), 1.05);
	        float origScale = saveScale;
	        saveScale *= mScaleFactor;
	        if (saveScale > maxScale) {
	            saveScale = maxScale;
	            mScaleFactor = maxScale / origScale;
	        } else if (saveScale < minScale) {
	            saveScale = minScale;
	            mScaleFactor = minScale / origScale;
	        }
	        right = width * saveScale - width - (2 * redundantXSpace * saveScale);
	        bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
	        if (origWidth * saveScale <= width || origHeight * saveScale <= height) {
	            matrix.postScale(mScaleFactor, mScaleFactor, width / 2, height / 2);
	            if (mScaleFactor < 1) {
	                matrix.getValues(m);
	                float x = m[Matrix.MTRANS_X];
	                float y = m[Matrix.MTRANS_Y];
	                if (mScaleFactor < 1) {
	                    if (Math.round(origWidth * saveScale) < width) {
	                        if (y < -bottom)
	                            matrix.postTranslate(0, -(y + bottom));
	                        else if (y > 0)
	                            matrix.postTranslate(0, -y);
	                    } else {
	                        if (x < -right) 
	                            matrix.postTranslate(-(x + right), 0);
	                        else if (x > 0) 
	                            matrix.postTranslate(-x, 0);
	                    }
	                }
	            }
	        } else {
	            matrix.postScale(mScaleFactor, mScaleFactor, detector.getFocusX(), detector.getFocusY());
	            matrix.getValues(m);
	            float x = m[Matrix.MTRANS_X];
	            float y = m[Matrix.MTRANS_Y];
	            if (mScaleFactor < 1) {
	                if (x < -right) 
	                    matrix.postTranslate(-(x + right), 0);
	                else if (x > 0) 
	                    matrix.postTranslate(-x, 0);
	                if (y < -bottom)
	                    matrix.postTranslate(0, -(y + bottom));
	                else if (y > 0)
	                    matrix.postTranslate(0, -y);
	            }
	        }
	        return true;
	
	    }
	}
	
	@Override
	protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
	{
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    
	    /*
	    if (saveScale == 1 || bmWidth == 0) {
	    	matrix.postTranslate(redundantXSpace, redundantYSpace);
	    	setImageMatrix(matrix);
	    	
	    	Log.d("TouchImageView", "**** redundantXSpace = " + redundantXSpace + ", redundantYSpace = " + redundantYSpace);
	    	Log.d("TouchImageView", "**** matrix = " + matrix.toShortString());
	    	return;
	    }
	    */
	    
	    width = MeasureSpec.getSize(widthMeasureSpec);
	    height = MeasureSpec.getSize(heightMeasureSpec);
	    
	    Log.d("TouchImageView", "**** width = " + getWidth() + ", height = " + getHeight());
	    Log.d("TouchImageView", "**** measure width = " + getMeasuredWidth() + ", height = " + getMeasuredHeight());
	    Log.d("TouchImageView", "**** requested measure width = " + width + ", height = " + height);
	    
	    //Fit to screen.
	    float scale;
	    float scaleX =  (float)width / (float)bmWidth;
	    float scaleY = (float)height / (float)bmHeight;
	    scale = Math.min(scaleX, scaleY);
	    matrix.setScale(scale, scale);
	    setImageMatrix(matrix);
	    saveScale = 1f;
	
	    // Center the image
	    redundantYSpace = (float)height - (scale * (float)bmHeight) ;
	    redundantXSpace = (float)width - (scale * (float)bmWidth);
	    redundantYSpace /= (float)2;
	    redundantXSpace /= (float)2;
	
	    matrix.postTranslate(redundantXSpace, redundantYSpace);
	
	    origWidth = width - 2 * redundantXSpace;
	    origHeight = height - 2 * redundantYSpace;
	    right = width * saveScale - width - (2 * redundantXSpace * saveScale);
	    bottom = height * saveScale - height - (2 * redundantYSpace * saveScale);
	    setImageMatrix(matrix);
	    
	    Log.d("TouchImageView", "**** redundantXSpace = " + redundantXSpace + ", redundantYSpace = " + redundantYSpace);
    	Log.d("TouchImageView", "**** matrix = " + matrix.toShortString());
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		mScaleDetector.onTouchEvent(event);
		
        matrix.getValues(m);
        float x = m[Matrix.MTRANS_X];
        float y = m[Matrix.MTRANS_Y];
        PointF curr = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                last.set(event.getX(), event.getY());
                start.set(last);
                mode = DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                	float deltaX = curr.x - last.x;
                    float deltaY = curr.y - last.y;
                    
                    float scaleWidth = Math.round(origWidth * saveScale);
                    float scaleHeight = Math.round(origHeight * saveScale);
                    if (scaleWidth < width) {
                        deltaX = 0;
                        if (y + deltaY > 0)
                            deltaY = -y;
                        else if (y + deltaY < -bottom)
                            deltaY = -(y + bottom); 
                    } else if (scaleHeight < height) {
                        deltaY = 0;
                        if (x + deltaX > 0)
                            deltaX = -x;
                        else if (x + deltaX < -right)
                            deltaX = -(x + right);
                    } else {
                        if (x + deltaX > 0)
                            deltaX = -x;
                        else if (x + deltaX < -right)
                            deltaX = -(x + right);

                        if (y + deltaY > 0)
                            deltaY = -y;
                        else if (y + deltaY < -bottom)
                            deltaY = -(y + bottom);
                    }
                    matrix.postTranslate(deltaX, deltaY);
                    last.set(curr.x, curr.y);
                    
                    // Create trigger for move event
                    if (mTouchMoveListener != null)
                    	mTouchMoveListener.onMove((int)deltaX, (int)deltaY);
                }
                break;
            case MotionEvent.ACTION_UP:
                mode = NONE;
                int xDiff = (int) (curr.x - start.x);
                int yDiff = (int) (curr.y - start.y);
                if (Math.abs(xDiff) < CLICK && Math.abs(yDiff) < CLICK) {
                    performClick();
                } else {
                	if (mTouchMoveListener != null && saveScale == 1f) {
                    	if (Math.abs(xDiff) < LIMIT_STEP && Math.abs(yDiff) < LIMIT_STEP)
            				break;
            			
            			if (Math.abs(xDiff) > Math.abs(yDiff) ) {
            				if (xDiff > 0)
            					mTouchMoveListener.onMoveRight();
            				else if (xDiff < 0)
            					mTouchMoveListener.onMoveLeft();
            			} else {
            				if (yDiff > 0)
            					mTouchMoveListener.onMoveBottom();
            				else if (yDiff < 0)
            					mTouchMoveListener.onMoveTop();
            			}
                    }
                    break;
                }
                
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
        }
        setImageMatrix(matrix);
        invalidate();
        
        return true; // indicate event was handled
	}
	
	/*
	@Override
	public void setImageMatrix(Matrix matrix) {
		matrix.getValues(m);
		
        float x = m[Matrix.MTRANS_X];
        float y = m[Matrix.MTRANS_Y];
        float scale = m[Matrix.MSCALE_X];
        float rotate = m[Matrix.MSKEW_X];
        
        setTranslationX(x);
        setTranslationX(y);
        setScaleX(scale);
        setScaleY(scale);
        setRotation(rotate);
	}
	*/
}