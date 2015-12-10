package im.chatify.page.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.model.CIChatContact;
import im.chatify.xabber.android.data.account.CommonState;
import im.chatify.xabber.android.data.account.OnAccountChangedListener;
import im.chatify.xabber.android.data.entity.BaseEntity;
import im.chatify.xabber.android.data.message.OnChatChangedListener;
import im.chatify.xabber.android.data.roster.AbstractContact;
import im.chatify.xabber.android.data.roster.OnContactChangedListener;

/**
 * Created by administrator on 8/23/15.
 */
public class CIChatHistoryFragment extends Fragment implements MaterialTabListener, OnAccountChangedListener,
        OnContactChangedListener, OnChatChangedListener, CIChatHistoryAdapter.OnContactListChangedListener, View.OnClickListener {

    private ListView                    mLVChatHistory;
    private MaterialTabHost             mTHPage;
    private Button                      mBTRefresh;
//    private CIBusinessChatListView      mVBusinessChatList;
//    private CICustomerChatListView      mVCustomerChatList;
    private CIChatHistoryAdapter        mChatHistoryAdapter;

    private ArrayList<CIChatContact>    mContactList = new ArrayList<CIChatContact>();

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

    @Override
    public void onResume() {
        super.onResume();
        CIApp.getInstance().addUIListener(OnAccountChangedListener.class, this);
        CIApp.getInstance().addUIListener(OnContactChangedListener.class, this);
        CIApp.getInstance().addUIListener(OnChatChangedListener.class, this);
        mChatHistoryAdapter.onChange();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterListeners();
    }

    private void initUI(ViewGroup view) {

//        mVCustomerChatList = (CICustomerChatListView)view.findViewById(R.id.vCustomerChatList);
//        mVBusinessChatList = (CIBusinessChatListView)view.findViewById(R.id.vBusinessChatList);

        mLVChatHistory = (ListView)view.findViewById(R.id.lvChatHistory);

//        mTHPage = (MaterialTabHost)view.findViewById(R.id.thPage);
//        mTHPage.addTab(new MaterialTab(getActivity(), false).setText(getResources().getString(R.string.chat_business)).setTabListener(this));
//        mTHPage.addTab(new MaterialTab(getActivity(), false).setText(getResources().getString(R.string.chat_customer)).setTabListener(this));

        mChatHistoryAdapter = new CIChatHistoryAdapter(getActivity(), R.layout.listitem_chathistory, this, this);
        mLVChatHistory.setAdapter(mChatHistoryAdapter);
        mLVChatHistory.setOnItemClickListener(mChatHistoryClickListener);
    }

    void unregisterListeners() {
        CIApp.getInstance().removeUIListener(OnAccountChangedListener.class, this);
        CIApp.getInstance().removeUIListener(OnContactChangedListener.class, this);
        CIApp.getInstance().removeUIListener(OnChatChangedListener.class, this);
        mChatHistoryAdapter.removeRefreshRequests();
    }

    UpdatableAdapter getAdapter() {
        return mChatHistoryAdapter;
    }

    Filterable getFilterableAdapter() {
        return mChatHistoryAdapter;
    }

    @Override
    public void onTabSelected(MaterialTab tab) {

        /*
        mTHPage.setSelectedNavigationItem(tab.getPosition());

        if (tab.getPosition() == 0) {
            mVBusinessChatList.setVisibility(View.VISIBLE);
            mVCustomerChatList.setVisibility(View.GONE);
        } else {
            mVBusinessChatList.setVisibility(View.GONE);
            mVCustomerChatList.setVisibility(View.VISIBLE);
        }
        */

    }

    @Override
    public void onTabReselected(MaterialTab tab) {

    }

    @Override
    public void onTabUnselected(MaterialTab tab) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onContactListChanged(CommonState commonState, boolean hasContacts, boolean hasVisibleContacts, boolean isFilterEnabled) {

    }

    @Override
    public void onAccountsChanged(Collection<String> accounts) {
        mChatHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChatChanged(String account, String user, boolean incoming) {
        if (incoming) {
            mChatHistoryAdapter.refreshRequest();
        }
    }

    @Override
    public void onContactsChanged(Collection<BaseEntity> entities) {
        mChatHistoryAdapter.notifyDataSetChanged();
    }

    private AdapterView.OnItemClickListener mChatHistoryClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            AbstractContact contact = (AbstractContact)mChatHistoryAdapter.getItem(i);
            Intent intent = new Intent(getActivity(), CIChatActivity.class);
            intent.putExtra(CIChatActivity.KEY_USER, contact.getUser());
            intent.putExtra(CIChatActivity.KEY_ACCOUNT, contact.getAccount());
            getActivity().startActivity(intent);
        }
    };
}
