package com.gpit.android.image.cache;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Stack;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import com.gpit.android.library.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

public class ImageManager {
	
	private final static String TAG = ImageManager.class.getSimpleName();

	private Thread imageLoaderThread = new Thread(new ImageQueueManager());
	MemoryCache memoryCache=new MemoryCache();
	private HashMap<ImageView, String> imageMap = new HashMap<ImageView, String>();
	ImageQueue imageQueue;
	private File cacheDir;
	
	public ImageManager(Context con){
		
		imageLoaderThread.setPriority(Thread.NORM_PRIORITY - 1);
		
		String sdState = android.os.Environment.getExternalStorageState();
	    if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) {
	    	cacheDir = con.getExternalCacheDir();    
	    } else {
	    	cacheDir = con.getCacheDir();
	    }

	    if(!cacheDir.exists()) {
	    	cacheDir.mkdirs();
	    }
	    
		imageQueue = new ImageQueue();
	}
	
	public void setCacheSize(long size) {
		memoryCache.setLimit(size);
	}
	
	public void clear(){
		imageQueue.CleanStack();
		memoryCache.clear();
		imageMap.clear();
	}
	
	public boolean clearFilesInMemory(){
		
		File[] files = cacheDir.listFiles();
		
		if (files == null || files.length < 1)
			return true;
		
		int filesLength = files.length;
		for (int i=0; i<filesLength; i++) {
			File newFile = files[i];
			newFile.delete();
		}
		return true;
		
	}
	
	public boolean sizeExceeds(){
		return false;
	}
	
	public void displayImage(String url, ImageView iv) {
		displayImage(url, iv, null);
	}
	
	public void displayImage(String url, ImageView iv, ImageLoadedListener listener) {
		if (url == null)
			return;
		
		Bitmap bm = checkIfImageinCache(url);
		if (bm != null) {
			setImageBitmap(iv, bm, listener);
			memoryCache.put(url, bm);
			return;
		}
		
		imageMap.put(iv, url);
		Bitmap bitmap=memoryCache.get(url);
		
		if(bitmap != null) {
			setImageBitmap(iv, bitmap, listener);
		} else {
			queueImage(url, iv, listener, false);
//		    iv.setImageResource(R.drawable.loading_image);
		}
	}
	
	public void displayCircleImage(String url, ImageView iv, ImageLoadedListener listener) {
		Bitmap bm = checkIfImageinCache(url);
		if (bm != null) {
			bm = getCricleCroppedBitmap(bm);
			setImageBitmap(iv, bm, listener);
			memoryCache.put(url, bm);
			return;
		}
		
		imageMap.put(iv, url);
		Bitmap bitmap=memoryCache.get(url);
		
		if(bitmap != null) {
			bitmap = getCricleCroppedBitmap(bitmap);
			setImageBitmap(iv, bitmap, listener);
		} else {
			queueImage(url, iv, listener, true);
//		    iv.setImageResource(R.drawable.loading_image);
		}
		}
	
	public Bitmap getCricleCroppedBitmap(Bitmap bitmap) {
		
		if (bitmap == null)		return null;
		
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	            bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
	            bitmap.getWidth() / 2, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
	    //return _bmp;
	    return output;
	}
	
	private void setImageBitmap(ImageView iv, Bitmap bitmap, ImageLoadedListener listener) {
		if (bitmap == null)
			return;
		
		boolean result = true;
		BitmapDrawable drawable = new BitmapDrawable(bitmap);
		if (listener != null) {
			result = listener.onImageLoaded(iv, null, drawable);
		}
		
		if (result) {
			iv.setImageDrawable(drawable);
		}
	}
	
	private Bitmap checkIfImageinCache(String url){
		String fileName = String.valueOf(url.hashCode());
		File f = new File(cacheDir, fileName);
		
		try {
		Bitmap bmp = BitmapFactory.decodeFile(f.getPath());
		if (bmp != null)
			return bmp;
		} catch (OutOfMemoryError error) {
			Log.e("ImageManager", "OutOfMemoryError");
			memoryCache.refreshStroage();
			
			Bitmap bmp = BitmapFactory.decodeFile(f.getPath());
			if (bmp != null)
				return bmp;
		}
		
		return null;
	}
	
	private void queueImage(String url, ImageView iv, ImageLoadedListener listener, boolean bClip){
		imageQueue.Clean(iv);
		ImageRef newRef = new ImageRef(url, iv, listener, bClip);
		
		synchronized (imageQueue.imageRefs) {
			imageQueue.imageRefs.push(newRef);
			imageQueue.imageRefs.notifyAll();
		}
		
		if (imageLoaderThread.getState() == Thread.State.NEW)
			imageLoaderThread.start();
		
	}
	
	private class ImageQueue{
		
		private Stack<ImageRef> imageRefs = new Stack<ImageRef>();
		
		public void CleanStack(){
			if (imageRefs != null) {
				imageRefs.clear();
				synchronized (imageQueue.imageRefs) {
					imageRefs.notifyAll();
				}
			}
		}
		
		public void Clean(ImageView view) {
			for(int i = 0 ;i < imageRefs.size();) {
				if(imageRefs.get(i).iv == view)
					imageRefs.remove(i);
			    else ++i;
			}
		}
	}
	
	private class ImageRef{
		public String url;
		public ImageView iv;
		public ImageLoadedListener listener;
		public boolean bClip;
		public ImageRef(String url, ImageView iv, ImageLoadedListener listener, boolean bclip){
			this.url = url;
			this.iv = iv;
			this.listener = listener;
			this.bClip = bclip;
		}
	}
	
	private class ImageQueueManager implements Runnable{

		public void run() {
			try{
			
			while(true){
				// Thread waits until there are images in the 
		        // queue to be retrieved
		        if(imageQueue.imageRefs.size() == 0) {
		          synchronized(imageQueue.imageRefs) {
		            imageQueue.imageRefs.wait();
		          }
		        }
		        
		        // when we have images to load
		        if (imageQueue.imageRefs.size() != 0 ){
		        	ImageRef imagetoLoad;
		        	
		        	synchronized (imageQueue.imageRefs) {
						imagetoLoad = imageQueue.imageRefs.firstElement();
						imageQueue.imageRefs.remove(0);
					}
		        	
		        	if(imageViewReused(imagetoLoad)) {
		        		Log.i(TAG, "getting image for an image failed");
		        		continue;
		        	}
		        	
		        	Log.i(TAG, "getting image for an image");
		        	Bitmap bmp = getBitMap(imagetoLoad.url);
		        	if(bmp!=null){
		        	if (imagetoLoad.bClip) {
		        		bmp = getCricleCroppedBitmap(bmp);
		        	}
		        	
		        	memoryCache.put(imagetoLoad.url, bmp);
		        	
		        	if (!imageViewReused(imagetoLoad))
		        	{
		        		BitmapDisplayer bmpDisplayer = 
		        				new BitmapDisplayer(bmp, imagetoLoad.iv, imagetoLoad.listener);
		        		
		        			Activity a = null;
		        			if (imagetoLoad.iv.getContext() instanceof Activity) {
		        				a = (Activity) imagetoLoad.iv.getContext();
		        			}
		        			
		        			if (a != null)
		        		a.runOnUiThread(bmpDisplayer);
		        	}
		        }
		        }
		        if (Thread.interrupted())
		        	break;
			}
			} catch (InterruptedException e){
				//Log.i(TAG, "interrupted exception occured " + e.toString());
			}
		}
	}
	
    boolean imageViewReused(ImageRef photoToLoad){
        String tag=imageMap.get(photoToLoad.iv);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
	
	private class BitmapDisplayer implements Runnable{

		Bitmap bmp;
		ImageView iv;
		ImageLoadedListener listener;
		
		public BitmapDisplayer(Bitmap bmp, ImageView iv, ImageLoadedListener listener){
			this.bmp = bmp;
			this.iv = iv;
			this.listener = listener;
		}
		
		public void run() {
			if (bmp != null && !bmp.isRecycled()){
				setImageBitmap(iv, bmp, listener);
			} else{
//				iv.setImageResource(R.drawable.loading_image);
			}
		}
	}
	
	private Bitmap getBitMap(String url){
		boolean isHttps = false;
		
		if ( !url.contains("http")  && !url.contains("https"))
			return null;
		
		if (url.startsWith("https"))
			isHttps = true;
		
		String fileName = String.valueOf(url.hashCode());
		File f = new File(cacheDir, fileName);
		
		Bitmap bmp = BitmapFactory.decodeFile(f.getPath());
		if (bmp != null)
			return bmp;
		
		try {
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

		       DefaultHttpClient client = new DefaultHttpClient();

		       if (isHttps) {
			       SchemeRegistry registry = new SchemeRegistry();
			       SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
			       socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			       registry.register(new Scheme("https", socketFactory, 443));
			       SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
			       client = new DefaultHttpClient(mgr, client.getParams());
		       }

		       // Set verifier      
		       HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

		       // Example send http request
		       HttpGet httpPost = new HttpGet(url);
		       HttpResponse response = client.execute(httpPost);
		       HttpEntity entity = response.getEntity();
		       InputStream is = entity.getContent();
			
		       bmp = BitmapFactory.decodeStream(is);
			
			if (bmp == null){
				is.close();
				return null;
			}
			
			if (!sizeExceeds())
				WriteFile(bmp, f);
			is.close();
			return bmp;
			
		} catch (MalformedURLException e) {
			//Log.i(TAG, "mal formed url "+e.toString());
		} catch (IOException e) {
			//Log.i(TAG, "ioexception "+e.toString());
		}
		return null;
	}
	
	private void WriteFile(Bitmap bmp, File f){
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream(f);
			bmp.compress(Bitmap.CompressFormat.JPEG, 70, out);
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
					//Log.i(TAG, "failed while closing stream " + e.toString());
				}
		} catch (FileNotFoundException e) {
			//Log.i(TAG, "file not found exception " + e.toString());
		}
		
	}
	
	
}
