package com.gpit.android.util;

import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.telephony.TelephonyManager;
import android.util.Log;

public class Network{
	private static final String TAG = Network.class.getCanonicalName();
	
	private Context mContext;
	private WifiManager mWifiManager;
	private WifiLock mWifiLock;
	boolean museWifi = true;
	boolean muse3G = true;

	private static final String[] VPN_INTERFACENAME = {"tun0","ppp0"};
	
	private static int ConnectivityManager_TYPE_WIMAX = 6;
	
	public static enum DNS_TYPE {
		DNS_1, DNS_2, 
	}
	
	public Network(Context context) {
		mContext = context;
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
	}
	
	public String getLocalIP(boolean ipv6) {
		
		final HashMap<String, String> addressMap = new HashMap<String, String>();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();			
					if (!inetAddress.isLoopbackAddress()) {
						if (((inetAddress instanceof Inet4Address) && !ipv6) || ((inetAddress instanceof Inet6Address) && ipv6)) {
							addressMap.put(intf.getName(), inetAddress.getHostAddress().toString());
						}
					}
				}
			}
			if(addressMap.size() > 0){
				String addres;
				for (int i = 0; i < VPN_INTERFACENAME.length; i++) {				
					final String vpnAddr = addressMap.get(VPN_INTERFACENAME[i]);
					if(vpnAddr!=null&&vpnAddr.length()>0){
						return vpnAddr;
					}					
				}
				addres =getWifiIpAddress();
				if(addres == null)
					addres = addressMap.values().iterator().next();
				return addres;
			}
		} catch (SocketException ex) {
			Log.e(TAG, ex.toString());
		}		

		return null;
	}
	
	String getWifiIpAddress(){
		if(!mWifiManager.isWifiEnabled())  {  			 
			return null;  			  
			}  
			  
			WifiInfo wifiinfo= mWifiManager.getConnectionInfo();  			  
			String ip=intToIp(wifiinfo.getIpAddress()); 
			return ip;
	}
	
	private String intToIp(int i)  {
		return (i & 0xFF)+ "." + ((i >> 8 ) & 0xFF)+ "." + ((i >> 16 ) & 0xFF) +"."+((i >> 24 ) & 0xFF);
	}
	
	public boolean acquire() {		
		
		if(mWifiManager == null){
			return false;
		}
		
		if (mWifiLock!=null&&mWifiLock.isHeld()) {
			return true;
		}

		boolean connected = false;
		NetworkInfo networkInfo = ((ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (networkInfo == null) {
			return false;
		}

		int netType = networkInfo.getType();
		int netSubType = networkInfo.getSubtype();

		if (museWifi && (netType == ConnectivityManager.TYPE_WIFI)) {
			if (mWifiManager != null && mWifiManager.isWifiEnabled()) {
				mWifiLock = mWifiManager.createWifiLock(
						WifiManager.WIFI_MODE_FULL, TAG);
				final WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
				if (wifiInfo != null && mWifiLock != null) {
					final DetailedState detailedState = WifiInfo
							.getDetailedStateOf(wifiInfo.getSupplicantState());
					if (detailedState == DetailedState.CONNECTED
							|| detailedState == DetailedState.CONNECTING
							|| detailedState == DetailedState.OBTAINING_IPADDR) {
						mWifiLock.acquire();
						connected = true;
					}
				}
			} else {
				Log.d(TAG, "WiFi not enabled");
			}
		} else if (muse3G&& (netType == ConnectivityManager.TYPE_MOBILE || netType == ConnectivityManager_TYPE_WIMAX)) {
			if ((netSubType >= TelephonyManager.NETWORK_TYPE_UMTS)
					|| // HACK
					(netSubType == TelephonyManager.NETWORK_TYPE_GPRS)
					|| (netSubType == TelephonyManager.NETWORK_TYPE_EDGE)) {

				connected = true;
			}
		}

		if (!connected) {
			Log.d(TAG, "No active network");
			return false;
		}

		return true;
	}

	public String getSysDnsServer(DNS_TYPE type) {
		String dns = null;
		switch (type) {
			case DNS_1: default: dns = "dns1"; break;
			case DNS_2: dns = "dns2"; break;
		}
		
		if (mWifiManager != null&&mWifiManager.isWifiEnabled()) {

			String[] dhcpInfos = mWifiManager.getDhcpInfo().toString().split(" ");
			int i = 0;

			while (i++ < dhcpInfos.length) {
				if (dhcpInfos[i - 1].equals(dns)) {
					if(dhcpInfos[i]!=null&&!dhcpInfos[i].equals("0.0.0.0"))
						return dhcpInfos[i];
					break;
				}
			}
		}
		return getMobileNetworkDns(type);
	}
	
	
	String getMobileNetworkDns(DNS_TYPE dnsType){
		String dns;
		Class<?> SystemProperties = null;
		Method method = null;
		
		switch (dnsType) {
		case DNS_1: default: dns = "net.rmnet0.dns1"; break;
		case DNS_2: dns = "net.rmnet0.dns2"; break;
		}
		
		try {
			SystemProperties = Class.forName("android.os.SystemProperties");
		} catch (ClassNotFoundException e) {		
			e.printStackTrace();
			return null;
		}
		
		try {
			method = SystemProperties.getMethod("get", new Class[] { String.class });
		} catch (SecurityException e) {			
			e.printStackTrace();
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			return null;
		}
		
		String value = null;
		try {
			value = (String) method.invoke(null, dns);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	    if (value != null && !"".equals(value) && !value.equals("0.0.0.0"))
	    	return value;
		
		return null;
	}
	public boolean release() {
		
		if (mWifiLock != null) {
			if(mWifiLock.isHeld()){
				mWifiLock.release();
			}	
			mWifiLock = null;
		}
		return true;
	}
}
