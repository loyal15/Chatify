package com.gpit.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
public class GPSHandler {
	public final static int REFRESH_UPDATE_MIN_TIME = 1;
	public final static float REFRESH_UPDATE_MIN_DISTANCE = 0.0f;

	// private int mMinTime;
	// private int mMinDistance;
	// public static final long MAX_MTIMEOUT = 1000 * 60 * 60 * 24 * 10;

	private Context mContext;
	private LocationManager locationManager;
	private OnLocationUpdateListener mLocationUpdateListener;
	private boolean gps_enabled = false;
	private boolean network_enabled = false;

	Location mCurrLoc = null;
	
	@SuppressWarnings("unused")
	private boolean mServiceDisallowedByUser = false;
	
	boolean waitingDialogFlag = false;
	private DialogInterface.OnClickListener dialogClickYes = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,
				int which) {
			Intent intent = new Intent(
					android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			mContext.startActivity(intent);
			dialog.dismiss();
			if(waitingDialogFlag)
			{
				Utils.showWaitingDlg(mContext);
				waitingDialogFlag = false;
			}
		}
	};
	
	private DialogInterface.OnClickListener dialogClickNo = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog,
				int which) {
			mServiceDisallowedByUser = true;
			mLocationUpdateListener.onCancel();
			if(waitingDialogFlag)
			{
				Utils.showWaitingDlg(mContext);
				waitingDialogFlag = false;
			}
		}
	};

	public boolean getLocation(Context context,
			final OnLocationUpdateListener listener, final int minTime,
			final float minDistance, boolean checkAvailability) {
		mContext = context;
		network_enabled = false;
		gps_enabled = false;
	
		// I use OnLocationUpdateListener callback class to pass location value
		// from GPSHandler to user code.
		mLocationUpdateListener = listener;
		if (locationManager == null)
			locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);

		// exceptions will be thrown if provider is not permitted.
		try {
			gps_enabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			network_enabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// don't start listeners if no provider is enabled
		if (checkAvailability) {			
			if (!gps_enabled && !network_enabled) {				
				((Activity) mContext).runOnUiThread(new Runnable() {
					@Override
					public void run() {						
						if(Utils.isWaitingDlgShowed())
						{
							Utils.hideWaitingDlg();
							waitingDialogFlag = true;
						}
						
						AlertDialog alertDialog = new AlertDialog.Builder(
								mContext).create();
						alertDialog.setTitle("Location Service");
						alertDialog.setMessage("May I use your location?");
						alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
								"Yes", dialogClickYes);
						alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
								"No", dialogClickNo);
							
						alertDialog.show();
					}

				});
			}
		}

		if (minTime == 0) {
			mCurrLoc = getLatestLocation(true);
			if (mCurrLoc != null)			
				return true;			
		}
		
		Log.d("gpshandler", "*** gps_enabled = " + gps_enabled);
		Log.d("gpshandler", "*** network_enabled = " + network_enabled);
		if (gps_enabled) {
			if (!checkAvailability && (context instanceof Activity)) {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() { 
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER, minTime,
								minDistance, mGPSLocationListener);						
					}
				});
			} else {								
				locationManager.requestLocationUpdates(
						LocationManager.GPS_PROVIDER, minTime, minDistance,
						mGPSLocationListener);				
			}
		}

		if (network_enabled) {
			if (!checkAvailability && (context instanceof Activity)) {
				((Activity) context).runOnUiThread(new Runnable() {
					@Override
					public void run() {			
						locationManager.requestLocationUpdates(
								LocationManager.NETWORK_PROVIDER, minTime,
								minDistance, mNetworkLocationListener);						
					}
				});
			} else {	
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, minTime, minDistance,
						mNetworkLocationListener);				
			}
		}

		return true;
	}

	public boolean checkEnableGPS() {
		try {
			if (locationManager == null) {
				locationManager = (LocationManager) mContext.getSystemService(Service.LOCATION_SERVICE);
			}

			// getting GPS status
			boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

			// getting network status
			/* boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER); */

			if (!isGPSEnabled /* && !isNetworkEnabled */) {
				// no network provider is enabled
				return false;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	public void stop() {
		if (locationManager != null) {
			locationManager.removeUpdates(mNetworkLocationListener);
			locationManager.removeUpdates(mGPSLocationListener);
		}
	}

	public Location getLatestLocation() {		
		return getLatestLocation(false);
	}
	
	private Location getLatestLocation(boolean enableCallback) {		
		Location net_loc = null, gps_loc = null;

		if (gps_enabled)
			gps_loc = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (network_enabled)
			net_loc = locationManager
					.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		// if there are both values use the latest one
		if (gps_loc != null && net_loc != null) {
			if (gps_loc.getTime() > net_loc.getTime()) {
				mCurrLoc = gps_loc;
				if (enableCallback && mLocationUpdateListener != null)
					mCurrLoc = mLocationUpdateListener.gotLocation(gps_loc);
				return gps_loc;
			} else {
				mCurrLoc = net_loc;
				if (enableCallback && mLocationUpdateListener != null)
					mCurrLoc = mLocationUpdateListener.gotLocation(net_loc);
				return net_loc;
			}
//			,"GPSHandler getLatestLocation, both values, returning");
//			return mCurrLoc;
		}

		if (gps_loc != null) {
			mCurrLoc = gps_loc;
			if (enableCallback && mLocationUpdateListener != null)
				mCurrLoc = mLocationUpdateListener.gotLocation(gps_loc);			
			return gps_loc;
//			return mCurrLoc;
		}
		if (net_loc != null) {
			mCurrLoc = net_loc;
			if (enableCallback && mLocationUpdateListener != null)
				mCurrLoc = mLocationUpdateListener.gotLocation(net_loc);			
			return net_loc;
//			return mCurrLoc;
		}
		
		/*
		if (enableCallback && mLocationUpdateListener != null)
			mCurrLoc = mLocationUpdateListener.gotLocation(null);
		*/
		
		return mCurrLoc;
	}

	private LocationListener mGPSLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {			
			if (location != null)
			{
				Log.d("gpshandler", "Location changed = " + location);
			}
			
			if (mLocationUpdateListener.gotLocation(location) == null) {
				stop();
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {			
		}
	};

	private LocationListener mNetworkLocationListener = new LocationListener() {
		public void onLocationChanged(Location location) {			
			if (location != null)
			{
				Log.d("gpshandler", "Location changed = " + location);
			}
			
			if (mLocationUpdateListener.gotLocation(location) == null) {
				stop();
			}
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public static abstract class OnLocationUpdateListener {
		public abstract Location gotLocation(Location location);
		public abstract void onCancel();
	}
}
