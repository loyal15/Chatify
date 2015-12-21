package im.chatify.page.about;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import im.chatify.R;

/**
 * Created by administrator on 9/4/15.
 */
public class CIAboutFragment extends Fragment {

    private Spinner mSPReason;

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
        ViewGroup view = (ViewGroup)inflater.inflate(R.layout.fragment_ciabout, container, false);
        initUI(view);

        return view;
    }

    private void initUI(ViewGroup view) {

        mSPReason = (Spinner)view.findViewById(R.id.spReason);

        final ArrayList<String> reasons = new ArrayList<String>();
        reasons.add("Tag me with Chatify");
        reasons.add("General enquiry");
        reasons.add("Feedback");
        reasons.add("Others");

        final SpinnerAdapter reasonAdapter = new SpinnerAdapter() {
            @Override
            public View getDropDownView(int i, View view, ViewGroup viewGroup) {

                String reason = reasons.get(i);

                view = LayoutInflater.from(getActivity()).inflate(R.layout.spinneritem_category_drop, viewGroup, false);

                ImageView ivCategory = (ImageView) view.findViewById(R.id.ivCategory);
                TextView tvCategory = (TextView) view.findViewById(R.id.tvCategoryName);

                ivCategory.setVisibility(View.GONE);
                tvCategory.setText(reason);

                return view;
            }

            @Override
            public void registerDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

            }

            @Override
            public int getCount() {
                return reasons.size();
            }

            @Override
            public Object getItem(int i) {
                return reasons.get(i);
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {

                String reason = reasons.get(i);

                view = LayoutInflater.from(getActivity()).inflate(R.layout.spinneritem_category_drop, viewGroup, false);

                ImageView ivCategory = (ImageView) view.findViewById(R.id.ivCategory);
                TextView tvCategory = (TextView) view.findViewById(R.id.tvCategoryName);

                ivCategory.setVisibility(View.GONE);
                tvCategory.setText(reason);

                return view;
            }

            @Override
            public int getItemViewType(int i) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }
        };

        mSPReason.setAdapter(reasonAdapter);
    }
}
