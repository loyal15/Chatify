package vsppsgv.chatify.im.common.phoneno;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import junit.framework.Assert;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Copied from Spaxtel project
// We don't like such coding rules, but lets keep it until we understand fully.
public class CIPhoneNormalizer {
	private static final Pattern GLOBAL_PHONE_NUMBER_PATTERN =
            Pattern.compile("[\\+]?[0-9.-]+");
	
	private static CIPhoneNormalizer instance = null;
	
	private Context mContext;
	
	private boolean initialzed = false;
	
	private byte[] data;
	private ByteBuffer buffer;
	private String defaultCountry;
	private String defaultCallingCode;
	private HashMap<String, Integer> callingCodeOffsets;
	private HashMap<String, ArrayList<String>> callingCodeCountries;
	private HashMap<String, CallingCodeInfo> callingCodeData;
	private HashMap<String, String> countryCallingCode;

	private CallingCodeHelper mCallingCodeHelper;
	
	private CIDialerFormattingTextWatcher mTextWatcher;
	
	public static CIPhoneNormalizer getInstance(Context context) {
		if (instance == null) {
			instance = new CIPhoneNormalizer(context);
		}
		
		instance.setContext(context);
		
		return instance;
	}
	
	public void setContext(Context context) {
		mContext = context;
	}

	private CIPhoneNormalizer(Context context) {
		this(context, null);
	}

	private CIPhoneNormalizer(Context context, String countryCode) {
		mContext = context;
		
		mTextWatcher = new CIDialerFormattingTextWatcher(context);
		
		init(countryCode);
	}

	/**
	 * Normalize phone number as readable strings
	 * @param orig
	 * @return
	 */
	public String normalize(String orig) {
		if (!initialzed) {
			return orig;
		}
		
		String formattedText = mTextWatcher.reformat(orig, 0);
		
		return formattedText;
	}
	
	public boolean hasInvalidCharacter(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			return true;
		}

