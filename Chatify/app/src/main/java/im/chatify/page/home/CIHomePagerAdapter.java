package im.chatify.page.home;

import android.database.DataSetObserver;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import im.chatify.R;
import im.chatify.common.ui.SlidingTabLayout;
import im.chatify.page.business.CIBusinessListFragment;
import im.chatify.page.chat.CIChatHistoryFragment;
import im.chatify.page.deals.CIDealsCategoryFragment;

/**
 * Created by administrator on 8/12/15.
 */
public class CIHomePagerAdapter extends FragmentPagerAdapter implements SlidingTabLayout.TabIconProvider {

    final int PAGE_COUNT = 3;

    private static final String TAG = CIHomePagerAdapter.class.getSimpleName();

    private static final int iconRes[] = {
            R.mipmap.ic_map_white,
            R.mipmap.ic_chat_white,
            R.mipmap.ic_loyalty_white
            /*
            R.drawable.ic_tab_map,
            R.drawable.ic_tab_chat,
            R.drawable.ic_tab_business
            */
    };

    public CIHomePagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new CIBusinessListFragment();

            case 1:
                return new CIChatHistoryFragment();

            case 2:
                return new CIDealsCategoryFragment();

        }

        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
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