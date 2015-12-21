/**
 * @author HoeyLi
 * @email hoeyli@126.com
 * 
 * Application Class
 */

package im.chatify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDexApplication;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import junit.framework.Assert;

import org.jivesoftware.smack.provider.ProviderFileLoader;
import org.jivesoftware.smack.provider.ProviderManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import im.chatify.common.CIFileLog;
import im.chatify.common.CILocales;
import im.chatify.common.CINativeLoader;
import im.chatify.common.ui.CIPackageRunningMode;
import im.chatify.model.CIConst;
import im.chatify.xabber.android.data.BaseManagerInterface;
import im.chatify.xabber.android.data.BaseUIListener;
import im.chatify.xabber.android.data.LogManager;
import im.chatify.xabber.android.data.NetworkException;
import im.chatify.xabber.android.data.OnClearListener;
import im.chatify.xabber.android.data.OnCloseListener;
import im.chatify.xabber.android.data.OnErrorListener;
import im.chatify.xabber.android.data.OnInitializedListener;
import im.chatify.xabber.android.data.OnLoadListener;
import im.chatify.xabber.android.data.OnLowMemoryListener;
import im.chatify.xabber.android.data.OnTimerListener;
import im.chatify.xabber.android.data.OnUnloadListener;
import im.chatify.xabber.android.data.OnWipeListener;
import im.chatify.xabber.android.service.XabberService;

public class CIApp extends MultiDexApplication {
	public final static String TAG = "PFApp";
	
	public static CIApp APP;
	
	private final static int ONGOING_SERVICE_WAKEUP_CYCLE_TIME = (1 * 60 * 1000);
	private final static int CHECK_VERSION_CYCLE_TIME = (30 * 60 * 1000);
	
	private static int checkVersionTimeout = 0;

	private static String visibleActivityName;

	private final ArrayList<Object> registeredManagers;
	/**
	 * Thread to execute tasks in background..
	 */
	private final ExecutorService backgroundExecutor;
	/**
	 * Handler to execute runnable in UI thread.
	 */
	private final Handler handler;
	/**
	 * Unmodifiable collections of managers that implement some common
	 * interface.
	 */
	private Map<Class<? extends BaseManagerInterface>, Collection<? extends BaseManagerInterface>> managerInterfaces;
	private Map<Class<? extends BaseUIListener>, Collection<? extends BaseUIListener>> uiListeners;
	/**
	 * Where data load was requested.
	 */
	private boolean serviceStarted;
	/**
	 * Whether application was initialized.
	 */
	private boolean initialized;
	/**
	 * Whether user was notified about some action in contact list activity
	 * after application initialization.
	 */
	private boolean notified;
	/**
	 * Whether application is to be closed.
	 */
	private boolean closing;
	/**
	 * Whether {@link #onServiceDestroy()} has been called.
	 */
	private boolean closed;
	private final Runnable timerRunnable = new Runnable() {

		@Override
		public void run() {
			for (OnTimerListener listener : getManagers(OnTimerListener.class))
				listener.onTimer();
			if (!closing)
				startTimer();
		}

	};
	/**
	 * Future for loading process.
	 */
	private Future<Void> loadFuture;

	public static volatile Handler applicationHandler = null;

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

