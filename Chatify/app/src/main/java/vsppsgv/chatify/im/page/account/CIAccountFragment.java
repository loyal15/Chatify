package vsppsgv.chatify.im.page.account;

import android.os.Bundle;
import com.support.v4.preference.PreferenceFragment;

import vsppsgv.chatify.im.R;

/**
 * Created by administrator on 9/4/15.
 */
public class CIAccountFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        addPreferencesFromResource(R.xml.prefs_ciaccount);
    }
}
