package com.gpit.android.image.cache;

import java.io.File;

import com.gpit.android.library.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RemoteImageView extends ImageView {
	private ImageLoadedListener mListener;
	
	private String mLocal;
	private String mRemote;
	private int mWidth = -1;
	private int mHeight = -1;
	private HTTPThread mThread = null;
	private Drawable mDrawable;
	
	public RemoteImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setLocalURI(String local) {
		mLocal = local;
	}

	public void setRemoteURI(String uri) {
		if (uri.startsWith("http")) {
			if (mRemote != null && !mRemote.equals(uri))
				mDrawable = null;
			
			mRemote = uri;
		}
	}

	public void setImageSize(int width, int height) {
		mWidth = width;
		
	}
	
	public void loadImage() {
		loadImage(null);
	}
	
	public void loadImage(ImageLoadedListener listener) {
		mListener = listener;
		
		if (mRemote != null) {
			if (mLocal == null) {
				mLocal = Environment.getExternalStorageDirectory() + "/.remote-image-view-cache/" + mRemote.hashCode() + ".jpg";
			}
			// check for the local file here instead of in the thread because
			// otherwise previously-cached files wouldn't be loaded until after
			// the remote ones have been downloaded.
			File local = new File(mLocal);
			if (local.exists()) {
				setFromLocal();
			} else {
				// we already have the local reference, so just make the parent
				// directories here instead of in the thread.
				local.getParentFile().mkdirs();
				queue();
			}
		}
	}

	@Override
	public void finalize() {
		if (mThread != null) {
			HTTPQueue queue = HTTPQueue.getInstance();
			queue.dequeue(mThread);
		}
	}

	private void queue() {
		if (mThread == null) {
			mThread = new HTTPThread(mRemote, mLocal, mHandler);
			HTTPQueue queue = HTTPQueue.getInstance();
			queue.enqueue(mThread, HTTPQueue.PRIORITY_HIGH);
		}
		setImageResource(R.drawable.ic_launcher_test);
	}

	private void setFromLocal() {
		mThread = null;
		
		if (mDrawable == null)
			mDrawable = Drawable.createFromPath(mLocal);
		if (mDrawable != null) {
			setImageDrawable(mDrawable);
			
			if (mListener != null)
				mListener.onImageLoaded(this, mLocal, mDrawable);
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setFromLocal();
		}
	};
}
