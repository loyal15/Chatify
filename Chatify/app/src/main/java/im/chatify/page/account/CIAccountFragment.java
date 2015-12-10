package im.chatify.page.account;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.text.InputType;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.machinarius.preferencefragment.PreferenceFragment;
import com.gpit.android.util.Utils;
import com.jenzz.materialpreference.Preference;
import com.jenzz.materialpreference.SwitchPreference;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.model.CIConst;
import im.chatify.xabber.android.data.OnLoadListener;
import im.chatify.xabber.android.data.extension.vcard.OnVCardListener;
import im.chatify.xabber.android.data.extension.vcard.OnVCardSaveListener;
import im.chatify.xabber.android.data.extension.vcard.VCardManager;
import im.chatify.xabber.xmpp.address.Jid;

/**
 * Created by administrator on 9/4/15.
 */
public class CIAccountFragment extends PreferenceFragment implements OnLoadListener, OnVCardListener, OnVCardSaveListener {

    private PreferenceCategory  mPFCProfile;
    private Preference          mPFullName;
    private Preference          mPPhoneNum;
    private Preference          mPLandline;
    private Preference          mPStreet;
    private Preference          mPTown;
    private Preference          mPCity;
    private Preference          mPState;
    private SwitchPreference    mSPNotification;
    private SwitchPreference    mSPSound;
    private VCard               mVCard;
    private String              mAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        addPreferencesFromResource(R.xml.prefs_ciaccount_business);
        initUI();

        VCardManager.getInstance().request(mAccount, Jid.getBareAddress(mAccount));

