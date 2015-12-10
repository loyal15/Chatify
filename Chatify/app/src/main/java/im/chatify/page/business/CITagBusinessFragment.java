package im.chatify.page.business;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.gc.materialdesign.views.ButtonRectangle;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.gpit.android.util.Utils;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.common.CIGPSClient;
import im.chatify.common.ui.CICommonFragment;
import im.chatify.common.ui.phonecode.Country;
import im.chatify.common.ui.phonecode.PhoneUtils;
import im.chatify.model.CICategory;
import im.chatify.model.CIConst;
import im.chatify.service.xmpp.CIIQStanzaManager;
import im.chatify.xabber.android.data.OnLoadListener;
import im.chatify.xabber.android.data.account.AccountManager;
import im.chatify.xabber.android.data.connection.ConnectionItem;
import im.chatify.xabber.android.data.connection.OnAuthorizedListener;
import im.chatify.xabber.android.data.connection.OnResponseListener;
import im.chatify.xabber.android.data.extension.vcard.OnVCardSaveListener;
import im.chatify.xabber.android.data.extension.vcard.VCardManager;
import im.chatify.xabber.xmpp.address.Jid;


/**
 * Created by administrator on 9/4/15.
 */
public class CITagBusinessFragment extends CICommonFragment implements OnLoadListener, OnVCardSaveListener, OnAuthorizedListener {

    private Spinner                         mSPCategory;
    private MaterialEditText                mETBusinessName;
    private MaterialEditText                mETEmail;
    private MaterialEditText                mETPhone;
    private MaterialEditText                mETStreet;
    private MaterialEditText                mETTown;
    private MaterialEditText                mETCity;
    private MaterialEditText                mETState;
    private Spinner                         mSPCountry;
    private ButtonRectangle                 mBRCancel;
    private ButtonRectangle                 mBRSend;
    private CIBusinessCountryAdapter        mCountryAdapter;
    private CIBusinessCategoryAdapter       mCategoryAdapter;
    private VCard                           mVCard;
    private IQ                              mReqCategoryStanza;
    private String                          mAccount;

