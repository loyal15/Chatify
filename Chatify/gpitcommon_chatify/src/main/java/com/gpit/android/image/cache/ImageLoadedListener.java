package com.gpit.android.image.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public interface ImageLoadedListener {
	public boolean onImageLoaded(ImageView imageView, String local, Drawable drawable);
}
