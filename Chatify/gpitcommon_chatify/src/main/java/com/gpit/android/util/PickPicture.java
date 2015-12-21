package com.gpit.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.widget.Toast;

public class PickPicture {
	private final static String TAG = "Pick Picture";

	public final static int SELECT_PICTURE_FROM_GALLERY = 100;
	public final static int SELECT_PICTURE_FROM_CAMERA = 101;
	
	public final static int OS_MAX_WIDTH = 2048;
	public final static int OS_MAX_HEIGHT = OS_MAX_WIDTH;

	public static int MAX_WIDTH = OS_MAX_WIDTH;
	public static int MAX_HEIGHT = MAX_WIDTH;
	public final static int MINIMUM_MAX_WIDTH = 2048;
	public final static int MINIMUM_MAX_HEIGHT = MINIMUM_MAX_WIDTH;
	
	private static PickPicture instance;
	private String mTempImagePath;

	public static PickPicture getInstance(Activity activity) {
		if (instance == null) {
			instance = new PickPicture(activity);
		}
		
		instance.setActivity(activity);

		return instance;
	}

	private Activity mActivity;

	private OnPickPictureCompleteListener mListener;

	public PickPicture(Activity activity) {
		setActivity(activity);
	}

	public void setActivity(Activity activity) {
		mActivity = activity;
	}
	
	public void setListener(OnPickPictureCompleteListener listener) {
		mListener = listener;
	}

	public void setMaxSize(int maxWidth, int maxHeight) {
		if (maxWidth > OS_MAX_WIDTH) {
			maxWidth = OS_MAX_WIDTH;
		}
		
		if (maxHeight > OS_MAX_HEIGHT) {
			maxHeight = OS_MAX_HEIGHT;
		}
		
		MAX_WIDTH = maxWidth;
		MAX_HEIGHT = maxHeight;
	}
	
	public void pickPictureFromGallery(OnPickPictureCompleteListener listener) {
		setListener(listener);

		pickPictureFromGallery();
	}

	public void pickPictureFromGallery() {
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		i.setType("image/*");
		mActivity.startActivityForResult(i, SELECT_PICTURE_FROM_GALLERY);
	}
	
	public void pickPictureFromCamera(OnPickPictureCompleteListener listener) {
		setListener(listener);
		
		pickPictureFromCamera(mActivity);
	}
	
	public void pickPictureFromCamera(Activity activity) {
		
		setActivity(activity);
		
    	Intent takePictureFromCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	
		mTempImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.pick.png";
		File file = new File(mTempImagePath);
		takePictureFromCameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		mActivity.startActivityForResult(takePictureFromCameraIntent, SELECT_PICTURE_FROM_CAMERA);
	}

