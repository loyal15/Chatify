package im.chatify.page.deals;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.novoda.sexp.finder.ElementFinder;

import org.jivesoftware.smack.packet.Stanza;

import java.util.ArrayList;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.model.CICategory;
import im.chatify.model.CIConst;
import im.chatify.xabber.android.data.connection.ConnectionItem;
import im.chatify.xabber.android.data.connection.OnAuthorizedListener;

/**
 * Created by administrator on 11/7/15.
 */
public class CIDealsCategoryFragment extends Fragment implements OnAuthorizedListener {

    private StaggeredGridLayoutManager  mSGLCategory;
    private RecyclerView                mRVCategory;
    private CIDealsCategoryAdapter      mDealsCategoryAdapter;
    private ArrayList<CICategory>       mCategoryList = new ArrayList<CICategory>();
    private String                      mAccount;
    private Stanza                      mReqCategoryStanza;
    private ElementFinder<String>       mElementFinder;

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
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_cidealscategory, container, false);

        initData();
        initUI(rootView);

        return rootView;
    }

    @Override
    public void onResume() {

        super.onResume();
        CIApp.getInstance().addManager(this);
    }

    @Override
    public void onPause() {

        super.onPause();

    }

    private void initUI(ViewGroup view) {

        mRVCategory = (RecyclerView)view.findViewById(R.id.rvDealCategory);
        mRVCategory.setHasFixedSize(true);

        mSGLCategory = new StaggeredGridLayoutManager(2, 1);
        mRVCategory.setLayoutManager(mSGLCategory);

        mDealsCategoryAdapter = new CIDealsCategoryAdapter(getActivity(), mCategoryList);
        mRVCategory.setAdapter(mDealsCategoryAdapter);
    }

    private void initData() {

        SharedPreferences preference = CIApp.getPreference(getContext());
        mAccount = preference.getString(CIConst.KEY_ACCOUNT, "");
    }

    /*
    private void loadCategory() {

        final ArrayList<CICategory> categoryList = CICategory.getCategoryList(getActivity(), false);

        if (categoryList.size() == 0) {

            CIBusinessAPI businessAPI = new CIBusinessAPI(getActivity(), true);
            businessAPI.getCategoryList(new OnAPICompletedListener() {
                @Override
                public void onCompleted(Object result) {

                    ArrayList<CICategory> categories = (ArrayList<CICategory>)result;

                    CICategory.setCategoryList(categories, false);

                    mCategoryList.clear();
                    mCategoryList.addAll(categories);
                    mDealsCategoryAdapter.notifyDataSetChanged();
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
            }, false);

        } else {

            mCategoryList.clear();
            mCategoryList.addAll(categoryList);
            mDealsCategoryAdapter.notifyDataSetChanged();
        }
    }
    */

    @Override
    public void onAuthorized(ConnectionItem connection) {

//        loadCategory();
    }
}
