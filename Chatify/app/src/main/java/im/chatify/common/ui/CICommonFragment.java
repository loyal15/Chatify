package im.chatify.common.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.gpit.android.util.Utils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;

import im.chatify.CIApp;
import im.chatify.model.CIConst;

/**
 * Created by administrator on 11/4/15.
 */
public class CICommonFragment extends Fragment {

    private ProgressDialog      mProgressDlg;

    protected void showProgress(String title, boolean cancelable) {
        mProgressDlg = Utils.openNewDialog(getActivity(), title, cancelable, false);

        if ( cancelable ) {
            mProgressDlg.setOnCancelListener(mDialogCancelListener);
        }
    }

    protected void dismissProgress() {
        try {
            if (mProgressDlg != null && mProgressDlg.isShowing())
                mProgressDlg.dismiss();
        } catch (Exception e) {}
    }

    protected void showToast(String msg) {

        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
    }

    protected VCard parseVCard(String xml) throws XmlPullParserException, IOException, SmackException {

        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();

        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(new StringReader(xml));

        int eventType = parser.next();

        if (eventType != XmlPullParser.START_TAG) {
            throw new IllegalStateException(String.valueOf(eventType));
        }

        if (!VCard.ELEMENT.equals(parser.getName())) {
            throw new IllegalStateException(parser.getName());
        }

        if (!VCard.NAMESPACE.equals(parser.getNamespace())) {
            throw new IllegalStateException(parser.getNamespace());
        }

        return (new VCardProvider()).parse(parser);
    }

    protected void saveVcard(VCard vcard) {

        SharedPreferences.Editor editor = CIApp.getPreferenceEditor(getContext());
        editor.putString(CIConst.KEY_MY_VCARD, vcard.getChildElementXML().toString());
        editor.commit();
    }

    private DialogInterface.OnCancelListener mDialogCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            // Cancel network transfer

        }
    };
}
