package com.gpit.android.util;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.WeakHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.NodeList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.speech.RecognizerIntent;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.gpit.android.library.R;
import com.gpit.android.ui.common.Constant;

public class Utils {
	public static final int SECOND_MILISECONDS = 1000;
	public static final int MIN_MILISECONDS = 60 * SECOND_MILISECONDS;
	public static final int HOUR_MILISECONDS = 60 * MIN_MILISECONDS;
	public static final int DAY_MILISECONDS = 24 * HOUR_MILISECONDS;
	public static final int WEEK_MILISECONDS = 7 * DAY_MILISECONDS;
	
	// App
	public final static int DEVICE_TYPE_BETA = 0;
	public final static int DEVICE_TYPE_10 = 1;
	public final static int DEVICE_TYPE_11 = 2;
	public final static int DEVICE_TYPE_CUPCAKE = 3;
	public final static int DEVICE_TYPE_DONUT = 4;
	public final static int DEVICE_TYPE_ECLAIR = 5;
	public final static int DEVICE_TYPE_FROYO = 6;
	public final static int DEVICE_TYPE_GINGERBREAD = 7;
	public final static int DEVICE_TYPE_HONEYCOMB = 8;
	public final static int DEVICE_TYPE_ICS = 9;
	
	public final static String[] deviceTypes = new String[] {
		"Beta", 
		"1.0",
		"1.1",
		"Cupcake",
		"Donut",
		"Eclair",
		"Froyo",
		"Gingerbread",
		"Honeycomb",
		"Ice Cream Sandwich",
	};
	
	// File IO
	private final static int MAX_CACHE_FILE_PATH_LEN = 200;
	public final static String SPECIAL_CHARS_IN_FILEPATH = "\\/:*?\"<>|";
	
	private final static long GIGA_BYTE = 1024 * 1024 * 1024;
    private final static long MEGA_BYTE = 1024 * 1024;
    private final static long KILO_BYTE = 1024;
    
    // Fonts
    private static WeakHashMap<String, Typeface> fontMap = new WeakHashMap<String, Typeface>();
    
	// Special - Application Detail
	private static final String SCHEME = "package";
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	private static final String APP_PKG_NAME_22 = "pkg";
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	private static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";
	
	public static ProgressDialog mProgressDlg;
	
	// Version
	private final static String APP_VERSION_TITLE = "%s (v%s - No.%d)";
	private final static String APP_VERSION = "v%s - No.%d";
		
	/*************************************************************************************
	 * APP & SYSTEM FUNCTIONS
	 ************************************************************************************/
	public static String getGmailAcount(Context context) {
		AccountManager manager = AccountManager.get(context);
		Account[] accounts = manager.getAccountsByType("com.google");
		String acount = "";
		
		if (accounts != null && accounts.length > 0)
			acount = accounts[0].name;
		
		return acount;
	}
	
	public static String[] getOwners(Context context) {
		final AccountManager manager = AccountManager.get(context);
	    final Account[] accounts = manager.getAccountsByType("com.google");
	    final int size = accounts.length;
	    String[] names = new String[size];
	    for (int i = 0; i < size; i++) {
	      names[i] = accounts[i].name;
	    }
	    
	    return names;
	}
	
	public static UserName spiltName(Context context, String fullName) {
		UserName userName = new UserName();
		int start = fullName.indexOf(' ');
	    int end = fullName.lastIndexOf(' ');

	    if (start >= 0) {
	    	userName.firstName = fullName.substring(0, start);
	        if (end > start)
	        	userName.middleName = fullName.substring(start + 1, end);
	        userName.lastName = fullName.substring(end + 1, fullName.length());
	    }
	    
	    return userName;
	}
	
