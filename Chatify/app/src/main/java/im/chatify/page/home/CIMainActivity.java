package im.chatify.page.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.gpit.android.util.Utils;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.UnparsedIQ;
import org.jivesoftware.smack.util.PacketParserUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.common.ui.CICommonActivity;
import im.chatify.model.CICategory;
import im.chatify.model.CIConst;
import im.chatify.model.CISideItem;
import im.chatify.page.about.CIAboutFragment;
import im.chatify.page.account.CIAccountFragment;
import im.chatify.page.business.CITagBusinessFragment;
import im.chatify.page.tellafriend.CITellAFriendFragment;
import im.chatify.service.gcm.CIRegistrationIntentService;
import im.chatify.service.gcm.QuickstartPreferences;
import im.chatify.service.xmpp.CIIQStanzaManager;
import im.chatify.xabber.android.data.NetworkException;
import im.chatify.xabber.android.data.account.AccountManager;
import im.chatify.xabber.android.data.account.StatusMode;
import im.chatify.xabber.android.data.connection.ConnectionItem;
import im.chatify.xabber.android.data.connection.ConnectionManager;
import im.chatify.xabber.android.data.connection.OnAuthorizedListener;
import im.chatify.xabber.android.data.connection.OnResponseListener;

import static android.view.Gravity.START;

public class CIMainActivity extends CICommonActivity implements OnAuthorizedListener {

    private ListView                mLVSideMenu;
    private SideDrawerAdapter       mSDAdapter;
    private DrawerLayout            mDrawerLayout;
    private Toolbar                 mToolBar;
    private ActionBarDrawerToggle   mDrawerToggle;
    private View                    mVSideHeader;
    private ArrayList<CISideItem>   mSideMenuList;
    private int                     mSelectedSideIndex;

    private CIHomeFragment          mFHome;
    private Fragment mFAccount;
    private CITellAFriendFragment   mFTellAFriend;
    private CITagBusinessFragment   mFTagBusiness;
    private CIAboutFragment         mFSendYourFeedback;
    private Fragment                mFAbout;
    private BroadcastReceiver       mRegistrationBroadcastReceiver;
    private String                  mAccount;