        for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i ++) {

            PreferenceCategory category = (PreferenceCategory)getPreferenceScreen().getPreference(i);

            for (int j = 0; j < category.getPreferenceCount(); j ++) {
                Preference preference = (Preference) category.getPreference(j);
                preference.setOnPreferenceClickListener(new android.preference.Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(android.preference.Preference preference) {

                        String key = preference.getKey();

                        if (key.equals("pfProfilePhoneNum") || key.equals("pfAccountDeleteAccount") || key.equals("pfSettingSound") || key.equals("pfSettingNotification"))
                            return false;

                        showEditPreferenceDialog((Preference) preference);
                        return false;
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {

        super.onResume();

        CIApp.getInstance().addUIListener(OnVCardListener.class, this);
        CIApp.getInstance().addUIListener(OnVCardSaveListener.class, this);
    }

    @Override
    public void onPause() {

        super.onResume();

        CIApp.getInstance().removeUIListener(OnVCardListener.class, this);
        CIApp.getInstance().removeUIListener(OnVCardSaveListener.class, this);
    }

    @Override
    public void onLoad() {

    }

    private void initUI() {

        mAccount = CIApp.getPreference(getActivity()).getString(CIConst.KEY_ACCOUNT, "");

        mPFCProfile = (PreferenceCategory)findPreference("pfcProfile");
        mPFullName = (Preference)findPreference("pfProfileFullName");
        mPPhoneNum = (Preference)findPreference("pfProfilePhoneNum");
        mPLandline = (Preference)findPreference("pfProfileLandLine");
//        mPEmail = (Preference)findPreference("pfProfileEmail");
        mPStreet = (Preference)findPreference("pfProfileStreet");
        mPTown = (Preference)findPreference("pfProfileTown");
        mPCity = (Preference)findPreference("pfProfileCity");
        mPState = (Preference)findPreference("pfProfileState");

        mSPNotification = (SwitchPreference)findPreference("pfSettingNotification");
        mSPSound = (SwitchPreference)findPreference("pfSettingSound");

        mPFCProfile.removePreference(mPCity);
//        mPFCProfile.removePreference(mPEmail);
        mPFCProfile.removePreference(mPLandline);
        mPFCProfile.removePreference(mPState);
        mPFCProfile.removePreference(mPStreet);
        mPFCProfile.removePreference(mPTown);

        updateUI();
    }
    private void updateUI() {

        SharedPreferences preferences = CIApp.getPreference(getContext());

        VCard vcard = null;

        try {
            vcard = parseVCard(preferences.getString(CIConst.KEY_MY_VCARD, ""));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (SmackException e) {
            e.printStackTrace();
            return;
        }

        String fullName = "";

        if (vcard != null) {

            if (!Utils.isNullOrEmpty(vcard.getField("FN")))
                fullName = vcard.getField("FN");

            if (Utils.isNullOrEmpty(fullName))
                fullName = preferences.getString(CIConst.KEY_FULLNAME, "");

            mPFCProfile.addPreference(mPCity);
            mPFCProfile.addPreference(mPLandline);
            mPFCProfile.addPreference(mPState);
            mPFCProfile.addPreference(mPStreet);
            mPFCProfile.addPreference(mPTown);

            mPCity.setSummary(vcard.getField("CITY"));
            mPLandline.setSummary(vcard.getPhoneWork("WORK"));
            mPState.setSummary(vcard.getField("STATE"));
            mPStreet.setSummary(vcard.getField("STREET"));
            mPTown.setSummary(vcard.getAddressFieldHome("LOCALITY"));

        } else {

            fullName = preferences.getString(CIConst.KEY_FULLNAME, "");

            mPFCProfile.removePreference(mPCity);
            mPFCProfile.removePreference(mPLandline);
            mPFCProfile.removePreference(mPState);
            mPFCProfile.removePreference(mPStreet);
            mPFCProfile.removePreference(mPTown);
        }

        String account = preferences.getString(CIConst.KEY_ACCOUNT, "");

        mPFullName.setSummary(fullName);
        mPPhoneNum.setSummary(Jid.getName(account));

    }

    public static VCard parseVCard(String xml) throws XmlPullParserException, IOException, SmackException {

        if (Utils.isNullOrEmpty(xml))
            return null;

        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(new StringReader(xml));

        int eventType = parser.next();

        if (eventType != XmlPullParser.START_TAG) {
            throw new IllegalStateException(String.valueOf(eventType));
        }

        if (!VCard.ELEMENT.equals(parser.getName())) {
            throw new IllegalStateException(parser.getName());
        }

        if (!VCard.NAMESPACE.equals(parser.getNamespace())) {
            throw new IllegalStateException(parser.getNamespace());
        }

        return (new VCardProvider()).parse(parser);
    }

    private void saveVcard(VCard vcard) {

        SharedPreferences.Editor editor = CIApp.getPreferenceEditor(getContext());
        editor.putString(CIConst.KEY_MY_VCARD, vcard.getChildElementXML().toString());
        editor.commit();
    }

    private void showEditPreferenceDialog(final Preference preference) {

        final String key = preference.getKey();

        String hint = "";
        int inputType = 0;
        String value = "";

        if (preference.getSummary() != null)
            value = preference.getSummary().toString();

        if (key.equals("pfProfileFullName")) {
            hint = getResources().getString(R.string.login_fullname);
            inputType = InputType.TYPE_CLASS_TEXT;
        } else if (key.equals("pfProfileLandLine")) {
            hint = getResources().getString(R.string.landline);
            inputType = InputType.TYPE_CLASS_PHONE;
        } else if (key.equals("pfProfileStreet")) {
            hint = getResources().getString(R.string.tag_business_street);
            inputType = InputType.TYPE_CLASS_TEXT;
        } else if (key.equals("pfProfileTown")) {
            hint = getResources().getString(R.string.tag_business_town);
            inputType = InputType.TYPE_CLASS_TEXT;
        } else if (key.equals("pfProfileCity")) {
            hint = getResources().getString(R.string.tag_business_city);
            inputType = InputType.TYPE_CLASS_TEXT;
        } else if (key.equals("pfProfileState")) {
            hint = getResources().getString(R.string.tag_business_state);
            inputType = InputType.TYPE_CLASS_TEXT;
        }

        new MaterialDialog.Builder(getActivity())
                .title(hint)
                .titleColorRes(R.color.colorPrimary)
                .inputType(inputType)
                .input(hint, value, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {

                    }
                })
                .positiveText(getResources().getString(R.string.save))
                .negativeText(getResources().getString(R.string.close))
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);

                        String value = dialog.getInputEditText().getText().toString();

                        if (Utils.isNullOrEmpty(value))
                            return;

                        if (key.equals("pfProfileFullName")) {
                            mVCard.setField("FN", value);
                        } else if (key.equals("pfProfileLandLine")) {
                            mVCard.setPhoneWork("WORK", value);
                        } else if (key.equals("pfProfileStreet")) {
                            mVCard.setField("STREET", value);
                        } else if (key.equals("pfProfileTown")) {
                            mVCard.setAddressFieldHome("LOCALITY", value);
                        } else if (key.equals("pfProfileCity")) {
                            mVCard.setField("CITY", value);
                        } else if (key.equals("pfProfileState")) {
                            mVCard.setField("STATE", value);
                        }

                        preference.setSummary(value);
                        VCardManager.getInstance().saveVCard(mAccount, mVCard);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                    }
                })
                .show();
    }

    @Override
    public void onVCardReceived(String account, String bareAddress, VCard vCard) {

        String myAccount = CIApp.getPreference(getContext()).getString(CIConst.KEY_ACCOUNT, "");
        String fullName = CIApp.getPreference(getContext()).getString(CIConst.KEY_FULLNAME, "");

        if (!myAccount.equals(account))
            return;

        String fullname = vCard.getField("FN");

        if (Utils.isNullOrEmpty(fullname)) {
            fullname = fullName;
            vCard.setField("FN", fullname);
            VCardManager.getInstance().saveVCard(myAccount, vCard);
        }

        mVCard = vCard;

        updateUI();
    }

    @Override
    public void onVCardFailed(String account, String bareAddress) {

    }

    @Override
    public void onVCardSaveSuccess(String account) {

        String myAccount = CIApp.getPreference(getContext()).getString(CIConst.KEY_ACCOUNT, "");

        if (!myAccount.equals(account))
            return;

        saveVcard(mVCard);
    }

    @Override
    public void onVCardSaveFailed(String account) {

    }
}
