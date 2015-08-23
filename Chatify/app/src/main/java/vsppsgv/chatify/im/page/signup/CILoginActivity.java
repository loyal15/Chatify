package vsppsgv.chatify.im.page.signup;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.gc.materialdesign.views.ButtonRectangle;
import com.gpit.android.util.Utils;
import com.sinch.verification.Config;
import com.sinch.verification.SinchVerification;
import com.sinch.verification.Verification;
import com.sinch.verification.VerificationListener;

import vsppsgv.chatify.im.R;
import vsppsgv.chatify.im.common.CIGPSClient;
import vsppsgv.chatify.im.common.ui.CICommonActivity;
import vsppsgv.chatify.im.model.CIConst;

public class CILoginActivity extends CICommonActivity {

    private ButtonRectangle     mBRSignup;
    private AppCompatEditText   mETFullName;
    private AppCompatEditText   mETCountryCode;
    private AppCompatEditText   mETPhoneNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cilogin);
    }

    @Override
    protected void initUI() {

        mETFullName = (AppCompatEditText)findViewById(R.id.etFullName);
        mETPhoneNum = (AppCompatEditText)findViewById(R.id.etMobileNumber);
        mETCountryCode = (AppCompatEditText)findViewById(R.id.etCountryCode);

        mBRSignup = (ButtonRectangle)findViewById(R.id.brSignup);
        mBRSignup.setOnClickListener(mBRSignupClickListener);
    }

    @Override
    public boolean supportOffline() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cilogin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        CIGPSClient gpsClient = CIGPSClient.getInstance(this);
        gpsClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        CIGPSClient gpsClient = CIGPSClient.getInstance(this);
        gpsClient.disconnect();
    }


    private void createVerification(String phoneNumber) {

        showProgress(getResources().getString(R.string.verifying), true);

        Config config = SinchVerification.config().applicationKey(CIConst.DEV_SINCH_APP_KEY).context(getApplicationContext())
                .build();

        FlashVerificationListener listener = new FlashVerificationListener();
        Verification verification = SinchVerification.createFlashCallVerification(config, phoneNumber, listener);
        verification.initiate();
    }

    class FlashVerificationListener implements VerificationListener {

        @Override
        public void onInitiated() {
            Log.d(TAG, "Initialized!");
        }

        @Override
        public void onInitiationFailed(Exception exception) {
            Log.e(TAG, "Verification initialization failed: " + exception.getMessage());
            dismissProgress();
        }

        @Override
        public void onVerified() {
            Log.d(TAG, "Verified!");
            dismissProgress();
        }

        @Override
        public void onVerificationFailed(Exception exception) {
            Log.e(TAG, "Verification failed: " + exception.getMessage());
            dismissProgress();
        }
    }

    private View.OnClickListener mBRSignupClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String fullName = mETFullName.getText().toString();
            if (Utils.isNullOrEmpty(fullName)) {
                showToast(getResources().getString(R.string.login_error_fill_fullname));
                return;
            }

            String phoneCode = mETCountryCode.getText().toString();
            String phoneNumber = mETPhoneNum.getText().toString();

            if (Utils.isNullOrEmpty(phoneCode) || Utils.isNullOrEmpty(phoneNumber)) {
                showToast(getResources().getString(R.string.login_error_fill_phonenumber));
                return;
            }

            createVerification(phoneCode + phoneNumber);
        }
    };
}
