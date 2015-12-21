package im.chatify;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class CICommonSetting {
	protected Context mContext;
	
	// Preference
	protected SharedPreferences mPrefs;
	protected SharedPreferences.Editor mPrefsEditor;
	
	protected abstract SharedPreferences loadPreferences();
	
	public CICommonSetting(Context context) {
		mContext = context;
		
		init();
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	public SharedPreferences getPreferences() {
		return mPrefs;
	}
	
	public SharedPreferences.Editor getPreferencesEditor() {
		return mPrefsEditor;
	}
	
	private void init() {
		mPrefs = loadPreferences();
		mPrefsEditor = mPrefs.edit();
	}
}
