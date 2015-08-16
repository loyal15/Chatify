package com.gpit.android.camera;

import java.io.IOException;
import java.util.List;

import junit.framework.Assert;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.gpit.android.util.Utils;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
	private final static String LOG_TAG = "CameraPreview";
	
	// Video Quality
	private int[] mAvailableVideoQualities = null;
		
	private Activity mActivity;
	
	// Recorder
	private MediaRecorder mMediaRecorder;
	private boolean mIsRecording = false;
	
	// MUST BE SET BEFORE PREPARED
	private static String videoPath = "";
	private boolean mIsFront = false;
	
	// Camera
	private int mCameraID;
	private Camera mCamera;
	private List<Size> mSupportedPictureSizes;
	private List<Size> mSupportedPreviewSizes;
	private Size mPreviewSize = null;
	
	// Sound
	private MediaPlayer mShootPlayer;
	
	// View
	private SurfaceHolder mSurfaceHolder;
	
	// Orientation
	private int mOrientation;
	
	// Video quality
	private int mQuality;
	
	// Focus
	private AutoFocusCallback mAutoFocusCallback;
	
	public CameraPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		if (context instanceof Activity) {
			mActivity = (Activity)context;
		}
		
		// Retrieve params
		String orentiation = attrs.getAttributeValue(null, "orientation");
		if (orentiation != null)
			mOrientation = Integer.valueOf(attrs.getAttributeValue(null, "orientation"));
		Assert.assertTrue(mOrientation == 0 || mOrientation == 90);
		
		mQuality = CamcorderProfile.QUALITY_HIGH;
		String quality = attrs.getAttributeValue(null, "quality");
		if (quality != null)
			mQuality = Integer.valueOf(attrs.getAttributeValue(null, "quality"));
		
		if (!ensureCamera()) {
			Toast.makeText(mActivity, 
					"Can't connect camera. Please close camera related application", Toast.LENGTH_LONG).show();
			mActivity.finish();
		}
		
		// underlying surface is created and destroyed.
		mSurfaceHolder = getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		mSensorManager = (SensorManager) context.getSystemService(Context.
                SENSOR_SERVICE);
        mAccel = mSensorManager.getDefaultSensor(Sensor.
                TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_UI);
	}

	private int getDegrees() {
		int rotation = mActivity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}
		
		return degrees;
	}
	
	public void setCameraDisplayOrientation() {
		setCameraDisplayOrientation(getDegrees());
	}
	
	public void setCameraDisplayOrientation(int degrees) {
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(mCameraID, info);
		int rotation;
		
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			rotation = (info.orientation + degrees) % 360;
			rotation = (360 - rotation) % 360; // compensate
																		// the
																		// mirror
		} else { // back-facing
			rotation = (info.orientation - degrees + 360) % 360;
		}
		mCamera.setDisplayOrientation(rotation);
	}
	
	public static boolean hasCamera() {
		int numCameras = Camera.getNumberOfCameras();
		if (numCameras > 0) {
		  return true;
		}
		
		return false;
	}
	
	public static boolean hasCamera(int type) {
		int cameraCount = 0;
	    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	    cameraCount = Camera.getNumberOfCameras();
	    for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
	        Camera.getCameraInfo( camIdx, cameraInfo );
	        if (cameraInfo.facing == type) {
	            return true;
	        }
	    }

		return false;
	}
	
	/*************************************** OPTIONS ******************************************/
	public void setAvailableQualityOptions(int[] qualites) {
		mAvailableVideoQualities = qualites;
	}
	
	// Switch Camera
	public int getFrontCameraId() {
	    CameraInfo ci = new CameraInfo();
	    for (int i = 0 ; i < Camera.getNumberOfCameras(); i++) {
	        Camera.getCameraInfo(i, ci);
	        if (ci.facing == CameraInfo.CAMERA_FACING_FRONT) return i;
	    }
	    return -1; // No front-facing camera found
	}
	
	
	public boolean isAvailableFrontCamera() {
		if (getFrontCameraId() == -1)
			return false;
		
		return true;
	}
	
	public boolean isFrontCamera() {
		return mIsFront;
	}
	
	public void setFrontCamera(boolean isFront) {
		mIsFront = isFront;
		
		if (!mIsRecording) {
			startPreview();
		}
	}
	
	// Pickup Image
	public Exception takePicture(PictureCallback callback) {
		try {
			mCamera.takePicture(null, null, callback);
			shootSound();
		} catch (Exception e) {
			return e;
		}
		
		return null;
	}
	
	public void shootSound()
	{
	    AudioManager meng = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
	    int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);

	    if (volume != 0)
	    {
	        if (mShootPlayer == null)
	        	mShootPlayer = MediaPlayer.create(getContext(), Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
	        if (mShootPlayer != null)
	        	mShootPlayer.start();
	    }
	}
	
	public static void setVideoRecPath(Context context, String path) {
		videoPath = path;
		
		Utils.ensureFile(context, path);
	}
	
	// SCN
	public String getSCN() {
		Parameters params = mCamera.getParameters();
		String mode = params.getSceneMode();
		
		return mode;
	}
	
	public boolean setSCN(String mode) {
		if (!isAvailableSCN(mode)) {
			return false;
		}
		
		Parameters params = mCamera.getParameters();
		params.setSceneMode(mode);
		mCamera.setParameters(params);
		
		return true;
	}
	
	public List<String> getAvailableSCN() {
		if (mCamera == null)
			return null;
		
		Parameters params = mCamera.getParameters();
		List<String> modes = params.getSupportedSceneModes();
		
		return modes;
	}
	
	public boolean isAvailableSCN(String mode) {
		List<String> modes = getAvailableSCN();
		if (modes != null) {
			for (String sceneMode : modes) {
				if (sceneMode.equals(mode)) {
					return true;
				}
			}
		}
			
		return false;
	}
	
	// Exposure
	public int getExposure() {
		Parameters params = mCamera.getParameters();
		int value = params.getExposureCompensation();
		
		return value;
	}
	
	public void setExposure(int value) {
		if (mCamera == null)
			return;
		
		Parameters params = mCamera.getParameters();
		params.setExposureCompensation(value);
		mCamera.setParameters(params);
	}
	
	public List<String> getAvailableWB() {
		if (mCamera == null)
			return null;
		
		Parameters params = mCamera.getParameters();
		List<String> modes = params.getSupportedWhiteBalance();
		
		return modes;
	}
	
	// White Balance
	public boolean isAvailableWhiteBalance(String mode) {
		List<String> modes = getAvailableWB();
		for (String wbMode : modes) {
			if (wbMode.equals(mode)) {
				return true;
			}
		}
		
		return false;
	}
	public String getWhiteBalance() {
		if (mCamera == null)
			return null;
		
		Parameters params = mCamera.getParameters();
		String mode = params.getWhiteBalance();
		
		return mode;
	}
	
	public boolean setWhiteBalance(String mode) {
		if (!isAvailableWhiteBalance(mode)) {
			return false;
		}
		
		Parameters params = mCamera.getParameters();
		params.setWhiteBalance(mode);
		mCamera.setParameters(params);
		
		return true;
	}
	
	// Flash Mode
	public List<String> getAvailableFlashMode() {
		if (mCamera == null)
			return null;
		
		Parameters params = mCamera.getParameters();
		List<String> modes = params.getSupportedFlashModes();
		
		return modes;
	}
	
	public boolean isAvailableFlashMode(String mode) {
		List<String> modes = getAvailableFlashMode();
		for (String flashMode : modes) {
			if (flashMode.equals(mode)) {
				return true;
			}
		}

		return false;
	}

	public String getFlashMode() {
		Parameters params = mCamera.getParameters();
		String mode = params.getFlashMode();

		return mode;
	}

	public boolean setFlashMode(String mode) {
		if (!isAvailableFlashMode(mode)) {
			return false;
		}

		Parameters params = mCamera.getParameters();
		params.setFlashMode(mode);
		mCamera.setParameters(params);
		
		return true;
	}
	
	// Focus Mode
	public List<String> getAvailableFocusMode() {
		if (mCamera == null)
			return null;
		
		Parameters params = mCamera.getParameters();
		List<String> modes = params.getSupportedFocusModes();
		
		return modes;
	}
		
	public boolean isAvailableFocusMode(String mode) {
		List<String> modes = getAvailableFocusMode();
		for (String focusMode : modes) {
			if (focusMode.equals(mode)) {
				return true;
			}
		}

		return false;
	}

	public String getFocusMode() {
		Parameters params = mCamera.getParameters();
		String mode = params.getFocusMode();

		return mode;
	}
	
	public boolean setFocusMode(String mode) {
		if (!isAvailableFocusMode(mode)) {
			return false;
		}

		Parameters params = mCamera.getParameters();
		params.setFocusMode(mode);
		mCamera.setParameters(params);
		
		return true;
	}
	
	public synchronized void autoFocus() {
		if (setCameraFocus(myAutoFocusCallback)) {
			mAutoFocus = false;
		}
	}
	
	public boolean setCameraFocus(AutoFocusCallback autoFocus){
		if (mCamera == null)
			return false;
		
		try {
			if (mCamera.getParameters().getFocusMode().equals(mCamera.getParameters().FOCUS_MODE_AUTO) ||
		            mCamera.getParameters().getFocusMode().equals(mCamera.getParameters().FOCUS_MODE_MACRO)){
		        mCamera.autoFocus(autoFocus);
		        return true;
		    }
		} catch (Exception e) {
			e.printStackTrace();
		}
	    
	    return false;
	}
	
	public void setAutoFocusCallback(AutoFocusCallback callback) {
		mAutoFocusCallback = callback;
	}
	
	private boolean mAutoFocus = true;
	private SensorManager mSensorManager;
    private Sensor mAccel;
    private boolean mInitialized = false;
    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastZ = 0;
    @Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
    
	public synchronized void onSensorChanged(SensorEvent event) {
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		if (!mInitialized){
		    mLastX = x;
		    mLastY = y;
		    mLastZ = z;
		    mInitialized = true;
		}
		float deltaX  = Math.abs(mLastX - x);
		float deltaY = Math.abs(mLastY - y);
		float deltaZ = Math.abs(mLastZ - z);

		if (deltaX > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
		    if (setCameraFocus(myAutoFocusCallback))
		    	mAutoFocus = false;
		}
		if (deltaY > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing)
			if (setCameraFocus(myAutoFocusCallback))
		    	mAutoFocus = false;
		}
		if (deltaZ > .5 && mAutoFocus){ //AUTOFOCUS (while it is not autofocusing) */
			if (setCameraFocus(myAutoFocusCallback))
		    	mAutoFocus = false;
		}

		mLastX = x;
		mLastY = y;
		mLastZ = z;
	}
	
	// this is the autofocus call back
	private AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {
		public void onAutoFocus(boolean success, Camera camera) {
			if (mAutoFocusCallback != null)
				mAutoFocusCallback.onAutoFocus(success, camera);
			
			Thread.yield();
			Wait.oneSec();
			mAutoFocus = true;
		}
	};
        
	/*************************************** CAMERA ****************************************/
	public void startPreview() {
		releaseCamera();
		
		if (!ensureCamera()) {
			Toast.makeText(mActivity, 
					"Can't connect camera. Please close camera related application", 
					Toast.LENGTH_LONG).show();
			mActivity.finish();
			return;
		}
		
		try {
			mCamera.setPreviewDisplay(mSurfaceHolder);
			mCamera.startPreview();
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
			// TODO: add more exception handling logic here
		}
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
		
		startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Surface will be destroyed when we return, so stop the preview.
		// Because the CameraDevice object is not a shared resource, it's
		// very important to release it when the activity is paused.
		try {
			// Stop recording or preview
			mCamera.stopPreview();
			if (mIsRecording)
				mMediaRecorder.stop();
			
			releaseMediaRecorder();
			releaseCamera();
		} catch (Exception e) {
			
		}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		if (w == 0 || h == 0)
			return null;
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the
		// requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	@Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }
	
	/*
	@Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            final View child = this;

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }
	*/
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w,
			int h) {
		// stop preview before making changes
		try {
			mCamera.stopPreview();
		
			// Now that the size is known, set up the camera parameters and begin
	        // the preview.
	        Camera.Parameters parameters = mCamera.getParameters();
	        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
	        requestLayout();
	
	        mCamera.setParameters(parameters);
	        mCamera.startPreview();
		} catch (Exception e) {
			// ignore: tried to stop a non-existent preview
		}
	}

	/** A safe way to get an instance of the Camera object. */
	public Camera getCameraInstance(boolean front){
		try {
			if (mCamera == null) {
				mCamera = Camera.open(front ? 1 : 0); // attempt to get a Camera instance
			}
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return mCamera; // returns null if camera is unavailable
	}
	
	private boolean ensureCamera() {
		mCamera = getCameraInstance(mIsFront);
		if (mCamera == null)
			return false;
		mCameraID = mIsFront ? 1 : 0;
		setCameraDisplayOrientation();
		
		mCamera.setDisplayOrientation(mOrientation);
		// Adjust picture size
		mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
		int maxWidth = 0, maxHeight = 0;
		for (Size size : mSupportedPictureSizes) {
			if (maxWidth < size.width) {
				maxWidth = size.width;
				maxHeight = size.height;
			}
		}
		
		Camera.Parameters parameters = mCamera.getParameters();
		try {
			parameters.setFocusMode("auto");
			parameters.setPictureSize(maxWidth, maxHeight);
			mCamera.setParameters(parameters);
		} catch (Exception e) {}
		// Adjust preview size by orientation
		mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
		if (mOrientation == 90) {
			for (Size size : mSupportedPreviewSizes) {
				int width = size.width;
				size.width = size.height;
				size.height = width; 
			}
		}
		mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, getMeasuredWidth(), getMeasuredHeight());
		
		if (mPreviewSize != null) {
			try {
				// make any resize, rotate or reformatting changes here
				parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
				mCamera.setParameters(parameters);
			} catch (Exception e) {}
		}
		
		return true;
	}
	
	public boolean getSupportFrontCamera() {
		if (Camera.getNumberOfCameras() > 1)
			return true;
		return false;
	}
	
	private void releaseCamera(){
        if (mCamera != null){
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }
	
	/*************************************** RECORDER ******************************************/
	public void setCameraRecordOrientation() {
		setCameraRecordOrientation(getDegrees());
	}
	
	public void setCameraRecordOrientation(int degrees) {
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(mCameraID, info);
		int rotation;
		
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			rotation = (info.orientation - mOrientation + 360) % 360;
		} else { // back-facing
			rotation = (info.orientation + mOrientation) % 360;
		}
		mMediaRecorder.setOrientationHint(rotation);
	}
	
	public boolean startRecording() {
		if (mIsRecording)
			return true;
		
		releaseCamera();
		if (!prepareMediaRecorder()) {
			Toast.makeText(mActivity, "Please close other application are using camera", Toast.LENGTH_LONG).show();
			return false;
		}
		
		try {
			mMediaRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		mIsRecording = true;
		
		return true;
	}
	
	public boolean stopRecording() {
		if (!mIsRecording)
			return true;
		
		try {
			mMediaRecorder.stop();
			releaseMediaRecorder();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		mIsRecording = false;
		
		return true;
	}
	
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }


    private boolean prepareMediaRecorder() {
    	// mCamera = getCameraInstance(mIsFront);
    	ensureCamera();
    	if (mCamera == null)
    		return false;
    	
	    mMediaRecorder = new MediaRecorder();
	    
	    // Step 1: Unlock and set camera to MediaRecorder
	    // Camera.Parameters parameters = mCamera.getParameters();
        // parameters.setPreviewSize(320, 480);
	    mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        // Set orientation
        setCameraRecordOrientation();
        
	    // Step 2: Set sources
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

	    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
	    /*
	    for (int quality : VIDEO_QUALITY) {
	    	try {
	    		mMediaRecorder.setProfile(CamcorderProfile.get(quality));
	    		break;
	    	} catch (Exception e) {
	    	}
	    }
	    */
	    
	    if (mAvailableVideoQualities == null)
	    	mAvailableVideoQualities = new int[] {mQuality};
	    for (int quality : mAvailableVideoQualities) {
			try {
				mMediaRecorder.setProfile(CamcorderProfile.get(quality));

				// Set codec
				// mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);

				/*
				 * // Gingerbread and up can have wide band ie 16,000 hz
				 * recordings // (Okay quality for human voice) if (sdk >= 10) {
				 * //
				 * mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB
				 * ); mMediaRecorder.setAudioSamplingRate(16000); } else { //
				 * Other devices only have narrow band, ie 8,000 hz // (Same
				 * quality as a phone call, not really good quality for any
				 * purpose. // For human voice 8,000 hz means /f/ and /th/ are
				 * indistinguishable) //
				 * mMediaRecorder.setAudioEncoder(MediaRecorder
				 * .AudioEncoder.AMR_NB); }
				 */

				// mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
				// mMediaRecorder.setVideoSize(480, 320);
				// mMediaRecorder.setVideoFrameRate(20);
				// mMediaRecorder.setAudioEncoder(MediaRecorder.VideoEncoder.DEFAULT);

				// Step 4: Set output file
				mMediaRecorder.setOutputFile(videoPath);

				// Step 5: Set the preview output
				mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

				// Step 6: Prepare configured MediaRecorder
				mMediaRecorder.prepare();
				return true;
			} catch (Exception e) {
				Log.d(LOG_TAG,
						"Exception preparing MediaRecorder: "
								+ e.getMessage());
			}
		}

	    releaseMediaRecorder();
		return false;
	}
}