    private SparseArray<ArrayList<Country>> mCountriesMap = new SparseArray<ArrayList<Country>>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_citagbusiness, container, false);

        initUI(view);

        return view;
    }

    @Override
    public void onResume() {

        super.onResume();

        CIApp.getInstance().addManager(this);
        CIApp.getInstance().addUIListener(OnVCardSaveListener.class, this);
    }

    @Override
    public void onPause() {

        super.onPause();

        CIApp.getInstance().removeUIListener(OnVCardSaveListener.class, this);
    }

    private void initUI(ViewGroup view) {

        mAccount = CIApp.getPreference(getActivity()).getString(CIConst.KEY_ACCOUNT, "");

        mCategoryAdapter = new CIBusinessCategoryAdapter(getActivity());

        mSPCategory = (Spinner) view.findViewById(R.id.spCategory);
        mSPCategory.setAdapter(mCategoryAdapter);
        mSPCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if (pos == 0) {

                } else {

                }
            }
        });

        mETBusinessName = (MaterialEditText) view.findViewById(R.id.etBusinessName);
        mETEmail = (MaterialEditText) view.findViewById(R.id.etEmail);
        mETPhone = (MaterialEditText) view.findViewById(R.id.etPhone);
        mETStreet = (MaterialEditText) view.findViewById(R.id.etStreet);
        mETTown = (MaterialEditText) view.findViewById(R.id.etTown);
        mETCity = (MaterialEditText) view.findViewById(R.id.etCity);
        mETState = (MaterialEditText) view.findViewById(R.id.etState);

        mCountryAdapter = new CIBusinessCountryAdapter(getActivity());
        mSPCountry = (Spinner) view.findViewById(R.id.spCountry);
        mSPCountry.setAdapter(mCountryAdapter);

        mBRCancel = (ButtonRectangle) view.findViewById(R.id.brClearAll);
        mBRCancel.setOnClickListener(mBRCancelClickListener);

        mBRSend = (ButtonRectangle) view.findViewById(R.id.brSend);
        mBRSend.setOnClickListener(mBRSendClickListener);

        initData();
    }

    private void initData() {
        new AsyncPhoneInitTask(getActivity()).execute();

        loadCategory();
        //loadVcard();
    }

    private void loadVcard() {

        ArrayList<String> accounts = new ArrayList<String>(AccountManager.getInstance().getAllAccounts());
        String account = accounts.get(0);
        VCardManager.getInstance().request(account, Jid.getBareAddress(account));
    }

    /*
    private void loadCategory() {

        final ArrayList<CICategory> categoryList = CICategory.getCategoryList(getActivity(), true);

        if (categoryList.size() == 0) {

            CIBusinessAPI businessAPI = new CIBusinessAPI(getActivity(), true);
            businessAPI.getCategoryList(new OnAPICompletedListener() {
                @Override
                public void onCompleted(Object result) {

                    ArrayList<CICategory> categories = (ArrayList<CICategory>)result;

                    CICategory.setCategoryList(categories, true);
                    mCategoryAdapter.addAll(categoryList);

                }

                @Override
                public void onCompleted() {

                }

                @Override
                public void onFailed(Object result) {

                }

                @Override
                public void onCanceled(Object result) {

                }
            }, true);

        } else {

            mCategoryAdapter.clear();
            mCategoryAdapter.addAll(categoryList);
        }
    }
    */

    private void clearAll() {

        mSPCategory.setSelection(0);
        mETBusinessName.setText("");
        mETEmail.setText("");
        mETPhone.setText("");
        mETStreet.setText("");
        mETTown.setText("");
        mETCity.setText("");
        mETState.setText("");
    }

    private void updateUI() {

        mETBusinessName.setText(mVCard.getField("ORGNAME"));
        mETEmail.setText(mVCard.getEmailHome());
        mETPhone.setText(mVCard.getPhoneWork("WORK"));
        mETStreet.setText(mVCard.getAddressFieldHome("STREET"));
        mETTown.setText(mVCard.getAddressFieldHome("LOCALITY"));
        mETCity.setText(mVCard.getField("CITY"));
        mETState.setText(mVCard.getField("STATE"));

        try {

            int categoryId = Integer.parseInt(mVCard.getField("CATID"));

            ArrayList<CICategory> categories = CICategory.getCategoryList(getActivity(), true);

            for (int i = 0; i < CICategory.getCategoryList(getActivity(), true).size(); i ++) {

                CICategory category = categories.get(i);

                if (category.categoryId == categoryId) {
                    mSPCategory.setSelection(i);
                    break;
                }
            }

        } catch (NumberFormatException e) {

        }

        String countryCode = mVCard.getField("CTRY");

        for (int i = 0; i < mCountryAdapter.getCount(); i ++) {

            Country country = mCountryAdapter.getItem(i);

            if (country.getCountryISO().equals(countryCode)) {
                mSPCountry.setSelection(i);
                break;
            }
        }
    }

    private void loadCategory() {

        CIIQStanzaManager.getInstance(getContext()).sendIQStanzaForLoadCategories(mAccount, true, new OnResponseListener() {
            @Override
            public void onReceived(String account, String packetId, IQ iq) {

                mCategoryAdapter.clear();
                mCategoryAdapter.addAll(CICategory.getCategoryList(getContext(), true));
                mCategoryAdapter.notifyDataSetChanged();
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
        });
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onVCardSaveSuccess(String account) {

        showToast(getResources().getString(R.string.tag_business_vcard_save_success));

        clearAll();

        saveVcard(mVCard);
    }

    @Override
    public void onVCardSaveFailed(String account) {

        showToast(getResources().getString(R.string.tag_business_vcard_save_error));
    }

    @Override
    public void onAuthorized(ConnectionItem connection) {

        loadCategory();
    }

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
                mSPCountry.setSelection(mSpinnerPosition);
            }
        }
    }

    private View.OnClickListener mBRCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            clearAll();
        }
    };

    private View.OnClickListener mBRSendClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ArrayList<String> accounts = new ArrayList<String>(AccountManager.getInstance().getAllAccounts());
            String account = accounts.get(0);

            String business = mETBusinessName.getText().toString();
            if (Utils.isNullOrEmpty(business)) {
                mETBusinessName.requestFocus();
                return;
            }

            String email = mETEmail.getText().toString();
            if (Utils.isNullOrEmpty(email)) {
                mETEmail.requestFocus();
                return;
            }

            String phone = mETPhone.getText().toString();
            if (Utils.isNullOrEmpty(phone)) {
                mETPhone.requestFocus();
                return;
            }

            String street = mETStreet.getText().toString();
            if (Utils.isNullOrEmpty(street)) {
                mETState.requestFocus();
                return;
            }

            String town = mETTown.getText().toString();
            if (Utils.isNullOrEmpty(town)) {
                mETTown.requestFocus();
                return;
            }

            String city = mETCity.getText().toString();
            if (Utils.isNullOrEmpty(city)) {
                mETCity.requestFocus();
                return;
            }

            String state = mETState.getText().toString();
            if (Utils.isNullOrEmpty(state)) {
                mETState.requestFocus();
                return;
            }

            Country country = mCountryAdapter.getItem(mSPCountry.getSelectedItemPosition());

            if (mSPCategory.getSelectedItemPosition() == 0)
                return;

            CICategory category = CICategory.getCategoryList(getActivity(), true).get(mSPCategory.getSelectedItemPosition() - 1);

            if (mVCard == null)
                mVCard = new VCard();

//            mVCard.setFirstName(business);
            mVCard.setAddressFieldHome("STREET", street);
            mVCard.setAddressFieldHome("LOCALITY", town);
            mVCard.setAddressFieldHome("PCODE", country.getCountryCodeStr());
            mVCard.setAddressFieldHome("CTRY", country.getCountryISO());
            mVCard.setField("PHONE", phone);
            mVCard.setEmailHome(email);
            mVCard.setField("ORGNAME", business);
            mVCard.setField("CATID", String.valueOf(category.categoryId));
            mVCard.setField("CATNAM", category.categoryName);
            mVCard.setField("EMAIL", email);
            mVCard.setField("CTRY", country.getCountryISO());
            mVCard.setField("DESCP", "");
            mVCard.setField("CITY", city);
            mVCard.setField("STATE", state);
            mVCard.setField("LAT", String.valueOf(CIGPSClient.getInstance(getActivity()).getLatitude()));
            mVCard.setField("LNG", String.valueOf(CIGPSClient.getInstance(getActivity()).getLongitude()));
            mVCard.setField("TAG", "1");


            VCardManager.getInstance().saveVCard(account, mVCard);
        }
    };
}
