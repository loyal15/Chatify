package im.chatify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import com.gpit.android.util.Utils;
import java.util.Locale;
import im.chatify.model.CIConst;

public class CIAppSetting extends CICommonSetting {
	private final static String PREFS_KEY_APP_VERSION = "app_version";
	private final static String PREFS_KEY_SELECTED_LOCALE_COUNTRY = "selected_locale_country";
	private final static String PREFS_KEY_SELECTED_LOCALE_LANG = "selected_locale_lang";
	private final static String PREFS_KEY_CONFIG_LANG_SELECTED = "config_lang_selected";
	private final static String PREFS_KEY_CONFIG_LANG_SELECTED_INDEX = "config_lang_selected_index";
	private final static String PREFS_KEY_CONFIG_REWARD_RINGTONE = "config_reward_ringtone";
	private final static String PREFS_KEY_LOCATION_LAT = "location_latitude";
	private final static String PREFS_KEY_LOCATION_LONG = "location_longitude";
	
	private Locale mSelectedLocale;
	
	public static CIAppSetting instance;
	
	public static CIAppSetting getInstance(Context context) {
		if (instance == null) {
			instance = new CIAppSetting(context);
		}
		
		instance.setContext(context);
		
		return instance;
	}
	
	private CIAppSetting(Context context) {
		super(context);
		
		init();
	}
	
	@Override
	public SharedPreferences loadPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(mContext);
	}
	
	private void init() {
        checkVersion();
	}
	
	private void checkVersion() {
		int currentVersion = Utils.getAppVersionCode(mContext);
		int oldVersion = getAppVersion();
		
		if (currentVersion != oldVersion) {
			setAppVersion(currentVersion);
//			BBApp.getInstance().onUpgrade();
		}
	}
	
	public void setAppVersion(int versionCode) {
		mPrefsEditor.putInt(PREFS_KEY_APP_VERSION, versionCode);
		mPrefsEditor.commit();
	}
	
	public int getAppVersion() {
		int version = mPrefs.getInt(PREFS_KEY_APP_VERSION, 0);
		return version;
	}
	
	public void setLocale(Locale locale) {
		mPrefsEditor.putString(PREFS_KEY_SELECTED_LOCALE_COUNTRY, locale.getCountry());
		mPrefsEditor.putString(PREFS_KEY_SELECTED_LOCALE_LANG, locale.getLanguage());
		mPrefsEditor.commit();
		
		mSelectedLocale = locale;
		
		applyLocale();
	}
	
	public Locale getLocale() {
		if (mSelectedLocale == null) {
			String country = mPrefs.getString(PREFS_KEY_SELECTED_LOCALE_COUNTRY, null);
			String lang = mPrefs.getString(PREFS_KEY_SELECTED_LOCALE_LANG, null);
			if (country != null && lang != null) {
				mSelectedLocale = new Locale(lang, country);
			}
		}
		
		return mSelectedLocale;
	}
	
	public void applyLocale() {
		
		Locale currentAppLocale = getLocale();
		
		if ( currentAppLocale != null ) {
			
			Locale phoneLocale = mContext.getResources().getConfiguration().locale;
			Locale.setDefault(currentAppLocale);
			
	        Configuration appConfig = new Configuration();
	        appConfig.locale = currentAppLocale;
	        mContext.getResources().updateConfiguration(appConfig,
	        		mContext.getResources().getDisplayMetrics());
	        
	        if (!currentAppLocale.equals(phoneLocale)) {
		        Intent localeIntent = new Intent(CIConst.BROADCAST_LOCALE_CHANGED);
				mContext.sendBroadcast(localeIntent);
			}
	        
	        // RemoteLogger.i("applyLocale", locale.getLanguage());
		}
    } 
	
	public void setPreferLang(boolean selected) {
		mPrefsEditor.putBoolean(PREFS_KEY_CONFIG_LANG_SELECTED, selected);
		mPrefsEditor.commit();
	}
	
	public void setLanguageIndex(int index) {
		mPrefsEditor.putInt(PREFS_KEY_CONFIG_LANG_SELECTED_INDEX, index);
		mPrefsEditor.commit();
		
		CIApp.getInstance().onLanguageChanged(index);
	}
	
	public int getLanguageIndex() {
		return mPrefs.getInt(PREFS_KEY_CONFIG_LANG_SELECTED_INDEX, -1);
	}
	
	public boolean checkPreferLangSelected() {
		return mPrefs.getBoolean(PREFS_KEY_CONFIG_LANG_SELECTED, false);
	}
	
	public void setRewardRingtone(boolean ringtone) {
		mPrefsEditor.putBoolean(PREFS_KEY_CONFIG_REWARD_RINGTONE, ringtone);
		mPrefsEditor.commit();
	}
	
	public boolean getRewardRingtone() {
		return mPrefs.getBoolean(PREFS_KEY_CONFIG_REWARD_RINGTONE, true);
	}
	
	public void setLatitude(double latitude) {
		mPrefsEditor.putLong(PREFS_KEY_LOCATION_LAT, Double.doubleToLongBits(latitude));
		mPrefsEditor.commit();
	}
	
	public void setLongitude(double longitude) {
		mPrefsEditor.putLong(PREFS_KEY_LOCATION_LONG, Double.doubleToLongBits(longitude));
		mPrefsEditor.commit();
	}
	
	public double getLatitude() {
		double latitude = Double.longBitsToDouble(mPrefs.getLong(PREFS_KEY_LOCATION_LAT, 0));
		return latitude;
	}
	
	public double getLongitude() {
		double longitdue = Double.longBitsToDouble(mPrefs.getLong(PREFS_KEY_LOCATION_LONG, 0));
		return longitdue;
	}
}
