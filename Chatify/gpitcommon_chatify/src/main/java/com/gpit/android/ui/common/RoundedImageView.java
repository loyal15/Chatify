package com.gpit.android.ui.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
	private int mCornerPixel = 0;

	public RoundedImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setCornerPixel(int cornerPixel) {
		mCornerPixel = cornerPixel;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mCornerPixel > 0) {
			Path clipPath = new Path();
			int w = this.getWidth();
			int h = this.getHeight();
			clipPath.addRoundRect(new RectF(0, 0, w, h), mCornerPixel,
					mCornerPixel, Path.Direction.CW);
			canvas.clipPath(clipPath);
		}
		super.onDraw(canvas);

	}

	/*
	 * @Override public void setImageBitmap(Bitmap bm) { if (bm != null &&
	 * mCornerPixel > 0) { Bitmap output = Bitmap.createBitmap(bm.getWidth(),
	 * bm.getHeight(), Config.ARGB_8888); Canvas canvas = new Canvas(output);
	 * 
	 * final int color = 0xff424242; final Paint paint = new Paint(); final Rect
	 * rect = new Rect(0, 0, bm.getWidth(), bm.getHeight()); final RectF rectF =
	 * new RectF(rect); final float roundPx = mCornerPixel;
	 * 
	 * paint.setAntiAlias(true); canvas.drawARGB(0, 0, 0, 0);
	 * paint.setColor(color);
	 * 
	 * canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	 * 
	 * paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	 * canvas.drawBitmap(bm, rect, rect, paint);
	 * 
	 * bm = output; }
	 * 
	 * super.setImageBitmap(bm); }
	 * 
	 * @Override public void setImageDrawable (Drawable drawable) { if (drawable
	 * != null && mCornerPixel > 0) { Bitmap bm =
	 * ((BitmapDrawable)drawable).getBitmap(); if (bm != null) { Bitmap output =
	 * Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.ARGB_8888);
	 * Canvas canvas = new Canvas(output);
	 * 
	 * final int color = 0xff424242; final Paint paint = new Paint(); final Rect
	 * rect = new Rect(0, 0, bm.getWidth(), bm.getHeight()); final RectF rectF =
	 * new RectF(rect); final float roundPx = mCornerPixel;
	 * 
	 * paint.setAntiAlias(true); canvas.drawARGB(0, 0, 0, 0);
	 * paint.setColor(color);
	 * 
	 * canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	 * 
	 * paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	 * canvas.drawBitmap(bm, rect, rect, paint);
	 * 
	 * drawable = new BitmapDrawable(output); } }
	 * 
	 * super.setImageDrawable(drawable); }
	 */
}
