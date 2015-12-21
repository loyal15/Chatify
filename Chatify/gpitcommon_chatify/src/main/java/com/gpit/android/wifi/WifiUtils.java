package com.gpit.android.wifi;

import java.util.List;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiUtils {
	public static String getConnectedSSID(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String ssid = wifiInfo.getSSID();
		
		return ssid;
	}
	
	public static String getMacAddress(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		String macID = wifiInfo.getMacAddress();
		
		return macID;
	}
	
	public static boolean connectToSpecifiedSSID(Context context, String ssid, String pass) {
		WifiConfiguration conf = new WifiConfiguration();
		conf.SSID = "\"" + ssid + "\"";   // Please note the quotes. String should contain ssid in quotes
		
		if (pass != null) {
			conf.wepKeys[0] = "\"" + pass + "\""; 
			conf.wepTxKeyIndex = 0;
			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			conf.preSharedKey = "\""+ pass +"\"";
			conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
		}
		
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
		wifiManager.addNetwork(conf);
		
		List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
		for( WifiConfiguration wifiConf : list ) {
		    if(wifiConf.SSID != null && wifiConf.SSID.equals("\"" + ssid + "\"")) {
		    	wifiManager.disconnect();
		    	wifiManager.enableNetwork(wifiConf.networkId, true);
		    	wifiManager.reconnect();                

		        return true;
		    }           
		 }
		
		return false;
	}
}
