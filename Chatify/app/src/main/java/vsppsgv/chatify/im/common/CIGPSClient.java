package vsppsgv.chatify.im.common;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class CIGPSClient implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private static CIGPSClient 		instance;
	private static GoogleApiClient 	mGoogleApiClient;
	private LocationRequest 		mLocationRequest;
	private Location mLocation;
	
	public static CIGPSClient getInstance(Context context) {
		if (instance == null) {
			instance = new CIGPSClient(context);
		}
		
		instance.setContext(context);
		
		return instance;
	}
	
	private CIGPSClient(Context context) {
		mGoogleApiClient = new GoogleApiClient.Builder(context)
		.addApi(LocationServices.API)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.build();
	}
	
	public void setContext(Context context) {
//		mContext = context;
	}
	
	public void connect() {
		mGoogleApiClient.connect();
	}
	
	public void disconnect() {
		mGoogleApiClient.disconnect();
	}
	
	public double getLatitude() {
		if ( mLocation == null )
			return 0;
		
		return mLocation.getLatitude();
	}
	
	public double getLongitude() {
		if ( mLocation == null )
			return 0;
		
		return mLocation.getLongitude();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		
	}

	@Override
	public void onConnected(Bundle result) {
		
		mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000); // Update location every second

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionSuspended(int i) {

	}

	@Override
	public void onLocationChanged(Location location) {

	}
}
