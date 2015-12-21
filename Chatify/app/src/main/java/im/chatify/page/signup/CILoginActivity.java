package im.chatify.page.signup;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.gpit.android.util.Utils;
import com.sinch.verification.Config;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.XMPPError;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.common.CIGPSClient;
import im.chatify.common.CIUtils;
import im.chatify.common.ui.CICommonActivity;
import im.chatify.common.ui.phonecode.Country;
import im.chatify.common.ui.phonecode.CountryAdapter;
import im.chatify.common.ui.phonecode.CustomPhoneNumberFormattingTextWatcher;
import im.chatify.common.ui.phonecode.OnPhoneChangedListener;
import im.chatify.common.ui.phonecode.PhoneUtils;
import im.chatify.model.CIConst;
import im.chatify.page.home.CIMainActivity;
import im.chatify.service.xmpp.CIIQStanzaManager;
import im.chatify.xabber.android.data.NetworkException;
import im.chatify.xabber.android.data.account.AccountManager;
import im.chatify.xabber.android.data.account.AccountType;
import im.chatify.xabber.android.data.account.ArchiveMode;
import im.chatify.xabber.android.data.connection.ConnectionItem;
import im.chatify.xabber.android.data.connection.ConnectionManager;
import im.chatify.xabber.android.data.connection.OnAuthorizedListener;
import im.chatify.xabber.android.data.connection.OnPacketListener;
import im.chatify.xabber.android.data.connection.OnResponseListener;
import im.chatify.xabber.android.data.connection.ProxyType;
import im.chatify.xabber.android.data.connection.TLSMode;
import im.chatify.xabber.android.data.extension.vcard.OnVCardListener;
import im.chatify.xabber.android.data.extension.vcard.OnVCardSaveListener;
import im.chatify.xabber.android.data.extension.vcard.VCardManager;
import im.chatify.xabber.xmpp.address.Jid;

public class CILoginActivity extends CICommonActivity implements OnPacketListener, OnResponseListener, OnAuthorizedListener, OnVCardListener, OnVCardSaveListener {

    private static final String KEY_ACCOUNT = "KEY_ACCOUNT";
    private static final String KEY_FULLNAME = "KEY_FULLNAME";

    private ButtonRectangle     mBRSignup;
    private AppCompatEditText   mETFullName;
    private AppCompatEditText   mETCountryCode;
    private AppCompatEditText   mETPhoneNum;
    private Spinner             mSpCountry;


    private SparseArray<ArrayList<Country>> mCountriesMap = new SparseArray<ArrayList<Country>>();
    private String              mAccount;
    private String              mFullName;
    private String              mPhoneNumber;
    private CountryAdapter      mCountryAdapter;
    protected String            mLastEnteredPhone;

