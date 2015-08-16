package vsppsgv.chatify.im.common.phoneno;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.content.Context;
import android.text.TextUtils;

class CallingCodeHelper {
	private static class CallingCodesXmlHandler extends XmlHandler {
		private static final String ATTRIBUTE_ID = "id";
		private static final String ATTRIBUTE_ISO_COUNTRY = "isoCountry";
		private static final String TAG_CALLING_CODE = "callingCode";
		private HashMap<String, String> mCallingCodes;
		private String mCallingCode;
		private String mIsoCountryCode;

		public CallingCodesXmlHandler(HashMap<String, String> callingCodes) {
			mCallingCodes = callingCodes;
		}

		@Override
		public void endElement(String localName, String elementValue) {
			if (localName.equalsIgnoreCase(TAG_CALLING_CODE)) {
				mCallingCodes.put(mCallingCode, mIsoCountryCode);
				return;
			}
		}

		@Override
		public void startElement(String localName, Attributes attributes) {
			if (localName.equalsIgnoreCase(TAG_CALLING_CODE)) {
				mCallingCode = attributes.getValue(ATTRIBUTE_ID);
				mIsoCountryCode = attributes.getValue(ATTRIBUTE_ISO_COUNTRY);
				return;
			}
		}
	}

	public static final String UNKNOWN_ISO_COUNTRY_CODE = "??";
	private HashMap<String, String> mCallingCodes = new HashMap<String, String>();
	private int mMaxCodeLength;

	public CallingCodeHelper(Context context) {
		initialize(context);
	}

	private void initialize(Context context) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			sp.parse(context.getAssets().open("phone/phone_country_code.xml"),
					new CallingCodesXmlHandler(mCallingCodes));
			initMaxCodeLength();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initMaxCodeLength() {
		mMaxCodeLength = 0;
		for (String callingCode : mCallingCodes.keySet()) {
			mMaxCodeLength = Math.max(mMaxCodeLength, callingCode.length());
		}
	}

	public String getCountryPhoneCode(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber))
			return UNKNOWN_ISO_COUNTRY_CODE;
		phoneNumber = phoneNumber.replaceAll("\\D", "");
		int currentLength = Math.min(phoneNumber.length(), mMaxCodeLength);
		String currentCode = "";
		while (currentLength > 0) {
			currentCode = phoneNumber.substring(0, currentLength);
			String isoCode = mCallingCodes.get(currentCode);
			if (isoCode != null)
				return currentCode;
			currentLength--;
		}
		return UNKNOWN_ISO_COUNTRY_CODE;
	}
	
	public String getCountryISOCode(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber))
			return UNKNOWN_ISO_COUNTRY_CODE;
		phoneNumber = phoneNumber.replaceAll("\\D", "");
		int currentLength = Math.min(phoneNumber.length(), mMaxCodeLength);
		String currentCode = "";
		while (currentLength > 0) {
			currentCode = phoneNumber.substring(0, currentLength);
			String isoCode = mCallingCodes.get(currentCode);
			if (isoCode != null)
				return isoCode;
			currentLength--;
		}
		return UNKNOWN_ISO_COUNTRY_CODE;
	}
}
