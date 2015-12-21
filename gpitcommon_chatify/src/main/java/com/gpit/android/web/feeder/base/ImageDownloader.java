package com.gpit.android.web.feeder.base;

import java.lang.ref.*;
import java.util.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.*;
import android.os.*;
import android.util.*;
import android.widget.*;

public class ImageDownloader {

	private static final String TAG = ImageDownloader.class.getSimpleName();

	private static ImageDownloader instance = null;

	private Context mContext;
	private HashMap<String, WeakReference<Drawable>> m_imageRepo = null;
	
	private ImageDownloader(Context context) {
		mContext = context;
		
		if (m_imageRepo == null) {
			m_imageRepo = new HashMap<String, WeakReference<Drawable>>();
		}
	}

	public static ImageDownloader getInstance(Context context) {
		if (instance == null) {
			instance = new ImageDownloader(context);
		}
		return instance;
	}

	public void download(String url, ImageView imageView) {

		Drawable d = null;
		
		if (m_imageRepo.get(url) != null) {
			d = m_imageRepo.get(url).get();
		}

		if (d != null) {
			if (imageView != null)
				imageView.setImageDrawable(d);
		} else {
			if (imageView != null)
				imageView.setTag(url);
			DrawableDownloaderTask task = new DrawableDownloaderTask(imageView);
			task.execute(url);
		}
	}

	class DrawableDownloaderTask extends AsyncTask<String, Void, Drawable> {
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public DrawableDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		}

		@Override
		// Actual download method, run in the task thread
		protected Drawable doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			url = params[0];
			Log.d(TAG, "Start Getting Image from URL : " + url);
			return loadDrawableFromURL(url);
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Drawable d) {
			if (isCancelled()) {
				d = null;
			}

			if (d == null) {
				Log.d(TAG, "Bitmap is NULL for the URL : " + url);
				return;
			}
			Log.d(TAG, "Got Bitmap for the URL : " + url);
			if (imageViewReference != null) {
				try {

					ImageView imageView = imageViewReference.get();
					if (imageView != null && imageView.getTag().toString().equals(url)) {
						imageView.setImageDrawable(d);
					}
					m_imageRepo.put(url, new WeakReference<Drawable>(d));
				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				} finally {
				}
			}
		}
	}
	
	private Drawable loadDrawableFromURL(String url) {
		try {
			// InputStream is = (InputStream) new URL(url).getContent();
			Bitmap bitmap = ImageFeeder.FEEDER.getImage(mContext, url, -1, -1, true, null, null);
			Drawable d = new BitmapDrawable(bitmap);
			return d;
		} catch (Exception e) {
			if (e.getMessage() != null)
				Log.e(TAG, e.getMessage());
			return null;
		} catch (OutOfMemoryError ooe) {
			System.gc();
			
			return loadDrawableFromURL(url);
		}
	}
}