		phoneNumber = strip(phoneNumber);
		Matcher match = GLOBAL_PHONE_NUMBER_PATTERN.matcher(phoneNumber);
		return !match.matches();
	}
	
	/**
	 * Check phone number is valid or not
	 * @param phoneNumber
	 * @return
	 */
	public boolean isPhoneNumberValid(String phoneNumber) {
		if (!initialzed) {
			return true;
		}
		String str = strip(phoneNumber);

		if (hasInvalidCharacter(str)) {
			return false;
		}
		
		if (str.startsWith("+")) {
			String rest = str.substring(1);
			CallingCodeInfo info = findCallingCodeInfo(rest);
			return info != null && info.isValidPhoneNumber(rest);
		} else {
			CallingCodeInfo info = callingCodeInfo(defaultCallingCode);
			if (info == null) {
				return false;
			}

			String accessCode = info.matchingAccessCode(str);
			if (accessCode != null) {
				String rest = str.substring(accessCode.length());
				if (rest.length() != 0) {
					CallingCodeInfo info2 = findCallingCodeInfo(rest);
					return info2 != null && info2.isValidPhoneNumber(rest);
				} else {
					return false;
				}
			} else {
				return info.isValidPhoneNumber(str);
			}
		}
	}
	
	/**
	 * Convert phone number to E164 format which is available at the spaxtel service
	 * @param phoneNo
	 * @return
	 */
	public String getE164PhoneFormat(String phoneNo) {
		if (phoneNo == null)
			return null;
		
		phoneNo = normalize(phoneNo);
		phoneNo = strip(phoneNo);
		phoneNo = convertToServerFormat(phoneNo);
		
		return phoneNo;
	}
	
	public String strip(String str) {
		StringBuilder res = new StringBuilder(str);
		String phoneChars = "0123456789+*#";
		for (int i = res.length() - 1; i >= 0; i--) {
			if (!phoneChars.contains(res.substring(i, i + 1))) {
				res.deleteCharAt(i);
			}
		}
		return res.toString();
	}

	public String stripExceptNumbers(String str, boolean includePlus) {
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

	public String stripExceptNumbers(String str) {
		return stripExceptNumbers(str, false);
	}
	
	
	@Deprecated
	public String defaultCallingCode() {
		return callingCodeForCountryCode(defaultCountry);
	}

	@Deprecated
	public String callingCodeForCountryCode(String countryCode) {
		return countryCallingCode.get(countryCode.toLowerCase(Locale.getDefault()));
	}

	@Deprecated
	public ArrayList<String> countriesForCallingCode(String callingCode) {
		if (callingCode.startsWith("+")) {
			callingCode = callingCode.substring(1);
		}

		return callingCodeCountries.get(callingCode);
	}

	@Deprecated
	public CallingCodeInfo findCallingCodeInfo(String phoneNo) {
		CallingCodeInfo res = null;
		for (int i = 0; i < 3; i++) {
			if (i < phoneNo.length()) {
				res = callingCodeInfo(phoneNo.substring(0, i + 1));
				if (res != null) {
					break;
				}
			} else {
				break;
			}
		}

		return res;
	}

	public String getCountryIsoCode(String phoneNumber) {
		phoneNumber = normalize(phoneNumber);
		phoneNumber = stripExceptNumbers(phoneNumber, false);
		String countryIsoCode = mCallingCodeHelper.getCountryISOCode(phoneNumber);
		
		return countryIsoCode;
	}
	
	public String getCountryCallingCode(String phoneNumber) {
		phoneNumber = normalize(phoneNumber);
		phoneNumber = stripExceptNumbers(phoneNumber, false);
		String countryPhoneCode = mCallingCodeHelper.getCountryPhoneCode(phoneNumber);
		
		return countryPhoneCode;
	}
	
	public String getLocalPhoneNumber(String phoneNumber) {
		phoneNumber = normalize(phoneNumber);
		phoneNumber = stripExceptNumbers(phoneNumber, false);
		
		String countryPhoneCode = getCountryCallingCode(phoneNumber);
		
		if (countryPhoneCode != null) {
			Assert.assertTrue(phoneNumber.startsWith(countryPhoneCode) && phoneNumber.length() >= countryPhoneCode.length());
			phoneNumber = phoneNumber.substring(countryPhoneCode.length());
		}
		
		return phoneNumber;
	}
	
	private int value32(int offset) {
		if (offset + 4 <= data.length) {
			buffer.position(offset);
			return buffer.getInt();
		} else {
			return 0;
		}
	}

	private short value16(int offset) {
		if (offset + 2 <= data.length) {
			buffer.position(offset);
			return buffer.getShort();
		} else {
			return 0;
		}
	}

	private String valueString(int offset) {
		try {
			for (int a = offset; a < data.length; a++) {
				if (data[a] == '\0') {
					if (offset == a - offset) {
						return "";
					}
					return new String(data, offset, a - offset);
				}
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	private CallingCodeInfo callingCodeInfo(String callingCode) {
		CallingCodeInfo res = callingCodeData.get(callingCode);
		if (res == null) {
			Integer num = callingCodeOffsets.get(callingCode);
			if (num != null) {
				final byte[] bytes = data;
				int start = num;
				int offset = start;
				res = new CallingCodeInfo();
				res.callingCode = callingCode;
				res.countries = callingCodeCountries.get(callingCode);
				callingCodeData.put(callingCode, res);

				int block1Len = value16(offset);
				offset += 2;

				offset += 2;
				int block2Len = value16(offset);
				offset += 2;

				offset += 2;
				int setCnt = value16(offset);
				offset += 2;

				offset += 2;

				ArrayList<String> strs = new ArrayList<String>(5);
				String str;
				while ((str = valueString(offset)).length() != 0) {
					strs.add(str);
					offset += str.length() + 1;
				}
				res.trunkPrefixes = strs;
				offset++;

				strs = new ArrayList<String>(5);
				while ((str = valueString(offset)).length() != 0) {
					strs.add(str);
					offset += str.length() + 1;
				}
				res.intlPrefixes = strs;

				ArrayList<RuleSet> ruleSets = new ArrayList<RuleSet>(setCnt);
				offset = start + block1Len;
				for (int s = 0; s < setCnt; s++) {
					RuleSet ruleSet = new RuleSet();
					ruleSet.matchLen = value16(offset);
					offset += 2;
					int ruleCnt = value16(offset);
					offset += 2;
					ArrayList<PhoneRule> rules = new ArrayList<PhoneRule>(
							ruleCnt);
					for (int r = 0; r < ruleCnt; r++) {
						PhoneRule rule = new PhoneRule();
						rule.minVal = value32(offset);
						offset += 4;
						rule.maxVal = value32(offset);
						offset += 4;
						rule.byte8 = (int) bytes[offset++];
						rule.maxLen = (int) bytes[offset++];
						rule.otherFlag = (int) bytes[offset++];
						rule.prefixLen = (int) bytes[offset++];
						rule.flag12 = (int) bytes[offset++];
						rule.flag13 = (int) bytes[offset++];
						int strOffset = value16(offset);
						offset += 2;
						rule.format = valueString(start + block1Len + block2Len
								+ strOffset);

						int openPos = rule.format.indexOf("[[");
						if (openPos != -1) {
							int closePos = rule.format.indexOf("]]");
							rule.format = String.format("%s%s",
									rule.format.substring(0, openPos),
									rule.format.substring(closePos + 2));
						}

						rules.add(rule);

						if (rule.hasIntlPrefix) {
							ruleSet.hasRuleWithIntlPrefix = true;
						}
						if (rule.hasTrunkPrefix) {
							ruleSet.hasRuleWithTrunkPrefix = true;
						}
					}
					ruleSet.rules = rules;
					ruleSets.add(ruleSet);
				}
				res.ruleSets = ruleSets;
			}
		}

		return res;
	}

	private static final String REGEX_SERVER_PHONE_FORMAT = "\\+\\d{7,20}";

	/**
	 * Convert phone number to server format "+CODENUMBER". Removes all characters except digits and add "+" at begin.
	 * E164 format
	 * 
	 * @param phoneNumber
	 * @return converted value
	 */
	public String convertToServerFormat(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			return "";
		}
		
		return "+" + phoneNumber.replaceAll("\\D", "");
	}

	public boolean isConvertedMatchesToServerFormat(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			return false;
		}
		
		if (!phoneNumber.startsWith("+")) {
			return false;
		}
		
		return convertToServerFormat(phoneNumber).matches(REGEX_SERVER_PHONE_FORMAT);
	}
	
	public boolean isMatchesToServerFormat(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber)) {
			return false;
		}
		
		if (!phoneNumber.startsWith("+")) {
			return false;
		}
		
		return phoneNumber.matches(REGEX_SERVER_PHONE_FORMAT);
	}
	
	private void parseDataHeader() {
		int count = value32(0);
		int base = count * 12 + 4;
		int spot = 4;
		for (int i = 0; i < count; i++) {
			String callingCode = valueString(spot);
			spot += 4;
			String country = valueString(spot);
			spot += 4;
			int offset = value32(spot) + base;
			spot += 4;

			if (country.equals(defaultCountry)) {
				defaultCallingCode = callingCode;
			}

			countryCallingCode.put(country, callingCode);

			callingCodeOffsets.put(callingCode, offset);
			ArrayList<String> countries = callingCodeCountries.get(callingCode);
			if (countries == null) {
				countries = new ArrayList<String>();
				callingCodeCountries.put(callingCode, countries);
			}
			countries.add(country);
		}

		if (defaultCallingCode != null) {
			callingCodeInfo(defaultCallingCode);
		}
	}
	
	private void init(String countryCode) {
		mCallingCodeHelper = new CallingCodeHelper(mContext);
		
		try {
			InputStream stream = mContext.getAssets().open("phone/phone_formats.dat");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = stream.read(buf, 0, 1024)) != -1) {
				bos.write(buf, 0, len);
			}
			data = bos.toByteArray();
			buffer = ByteBuffer.wrap(data);
			buffer.order(ByteOrder.LITTLE_ENDIAN);
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("error", "Message: " + e.getLocalizedMessage());
			return;
		}

		if (countryCode != null && countryCode.length() != 0) {
			defaultCountry = countryCode;
		} else {
			Locale loc = Locale.getDefault();
			defaultCountry = loc.getCountry().toLowerCase(Locale.getDefault());
		}
		callingCodeOffsets = new HashMap<String, Integer>(255);
		callingCodeCountries = new HashMap<String, ArrayList<String>>(255);
		callingCodeData = new HashMap<String, CallingCodeInfo>(10);
		countryCallingCode = new HashMap<String, String>(255);

		parseDataHeader();
		initialzed = true;
	}
}
