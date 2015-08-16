/**
 * @author HoeyLi
 * @email hoeyli@126.com
 * 
 * Application Class
 */

package vsppsgv.chatify.im;

import android.graphics.Bitmap;
import android.support.multidex.MultiDexApplication;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import junit.framework.Assert;

import java.util.Locale;
import vsppsgv.chatify.im.common.CIFileLog;
import vsppsgv.chatify.im.common.CILocales;
import vsppsgv.chatify.im.common.ui.CIPackageRunningMode;
import vsppsgv.chatify.im.model.CIConst;

public class CIApp extends MultiDexApplication {
	public final static String TAG = "PFApp";
	
	public static CIApp APP;
	
	private final static int ONGOING_SERVICE_WAKEUP_CYCLE_TIME = (1 * 60 * 1000);
	private final static int CHECK_VERSION_CYCLE_TIME = (30 * 60 * 1000);
	
	private static int checkVersionTimeout = 0;

	// UI
	private static String visibleActivityName;
	
	public static boolean predefinedDevModeOn() {
		if (CIConst.PACKAGE_RUNNING_MODE == CIPackageRunningMode.PACKAGE_MODE_DEV_DEVELOPMENT ||
				CIConst.PACKAGE_RUNNING_MODE == CIPackageRunningMode.PACKAGE_MODE_DEV_PRODUCT ||
				CIConst.PACKAGE_RUNNING_MODE == CIPackageRunningMode.PACKAGE_MODE_DEV_TEST) {
			return true;
		}
		
		return false;
	}
	
	public static boolean predefinedProductModeOn() {
		if (CIConst.PACKAGE_RUNNING_MODE == CIPackageRunningMode.PACKAGE_MODE_PRODUCT) {
			return true;
		}
		
		return false;
	}
	
	public static boolean devModeOn() {
		if (CIConst.PACKAGE_RUNNING_MODE == CIPackageRunningMode.PACKAGE_MODE_DEV_DEVELOPMENT ||
				CIConst.PACKAGE_RUNNING_MODE == CIPackageRunningMode.PACKAGE_MODE_DEV_TEST) {
			return true;
		}
		
		return false;
	}
	
	public static boolean testModeOn() {
		if (CIConst.PACKAGE_RUNNING_MODE == CIPackageRunningMode.PACKAGE_MODE_DEV_TEST) {
			return true;
		}
		
		return false;
	}
	
	public static void setPackageRunningMode(CIPackageRunningMode runningMode) {

		CIConst.PACKAGE_RUNNING_MODE = runningMode;
		if (devModeOn()) {
			// Mint
//			PFConst.MINT_API_KEY = PFConst.DEV_MINT_API_KEY;
			
		} else {

			Assert.assertTrue(CIConst.PACKAGE_RUNNING_MODE == CIPackageRunningMode.PACKAGE_MODE_DEV_PRODUCT ||
					CIConst.PACKAGE_RUNNING_MODE == CIPackageRunningMode.PACKAGE_MODE_PRODUCT);
			Assert.assertFalse(predefinedProductModeOn() && CIConst.LOGIN_WITHOUT_CORRECT_PASSWORD);
			
			// DB
//			BBConst.APP_LOCAL_DATABASE_NAME = BBConst.PROD_APP_LOCAL_DATABASE_NAME;
						
			// Mint
//			BBConst.MINT_API_KEY = BBConst.PROD_MINT_API_KEY;
			
		}
	}
	
	public static SharedPreferences getPreference(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return prefs;
	}
	
	public static SharedPreferences.Editor getPreferenceEditor(Context context) {
		return getPreference(context).edit();
	}
	
	public static CIApp getInstance() {
		return APP;
	}
	
	
	@Override
	public void onCreate() {
//		PFFileLog.logMessage("BBApp onCreate called");
		preCreate();
		super.onCreate();
		
		APP = this;
		
		setPackageRunningMode(CIConst.PACKAGE_RUNNING_MODE);
        initApp();
	}

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
	