    private static final int    STATUS_REGISTER = 1;
    private static final int    STATUS_VERIFICATION = 2;
    private static final int    STATUS_RETRIEVE_PASSWORD = 3;
    private static final int    STATUS_LOGIN = 4;
    private static final int    STATUS_LOGIN_COMPLETED = 5;
    private int                 mStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cilogin);
    }

    @Override
    public void onResume() {
        super.onResume();

        CIApp.getInstance().addManager(this);
        CIApp.getInstance().addUIListener(OnVCardListener.class, this);
        CIApp.getInstance().addUIListener(OnVCardSaveListener.class, this);
    }

    @Override
    public void onPause() {
        super.onPause();

        CIApp.getInstance().removeUIListener(OnVCardListener.class, this);
        CIApp.getInstance().removeUIListener(OnVCardSaveListener.class, this);
    }

    @Override
    protected void initUI() {

        mETFullName = (AppCompatEditText)findViewById(R.id.etFullName);
        mETPhoneNum = (AppCompatEditText)findViewById(R.id.etMobileNumber);
        mETCountryCode = (AppCompatEditText)findViewById(R.id.etCountryCode);

        mBRSignup = (ButtonRectangle)findViewById(R.id.brSignup);
        mBRSignup.setOnClickListener(mBRSignupClickListener);

        mSpCountry = (Spinner)findViewById(R.id.spCountry);
        mSpCountry.setOnItemSelectedListener(mOnItemSelectedListener);
        mCountryAdapter = new CountryAdapter(this);
        mSpCountry.setAdapter(mCountryAdapter);

        mETPhoneNum.addTextChangedListener(new CustomPhoneNumberFormattingTextWatcher(mOnPhoneChangedListener));
        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (dstart > 0 && !Character.isDigit(c)) {
                        return "";
                    }
                }
                return null;
            }
        };

        mETPhoneNum.setFilters(new InputFilter[]{filter});

        new AsyncPhoneInitTask(this).execute();
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean supportOffline() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cilogin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        CIGPSClient gpsClient = CIGPSClient.getInstance(this);
        gpsClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        CIGPSClient gpsClient = CIGPSClient.getInstance(this);
        gpsClient.disconnect();
    }

    private void createVerification(String phoneNumber) {

        Log.v("Verification started", "Verification started");

        mStatus = STATUS_VERIFICATION;

        showProgress(getResources().getString(R.string.common_progress_title_loading), false);


        Config config = SinchVerification.config()
                .applicationKey(CIConst.DEV_SINCH_APP_KEY)
                .context(getApplicationContext())
                .build();

        FlashVerificationListener listener = new FlashVerificationListener();
        Verification verification = SinchVerification.createFlashCallVerification(config, phoneNumber, listener);

        verification.initiate();
    }

    private void registerUser() {

        mStatus = STATUS_REGISTER;

        showProgress(getResources().getString(R.string.common_progress_title_loading), false);

        List<AccountType> accountTypes = AccountManager.getInstance().getAccountTypes();
        AccountType accountType = accountTypes.get(0);
        accountType.setAllowServer(true);
        accountType.setHost(CIConst.SERVER_HOST);
        accountType.setPort(CIConst.SERVER_PORT);
        accountType.setTlsRequired(true);

        String account;

        Map<String,String> attributes = new HashMap<String,String>();

        try {
            account = AccountManager.getInstance().addAccount(
                    String.format("%s@%s", mPhoneNumber, CIConst.SERVER_NAME),
                    CIIQStanzaManager.PASSWORD, accountType,
                    false,
                    false,
                    false,
                    attributes,
                    true
            );

            mAccount = account;

        } catch (NetworkException e) {
            CIApp.getInstance().onError(e);
            return;
        }
    }

    private void registerTempUser() {

        mPhoneNumber = "4915735983513";
        mStatus = STATUS_REGISTER;

        showProgress(getResources().getString(R.string.common_progress_title_loading), false);

        List<AccountType> accountTypes = AccountManager.getInstance().getAccountTypes();
        AccountType accountType = accountTypes.get(0);
        accountType.setAllowServer(true);
        accountType.setHost(CIConst.SERVER_HOST);
        accountType.setPort(CIConst.SERVER_PORT);
        accountType.setTlsRequired(true);

        String account;

        Map<String,String> attributes = new HashMap<String,String>();

        try {
            account = AccountManager.getInstance().addAccount(
                    String.format("%s@%s", mPhoneNumber, CIConst.SERVER_NAME),
                    CIIQStanzaManager.PASSWORD, accountType,
                    false,
                    false,
                    false,
                    attributes,
                    true
            );

            mAccount = account;

        } catch (NetworkException e) {
            CIApp.getInstance().onError(e);
            return;
        }
    }

    private void retrievePassword() {

        showProgress(getResources().getString(R.string.common_progress_title_loading), false);

        mStatus = STATUS_RETRIEVE_PASSWORD;

        try {
            ConnectionManager.getInstance().retrievePassword(mAccount);
        } catch (NetworkException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private void login(String password) {

        mStatus = STATUS_LOGIN;

        AccountManager.getInstance().updateAccount(
                mAccount,
                true,
                CIConst.SERVER_HOST,
                CIConst.SERVER_PORT,
                CIConst.SERVER_NAME,
                mPhoneNumber,
                true,
                password,
                CIUtils.getXMPPResourceName(this),
                0,
                true,
                true,
                TLSMode.legacy,
                false,
                ProxyType.none,
                "",
                0,
                "",
                "",
                true,
                ArchiveMode.available,
                0,
                null
        );

    }

    /*
    private void saveProfile() {

        VCard newCard = new VCard();
        newCard.setFirstName("Bastian");
        newCard.setLastName("Becker");
        newCard.setField("type", "profile");
        VCardManager.getInstance().saveVCard(accounts.get(0), vcard);
    }
    */

    private void onError() {
        dismissProgress();

        if (AccountManager.getInstance().getAllAccounts().size() > 0)
            AccountManager.getInstance().removeAccount(mAccount);
    }

    private void onSuccess() {
        dismissProgress();

        if (mStatus < STATUS_LOGIN_COMPLETED) {
            saveUserInfo();
            showMainUI();
        }
    }

    private void showMainUI() {

        mStatus = STATUS_LOGIN_COMPLETED;

        Intent intent = new Intent(this, CIMainActivity.class);
        startActivity(intent);
        finishWithoutAnimation();
    }

    private void saveUserInfo() {

        VCardManager.getInstance().request(mAccount, Jid.getBareAddress(mAccount));

        SharedPreferences.Editor editor = CIApp.getPreferenceEditor(this);
        editor.putString(KEY_ACCOUNT, mAccount);
        editor.putString(KEY_FULLNAME, mFullName);
        editor.commit();
    }

    protected String validate() {
        String region = null;
        String phone = null;
        if (mLastEnteredPhone != null) {
            try {
                Phonenumber.PhoneNumber p = PhoneNumberUtil.getInstance().parse(mLastEnteredPhone, null);
                StringBuilder sb = new StringBuilder(16);
                sb.append('+').append(p.getCountryCode()).append(p.getNationalNumber());
                phone = sb.toString();
                region = PhoneNumberUtil.getInstance().getRegionCodeForNumber(p);
            } catch (NumberParseException ignore) {
            }
        }
        if (region != null) {
            return phone;
        } else {
            return null;
        }
    }

    protected void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    protected void showKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    @Override
    public void onPacket(ConnectionItem connection, String bareAddress, Stanza packet) {

        String stanzaId = packet.getStanzaId();

        if (stanzaId == null)
            return;

        if (stanzaId.equals("TTTT-1")) {

            if (mStatus < STATUS_VERIFICATION) {

                XMPPError error = packet.getError();

                if (error == null || error.getCondition().name().equals("conflict")) {
                    dismissProgress();
                    createVerification("+" + mPhoneNumber);
                }
                else
                    onError();
            }

        } else if (stanzaId.equals("XXXX-1")) {

            if (mStatus < STATUS_LOGIN) {

                XMPPError error = packet.getError();

                if (error == null || (error != null && error.getCondition().name().equals("conflict"))) {

                    try {

                        XmlPullParser parse = PacketParserUtils.getParserFor(packet.toString(), "password");
                        String password = PacketParserUtils.parseElementText(parse);

                        if (password != null) {
                            login(password);
                            return;
                        }

                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                onError();
            }

        }
    }

    @Override
    public void onReceived(String account, String packetId, IQ iq) {

    }

    @Override
    public void onError(String account, String packetId, IQ iq) {

    }

    @Override
    public void onTimeout(String account, String packetId) {

    }

    @Override
    public void onDisconnect(String account, String packetId) {

    }

    @Override
    public void onAuthorized(ConnectionItem connection) {

        onSuccess();
    }

    @Override
    public void onVCardReceived(String account, String bareAddress, VCard vCard) {


    }

    @Override
    public void onVCardFailed(String account, String bareAddress) {

    }

    @Override
    public void onVCardSaveSuccess(String account) {

    }

    @Override
    public void onVCardSaveFailed(String account) {

    }

    class FlashVerificationListener implements VerificationListener {

        @Override
        public void onInitiated() {
            Log.d(TAG, "Initialized!");
        }

        @Override
        public void onInitiationFailed(Exception exception) {
            Log.e(TAG, "Verification initialization failed: " + exception.getMessage());
            dismissProgress();
        }

        @Override
        public void onVerified() {
            Log.d(TAG, "Verified!");
            dismissProgress();

            if (mStatus < STATUS_RETRIEVE_PASSWORD)
                retrievePassword();
        }

        @Override
        public void onVerificationFailed(Exception exception) {
            Log.e(TAG, "Verification failed: " + exception.getMessage());
            dismissProgress();
        }
    }

    private View.OnClickListener mBRSignupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String fullName = mETFullName.getText().toString();
            if (Utils.isNullOrEmpty(fullName)) {
                showToast(getResources().getString(R.string.login_error_fill_fullname));
                return;
            }

            mFullName = fullName;

            String phoneCode = mETCountryCode.getText().toString();
            String phoneNumber = mETPhoneNum.getText().toString();

            if (Utils.isNullOrEmpty(phoneCode) || Utils.isNullOrEmpty(phoneNumber)) {
                showToast(getResources().getString(R.string.login_error_fill_phonenumber));
                return;
            }

            if (phoneCode.contains("+"))
                phoneCode.replace("+", "");

            mPhoneNumber = mETPhoneNum.getText().toString();

            registerUser();
        }
    };

    protected class AsyncPhoneInitTask extends AsyncTask<Void, Void, ArrayList<Country>> {

        private int mSpinnerPosition = -1;
        private Context mContext;

        public AsyncPhoneInitTask(Context context) {
            mContext = context;
        }

        @Override
        protected ArrayList<Country> doInBackground(Void... params) {
            ArrayList<Country> data = new ArrayList<Country>(233);
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    Country c = new Country(mContext, line, i);
                    data.add(c);
                    ArrayList<Country> list = mCountriesMap.get(c.getCountryCode());
                    if (list == null) {
                        list = new ArrayList<Country>();
                        mCountriesMap.put(c.getCountryCode(), list);
                    }
                    list.add(c);
                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
            if (!TextUtils.isEmpty(mETPhoneNum.getText())) {
                return data;
            }
            String countryRegion = PhoneUtils.getCountryRegionFromPhone(mContext);
            int code = PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryRegion);
            ArrayList<Country> list = mCountriesMap.get(code);
            if (list != null) {
                for (Country c : list) {
                    if (c.getPriority() == 0) {
                        mSpinnerPosition = c.getNum();
                        break;
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<Country> data) {

            mCountryAdapter.addAll(data);

            if (mSpinnerPosition > 0) {
                mSpCountry.setSelection(mSpinnerPosition);
            }
        }
    }

    protected OnPhoneChangedListener mOnPhoneChangedListener = new OnPhoneChangedListener() {
        @Override
        public void onPhoneChanged(String phone) {
            try {
                mLastEnteredPhone = phone;
                Phonenumber.PhoneNumber p = PhoneNumberUtil.getInstance().parse(phone, null);
                ArrayList<Country> list = mCountriesMap.get(p.getCountryCode());
                Country country = null;
                if (list != null) {
                    if (p.getCountryCode() == 1) {
                        String num = String.valueOf(p.getNationalNumber());
                        if (num.length() >= 3) {
                            String code = num.substring(0, 3);
                            if (CANADA_CODES.contains(code)) {
                                for (Country c : list) {
                                    // Canada has priority 1, US has priority 0
                                    if (c.getPriority() == 1) {
                                        country = c;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (country == null) {
                        for (Country c : list) {
                            if (c.getPriority() == 0) {
                                country = c;
                                break;
                            }
                        }
                    }
                }
                if (country != null) {
                    final int position = country.getNum();
                    mSpCountry.post(new Runnable() {
                        @Override
                        public void run() {
                            mSpCountry.setSelection(position);
                        }
                    });
                }
            } catch (NumberParseException ignore) {
            }

        }
    };

    protected AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Country c = (Country) mSpCountry.getItemAtPosition(position);
            if (mLastEnteredPhone != null && mLastEnteredPhone.startsWith(c.getCountryCodeStr())) {
                return;
            }

            mETPhoneNum.getText().clear();
            mETPhoneNum.getText().insert(mETPhoneNum.getText().length() > 0 ? 1 : 0, String.valueOf(c.getCountryCode()));
            mETPhoneNum.setSelection(mETPhoneNum.length());
            mLastEnteredPhone = null;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    protected static final TreeSet<String> CANADA_CODES = new TreeSet<String>();

    static {
        CANADA_CODES.add("204");
        CANADA_CODES.add("236");
        CANADA_CODES.add("249");
        CANADA_CODES.add("250");
        CANADA_CODES.add("289");
        CANADA_CODES.add("306");
        CANADA_CODES.add("343");
        CANADA_CODES.add("365");
        CANADA_CODES.add("387");
        CANADA_CODES.add("403");
        CANADA_CODES.add("416");
        CANADA_CODES.add("418");
        CANADA_CODES.add("431");
        CANADA_CODES.add("437");
        CANADA_CODES.add("438");
        CANADA_CODES.add("450");
        CANADA_CODES.add("506");
        CANADA_CODES.add("514");
        CANADA_CODES.add("519");
        CANADA_CODES.add("548");
        CANADA_CODES.add("579");
        CANADA_CODES.add("581");
        CANADA_CODES.add("587");
        CANADA_CODES.add("604");
        CANADA_CODES.add("613");
        CANADA_CODES.add("639");
        CANADA_CODES.add("647");
        CANADA_CODES.add("672");
        CANADA_CODES.add("705");
        CANADA_CODES.add("709");
        CANADA_CODES.add("742");
        CANADA_CODES.add("778");
        CANADA_CODES.add("780");
        CANADA_CODES.add("782");
        CANADA_CODES.add("807");
        CANADA_CODES.add("819");
        CANADA_CODES.add("825");
        CANADA_CODES.add("867");
        CANADA_CODES.add("873");
        CANADA_CODES.add("902");
        CANADA_CODES.add("905");
    }
}
