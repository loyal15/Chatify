package vsppsgv.chatify.im.page.business;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vsppsgv.chatify.im.R;

/**
 * Created by administrator on 9/4/15.
 */
public class CITagBusinessFragment extends Fragment {

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
        return inflater.inflate(R.layout.fragment_citagbusiness, container, false);
    }

    private void initUI(ViewGroup view) {

    }
}