	private void preCreate() {
		// java.lang.ClassNotFoundException: android.os.AsyncTask caused by AdMob / Google Play Services
		// https://code.google.com/p/android/issues/detail?id=81083
		try {
			Class.forName("android.os.AsyncTask");
		} catch (Throwable ignore) {
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		CIFileLog.logMessage("BBApp onConfigurationChanged called");
		
		super.onConfigurationChanged(newConfig);
		
	}
	
	@Override
	public void onLowMemory() {
		CIFileLog.logMessage("BBApp onLowMemory called");
		
		super.onLowMemory();
	}
	
	@Override
	public void onTrimMemory(int level) {
		CIFileLog.logMessage(String.format(Locale.getDefault(), "BBApp onTrimMemory called : level = %d", level));
		
		super.onTrimMemory(level);
	}
	
	@Override
	public void onTerminate () {
		CIFileLog.logMessage("BBApp onTerminate called");
		super.onTerminate();
	}
	
	
	public void initApp() {
		stopApp();

		// Init ImageLoader
		initImageLoader();
		
		// Init local database
        initDB();

        // Init setting
        CIAppSetting.getInstance(getApplicationContext());

        // Init utils
        CILocales.getInstance(getApplicationContext());
        
        // Set checking version to make sure to start the checking version immediately
        /// We ignored 1st checking, because it will be done at the splash screen.
        // checkVersionTimeout = CHECK_VERSION_CYCLE_TIME;
        
        startOngoingServices(getApplicationContext());
        registerWakeUpAlarm(getApplicationContext());
	}

	private void initImageLoader() {
				
		// Config image downloader
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
        .cacheOnDisk(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.defaultDisplayImageOptions(defaultOptions)
				.threadPoolSize(3) // equal to default value
				.threadPriority(Thread.NORM_PRIORITY - 1) // equal to default value
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // equal to default value
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // equal to default value
				.build();
	}
	
	private void initDB() {

	}

	public void stopApp() {
		// Unregister service waker
		unregisterWakeUpAlarm(getApplicationContext());
		
		// Stop services
		stopOngoingServices(getApplicationContext());
		
		CIFileLog.logMessage("*************************** Application De-Initialized! ***************************");
	}
	
	/****************************** Credential ***************************************/
	public static String getCurrentUserId(Context context) {

        /*
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (prefs.contains(BBUserItem.PREFS_KEY_CURRENT_USER_ID)) {
			return prefs.getString(BBUserItem.PREFS_KEY_CURRENT_USER_ID, null);
		}
		*/

		return null;
	}
		
	/****************************** Call back ***************************************/
	public void onUpgrade() {
		stopOngoingServices(getApplicationContext());
		startOngoingServices(getApplicationContext());
	}
	
	public void onLoggedInAccount() {

	}

	public void onLoggedOutAccount() {

		// Remove notifications
//		PFNotificationManager.getInstance(this).removeAllNotifications();
		
//		PFDatabase.closeDatabase();
	}
	
	public void onLanguageChanged(int langIdx) {
		Intent intent = new Intent(CIConst.BROADCAST_LOCALE_CHANGED);
		sendBroadcast(intent);
	}
	
	/****************************** Services ******************************/
	public static void startOngoingServices(Context context) {

	}
	
	public static void stopOngoingServices(Context context) {

	}
	
	// Alarm to ensure to run ongoing services
	public static void registerWakeUpAlarm(Context context) {

//		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//		Intent intent = new Intent(context, BBWakeupReceiver.class);
//		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, /*PendingIntent.FLAG_CANCEL_CURRENT*/0);
//
//		// wake up every 5 minutes to ensure service stays alive
//		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), ONGOING_SERVICE_WAKEUP_CYCLE_TIME, pendingIntent);
	}
	
	public static void unregisterWakeUpAlarm(Context context) {
//		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//		Intent intent = new Intent(context, BBWakeupReceiver.class);
//		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, /*PendingIntent.FLAG_CANCEL_CURRENT*/0);
//		am.cancel(pendingIntent);
	}

	/****************************** UI ******************************/
	public static String getCurrentVisibleActivityName() {
		return visibleActivityName;
	}
	
	public static void setCurrentVisibleActivityName(String name) {
		if (name != null) {
//			RemoteLogger.d(TAG, "Current page (" + name + ")");
		} else if (visibleActivityName != null){
//			RemoteLogger.d(TAG, "Hidden page (" + visibleActivityName + ")");
		}
		
		visibleActivityName = name;
	}
	
	public static boolean isScreenVisible(String activityName) {
		if (visibleActivityName == null) {
			return false;
		}
		
		if (visibleActivityName.equals(activityName)) {
			return true;
		}
		
		return false;
	}
}