	public CIApp() {

		APP = this;
		serviceStarted = false;
		initialized = false;
		notified = false;
		closing = false;
		closed = false;
		uiListeners = new HashMap<>();
		managerInterfaces = new HashMap<>();
		registeredManagers = new ArrayList<>();

		handler = new Handler();
		backgroundExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable runnable) {
				Thread thread = new Thread(runnable, "Background executor service");
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.setDaemon(true);
				return thread;
			}
		});

	}
	public static CIApp getInstance() {

		if (APP == null) {
			throw new IllegalStateException();
		}

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

		for (OnLowMemoryListener listener : getManagers(OnLowMemoryListener.class)) {
			listener.onLowMemory();
		}

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

		requestToClose();

		CIFileLog.logMessage("BBApp onTerminate called");
		super.onTerminate();
	}
	
	
	public void initApp() {
		stopApp();

		applicationHandler = new Handler(getInstance().getMainLooper());
		CINativeLoader.initNativeLibs(CIApp.getInstance());

		// Init XMPP
		initXMPP();

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

	private void initXMPP() {

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

		ArrayList<String> contactManager = new ArrayList<>();
		TypedArray contactManagerClasses = getResources().obtainTypedArray(R.array.contact_managers);
		for (int index = 0; index < contactManagerClasses.length(); index++) {
			contactManager.add(contactManagerClasses.getString(index));
		}
		contactManagerClasses.recycle();

		TypedArray managerClasses = getResources().obtainTypedArray(R.array.managers);
		for (int index = 0; index < managerClasses.length(); index++) {
			if (isContactsSupported() || !contactManager.contains(managerClasses.getString(index))) {
				try {
					Class.forName(managerClasses.getString(index));
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		}
		managerClasses.recycle();

		TypedArray tableClasses = getResources().obtainTypedArray(R.array.tables);
		for (int index = 0; index < tableClasses.length(); index++) {
			try {
				Class.forName(tableClasses.getString(index));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		tableClasses.recycle();

		startService(XabberService.createIntent(this));
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

	/**
	 * Whether application is initialized.
	 */
	public boolean isInitialized() {
		return initialized;
	}

	private void onLoad() {

		ProviderManager.addLoader(new ProviderFileLoader(getResources().openRawResource(R.raw.smack)));

		for (OnLoadListener listener : getManagers(OnLoadListener.class)) {
			LogManager.i(listener, "onLoad");
			listener.onLoad();
		}
	}

	private void onInitialized() {
		for (OnInitializedListener listener : getManagers(OnInitializedListener.class)) {
			LogManager.i(listener, "onInitialized");
			listener.onInitialized();
		}
		initialized = true;
		XabberService.getInstance().changeForeground();
		startTimer();
	}

	private void onClose() {
		LogManager.i(this, "onClose");
		for (Object manager : registeredManagers) {
			if (manager instanceof OnCloseListener) {
				((OnCloseListener) manager).onClose();
			}
		}
		closed = true;
	}

	private void onUnload() {
		LogManager.i(this, "onUnload");
		for (Object manager : registeredManagers) {
			if (manager instanceof OnUnloadListener) {
				((OnUnloadListener) manager).onUnload();
			}
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	public boolean doNotify() {
		if (notified) {
			return false;
		}
		notified = true;
		return true;
	}

	public void onServiceStarted() {
		if (serviceStarted) {
			return;
		}
		serviceStarted = true;
		LogManager.i(this, "onStart");
		loadFuture = backgroundExecutor.submit(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try {
					onLoad();
				} finally {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// Throw exceptions in UI thread if any.
							try {
								loadFuture.get();
							} catch (InterruptedException | ExecutionException e) {
								throw new RuntimeException(e);
							}
							onInitialized();
						}
					});
				}
				return null;
			}
		});
	}

	public void requestToClose() {
		closing = true;
		stopService(XabberService.createIntent(this));
	}

	public boolean isClosing() {
		return closing;
	}

	public boolean isContactsSupported() {
		return checkCallingOrSelfPermission("android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED;
	}

	public void onServiceDestroy() {
		if (closed) {
			return;
		}
		onClose();
		runInBackground(new Runnable() {
			@Override
			public void run() {
				onUnload();
			}
		});
	}

	private void startTimer() {
		runOnUiThreadDelay(timerRunnable, OnTimerListener.DELAY);
	}

	public void addManager(Object manager) {
		registeredManagers.add(manager);
	}

	public <T extends BaseManagerInterface> Collection<T> getManagers(Class<T> cls) {
		if (closed) {
			return Collections.emptyList();
		}
		Collection<T> collection = (Collection<T>) managerInterfaces.get(cls);
		if (collection == null) {
			collection = new ArrayList<>();
			for (Object manager : registeredManagers) {
				if (cls.isInstance(manager)) {
					collection.add((T) manager);
				}
			}
			collection = Collections.unmodifiableCollection(collection);
			managerInterfaces.put(cls, collection);
		}
		return collection;
	}

	public void requestToClear() {
		runInBackground(new Runnable() {
			@Override
			public void run() {
				clear();
			}
		});
	}

	private void clear() {
		for (Object manager : registeredManagers) {
			if (manager instanceof OnClearListener) {
				((OnClearListener) manager).onClear();
			}
		}
	}

	public void requestToWipe() {
		runInBackground(new Runnable() {
			@Override
			public void run() {
				clear();
				for (Object manager : registeredManagers)
					if (manager instanceof OnWipeListener)
						((OnWipeListener) manager).onWipe();
			}
		});
	}

	private <T extends BaseUIListener> Collection<T> getOrCreateUIListeners(Class<T> cls) {
		Collection<T> collection = (Collection<T>) uiListeners.get(cls);
		if (collection == null) {
			collection = new ArrayList<T>();
			uiListeners.put(cls, collection);
		}
		return collection;
	}

	public <T extends BaseUIListener> Collection<T> getUIListeners(Class<T> cls) {
		if (closed) {
			return Collections.emptyList();
		}
		return Collections.unmodifiableCollection(getOrCreateUIListeners(cls));
	}

	public <T extends BaseUIListener> void addUIListener(Class<T> cls, T listener) {
		getOrCreateUIListeners(cls).add(listener);
	}

	public <T extends BaseUIListener> void removeUIListener(Class<T> cls, T listener) {
		getOrCreateUIListeners(cls).remove(listener);
	}

	public void onError(final int resourceId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				for (OnErrorListener onErrorListener : getUIListeners(OnErrorListener.class)) {
					onErrorListener.onError(resourceId);
				}
			}
		});
	}

	public void onError(NetworkException networkException) {
		LogManager.exception(this, networkException);
		onError(networkException.getResourceId());
	}

	public void runInBackground(final Runnable runnable) {
		backgroundExecutor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (Exception e) {
					LogManager.exception(runnable, e);
				}
			}
		});
	}

	public void runOnUiThread(final Runnable runnable) {
		handler.post(runnable);
	}

	/**
	 * Submits request to be executed in UI thread.
	 */
	public void runOnUiThreadDelay(final Runnable runnable, long delayMillis) {
		handler.postDelayed(runnable, delayMillis);
	}
}
