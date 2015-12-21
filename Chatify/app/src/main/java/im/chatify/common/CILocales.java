package im.chatify.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.gpit.android.util.Iso2Phone;
import com.gpit.android.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import im.chatify.common.phoneno.CIPhoneNormalizer;
import im.chatify.model.CIConst;

/**
 * Its the wrapping class from BBPhoneNormalizer
 * @author 
 *
 */
public class CILocales {
	
	private static CILocales instance;
	
	public static CILocales getInstance(Context context) {
		if (instance == null) {
			instance = new CILocales(context);
		}
		
		instance.setContext(context);
		
		return instance;
	}
	
	private Context mContext;
	private ArrayList<Locale> mCountryLocales = new ArrayList<Locale>();
	private ArrayList<String> mCountries = new ArrayList<String>();
	private HashMap<String, String> mAdditinalCountries = new HashMap<String, String>();
	
	private CILocales(Context context) {
		mContext = context;
		registerReceiver();
		loadCountries();
	}
	
	public void setContext(Context context) {
		mContext = context;
	}
	
	private String getCountryCode(Locale locale) {
		if ( !Utils.isNullOrEmpty(locale.getVariant()) )
			return locale.getVariant();
		
		if ( !Utils.isNullOrEmpty(locale.getCountry()) )
			return locale.getCountry();
		
		if ( mAdditinalCountries.containsKey(locale.getDisplayCountry()) )
			return mAdditinalCountries.get(locale.getDisplayCountry());
		
		return "";
	}
	
	private void reloadCountries() {
		mCountryLocales.clear();
		mCountries.clear();
		mAdditinalCountries.clear();
		loadCountries();
	}
	
	public String getCountryCode(String countryName) {

		for (Locale locale : mCountryLocales) {
			if (locale.getDisplayCountry().equals(countryName))
				return getCountryCode(locale); 
		}
		
		return "";
	}
	
	public String getCountryName(String isoCode) {
		
		for (Locale locale : mCountryLocales) {
			if ( getCountryCode(locale).equals(isoCode) ) {
				return locale.getDisplayCountry();
			}
		}
		
		return null;
	}
	
	public String getCountryCallingCodeByCountryName(String countryName) {

		for (Locale locale : mCountryLocales) {

			if (locale.getDisplayCountry().equals(countryName)) {

				String countryCode = this.getCountryCode(countryName);
				
				String prefix = Iso2Phone.getPhone(countryCode);

				return prefix;
			}
		}

		return "";
	}
	
	public String getPhoneNumberIncludePrefix(String countryCode, String phoneNum) {
		
		if ( Utils.isNullOrEmpty(phoneNum) )
			return "";
		
		String prefix = Iso2Phone.getPhone(countryCode);
		
		return String.format("%s %s", prefix, phoneNum);
	}
	
	public static String getPhoneNumberWithCountryCallingCode(String countryCallingCode, String phoneNum) {
		
		if ( Utils.isNullOrEmpty(phoneNum) )
			return "";
		
		if ( Utils.isNullOrEmpty(countryCallingCode) )
			countryCallingCode = "+";
		
		return String.format("%s %s", countryCallingCode, phoneNum);
	}
	
	public ArrayList<String> getCountries() {
		return mCountries;
	}
	
	public ArrayList<Locale> getCountryLocales() {
		return mCountryLocales;
	};
	
	public int getCountryIndexByCountryCode(String countryCode) {
		
		int index = 0;
		
		for ( int i = 0; i < mCountryLocales.size(); i ++ ) {
			
			Locale locale = mCountryLocales.get(i);
			
			if ( getCountryCode(locale).equals(countryCode) ) {
				index = i;
				break;
			}
		}
		
		return index;
	}
	
	public int getCountryIndexByCountryName(String countryName) {
		
		int index = 0;
		
		for ( int i = 0; i < mCountries.size(); i ++ ) {
			
			String country = mCountries.get(i);
			if ( country.equals(countryName) ) {
				index = 0;
				break;
			}
		}
		
		return index;
	}
	
