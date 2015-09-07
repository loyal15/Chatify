package vsppsgv.chatify.im.page.chat;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import vsppsgv.chatify.im.R;

/**
 * Created by administrator on 8/23/15.
 */
public class CIChatListFragment extends Fragment implements MaterialTabListener {

    private ListView                    mLVChatHistory;
    private MaterialTabHost             mTHPage;
    private CIBusinessChatListView      mVBusinessChatList;
    private CICustomerChatListView      mVCustomerChatList;

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
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_cichatlist, container, false);

        initUI(rootView);

        return rootView;
    }

    private void initUI(ViewGroup view) {

        mVCustomerChatList = (CICustomerChatListView)view.findViewById(R.id.vCustomerChatList);
        mVBusinessChatList = (CIBusinessChatListView)view.findViewById(R.id.vBusinessChatList);

        mTHPage = (MaterialTabHost)view.findViewById(R.id.thPage);
        mTHPage.addTab(new MaterialTab(getActivity(), false).setText(getResources().getString(R.string.chat_business)).setTabListener(this));
        mTHPage.addTab(new MaterialTab(getActivity(), false).setText(getResources().getString(R.string.chat_customer)).setTabListener(this));
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        mTHPage.setSelectedNavigationItem(tab.getPosition());

        if (tab.getPosition() == 0) {
            mVBusinessChatList.setVisibility(View.VISIBLE);
            mVCustomerChatList.setVisibility(View.GONE);
        } else {
            mVBusinessChatList.setVisibility(View.GONE);
            mVCustomerChatList.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }
}
