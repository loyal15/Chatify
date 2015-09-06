package vsppsgv.chatify.im.page.home;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;

import vsppsgv.chatify.im.R;
import vsppsgv.chatify.im.common.ui.CICommonActivity;
import vsppsgv.chatify.im.model.CISideItem;
import vsppsgv.chatify.im.page.about.CIAboutFragment;
import vsppsgv.chatify.im.page.account.CIAccountFragment;
import vsppsgv.chatify.im.page.business.CITagBusinessFragment;
import vsppsgv.chatify.im.page.tellafriend.CITellAFriendFragment;

import static android.view.Gravity.START;

public class CIMainActivity extends CICommonActivity {

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
    private CIAboutFragment         mFAbout;

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
    }

    protected void initData() {

        mSideMenuList = new ArrayList<CISideItem>();

        mSideMenuList.add(new CISideItem(R.mipmap.ic_home_white, getResources().getString(R.string.sidemenu_home)));
        mSideMenuList.add(new CISideItem(R.mipmap.ic_person_white, getResources().getString(R.string.sidemenu_account)));
        mSideMenuList.add(new CISideItem(R.mipmap.ic_share_white, getResources().getString(R.string.sidemenu_tell_a_friend)));
        mSideMenuList.add(new CISideItem(R.mipmap.ic_pin_drop_white, getResources().getString(R.string.sidemenu_tag_your_business)));
        mSideMenuList.add(new CISideItem(R.mipmap.ic_info_white, getResources().getString(R.string.sidemenu_about_chatify)));
    }

        @Override
    public boolean supportOffline() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cimain, menu);
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

                mToolBar.setTitle(getResources().getString(R.string.sidemenu_about_chatify));

                if (mFAbout == null)
                    mFAbout = new CIAboutFragment();

                fragment = mFAbout;
                break;

            default:
                break;

        }

        fragmentManager.beginTransaction()
                .replace(R.id.llContent, fragment)
                .commit();
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
