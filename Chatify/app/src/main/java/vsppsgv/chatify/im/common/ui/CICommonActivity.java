package vsppsgv.chatify.im.common.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.gpit.android.util.NetworkUtils;
import com.gpit.android.util.Utils;
import com.splunk.mint.Mint;


import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import vsppsgv.chatify.im.CIAppSetting;
import vsppsgv.chatify.im.R;
import vsppsgv.chatify.im.model.CIConst;


public abstract class CICommonActivity extends ActionBarActivity {
    protected static String TAG;

    private ViewGroup           mRootView;
    protected boolean           mLiveOn = false;
    protected boolean           mVisible = false;
    private ProgressDialog      mProgressDlg;

    @Override
    protected void onCreate(Bundle bundle) {
        mLiveOn = true;

        TAG = getClass().getSimpleName();

        checkAvailablity();

        super.onCreate(bundle);

        registerReceiver();

//        ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        checkAvailablity();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mVisible = false;
    }

    @Override
    public void onResume() {
        checkAvailablity();

        applyConfig();

        super.onResume();

        mVisible = true;
    }

    @Override
    protected void onStart() {
        checkAvailablity();

        super.onStart();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void finish() {
        super.finish();

        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public void finishWithoutAnimation() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        mLiveOn = false;
        super.onDestroy();

        unregisterReceiver();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        applyConfig();
    }

    public void setContentView(int layoutResId) {
        View contentView = View.inflate(this, layoutResId, null);

        setContentView(contentView);
    }

    public void setContentView(View contentView) {
        setContentView(contentView, null);
    }

    public void setContentView(View contentView, LayoutParams params) {
        View rootView = wrapContentView(contentView);

        // Initialize Mint
//        Mint.initAndStartSession(this, BBConst.MINT_API_KEY);

        if (params != null) {
            super.setContentView(rootView, params);
        } else {
            super.setContentView(rootView);
        }

        _initUI();
    }

    @Override
    public View findViewById(int resId) {
        if (mRootView == null)
            return null;

        View view = mRootView.findViewById(resId);

        return view;
    }

    public boolean isAlive() {
        return mLiveOn;
    }

    private boolean checkAvailablity() {
        // Check application life-cycle. If application instances are released, we have to restart the application again.
        if (shouldbeAppAlive()) {
            Log.e(TAG, "There is no singleton instance at the memory");
            restartApplication();

            return false;
        }

        if (!supportOffline()) {
            if (!NetworkUtils.isNetworkAvailable(this)) {
//                Toast.makeText(this, getString(R.string.needs_online), Toast.LENGTH_LONG).show();
                finish();

                return false;
            }
        }

        // Check page security
        if (shouldbePassedLogin()) {
//            BBUserItem currUserItem = BBUserItem.getCurrUserItem();
//            if (currUserItem == null || currUserItem.getMLMMainAccount() == null) {
//                RemoteLogger.e(TAG, "current user item is null");
//                restartApplication();
//
//                return false;
//            }
        }

        return true;
    }

    public boolean shouldbeAppAlive() {
        return true;
    }

    private void restartApplication() {
        /*
        RemoteLogger.e(TAG, "Restart application due to background application's resource is released.");
        Intent intent;

        if (BBApp.predefinedDevModeOn()) {
            intent = new Intent(this, BBDevBootActivity.class);
        } else {
            intent = new Intent(this, BBProductBootActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

        System.exit(0);
        */
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    public void startActivityWithoutAnimation(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        intent.putExtra("requestCode", requestCode);
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
    }

    public boolean shouldbePassedLogin() {
        return true;
    }


    /************************* Broadcast Receiver **************************/
    private void registerReceiver() {
        registerReceiver(mUpgradeApplicationMessageReceiver, new IntentFilter(CIConst.BROADCAST_REQUEST_UPGRADE_APPLICATION));
        registerReceiver(mUpgradeApplicationMessageReceiver, new IntentFilter(CIConst.BROADCAST_RECOMMEND_UPGRADE_APPLICATION));
    }

    private void unregisterReceiver() {
        unregisterReceiver(mUpgradeApplicationMessageReceiver);
    }

    private BroadcastReceiver mUpgradeApplicationMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CIConst.BROADCAST_REQUEST_UPGRADE_APPLICATION)) {
                showForcelyRequestUpgradeDialog();
            } else if (intent.getAction().equals(CIConst.BROADCAST_RECOMMEND_UPGRADE_APPLICATION)) {
                showRecommendUpgradeDialog();
            }
        }
    };

    protected void showForcelyRequestUpgradeDialog() {

        /*
        Utils.showAlertDialog(this, getString(R.string.forcely_request_upgrade_application), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BBConst.WEB_SITE_URL));
                startActivity(intent);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 1000);
            }
        });
        */
    }

    protected void showRecommendUpgradeDialog() {

        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.recommend_upgrade_application));
        builder.setPositiveButton(android.R.string.ok, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(BBConst.WEB_SITE_URL));
                startActivity(intent);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(0);
                    }
                }, 1000);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                onUpgradeDialogCanceled();
            }
        });
        builder.show();
        */
    }

    protected void onUpgradeDialogCanceled() {

    }
    /************************* Apply Configure ***********************/
    private void applyConfig() {
        setLocale();
    }

    private void setLocale() {
        CIAppSetting.getInstance(this).applyLocale();
    }

    /************************* Initialize Layout ***********************/
    // Wrap content view in common layout which is included debug version and number
    private View wrapContentView(View contentView) {
//        if (BBApp.devModeOn() || BBConst.LOGIN_WITHOUT_CORRECT_PASSWORD) {
            mRootView = (ViewGroup) View.inflate(this, R.layout.activity_cicommon, null);
            ViewGroup containerView = (ViewGroup) mRootView.findViewById(R.id.flActivityContainer);
            containerView.addView(contentView);
//        } else {
//            mRootView = (ViewGroup) contentView;
//        }

        return mRootView;
    }

    protected void _initUI() {
        /*
        if (BBApp.devModeOn() || BBConst.LOGIN_WITHOUT_CORRECT_PASSWORD) {
            // Show application version name & code
            String version = Utils.getVersion(this);
            if (BBConst.LOGIN_WITHOUT_CORRECT_PASSWORD) {
                version += " (Unsafe)";
            }
            */
            String version = Utils.getVersion(this);
            ((TextView)findViewById(R.id.tvVersion)).setText(version);
//        }

        initUI();
    }

    protected void showActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

//        LayoutInflater inflator = LayoutInflater.from(this);
//        View v = inflator.inflate(R.layout.view_pfactionbar_title, null);
//        actionBar.setCustomView(v);


        actionBar.show();
    }

    protected void showProgress(String title, boolean cancelable) {
        mProgressDlg = Utils.openNewDialog(this, title, cancelable, false);

        if ( cancelable ) {
            mProgressDlg.setOnCancelListener(mDialogCancelListener);
        }
    }

    protected void dismissDialog() {
        try {
            if (mProgressDlg != null && mProgressDlg.isShowing())
                mProgressDlg.dismiss();
        } catch (Exception e) {}
    }

    protected void showToast(String message) {
        SuperToast superToast = new SuperToast(this);
        superToast.setAnimations(SuperToast.Animations.FADE);
        superToast.setDuration(SuperToast.Duration.MEDIUM);
        superToast.setBackground(SuperToast.Background.GRAY);
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setText(message);
        superToast.show();
    }



    private DialogInterface.OnCancelListener mDialogCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            // Cancel network transfer

        }
    };

    /************************* Abstract Interface ***********************/
    protected abstract void initUI();
    public abstract boolean supportOffline();
}