	public int getCountryIndexByCountryCallingCode(String countryCallingCode) {
		
		if ( Utils.isNullOrEmpty(countryCallingCode) )
			return 0;
		
		String countryIsoCode = CIPhoneNormalizer.getInstance(mContext).getCountryIsoCode(countryCallingCode);
		
		return getCountryIndexByCountryCode(countryIsoCode);
	}
	
	private void registerReceiver() {
		mContext.registerReceiver(mLocaleChangedReceiver, new IntentFilter(
				CIConst.BROADCAST_LOCALE_CHANGED));
	}
	
	private void loadCountries() {
		
		Locale[] locale = Locale.getAvailableLocales();
		ArrayList<String> countries = new ArrayList<String>();
		HashMap<String, Locale> additionalCountries = new HashMap<String, Locale>();
		
		for (Locale loc : locale) {
		
			String countryCode = getCountryCode(loc);
			String countryName = loc.getDisplayCountry();
			
			if ( Utils.isNullOrEmpty(Iso2Phone.getPhone(countryCode)) )
				continue;
			
			if ( countryCode.equals("MY") || countryCode.equals("HK") || 
					countryCode.equals("TW") || countryCode.equals("MO") ||
					countryCode.equals("CN") ) {
				
				if ( Utils.isNullOrEmpty(countryCode) == false )
					additionalCountries.put(countryCode, loc);
				
				continue;
				
			}
			
			if ( countryName.length() > 0 && !countries.contains(countryName) ) {
				countries.add(countryName);
				mCountryLocales.add(loc);
			}
		}

		Collections.sort(mCountryLocales, new Comparator<Locale>() {
			@Override
			public int compare(Locale lhs, Locale rhs) {
				String lhsCountry = lhs.getDisplayCountry();
				String rhsCountry = rhs.getDisplayCountry();
				String defaultCountry = Locale.getDefault().getDisplayCountry();

				// Default country will be the first one
				if (lhsCountry.equals(defaultCountry))
					return -1;

				if (rhsCountry.equals(defaultCountry))
					return 1;

				return lhsCountry.compareTo(rhsCountry);
			}
		});
		
		countries.clear();

//		countries.add(mContext.getString(R.string.select_your_country));
		
		if ( additionalCountries.containsKey("MY") )
			countries.add(additionalCountries.get("MY").getDisplayCountry());
		
		if ( additionalCountries.containsKey("HK") ) 
			countries.add(additionalCountries.get("HK").getDisplayCountry());
		
		if ( additionalCountries.containsKey("TW") )
			countries.add(additionalCountries.get("TW").getDisplayCountry());
		
		if ( additionalCountries.containsKey("MO") )
			countries.add(additionalCountries.get("MO").getDisplayCountry());

//		if ( additionalCountries.containsKey("CN") )
//			countries.add(mContext.getString(R.string.country_other));
		
		for (Locale localeItem : mCountryLocales) {
			countries.add(localeItem.getDisplayCountry());
		}

		mCountries.addAll(countries);
		
		// Add void local for "select your country"
		Locale voidLocale = new Locale("", "");
		mCountryLocales.add(0, voidLocale);
		
		int index = 1;
		
		if ( additionalCountries.containsKey("MY") ) {		
			mCountryLocales.add(index, additionalCountries.get("MY"));
			index ++;
		}
		
		if ( additionalCountries.containsKey("HK") ) {	
			mCountryLocales.add(index, additionalCountries.get("HK"));
			index ++;
		}
		
		if ( additionalCountries.containsKey("TW") ) {
			mCountryLocales.add(index, additionalCountries.get("TW"));
			index ++;
		}
		
		if ( additionalCountries.containsKey("MO") ) {	
			mCountryLocales.add(index, additionalCountries.get("MO"));
			index ++;
		}
		
		if ( additionalCountries.containsKey("CN") ) {		
			mCountryLocales.add(index, additionalCountries.get("CN"));
			index ++;
		}
	}
	
	BroadcastReceiver mLocaleChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			reloadCountries();
		}
	};
}
