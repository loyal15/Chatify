package im.chatify;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;

import im.chatify.common.ui.CICommonActivity;
import im.chatify.model.CIConst;
import im.chatify.page.home.CIMainActivity;
import im.chatify.page.signup.CILoginActivity;
import im.chatify.xabber.android.data.account.AccountItem;
import im.chatify.xabber.android.data.account.AccountManager;
import im.chatify.xabber.android.data.account.OnAccountChangedListener;
import im.chatify.xabber.android.data.connection.ConnectionItem;
import im.chatify.xabber.android.data.connection.OnAuthorizedListener;


public class CISplashActivity extends CICommonActivity implements OnAccountChangedListener, OnAuthorizedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cisplash);
    }

    @Override
    public void onResume() {
        super.onResume();

        CIApp.getInstance().addUIListener(OnAccountChangedListener.class,
                this);
        CIApp.getInstance().addManager(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        CIApp.getInstance().removeUIListener(OnAccountChangedListener.class, this);
    }

    @Override
    protected void initUI() {

        ArrayList<String> accounts = new ArrayList<String>(AccountManager.getInstance().getAllAccounts());

        if (accounts.size() > 0) {

            AccountItem accountItem = AccountManager.getInstance().getAccount(accounts.get(0));

            String password = accountItem.getConnectionSettings().getPassword();

            if (password != null && password.equals(CIConst.DFAULT_PASSWORD)) {
                AccountManager.getInstance().removeAccount(accountItem.getAccount());
                showLoginScreen();
            } else {
                showMainScreen();
            }
        } else {
//            if (!CIApp.getInstance().isClosing())
//                showLoginScreen();
        }
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
        finishWithoutAnimation();
    }

    private void showMainScreen() {

        Intent intent = new Intent(this, CIMainActivity.class);
        startActivity(intent);
        finishWithoutAnimation();
    }

    @Override
    public void onAccountsChanged(Collection<String> accounts) {

        if (accounts == null || accounts.size() == 0)
            showLoginScreen();
        else
            showMainScreen();
    }

    @Override
    public void onAuthorized(ConnectionItem connection) {

        Log.e("CISplash Activity authorized = ", "authorized");
    }
}
