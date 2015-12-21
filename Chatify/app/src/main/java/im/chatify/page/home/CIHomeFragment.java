package im.chatify.page.home;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import im.chatify.R;
import im.chatify.common.ui.SlidingTabLayout;

/**
 * Created by administrator on 9/4/15.
 */
public class CIHomeFragment extends Fragment {

    private ViewPager           mVPHome;
    private SlidingTabLayout    mSlidingTabLayout;

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

        View rootView = (View)inflater.inflate(R.layout.fragment_cihome, container, false);

        initUI(rootView);

        return rootView;
    }

    private void initUI(View view) {

        mVPHome = (ViewPager) view.findViewById(R.id.viewpager);
        mVPHome.setOffscreenPageLimit(3);
        mVPHome.setAdapter(new CIHomePagerAdapter(getChildFragmentManager()));

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setCustomTabView(R.layout.tab_img_layout, R.id.ivTabName);
        mSlidingTabLayout.setDistributeEvenly(true);

        mSlidingTabLayout.setViewPager(mVPHome);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }
        });

        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.material_deep_teal_500));

    }
}