	public static void showAppDetailView(Context context) {
		Intent intent;
		
		if (android.os.Build.VERSION.SDK_INT >= 9) {
	        /* on 2.3 and newer, use APPLICATION_DETAILS_SETTINGS with proper URI */
	        Uri packageURI = Uri.parse("package:" + context.getPackageName());
	        intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageURI);
	    }  else  {
	        /* on older Androids, use trick to show app details */
	    	intent = new Intent(Intent.ACTION_VIEW);
	    	intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName()); 
	        intent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
	    }
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
	}
	
	public static void showSetting(Context context) {
		Intent intent;
		
        intent = new Intent(Settings.ACTION_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		context.startActivity(intent);
	}
	
	public static void switchToHome(Context context) {
		Intent intent = new Intent();
    	intent.setAction(Intent.ACTION_MAIN)
    		.addCategory(Intent.CATEGORY_HOME)
    		.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    	context.startActivity(intent);
	}
	
	/**
	 * Check to see if a recognition activity is present
	 * @param context
	 * @return
	 */
	public static boolean isExistVoiceRecognizeActivity(Context context) {
		boolean isExist;
		
		PackageManager pm = context.getPackageManager();
		List<ResolveInfo> activities = pm.queryIntentActivities(
		  new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
		isExist = (activities.size() != 0);
		
		return isExist;
	}
	
	public static int getApiLevel() {
		int apiLevel = Build.VERSION.SDK_INT;
		
		return apiLevel;
	}
	
	public static String getDeviceID(Context context) {
		String androidID = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		
		return androidID;
	}
	
	public static String getDeviceName(Context context) {
		return android.os.Build.DEVICE;
	}
	
	public static int getDeviceTypeID() {
		int apiLevel = getApiLevel();
		int deviceTypeID = DEVICE_TYPE_BETA;
		
		if (apiLevel <= 1) {
			deviceTypeID = DEVICE_TYPE_10;
		} else if (apiLevel <= 2) {
			deviceTypeID = DEVICE_TYPE_11;
		} else if (apiLevel <= 3) {
			deviceTypeID = DEVICE_TYPE_CUPCAKE;
		} else if (apiLevel <= 4) {
			deviceTypeID = DEVICE_TYPE_DONUT;
		} else if (apiLevel <= 6) {
			deviceTypeID = DEVICE_TYPE_ECLAIR;
		} else if (apiLevel <= 8) {
			deviceTypeID = DEVICE_TYPE_FROYO;
		} else if (apiLevel <= 10) {
			deviceTypeID = DEVICE_TYPE_GINGERBREAD;
		} else if (apiLevel <= 13) {
			deviceTypeID = DEVICE_TYPE_HONEYCOMB;
		} else if (apiLevel <= 15) {
			deviceTypeID = DEVICE_TYPE_ICS;
		}
		
		return deviceTypeID;
	}
	
	public static String getDeviceType() {
		int deviceTypeID = getDeviceTypeID();
		
		return deviceTypes[deviceTypeID];
	}
	
	public static String getOSVersion() {
		String osVersion = System.getProperty("os.version") + "(" + android.os.Build.VERSION.INCREMENTAL + ")";
		return osVersion;
	}
	
	@Deprecated // 2012/05/17
	public static int getAppVersion(Context context) {
		return getAppVersionCode(context);
    }

	public static int getAppVersionCode(Context context) {
		try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }
	
	public static String getAppVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return pInfo.versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }
	
	public static String getAppstoreLink(Context context) {
		String appstoreLink = "https://play.google.com/store/apps/details?id=" + context.getPackageName();
		
		return appstoreLink;
	}
	
	public static String getIMEID(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String getSimSerialNumber = tm.getSimSerialNumber();
		
		return getSimSerialNumber;
	}
	
	public static String getPhoneNumber(Context context) {
		TelephonyManager mTelephonyMgr;
		String phoneNumber;
		
        mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE); 
        phoneNumber = mTelephonyMgr.getLine1Number();
        
        return phoneNumber;
	}
	
	public static String getMarketURL(Context context) {
		String marketURL = String.format("https://market.android.com/details?id=%s", context.getPackageName());
		
		return marketURL;
	}
	
	public static String getVersionTitle(Activity activity) {
		// Show application version name & code
    	String versionName = getAppVersionName(activity);
    	int versionCode = getAppVersionCode(activity);
    	String versionTitle = String.format(APP_VERSION_TITLE, getApplicationName(activity), versionName, versionCode);
    	
    	return versionTitle;
	}
	
	public static String getVersion(Context context) {
		// Show application version name & code
    	String versionName = getAppVersionName(context);
    	int versionCode = getAppVersionCode(context);
    	String versionTitle = String.format(APP_VERSION, versionName, versionCode);
    	
    	return versionTitle;
	}
	
	public static String getApplicationName(Context context) {
	    int stringId = context.getApplicationInfo().labelRes;
	    return context.getString(stringId);
	}
	
	public static void showVersionTitle(Activity activity) {
    	activity.setTitle(getVersionTitle(activity));
	}
	
	/*************************************************************************************
	 * CHECK AVAILBILITY
	 ************************************************************************************/
	/** Check if this device has a camera */
    private static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
    
    public static Intent getBestIntentInfo(Context context, Intent intent, String compare, String recommandedAppPackage) {
    	return getBestIntentInfo(context, intent, compare, recommandedAppPackage, null);
    }
    
    public static Intent getBestIntentInfo(Context context, Intent intent, String compare, String recommandedAppPackage, String exceptClass) {
		Assert.assertTrue(intent != null);

		if (compare == null)
			return intent;
		
		ResolveInfo best = null;
		final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);

        for (final ResolveInfo info : matches) {
        	if (info.activityInfo.name.toLowerCase().contains(compare)) {
        		if (exceptClass != null && exceptClass.equals(info.activityInfo.name))
        			continue;
        		
        		best = info;
        		if (info.activityInfo.applicationInfo.packageName.equals(recommandedAppPackage))
        			break;
        	}
        }
        
        if (best != null) {
        	intent.setPackage(best.activityInfo.packageName);
        	intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        } else {
        	String marketURL = String.format("https://market.android.com/details?id=%s", recommandedAppPackage);
        	intent = new Intent(Intent.ACTION_VIEW, Uri.parse(marketURL));
        	Toast.makeText(context, "Please download sharing app from \"" + recommandedAppPackage + "\"", Toast.LENGTH_LONG).show();
        }
        
        return intent;
	}
    
	/*************************************************************************************
	 * STRING FUNCTIONS
	 ************************************************************************************/
	public static int stringToInt(String value){
	    try
	    {
	      // the String to int conversion happens here
	      int i = Integer.parseInt(value.trim());
	      return i;
	    }
	    catch (NumberFormatException nfe)
	    {
	      System.out.println("NumberFormatException: " + nfe.getMessage());
	    }
		return -1;
	}
	
	// Get string from object
	public static String getClassString(Object object, String prefix) {
		StringBuffer result = new StringBuffer();
		@SuppressWarnings("rawtypes")
		Class cls;
		
		if (object == null)
			return "";
		
		cls = object.getClass();
		result.append("\n");
		result.append(prefix);
		result.append(cls.getName());
		result.append(" = {");
		
		try {
			// Retrieve all variables.
			Field[] fields = cls.getDeclaredFields();
			Field field;
			Object obj;
			String name, value;
			
			for (int i = 0 ; i < fields.length ; i++) {
				field = fields[i];
				
				name = field.getName();
				value = "";
				try {
					field.setAccessible(true);
					if ((field.getModifiers() & Modifier.FINAL) > 0)
						continue;
					obj = field.get(object);
					if ((obj.getClass() != cls))
						value = obj.toString();
					else
						continue;
				} catch (Exception e) {
				}
				
				result.append("\n" + prefix + "\t" + prefix);
				result.append(name);
				result.append(" = ");
				result.append(value);
			}
		} catch (Exception e) {
			result.append("exception = " + e.getMessage());
		}
		result.append("\n" + prefix + "}");
		
		return result.toString();
	}
	
	public static void append(StringBuffer buffer, String prefix, String value) {
		buffer.append(prefix);
		buffer.append(value);
	}
	
	public static void appendLine(StringBuffer buffer, String prefix, String value) {
		buffer.append("\n");
		buffer.append(prefix);
		buffer.append(value);
	}
	
	// Get directory path from file path
	public static String getDirPath(String filePath) {
		String dirPath;
		int pos;
		
		if (filePath == null)
			return null;
		
		dirPath = filePath;
		pos = filePath.lastIndexOf(File.separatorChar);
		if (pos != -1) {
			dirPath = filePath.substring(0, pos);
		}
		
		return dirPath;
	}
	
	// Get directory path from file path
	public static String getDirName(String dirPath) {
		String dirName;
		int pos;
		
		if (dirPath == null)
			return null;
		
		dirName = dirPath;
		pos = dirPath.lastIndexOf(File.separatorChar);
		if (pos != -1) {
			dirName = dirPath.substring(pos + 1);
		}
		
		return dirName;
	}
	
	public static long getDirSize(String path) {
    	return getDirSize(new File(path), 0);
	}
    
    public static long getDirSize(File path, long size) {
		File[] list = path.listFiles();
		int len;

		if(list != null) {
			len = list.length;

			for (int i = 0; i < len; i++) {
				try {
					if(list[i].isFile() && list[i].canRead()) {
						size += list[i].length();

					} else if(list[i].isDirectory() && list[i].canRead() && !isSymlink(list[i])) { 
						size = getDirSize(list[i], size);
					}
				} catch(IOException e) {
					Log.e("IOException", e.getMessage());
				}
			}
		}
		
		return size;
	}
    
    public static String getFileSizeString(long size) {
    	String sizeStr;
        if (size > GIGA_BYTE) {
        	sizeStr = String.format("%.2fGB", ((float)size / GIGA_BYTE));
        } else if (size > MEGA_BYTE) {
        	sizeStr = String.format("%.2fMB", ((float)size / MEGA_BYTE));
        } else if (size > KILO_BYTE) {
        	sizeStr = String.format("%.2fKB", ((float)size / KILO_BYTE));
        } else {
        	sizeStr = String.format("%dByte", size);
        }
        
        return sizeStr;
    }
    
    private static boolean isSymlink(File file) throws IOException {
		File fileInCanonicalDir = null;
		if (file.getParent() == null) {
			fileInCanonicalDir = file;
		} else {
			File canonicalDir = file.getParentFile().getCanonicalFile();
			fileInCanonicalDir = new File(canonicalDir, file.getName());
		}
		return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
	}
	public static Uri getResourcePath(Context context, String resType, String name) {
		String path = String.format("android.resource://%s/%s/%s", context.getPackageName(), resType, name);
		Uri resPathUri = Uri.parse(path);
		
		return resPathUri;
	}
	
	public static Uri getRawResourcePath(Context context, Class rawCls, String path) {
		int resID = (Integer)PrivateAccessor.getPrivateField(rawCls, rawCls, path);
        Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" 
        		+ resID);
		
		return uri;
	}
	
	public static boolean isNullOrEmpty(String string) {
		if (string == null || string.isEmpty())
			return true;
		
		return false;
	}
	
	public static boolean equalsStringExceptNull(String string1, String string2) {
		if (isNullOrEmpty(string1) || isNullOrEmpty(string2))
			return false;
		
		return string1.equals(string2);
	}
	
	public static boolean equalsStringIncludeNull(String string1, String string2) {
		if (string1 == string2) {
			return true;
		}
		
		if (string1 == null || string2 == null) {
			return false;
		}
		
		return string1.equals(string2);
	}
	
	public static boolean containInSensitiveString(String string1, String string2) {
		if (isNullOrEmpty(string1) || isNullOrEmpty(string2))
			return false;

		String lcString1 = string1.toLowerCase(Locale.getDefault());
		String lcString2 = string2.toLowerCase(Locale.getDefault());
		
		return lcString1.contains(lcString2);
	}
	
	/*************************************************************************************
	 * TIME & DATE FUNCTIONS
	 ************************************************************************************/
	public static long getTimeSecs() {
		return Utils.getTimeMilis() / 1000;
	}
	
	public static long getTimeMilis() {
		return System.currentTimeMillis();
	}

	// Ex: tiemzone: "UTC+8"
	public static long getTimeMilis(String timezone) {
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(timezone));
		// cal.setTimeInMillis(getTimeMilis());
		long milis = cal.getTimeInMillis();
		
		return milis;
	}
	
	
	public static long getDateTimeMilis() {
		Date nowDate = new Date();
		return getDateTimeMilis(nowDate.getTime());
	}
	
	public static long getDateTimeMilis(long miliseconds) {
		Date nowDate = new Date();
		nowDate.setTime(miliseconds);
		nowDate.setHours(0);
		nowDate.setMinutes(0);
		nowDate.setSeconds(0);
		
		return nowDate.getTime() / Constant.SECOND_MILISECONDS * Constant.SECOND_MILISECONDS;
	}
	
	public static long getWeekTimeMilis() {
		Date nowDate = new Date();
		return getWeekTimeMilis(nowDate.getTime());
	}
	
	public static long getWeekTimeMilis(long miliseconds) {
		Date date = new Date(miliseconds);
		Calendar cal = new GregorianCalendar();
		
		cal.set(date.getYear() + 1900, date.getMonth(), date.getDate(), 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTimeInMillis();
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		
		return cal.getTimeInMillis();
	}
	
	public static long getWeekTimeMilis(long miliseconds, int weekIndex) {
		Date date = new Date(miliseconds);
		Calendar cal = new GregorianCalendar();
		
		// Load new weekly data from the database
		cal.set(date.getYear() + 1900, date.getMonth(), date.getDate(), 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTimeInMillis();
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		cal.getTimeInMillis();
		cal.set(Calendar.WEEK_OF_MONTH, weekIndex);
		
		return cal.getTimeInMillis();
	}
	
	public static long getDayTimeMilis(long miliseconds, int dayIndex) {
		Date date = new Date(miliseconds);
		Calendar cal = new GregorianCalendar();
		
		// Load new weekly data from the database
		cal.set(date.getYear() + 1900, date.getMonth(), date.getDate(), 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.getTimeInMillis();
		
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek() + dayIndex);
		
		return cal.getTimeInMillis();
	}
	
	public static long getDateTimeMilis(long miliseconds, int dateIndex) {
		Date date = new Date(miliseconds);
		Calendar cal = new GregorianCalendar();
		
		cal.set(date.getYear() + 1900, date.getMonth(), dateIndex + 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTimeInMillis();
	}
	
	public static long getMonthTimeMilis(long miliseconds) {
		Date date = new Date(miliseconds);
		
		return getMonthTimeMilis(miliseconds, date.getMonth());
	}
	
	public static long getMonthTimeMilis(long miliseconds, int monthIndex) {
		Date date = new Date(miliseconds);
		Calendar cal = new GregorianCalendar();
		
		cal.set(date.getYear() + 1900, monthIndex, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTimeInMillis();
	}
	
	public static long getYearTimeMilis(long miliseconds) {
		Date date = new Date(miliseconds);
		
		return getYearTimeMilis(miliseconds, date.getYear() + 1900);
	}
	
	public static long getYearTimeMilis(long miliseconds, int year) {
		Date date = new Date(miliseconds);
		Calendar cal = new GregorianCalendar();
		
		cal.set(year, 0, 1, 0, 0, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTimeInMillis();
	}
	
	public static String getStringDate(long longDate){
		String resultDate;

		// TimeZone mytimezone = TimeZone.getDefault();
		// int offset = mytimezone.getRawOffset();
		
		//long tmpPubDate = Long.parseLong(longDate + "000") + offset;
		long tmpPubDate = Long.parseLong(longDate + "000");
		//Long tmpPubDate = Long.valueOf(longDate*1000);

		Date publicationDate = new Date(tmpPubDate);

		SimpleDateFormat formatter_date = new SimpleDateFormat ( "dd MMM yyyy",Locale.ENGLISH );
		// formatter_date.setTimeZone(TimeZone.getTimeZone("UTC"));
		resultDate = formatter_date.format(publicationDate);
		
		return resultDate;
	}
	
	public static String getEnStringDate(String dateString){
		
		SimpleDateFormat shortFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat mediumFormat = new SimpleDateFormat("dd MMM yyyy",Locale.ENGLISH);
		// shortFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		// mediumFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		String resultDate = null;
		
		try {
			resultDate = mediumFormat.format(shortFormat.parse(dateString));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return resultDate;
	}
	
	public static String getDateFromEnDate(String dateEnString){
		
		SimpleDateFormat formatter_one = new SimpleDateFormat ( "dd MMM yyyy",Locale.ENGLISH );
		SimpleDateFormat formatter_two = new SimpleDateFormat ( "yyyy-MM-dd" );
		// formatter_one.setTimeZone(TimeZone.getTimeZone("UTC"));
		// formatter_two.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		ParsePosition pos = new ParsePosition ( 0 );
		Date frmTime = formatter_one.parse ( dateEnString, pos );
		String returnString = formatter_two.format ( frmTime );
		
		return returnString;
	}
	
	public static String getUTCDateString(long mTime, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat (format);
		String dateString;
		
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		dateString = dateFormat.format(new Date(mTime));
		
		return dateString;
	}
	
	public static String getDateString(long mTime, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat (format);
		String dateString;
		
		// dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		dateString = dateFormat.format(new Date(mTime));
		
		return dateString;
	}
	
	/**
	 * Get date from string
	 * @param dateStr
	 * @return
	 */
	public static Date getDate(String dateStr) {
		return getDate(dateStr, "dd/MM/yyyy");
	}
	
	public static Date getDate(String dateStr, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = null;
		
		// dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		try {
			date = dateFormat.parse(dateStr);
			// Log.i("date", "year = " + (1900 + date.getYear()) + ", month = " + date.getMonth() + ", date = " + date.getDate());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return date;
	}
	
	/*************************************************************************************
	 * IO FUNCTIONS
	 ************************************************************************************/
	// File IO function
	public static String getFileName(String filePath) {
		return getDirName(filePath);
	}
	
	public static boolean existSDCard(Context context) {
		String state;
		
		state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
        
	}
	
	public static long getSDCardFreeSize(Context context) {
		if (!existSDCard(context))
			return 0;
		
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		double sdAvailSize = (double)stat.getAvailableBlocks() *(double)stat.getBlockSize();

		return (long)sdAvailSize;
	}
	
	// File IO function
	public static String getFilePath(Context context, String path) {
		String fullPath;
		
		if (existSDCard(context)) {
			fullPath = getSDFilePath(context, path);
		} else {
			fullPath = getInternalFilePath(context, path);
		}
        
		return fullPath;
	}
	
	public static String getSDFilePath(Context context, String path) {
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    	String packageName = context.getApplicationContext().getPackageName();
    	sdcardPath += "/." + packageName + "/" + path;
    	
    	return sdcardPath;
	}
	
	public static String getInternalFilePath(Context context, String path) {
    	String fullPath = context.getFilesDir() + "/" + path;
    	
    	return fullPath;
	}
	
	public static String getAssetFilePath(Context context, String path) {
		String assetPath = "file:///android_asset/" + path;
		
		return assetPath;
	}
	
	public static String getRawFilePath(Context context, String path) {
		String rawPath = "android.resource://" + context.getPackageName() + "/" + path;
		
		return rawPath;
	}
	
	public static boolean createDir(String path) {
		boolean ret = true;
		
		File fileIn = new File(path);
		
		if (!fileIn.exists())
			ret = fileIn.mkdirs();
		
		return ret;
	}
	
	public static FileOutputStream getFileOutputStream(Activity activity, String fullPath) {
		FileOutputStream fos = null;
		boolean ret;
		
		try {
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				File file = new File(fullPath);
				
				if (!file.exists()) {
					ret = file.createNewFile();
					if (ret == false)
						return null;
				}
				
				fos = new FileOutputStream(file);
			} else {
				fos = activity.openFileOutput(fullPath, 
						Context.MODE_WORLD_WRITEABLE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return fos;
	}

	/**
	 * Delete recursive directory
	 * @param dir
	 */
	public static void deleteRecursive(File dir)
    {
        Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) 
            {
               File temp =  new File(dir, children[i]);
               if(temp.isDirectory())
               {
                   Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
                   deleteRecursive(temp);
               }
               else
               {
                   Log.d("DeleteRecursive", "Delete File" + temp.getPath());
                   boolean b = temp.delete();
                   if(b == false)
                   {
                       Log.d("DeleteRecursive", "DELETE FAIL");
                   }
               }
            }

            dir.delete();
        }
    }
	
	public static void deleteFile(String path) {
		File file = new File(path);
		file.delete();
	}
	
	/**
	 * Ensure directory existing
	 * @param activity
	 * @param path
	 */
	public static String ensureDir(Context context, String path) {
		return ensureDir(context, path, true);
	}
	
	public static String ensureDir(Context context, String path, boolean applyContextPath) {
		boolean result;
		
		if (applyContextPath)
			path = getFilePath(context, path);
		
		File file = new File(path);
		if (!file.exists()) {
			result = file.mkdirs();
			if (result == false) {
				Log.e("Storage Error", "There is no storage to cache app data");
				return null;
			}
		}
		
		return file.getAbsolutePath();
	}
	
	/**
	 * Ensure file existing
	 * @param activity
	 * @param path
	 */
	public static String ensureFile(Context context, String path) {
		File file = new File(path);
		if (file.exists())
			return file.getAbsolutePath();
		
		String dirPath = file.getParent();
		
		if (ensureDir(context, dirPath, false) == null)
			return null;
		
		try {
			new File(path).createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		return file.getAbsolutePath();
	}
	
	public static void normalizePath(StringBuffer path) {
		Assert.assertTrue(path != null);
		
		// Replace special characters
		// "\/:*?\"<>|"
		for (int i = 0 ; i < path.length() ; i++) {
			char c = path.charAt(i);
			if (SPECIAL_CHARS_IN_FILEPATH.indexOf(c) != -1) {
				// Replace special to "_"
				path.setCharAt(i, '_');
			}
		}
		
		// Truncate tail
		if (path.length() > MAX_CACHE_FILE_PATH_LEN) {
			CharSequence footer = path.subSequence(path.length() - MAX_CACHE_FILE_PATH_LEN, path.length());
			path.setLength(0);
			path.append(footer);
		}
	}
	
	// Copy dir from source to dest
	public static boolean copyDir(String srcPath, String dstPath) {
		File srcDir = new File(srcPath);
		String orgSrcPath, orgDstPath;
		
		orgSrcPath = srcPath;
		orgDstPath = dstPath;

		// Remove last separate char
		if (orgSrcPath.charAt(orgSrcPath.length() - 1) == File.separatorChar) {
			if (orgSrcPath.length() > 1)
				orgSrcPath = orgSrcPath.substring(0, orgSrcPath.length() - 1);
			else
				orgSrcPath = "";
		}
		
		if (orgDstPath.charAt(orgDstPath.length() - 1) == File.separatorChar) {
			if (orgDstPath.length() > 1)
				orgDstPath = orgDstPath.substring(0, orgDstPath.length() - 1);
			else
				orgDstPath = "";
		}

		// ensure destination directory
		if (!Utils.createDir(dstPath))
			return false;
		
		// Copy all files from free app to paid app
		File[] srcList = srcDir.listFiles();
		if (srcList == null)
			return true;
		for (File srcFile : srcList) {
			srcPath = srcFile.getAbsolutePath();
			dstPath = orgDstPath
					+ srcPath.substring(srcPath.indexOf(orgSrcPath)
							+ orgSrcPath.length());
			if (srcFile.isDirectory()) {
				copyDir(srcPath, dstPath);
				continue;
			}
			copyFile(srcPath, dstPath);

		}
		
		return true;
	}

	// Copy file from source to dest
	public static boolean copyFile(String srcPath, String dstPath) {
		FileInputStream srcInput;

		try {
			File srcFile = new File(srcPath);
			if (!srcFile.exists())
				return false;
			
			srcInput = new FileInputStream(srcFile);
			copyFile(srcInput, dstPath);
			srcInput.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static boolean copyFile(InputStream srcInput, String dstPath) {
		FileOutputStream dstOutput;
		
		File destFile;

		try {
			destFile = new File(dstPath);
			ensureDir(null, destFile.getParent(), false);
			destFile.createNewFile();
			
			dstOutput = new FileOutputStream(destFile);
			byte buffer[] = new byte[2048];
			do {
				int bytesRead = srcInput.read(buffer);
				if (bytesRead <= 0) {
					break;
				}
				dstOutput.write(buffer, 0, bytesRead);

			} while (true);
			dstOutput.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}


	public static void deleteDir(File dir) {
        Log.d("DeleteRecursive", "DELETEPREVIOUS TOP" + dir.getPath());
        if (dir.exists() && dir.isDirectory())
        {
            String[] children = dir.list();
            if (children != null) {
            for (int i = 0; i < children.length; i++) 
            {
               File temp =  new File(dir, children[i]);
               if(temp.isDirectory())
               {
                   Log.d("DeleteRecursive", "Recursive Call" + temp.getPath());
                   deleteDir(temp);
               }
               else
               {
                   Log.d("DeleteRecursive", "Delete File" + temp.getPath());
                   boolean b = temp.delete();
                   if(b == false)
                   {
                       Log.d("DeleteRecursive", "DELETE FAIL");
                   }
               }
            }
            }

            dir.delete();
        }
    }
	
	public static void copyAssetToSDRAM(Context context, String strFilename, String dstFileName) {
		try {
			// complete path to target file
			File fileTarget = new File(dstFileName);
			Utils.createDir(fileTarget.getParent());
			
			if (!fileTarget.exists())
				fileTarget.createNewFile();
			// data source stream
			AssetManager assetManager = context.getAssets();
			InputStream istr = assetManager.open(strFilename);

			// data destination stream
			// NOTE: at this point you'll get an exception if you don't have
			// permission to access SDRAM ! (see manifest)
			OutputStream ostr = new FileOutputStream(fileTarget);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = istr.read(buffer)) > 0) {
				ostr.write(buffer, 0, length);
			}
			ostr.flush();
			ostr.close();
			istr.close();

		} catch (Exception e) {
			Toast.makeText(context, "File-Copy Error: " + strFilename,
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	/*************************************************************************************
	 * LANGUAGE & IME FUNCTIONS
	 ************************************************************************************/
	/**
	 * Switch language of APP
	 * @param context
	 * @param pLocale
	 */
	public static void switchLanguage(Context context, Locale pLocale) { 
        Resources res = context.getResources(); 
        DisplayMetrics dm = res.getDisplayMetrics(); 
        Configuration conf = res.getConfiguration(); 
        conf.locale = pLocale;
        res.updateConfiguration(conf, dm);
    } 
	
	/*************************************************************************************
	 * UI FUNCTIONS
	 ************************************************************************************/
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void showFullScreen(Activity activity) {
		if (Build.VERSION.SDK_INT < 16) { //ye olde method
			activity.requestWindowFeature(Window.FEATURE_NO_TITLE); 
			activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
		} else { // Jellybean and up, new hotness
			if (activity.getActionBar() != null)
				activity.getActionBar().hide();
			
			activity.getWindow().getDecorView().setSystemUiVisibility(
	                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                | View.SYSTEM_UI_FLAG_FULLSCREEN
	                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		}
	}
	
	public static boolean isWaitingDlgShowed() {
		return mProgressDlg != null;
	}
	
	public static ProgressDialog showWaitingDlg(Context context) {
		return showWaitingDlg(context, Constant.DLG_WAIT_MSG);
	}
	
	public static ProgressDialog showWaitingDlg(Context context, String msg) {
		return showWaitingDlg(context, msg, false);
	}
	
	public static ProgressDialog showWaitingDlg(Context context, boolean cancelable) {
		return showWaitingDlg(context, Constant.DLG_WAIT_MSG, cancelable);
	}
	
	public static ProgressDialog showWaitingDlg(Context context, String msg, boolean cancelable) {
		hideWaitingDlg();
		
		if (mProgressDlg == null) {
			mProgressDlg = openNewDialog(context, msg, cancelable, false);
		}
		
		return mProgressDlg;
	}
	
	public static ProgressDialog openNewDialog(Context context, String msg, boolean cancelable) {
		return openNewDialog(context, msg, cancelable, false);
	}
	
	public static ProgressDialog openNewDialog(Context context, String msg, boolean cancelable, boolean outsideTouchCancelable) {
		ProgressDialog dialog = null;
		try {
			dialog = new ProgressDialog(context);
	
			dialog.setMessage(msg);
			dialog.setIndeterminate(true);
			dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(outsideTouchCancelable);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return dialog;
	}
	
	public static void hideWaitingDlg() {
		if (mProgressDlg != null) {
			try {
				mProgressDlg.dismiss();
			} catch (Exception e) {};
		}
		
		mProgressDlg = null;
	}

	public static void showAlertDialog(Context context, String msg, OnClickListener listener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg);
		builder.setPositiveButton("OK", listener);
		builder.show();
	}
	
	@Deprecated
	public static void hideWaitingDialog() {
		hideWaitingDlg();
	}

	// Check if current orientation is portrait or not
	@Deprecated
	public static boolean isPortraitScreen(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int orientation = display.getRotation();
		
		if (orientation == Surface.ROTATION_0 || orientation == Surface.ROTATION_180) {
			return true;
		} else {
			Assert.assertTrue(orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270);
			return false;
		}
	}
	
	public static boolean isPortraitScreen1(Context context) {
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int getOrientation(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int orientation = display.getRotation();
		
		switch (orientation) {
		case Surface.ROTATION_0:
			return 0;
		case Surface.ROTATION_90:
			return 90;
		case Surface.ROTATION_180:
			return 180;
		case Surface.ROTATION_270:
			return 270;
		}
		
		Assert.assertTrue(false);
		return -1;
	}
	
	public static boolean isXLargeScreen(Context context) {
		int sizeMask = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		return sizeMask >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}
	
	public static boolean isLargeScreen(Context context) {
		int sizeMask = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		return sizeMask < Configuration.SCREENLAYOUT_SIZE_XLARGE && sizeMask >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	public static boolean isNormalScreen(Context context) {
		int sizeMask = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		return sizeMask < Configuration.SCREENLAYOUT_SIZE_LARGE && sizeMask >= Configuration.SCREENLAYOUT_SIZE_NORMAL;
	}
	
	public static boolean isSmallScreen(Context context) {
		int sizeMask = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		return sizeMask < Configuration.SCREENLAYOUT_SIZE_NORMAL && sizeMask >= Configuration.SCREENLAYOUT_SIZE_SMALL;
	}
	
	public static boolean isPhoneSize(Context c) {
		return !Utils.isXLargeScreen(c);
	}
	
	// Enable/Disable all sub views.
	public static void setEnableAllSubViews(ViewGroup parent, boolean enable) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			View child = parent.getChildAt(i);
			child.setEnabled(enable);
			
			if (child instanceof ViewGroup)
				setEnableAllSubViews((ViewGroup)child, enable);
		}
	}
	
	// Set customized font located at asset folder
	public static Typeface loadFont(Context context, String fontName) {
		Typeface typeface = fontMap.get(fontName);
		if (typeface == null) {
			typeface = Typeface.createFromAsset(context.getAssets(), fontName);
			fontMap.put(fontName, typeface);
		}
		
		return typeface;
	}
	
	public static void setAssetFont(Context context, View view, String fontName) {
		setAssetFont(context, view, fontName, fontName, fontName);
	}
	
	public static void setAssetFont(Context context, View view, String regularFont, String boldFont, String italicFont) {
		Typeface regularTypeface = null;
		Typeface boldTypeface = null;
		Typeface italicTypeface = null;
		Typeface selectedFace = null;
		
		try {
			regularTypeface = loadFont(context, regularFont);
			boldTypeface = loadFont(context, boldFont);
			italicTypeface = loadFont(context, italicFont);
			
			if (view instanceof TextView) {
				TextView textView = ((TextView)view);
				
				selectedFace = regularTypeface;
				if (textView.getTypeface() != null) {
					Typeface typeFace = textView.getTypeface();
					if (typeFace.isBold()) {
						selectedFace = boldTypeface;
					} else if (typeFace.isItalic()) {
						selectedFace = italicTypeface;
					}
				}
				textView.setTypeface(selectedFace);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void setAssetFont(Context context, ViewGroup group, String fontName) {
		setAssetFont(context, group, fontName, fontName, fontName);
	}
	
	public static void setAssetFont(Context context, ViewGroup group, String regularFont, String boldFont, String italicFont) {
		for (int i = 0; i < group.getChildCount(); i++) {
			View child = group.getChildAt(i);
			
			if (child instanceof ViewGroup) {
				setAssetFont(context, (ViewGroup)child, regularFont, boldFont, italicFont);
			} else if (child instanceof TextView) {
				setAssetFont(context, (TextView)child, regularFont, boldFont, italicFont); 
			}
		}
	}
	
	public static int getPixels(Resources resources, int dp) {
		final float scale = resources.getDisplayMetrics().density;
		int px = (int) (dp * scale + 0.5f);

		return px;
	}
	
	/*************************************************************************************
	 * BITMAP FUNCTIONS
	 ************************************************************************************/
	/**
	 * Retrieve bitmap drawable object from specified image path
	 */
	/*
	 * This version tries to get a NinePatchDrawable directly from the resource stream
	 */
	// private static int mSuccess = 0;
	public static Drawable getBitmapDrawable(Context context, String imagePath) throws IOException {
		return getBitmapDrawable3(context, imagePath);
		
//		if(mSuccess == 1)
//			return getBitmapDrawable1(context, imagePath);
//		else if(mSuccess == 2)
//			return getBitmapDrawable2(context, imagePath);
//		else if(mSuccess == 3)
//			return getBitmapDrawable3(context, imagePath);
//		else
//			return getBitmapDrawable1(context, imagePath);
	}
	
	
	
	public static Drawable getBitmapDrawable1(Context context, String imagePath) throws IOException {
		Drawable drawable = null;

		Assert.assertTrue(imagePath != null);

		InputStream inStream;
		try {
			inStream = context.getAssets().open(imagePath);
			TypedValue value = new TypedValue();
			value.density = TypedValue.DENSITY_DEFAULT;
			drawable = Drawable.createFromResourceStream(context.getResources(), value, inStream, imagePath);
		} catch (Exception e) {
//			reportError_getBitmapDrawable(context, e, imagePath);
			return getBitmapDrawable2(context, imagePath);
		}

//		if(drawable instanceof NinePatchDrawable) {
//			Rect padding = new Rect();
//			drawable.getPadding(padding);
//			Log.d(KeyboardApp.LOG_TAG, "getBitmapDrawable():" + drawable.toString() + ",imagePath=" + imagePath + ",padding=" + padding.left + "," + padding.right + "," + padding.top + "," + padding.bottom);
//		}

		// mSuccess = 1;
		return drawable;
	}


	/*
	 * This version tries to decode the stream to a regular Bitmap, then construct a NinePatchDrawable using the bitmap and 9-patch chunk 
	 */
	public static Drawable getBitmapDrawable2(Context context, String imagePath) throws IOException {
		Drawable drawable = null;

		Assert.assertTrue(imagePath != null);

		try {
			InputStream inStream = context.getAssets().open(imagePath);
			Bitmap bitmap = BitmapFactory.decodeStream(inStream);
			byte[] chunk = bitmap.getNinePatchChunk();
			boolean isNinePatch = NinePatch.isNinePatchChunk(chunk);
			if(isNinePatch) {
				// <HACK>
				float density = context.getResources().getDisplayMetrics().density;
				Rect padding = new Rect();
				if(imagePath.contains("_ss_") || imagePath.contains("_any_")) {
					padding.left = (int) (12f*density);
					padding.top = (int) (16f*density);
					padding.right = (int) (12f*density);
					padding.bottom = (int) (8f*density);
				} else {
					padding.left = (int) (12f*density);
					padding.top = (int) (12f*density);
					padding.right = (int) (12f*density);
					padding.bottom = (int) (12f*density);
				}
				// </HACK>
				drawable = new NinePatchDrawable(context.getResources(), bitmap, chunk, padding, imagePath);
//				drawable.getPadding(padding);
//				Log.d(KeyboardApp.LOG_TAG, "getBitmapDrawable2():" + drawable.toString() + ",imagePath=" + imagePath + ",padding=" + padding.left + "," + padding.right + "," + padding.top + "," + padding.bottom);
			} else
				drawable = new BitmapDrawable(context.getResources(), bitmap);
		} catch (Exception e) {
			return getBitmapDrawable3(context, imagePath);
		}
		
		// mSuccess = 2;
		return drawable;
	}
	

	public static Drawable getBitmapDrawable3(Context context, String imagePath) throws IOException {
		String resourceName = context.getPackageName() + ":drawable/" + imagePath.replace('/', '_').replace(".9.png", "").replace(".png", "");
		int drawableId = context.getResources().getIdentifier(resourceName, null, null);
		if(drawableId <= 0)
			throw (new FileNotFoundException(imagePath));
		
		Drawable drawable = context.getResources().getDrawable(drawableId);
//		Rect padding = new Rect();
//		drawable.getPadding(padding);
//		Log.d(KeyboardApp.LOG_TAG, "getBitmapDrawable3():" + drawable.toString() + ",imagePath=" + imagePath + ",padding=" + padding.left + "," + padding.right + "," + padding.top + "," + padding.bottom);

		// mSuccess = 3;
		
		return drawable;
	}

	
	
	public static BitmapDrawable safeGetBitmapDrawable(Context context, String imagePath, int maxWidth, int maxHeight, 
			int degree, ImageScaleType scaleType, boolean isPanScan) {
		Assert.assertTrue(imagePath != null);
		
		BitmapDrawable mDrawable = null;
		Options option = getBitmapInfo(imagePath);
		int width = option.outWidth;
		int height = option.outHeight;
		
		
		try {
			if (width > maxWidth || height > maxHeight)
				throw new OutOfMemoryError();
			mDrawable = new BitmapDrawable(context.getResources(), imagePath);
		} catch (OutOfMemoryError error) {
			Bitmap bitmap;
			bitmap = makeThumb(imagePath, width, height, degree, scaleType, isPanScan);
			if (bitmap != null) {
				mDrawable = new BitmapDrawable(bitmap);
			}
		}
		
		return mDrawable;
	}
	
	public static Options getBitmapInfo(String pathName) {
		Options options = new Options();

		options.inJustDecodeBounds = true;
		options.outWidth = 0;
		options.outHeight = 0;
		options.inSampleSize = 1;

		BitmapFactory.decodeFile(pathName, options);
		
		return options;
	}
	
	public static Bitmap makeThumb(String pathName, int iWidth, int iHeight, int degree, 
			ImageScaleType scaleType, boolean isPanScan) {
		return makeThumb(pathName, iWidth, iHeight, degree, scaleType, isPanScan, false);
	}
	
	public static Bitmap makeThumb(String pathName, int iWidth, int iHeight, int degree, 
			ImageScaleType scaleType, boolean isPanScan, boolean resizeBoundary) {
		Options options = null;
		Bitmap thumb = null;
		
		try {
			if((new File(pathName)).exists())
				options = getBitmapInfo(pathName);
			else
				return null;
			
			int rotate = 0;
			
			ExifInterface exif = new ExifInterface(pathName);     //Since API Level 5
			int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
			if (exifOrientation != -1) {
				switch (exifOrientation) {
					case ExifInterface.ORIENTATION_ROTATE_90:
						rotate = 90;
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						rotate = 180;
						break;
					case ExifInterface. ORIENTATION_ROTATE_270:
						rotate = 270;
						break;
				} 
			}
			degree += rotate;
			
			if (options.outWidth > 0 && options.outHeight > 0) {
				// Now see how much we need to scale it down.
				if (iWidth == -1 || iHeight == -1) {
					iWidth = options.outWidth;
					iHeight = options.outHeight;
				}
				int widthFactor = (options.outWidth + iWidth - 1) / iWidth;
				int heightFactor = (options.outHeight + iHeight - 1) / iHeight;

				widthFactor = Math.min(widthFactor, heightFactor);
				widthFactor = Math.max(widthFactor, 1);

				// Now turn it into a power of two.
				if (widthFactor > 1) {
					int factor = 1;
					for (int i = 0 ; i < widthFactor ; i++) {
						int temp = (int)Math.pow(2, i); 
						if (temp > widthFactor)
							break;
						factor = temp;
					}
					widthFactor = factor;
				}
				
				options.inSampleSize = widthFactor;
				options.inJustDecodeBounds = false;
				
				/*
				BitmapDrawable drawable = new BitmapDrawable(imageBitmap);
				drawable.setGravity(gravity);
				drawable.setBounds(0, 0, iWidth, iHeight);
				thumb = drawable.getBitmap();

				boolean isPanScan = false;
				if (imageBitmap.getWidth() > iWidth)
					isPanScan =
				*/
				
				thumb = BitmapFactory.decodeFile(pathName, options);
			} else {
				thumb = BitmapFactory.decodeFile(pathName);
			}
			
			thumb = Utils.getResizeBitmap(thumb, iWidth, iHeight, degree, scaleType, isPanScan, resizeBoundary);
		} catch (java.lang.Exception e) {
		}

		return thumb;
	}
	
	// Rotates the bitmap by the specified degree.
    // If a new bitmap is created, the original bitmap is recycled.
    public static Bitmap rotate(Bitmap b, int degrees, boolean shouldFreeOrgBitmap) {
        if (degrees != 0 && b != null) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) b.getWidth() / 2, (float) b.getHeight() / 2);
            try {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if (b != b2) {
                	if (shouldFreeOrgBitmap)
                		b.recycle();
                    b = b2;
                }
            } catch (OutOfMemoryError ex) {
                // We have no memory to rotate. Return the original bitmap.
            }
        }
        return b;
    }
	
    public static Bitmap getResizeBitmap(Bitmap source, int newWidth, int newHeight, 
    		int degree, ImageScaleType scaleType, boolean isPanScan) {
    	return getResizeBitmap(source, newWidth, newHeight, degree, scaleType, isPanScan, false);
    }
    
    public static Bitmap getResizeBitmap(Bitmap source, int newWidth, int newHeight, 
    		int degree, ImageScaleType scaleType, boolean isPanScan, boolean resizeBoundary) {
        source = rotate(source, degree, false);
        
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        if (sourceWidth == newWidth && sourceHeight == newHeight)
        	return source;
        
        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger 
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = 1;
        
        switch (scaleType) {
        case SCALE_FIT_X:
        	scale = xScale;
        	break;
        case SCALE_FIT_Y:
        	scale = yScale;
        	break;
        case SCALE_FIT_XY:
        	Assert.assertTrue("Not Implemented" == null);
        	break;
        case SCALE_FIT_PROPER:
        	if (isPanScan) {
        		scale = Math.max(xScale, yScale);
        	} else {
        		scale = Math.min(xScale, yScale);
        	}
        	break;
        default:
        	Assert.assertTrue(false);
        }

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;
        Bitmap dest;
        RectF targetRect;
        if (!resizeBoundary) {
	        // Let's find out the upper left coordinates if the scaled bitmap
	        // should be centered in the new size give by the parameters
	        float left = (newWidth - scaledWidth) / 2;
	        float top = (newHeight - scaledHeight) / 2;
	
	        // The target rectangle for the new, scaled version of the source bitmap will now
	        // be
	        targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);
	
	        // Finally, we create a new bitmap of the specified size and draw our new,
	        // scaled bitmap onto it.
	        dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        } else {
        	dest = Bitmap.createBitmap((int)scaledWidth, (int)scaledHeight, source.getConfig());
        	targetRect = new RectF(0, 0, scaledWidth, scaledHeight);
        }
        
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);
	    
	    return dest;
    }
    
    public static Bitmap drawableToBitmap (Drawable drawable, int width, int height) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth == 0 || drawableHeight == 0) {
        	drawableWidth = width;
        	drawableHeight = height;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawableWidth, drawableHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap); 
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
    
	public static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
		v.draw(c);
		return b;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static int getScreenWidth(Context context) {
		return getDisplaySize(context).x;
	}
		
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static int getScreenHeight(Context context) {
		return getDisplaySize(context).y;
	}
		
	private static Point getDisplaySize(Context context) {
			
		if (Build.VERSION.SDK_INT >= 17) {
			return getDisplaySizeMinSdk17(context);
		} else if (Build.VERSION.SDK_INT >= 13) {
			return getDisplaySizeMinSdk13(context);
		} else {
			return getDisplaySizeMinSdk1(context);
			}
		}
		
	@TargetApi(17)
	private static Point getDisplaySizeMinSdk17(Context context) {
		final WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();
	
		final DisplayMetrics metrics = new DisplayMetrics();
		display.getRealMetrics(metrics);
		
		final Point size = new Point();
		size.x = metrics.widthPixels;
		size.y = metrics.heightPixels;
		
		return size;
	}
		
	@TargetApi(13)
	private static Point getDisplaySizeMinSdk13(Context context) {
			
		final WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();

		final Point size = new Point();
		display.getSize(size);

		return size;
		}
		
	@SuppressWarnings("deprecation")
	private static Point getDisplaySizeMinSdk1(Context context) {

		final WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		final Display display = windowManager.getDefaultDisplay();

		final Point size = new Point();
		size.x = display.getWidth();
		size.y = display.getHeight();

		return size;
	}
	
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		  int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		  if (resourceId > 0) {
		      result = context.getResources().getDimensionPixelSize(resourceId);
		  }
		  return result;
	}
	
	public static int isImage(Context context, String filename) {
		String[] music_extensions = context.getResources().getStringArray(R.array.image_extensions);
		int length = 0;
		
		for (String music_extension : music_extensions) {
			if (filename.endsWith(music_extension)) {
				length = music_extension.length();
				break;
			}
		}
		
		return length;
	}
	
	public static File createBitmapFile(Bitmap bitmap, String path) {
		Assert.assertTrue(bitmap != null);

		File file = null;

		try {
			path = new File(path).getAbsolutePath();
			// Save bitmap to local path
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

			file = new File(path);
			file.createNewFile();

			// write the bytes in file
			FileOutputStream fo = new FileOutputStream(file);
			fo.write(bytes.toByteArray());
			fo.close();
		} catch (Exception e) {
			file = null;
		}

		return file;
	}
	/************************************************************************************
	 * TAKEN CAMERA & IMAGE
	 ***********************************************************************************/
	public static String getCameraStorePath() {
	    File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
	    if (path.exists()) {
	        File test1 = new File(path, "100MEDIA/");
	        if (test1.exists()) {
	            path = test1;
	        } else {
	            File test2 = new File(path, "100ANDRO/");
	            if (test2.exists()) {
	                path = test2;
	            } else {
	                File test3 = new File(path, "Camera/");
	                if (!test3.exists()) {
	                    test3.mkdirs();
	                }
	                path = test3;
	            }
	        }
	    } else {
	        path = new File(path, "Camera/");
	        path.mkdirs();
	    }
	    
	    return path.getAbsolutePath();
    }
	
	  public static String getImagePath(Context context, Uri imgUri) {
	    	String[] filePathColumn = { MediaStore.Images.Media.DATA };

	        Cursor cursor = context.getContentResolver().query(imgUri,
	                filePathColumn, null, null, null);
	        cursor.moveToFirst();

	        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	        String picturePath = cursor.getString(columnIndex);
	        cursor.close();
	        
	        return picturePath;
	    }
	
	/*************************************************************************************
	 * CHECK VALIDATION
	 ************************************************************************************/
	public static boolean checkEmailFormat(String email) {
		if (email == null)
			return false;
		
		if(email.length() == 0) {
	        return false;
	    }

		String pttn = "^[a-zA-Z0-9.-_@]+$";
//		String pttn = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    Pattern p = Pattern.compile(pttn);
	    Matcher m = p.matcher(email);

	    if(m.matches()) {
	        return true;
	    }

	    return false;

	}
	
	public static boolean checkNameFormat(String name) {
		name = name.trim();
		if(name.length() == 0) {
	        return false;
	    }

	    return true;

	}
	
	public static boolean checkUrlFormat(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
	
	// TODO: Update
	public static boolean checkPhoneNoFormat(String phoneNo) {
		if (phoneNo == null)
			return false;
		
		Pattern p = Pattern.compile("^[\\+]\\d{3}\\d{7}$", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(phoneNo);
		return m.find();
	}
	
	public static String stripExceptNumbers(String str, boolean includePlus) {
		StringBuilder res = new StringBuilder(str);
		String phoneChars = "0123456789";
		if (includePlus) {
			phoneChars += "+";
		}
		for (int i = res.length() - 1; i >= 0; i--) {
			if (!phoneChars.contains(res.substring(i, i + 1))) {
				res.deleteCharAt(i);
			}
		}
		return res.toString();
	}
	
	public static boolean hasSpecialCharacters(String string) {
		Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(string);
		return m.find();
	}
	
	public static String getPhoneNoWithPlus(String phoneNo) {
		if (phoneNo != null && !phoneNo.startsWith("+")) {
			return "+" + phoneNo;
		}
		
		return phoneNo;
	}
	
	public static String getPhoneNoWithoutPlus(String phoneNo) {
		if (phoneNo != null && phoneNo.length() > 0 && phoneNo.startsWith("+")) {
			return phoneNo.substring(1);
		}
		
		return phoneNo;
	}
	
	/*********************************************************************
	 * NETWORK
	 *********************************************************************/
	public static boolean isConnected(Context context) {
		ConnectivityManager connect = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED
				|| connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING
				|| connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING
				|| connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED;
	}

	public static String getHost(String url) {
		String host = null;
		try {
			URI uri = new URI(url);
			host = uri.getHost();
			if (host == null)
				host = uri.toString();
		} catch (Exception e) {}
		
		return host;
	}
	
	public static Map<String, List<String>> getUrlParameters(String url) {
	    Map<String, List<String>> params = new HashMap<String, List<String>>();
	    try {
		    String[] urlParts = url.split("\\?");
		    if (urlParts.length > 1) {
		        String query = urlParts[1];
		        for (String param : query.split("&")) {
		            String pair[] = param.split("=");
		            String key = URLDecoder.decode(pair[0], "UTF-8");
		            String value = "";
		            if (pair.length > 1) {
		                value = URLDecoder.decode(pair[1], "UTF-8");
		            }
		            List<String> values = params.get(key);
		            if (values == null) {
		                values = new ArrayList<String>();
		                params.put(key, values);
		            }
		            values.add(value);
		        }
		    }
	    } catch (UnsupportedEncodingException e) {
	    }
	    
	    return params;
	}
	
	public static boolean sendSMS(Context context, String number, String message) {
		if (number == null || !PhoneNumberUtils.isWellFormedSmsAddress(number) || number.equals("CDMA"))
			return false;
		
		SmsManager sms = SmsManager.getDefault();
		
		try {
			sms.sendTextMessage(number, null, message, null, null);
		} catch (Exception e) {
			return false;
		}
            
		return true;
	}
	
	public static InetAddress getLocalAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        //return inetAddress.getHostAddress().toString();
                        return inetAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Common", ex.toString());
        }
        
        return null;
    }
	
	/*
	public static String getMacAddress() {
		try {
			InetAddress addresses = InetAddress.getByName(InetAddress
					.getLocalHost().getHostName());

			NetworkInterface ni = NetworkInterface.getByInetAddress(addresses);

			byte[] mac = ni.getHardwareAddress();
			System.out.println("=====" + mac);
			for (int i = 0; i < mac.length; i++) {

				System.out.format("%02X%s", mac[i], (i < mac.length - 1) ? "-"
						: "");

			}

		} catch (Exception e) {
			System.out.println("exception was occureddddddddd" + e);

		}
	}
	*/

	/*********************************************************************
	 * GPS
	 *********************************************************************/
	public static Location getLocationFromName(Geocoder geocder, String name) {
		List<Address> addresses = null;
		Location location = null;
		
		try {
			// MUST BE CHECKED
			addresses = geocder.getFromLocationName(name, 1);
		} catch (IOException e) {
		}
		
		if (addresses != null) {
			for (Address singleAddress : addresses) {
				location = new Location("");
				location.setLatitude(singleAddress.getLatitude());
				location.setLongitude(singleAddress.getLongitude());
			}
		}
		
		return location;
	}
	
	public static Address getAddressFromName(Geocoder geocoder, String location) {
 		List<Address> addresses = null;
 		Address address = null;
		try {
			addresses = geocoder.getFromLocationName(location, 1);
	 		if (addresses != null) {
				for (Address singleAddress:addresses) {
					address = singleAddress;
				}
	 		}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return address;
	}
	
	/**********************************************************************************
	 * CURSOR TO CSV
	 *********************************************************************************/
	public static StringBuffer cursorToCSV(Cursor cursor) {
		StringBuffer out = new StringBuffer();

		// loop thru all the records
		for (int i = 0; i < cursor.getColumnCount(); i++) {
			String columnName = cursor.getColumnName(i);
			if (i != 0)
				out.append(", ");
			out.append("{");
			out.append(columnName); // adds a column
			out.append("}");
		}
		out.append("\n");
		
		while (cursor.moveToNext()) {
			// loop thru all the columns
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				String column = cursor.getString(i);
				if (i != 0)
					out.append(", ");
				out.append(column); // adds a column
			}
			out.append("\n");
		}

		return out;
	}
	
	/**********************************************************************************
	 * XML PARSER
	 *********************************************************************************/
	public static String getNodeValue(NodeList list, int index) {
		String nodeValue = "";

		try {
			nodeValue = list.item(index).getChildNodes().item(0).getNodeValue();
		} catch (Exception e) {}
		
		return nodeValue;
	}
	
	public static int getNodeIntValue(NodeList list, int index) {
		int nodeValue = 0;

		try {
			nodeValue = Integer.parseInt(list.item(index).getChildNodes().item(0).getNodeValue());
		} catch (Exception e) {}
		
		return nodeValue;
	}
	
	public static long getNodeLongValue(NodeList list, int index) {
		long nodeValue = 0;

		try {
			nodeValue = Long.parseLong(list.item(index).getChildNodes().item(0).getNodeValue());
		} catch (Exception e) {}
		
		return nodeValue;
	}
	
	public static float getNodeFloatValue(NodeList list, int index) {
		float nodeValue = 0;

		try {
			nodeValue = Float.parseFloat(list.item(index).getChildNodes().item(0).getNodeValue());
		} catch (Exception e) {}
		
		return nodeValue;
	}
	
	public static boolean getNodeBooleanValue(NodeList list, int index) {
		boolean nodeValue = false;

		try {
			nodeValue = Boolean.parseBoolean(list.item(index).getChildNodes().item(0).getNodeValue());
		} catch (Exception e) {}
		
		return nodeValue;
	}
	
	/**********************************************************************************
	 * JSON PARSER
	 *********************************************************************************/
	@Deprecated
	public static String getJsonSafeGetString(JSONObject object, String key) {
		return safeGetJsonString(object, key);
	}
	
	public static String safeGetJsonString(JSONObject object, String key) {
		String value = "";

		if (!object.isNull(key)) {
			try {
				value = object.getString(key);
			} catch (JSONException e) {
			}
		}

		return value;
	}
	
	@Deprecated
	public static int getJsonSafeGetInt(JSONObject object, String key) {
		return safeGetJsonInt(object, key);
	}
	
	public static int safeGetJsonInt(JSONObject object, String key) {
		int value = -1;

		if (!object.isNull(key)) {
			try {
				value = object.getInt(key);
			} catch (JSONException e) {
			}
		}

		return value;
	}
	
	public static long safeGetJsonLong(JSONObject object, String key) {
		long value = -1;

		if (!object.isNull(key)) {
			try {
				value = object.getLong(key);
			} catch (JSONException e) {
			}
		}

		return value;
	}
	
	public static double safeGetJsonDouble(JSONObject object, String key) {
		double value = Double.NaN;

		if (!object.isNull(key)) {
			try {
				value = object.getDouble(key);
			} catch (JSONException e) {
				value = Double.NaN;
			}
		}

		return value;
	}
	
	/*************************************************************************************
	 * SECRET
	 ************************************************************************************/
	public static String md5(String source) {
		String hash = null;
		
		try {
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] bytes = source.getBytes();
			
			digester.update(bytes, 0, bytes.length);
			hash = new BigInteger(1, digester.digest()).toString(16);
		} catch (Exception e) {}
		
		
		return hash;
	}
	
	/**********************************************************************************
	 * Audio
	 *********************************************************************************/
	public static boolean isMicOn(Context context) {
		AudioManager manager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		
		return manager.isWiredHeadsetOn();
	}
	
	/**********************************************************************************
	 * Encode/Decode
	 *********************************************************************************/
	public static String safeUrlEncode(String msg) {
		if (msg != null)
			msg = URLEncoder.encode(msg);
		
		return msg;
	}
	
	/**********************************************************************************
	 * SPECIAL
	 *********************************************************************************/
	public static void showSoftInput(Activity activity, View view, boolean isShow) {
		if (view == null)
			return;
		
		InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (isShow) {
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
		} else {
			activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	public static void showInstalledAppDetails(Context context, String packageName) {
	    Intent intent = new Intent();
	    final int apiLevel = Build.VERSION.SDK_INT;
	    if (apiLevel >= 9) { // above 2.3
	        intent.setAction(ACTION_APPLICATION_DETAILS_SETTINGS);
	        Uri uri = Uri.fromParts(SCHEME, packageName, null);
	        intent.setData(uri);
	    } else { // below 2.3
	        final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
	        intent.setAction(Intent.ACTION_VIEW);
	        intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
	        intent.putExtra(appPkgName, packageName);
	    }
	    context.startActivity(intent);
	}
	
	public static void killActivities(Context context) {
		Intent killIntent = new Intent("killMyActivity");
		killIntent.setType("text/plain");
		context.sendBroadcast(killIntent);
	}

}