    private IQ                      mReqCategoryStanza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cimain);
    }

    @Override
    protected void initUI() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLVSideMenu = (ListView) findViewById(R.id.navdrawer);
        mToolBar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolBar != null) {
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationIcon(R.mipmap.ic_menu_white);
        }

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolBar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mSDAdapter = new SideDrawerAdapter(this, R.layout.listitem_side, mSideMenuList);
        mLVSideMenu.setAdapter(mSDAdapter);

        mLVSideMenu.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));
        mToolBar.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));
        mToolBar.setTitleTextColor(getResources().getColor(R.color.white));

        mLVSideMenu.setBackgroundColor(getResources().getColor(R.color.white));
        mToolBar.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));
        mDrawerLayout.closeDrawer(START);

        mLVSideMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mDrawerLayout.closeDrawer(START);
                mSelectedSideIndex = position - 1;
                mSDAdapter.notifyDataSetChanged();

                showPageByIndex(mSelectedSideIndex);
            }
        });

        mVSideHeader = LayoutInflater.from(this).inflate(R.layout.listheader_side, null);
        mLVSideMenu.addHeaderView(mVSideHeader);

        showPageByIndex(0);

        CIIQStanzaManager.getInstance(this).sendIQStanzaForLoadCategories(mAccount, true, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        CIApp.getInstance().addManager(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    protected void initData() {

        mSideMenuList = new ArrayList<CISideItem>();

        mSideMenuList.add(new CISideItem(R.mipmap.ic_home_white, getResources().getString(R.string.sidemenu_home)));
        mSideMenuList.add(new CISideItem(R.mipmap.ic_person_white, getResources().getString(R.string.sidemenu_account)));
        mSideMenuList.add(new CISideItem(R.mipmap.ic_share_white, getResources().getString(R.string.sidemenu_tell_a_friend)));
        mSideMenuList.add(new CISideItem(R.mipmap.ic_pin_drop_white, getResources().getString(R.string.sidemenu_tag_your_business)));
        mSideMenuList.add(new CISideItem(R.mipmap.ic_menu_feedback, getResources().getString(R.string.sidemenu_send_your_feedback)));
        mSideMenuList.add(new CISideItem(R.mipmap.ic_menu_info, getResources().getString(R.string.sidemenu_about_chatify)));

        mAccount = CIApp.getPreference(this).getString(CIConst.KEY_ACCOUNT, "");

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);


            }
        };

        if (checkPlayServices()) {
            Intent intent = new Intent(this, CIRegistrationIntentService.class);
            startService(intent);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {

            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public boolean supportOffline() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_cimain, menu);
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

    private void showPageByIndex(int index) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;

        switch (index) {

            case 0:

                mToolBar.setTitle(getResources().getString(R.string.app_name));

                if (mFHome == null)
                    mFHome = new CIHomeFragment();

                fragment = mFHome;
                break;

            case 1:

                mToolBar.setTitle(getResources().getString(R.string.sidemenu_account));

                if (mFAccount == null)
                    mFAccount = new CIAccountFragment();

                fragment = mFAccount;
                break;

            case 2:

                new MaterialDialog.Builder(this)
                        .title(R.string.share)
                        .titleColorRes(R.color.colorPrimary)
                        .customView(R.layout.view_tellafriend, false)
                        .positiveText(getResources().getString(R.string.close))
                        .show();

                /*
                mToolBar.setTitle(getResources().getString(R.string.sidemenu_tell_a_friend));

                if (mFTellAFriend == null)
                    mFTellAFriend = new CITellAFriendFragment();
                */

//                fragment = mFHome;

                return;
            case 3:

                mToolBar.setTitle(getResources().getString(R.string.sidemenu_tag_your_business));

                if (mFTagBusiness == null)
                    mFTagBusiness = new CITagBusinessFragment();

                fragment = mFTagBusiness;
                break;
            case 4:

                mToolBar.setTitle(getResources().getString(R.string.sidemenu_send_your_feedback));

                if (mFSendYourFeedback == null)
                    mFSendYourFeedback = new CIAboutFragment();

                fragment = mFSendYourFeedback;
                break;

            case 5:

                mToolBar.setTitle(getResources().getString(R.string.sidemenu_about_chatify));

                if (mFAbout == null)
                    mFAbout = new Fragment();

                fragment = mFAbout;
                break;

            default:
                break;

        }

        fragmentManager.beginTransaction()
                .replace(R.id.llContent, fragment)
                .commit();
    }

    @Override
    public void onAuthorized(ConnectionItem connection) {

        String token = CIApp.getPreference(CIMainActivity.this).getString(CIConst.KEY_GCM_TOKEN, "");

        CIIQStanzaManager.getInstance(CIMainActivity.this).sendIQStanzaForRegisterDeviceInfo(mAccount, token, Utils.getDeviceID(CIMainActivity.this), 7200, 3, 1);
        CIIQStanzaManager.getInstance(CIMainActivity.this).sendIQStanzaForLoadCategories(mAccount, true, null);
    }

    private class SideDrawerAdapter extends ArrayAdapter<CISideItem> {

        private ArrayList<CISideItem> mData;

        public SideDrawerAdapter(Context context, int resource, ArrayList<CISideItem> objects) {
            super(context, resource, objects);

            mData = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;

            CISideItem sideItem = (CISideItem) mData.get(position);

            LayoutInflater inflater = getLayoutInflater();
            row = inflater.inflate(R.layout.listitem_side, parent, false);

            ImageView ivSide = (ImageView) row.findViewById(R.id.ivSide);
            TextView tvTitle = (TextView) row.findViewById(R.id.tvTitle);

            ivSide.setImageResource(sideItem.imageResourceId);
            tvTitle.setText(sideItem.title);

            if (mSelectedSideIndex == position) {
                tvTitle.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), getResources().getString(R.string.font_roboto_medium)));
            } else {
                tvTitle.setTextColor(getResources().getColor(R.color.darkgray_color));
                tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), getResources().getString(R.string.font_roboto_regular)));
            }

            return row;
        }
    }
}
