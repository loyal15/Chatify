package vsppsgv.chatify.im.common.ui;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBarActivity;

import vsppsgv.chatify.im.page.business.CIMapListFragment;
import vsppsgv.chatify.im.page.chat.CIChatListFragment;

/**
 * Created by administrator on 8/12/15.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter implements SlidingTabLayout.TabIconProvider {

    final int PAGE_COUNT =3;

    private static final String TAG = ViewPagerAdapter.class.getSimpleName();

    private static final int iconRes[] = {
            /*
            R.drawable.ic_tab_map,
            R.drawable.ic_tab_chat,
            R.drawable.ic_tab_business
            */
    };

    public ViewPagerAdapter(ActionBarActivity activity) {
        super(activity.getSupportFragmentManager());
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new CIMapListFragment();

            case 1:
                return new CIChatListFragment();

            case 2:
//                return new CIBussinessDetailsFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getPageIconResId(int position) {
        return iconRes[position];
    }

}