	/**************************** Activity Result ********************************/
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_PICTURE_FROM_GALLERY) {
				try {
					if (intent != null) {
						Uri selectedImage = intent.getData();
						File tempImageFile = File.createTempFile("pick", ".png");
						tempImageFile.deleteOnExit();
						String tempImagePath = tempImageFile.getAbsolutePath();
						
						final String[] filePathColumn = { MediaColumns.DATA, MediaColumns.DISPLAY_NAME };
						Cursor cursor = mActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
						// some devices (OS versions return an URI of
						// com.android instead of com.google.android
						if (selectedImage.toString().startsWith(
								"content://com.android.gallery3d.provider")) {
							// use the com.google provider, not the com.android
							// provider.
							selectedImage = Uri.parse(selectedImage.toString().replace("com.android.gallery3d",
											"com.google.android.gallery3d"));
						}
						if (cursor != null) {
							cursor.moveToFirst();
							int columnIndex = cursor
									.getColumnIndex(MediaColumns.DATA);
							// if it is a picasa image on newer devices with OS
							// 3.0 and up
							if (selectedImage.toString().startsWith("content://com.google.android.gallery3d") || 
									selectedImage.toString().startsWith("content://com.sec.android.gallery3d.provider")) {
								columnIndex = cursor.getColumnIndex(MediaColumns.DISPLAY_NAME);
								if (columnIndex != -1) {
									// Do this in a background thread, since we
									// are fetching a large image from the web
									new DownloaderTask(selectedImage).execute(selectedImage, Uri.parse(tempImagePath));
								}
							} else { // it is a regular local image file
								String filePath = cursor.getString(columnIndex);
								cursor.close();
								// Copy source image to editing image
								Utils.copyFile(filePath, tempImagePath);
								if (adjustBitmap(tempImagePath) != null) {
									if (mListener != null) {
										mListener.onCompleted(tempImagePath);
									}
								} else {
									if (mListener != null) {
										mListener.onFailed(new Exception("Invalid Bitmap"));
									}
								}
							}
						}
						// If it is a picasa image on devices running OS prior
						// to 3.0
						else if (selectedImage != null && selectedImage.toString().length() > 0) {
							new DownloaderTask(selectedImage).execute(selectedImage, Uri.parse(tempImagePath));
						}
					}
				} catch (Exception e) {
					if (mListener != null) {
						mListener.onFailed(e);
					}
				}
			} else if ( requestCode == SELECT_PICTURE_FROM_CAMERA ) {
				
				adjustBitmap(mTempImagePath);
				
				File file = new File(mTempImagePath);
				
				if (file.exists()) {
					// Copy source image to editing image
					if (mListener != null) {
						mListener.onCompleted(mTempImagePath);
					}
				}
			}
		}
	}
	

	class DownloaderTask extends AsyncTask<Uri, Void, Boolean> {
		private Uri uri;
		private String filePath;

		public DownloaderTask(Uri uri) {
			this.uri = uri;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Utils.showWaitingDlg(mActivity);
		}

		@Override
		// Actual download method, run in the task thread
		protected Boolean doInBackground(Uri... params) {
			boolean result = false;

			try {
				// params comes from the execute() call: params[0] is the uri.
				uri = params[0];
				filePath = params[1].toString();

				try {
					InputStream is = null;
					
					if (uri.toString().startsWith("content://com.google.android.gallery3d") || 
							uri.toString().startsWith("content://com.sec.android.gallery3d.provider")) {
						is = mActivity.getContentResolver().openInputStream(uri);
					} else {
						is = new URL(uri.toString()).openStream();
					}

					FileOutputStream os = new FileOutputStream(filePath);
					copyStream(is, os);
					os.close();

					result = true;
				} catch (Exception ex) {
					Log.d(TAG, "Exception: " + ex.getMessage());
					// something went wrong
					ex.printStackTrace();

					if (mListener != null) {
						mListener.onFailed(ex);
					}
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (mListener != null) {
					mListener.onFailed(e);
				}
			}

			return result;
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Boolean result) {
			Utils.hideWaitingDlg();
			if (result != null && result) {
				if (adjustBitmap(filePath) != null) {
					if (mListener != null) {
						mListener.onCompleted(filePath);
					}
				} else {
					if (mListener != null) {
						mListener.onFailed(new Exception("Invalid Bitmap"));
					}
				}
			} else {
				if (mListener != null) {
					mListener.onFailed(new Exception("Unknown error"));
				}
			}
		}
	}

	public void copyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	private Bitmap adjustBitmap(String filePath) {
		try {
			Bitmap srcBitmap;

			srcBitmap = getBitmapAtDimensionFromFile(filePath);
			if (srcBitmap == null)
				return null;
			
			if (MAX_WIDTH > 2048 || MAX_HEIGHT > 2048)
				MAX_WIDTH = MAX_HEIGHT = 2048;
			int newWidth = Math.min(srcBitmap.getWidth(), MAX_WIDTH);
			int newHeight = Math.min(srcBitmap.getHeight(), MAX_HEIGHT);
			float scale = Math.min((float) newWidth / srcBitmap.getWidth(),
					(float) newHeight / srcBitmap.getHeight());
			newWidth = (int) (srcBitmap.getWidth() * scale);
			newHeight = (int) (srcBitmap.getHeight() * scale);

			if (srcBitmap.getWidth() != newWidth
					|| srcBitmap.getHeight() != newHeight) {
				Bitmap newBitmap = Bitmap.createScaledBitmap(srcBitmap,
						newWidth, newHeight, true);
				srcBitmap.recycle();
				srcBitmap = newBitmap;
			}

			// Save bitmap to edit path
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			boolean result = srcBitmap.compress(Bitmap.CompressFormat.JPEG,
					100, bytes);

			if (result) {
				// write the bytes in file
				FileOutputStream fo = new FileOutputStream(filePath);
				fo.write(bytes.toByteArray());
				fo.close();

				// Set background
				return srcBitmap;
			}
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(mActivity, "Loading failed: " + e.toString(),
					Toast.LENGTH_LONG).show();
			return null;
		}

		return null;
	}

	public Bitmap getBitmapAtDimensionFromFile(String filePath) {
		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		try {
			BitmapFactory.decodeFile(filePath, o);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// The new size we want to scale to is represented by size

		// Find the correct scale value. It should be the power of 2.
		int scale = 1;
		while (true) {
			// Decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			o2.inDither = false; // Disable Dithering mode
			o2.inTempStorage = new byte[32 * 1024];
			try {
				Bitmap bmp = BitmapFactory.decodeFile(filePath, o2);

				return getRotatedBitmap(bmp, filePath);
			} catch (OutOfMemoryError error) {
				scale *= 2;

				continue;
			}
		}
	}

	/**
	 * Method which rotates a bitmap in case it needs it
	 * 
	 * @param bmp
	 *            - the bitmap which we try to rotate
	 * @param path
	 *            - path of the file in the bitmap
	 * @return
	 */
	public static Bitmap getRotatedBitmap(Bitmap bmp, String path) {
		try {
			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION, 1);

			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
			} else if (orientation == 3) {
				matrix.postRotate(180);
			} else if (orientation == 8) {
				matrix.postRotate(270);
			} else {
				return bmp;
			}

			return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
					bmp.getHeight(), matrix, true); // rotating bitmap
		} catch (Exception e) {

			return null;
		}
	}
}
