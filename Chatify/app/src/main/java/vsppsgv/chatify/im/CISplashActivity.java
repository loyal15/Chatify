package vsppsgv.chatify.im;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.gpit.android.util.Utils;

import vsppsgv.chatify.im.common.ui.CICommonActivity;
import vsppsgv.chatify.im.page.signup.CILoginActivity;
import vsppsgv.chatify.im.service.xmpp.CISmackService;


public class CISplashActivity extends CICommonActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cisplash);

        String resourceName = "Android" + "-" + Utils.getOSVersion() + "-" + Utils.getDeviceName(this) + "-" + Utils.getAppVersionCode(this);

        if (resourceName != null) {

        }
    }

    @Override
    protected void initUI() {

        showMainScreen();
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean supportOffline() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cimain, menu);
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

    private void showLoginScreen() {

        Intent intent = new Intent(this, CILoginActivity.class);
        startActivity(intent);
    }

    private void showMainScreen() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.edit()
                .putString("xmpp_jid", "bastian@chatify.im")
                .putString("xmpp_password", "abcd1234")
                .commit();

        Intent intent = new Intent(this, CISmackService.class);
        this.startService(intent);
//        CIXmppChatAPI chatAPI = new CIXmppChatAPI(this, false);
//        chatAPI.connectToChatifyServer();

//        Intent intent = new Intent(this, CILoginActivity.class);
//        startActivity(intent);
    }
}
