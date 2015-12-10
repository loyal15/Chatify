package im.chatify.page.business;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.common.ui.CICommonActivity;
import im.chatify.model.CIBusiness;
import im.chatify.page.chat.CIChatActivity;
import im.chatify.xabber.android.data.NetworkException;
import im.chatify.xabber.android.data.account.AccountManager;
import im.chatify.xabber.android.data.message.MessageManager;
import im.chatify.xabber.android.data.roster.PresenceManager;
import im.chatify.xabber.android.data.roster.RosterManager;

public class CIBusinessDetailActivity extends CICommonActivity {

    public static final String KEY_BUSINESS = "KEY_BUSINESS";
    private CIBusiness                      mBusiness;
    private LinearLayout                    mLLChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cibusiness_detail);
    }

    @Override
    protected void initUI() {

        showBackArrow(true);

        mLLChat = (LinearLayout)findViewById(R.id.llChat);
        mLLChat.setOnClickListener(mLLChatClickListener);
    }

    @Override
    protected void initData() {

        Intent intent = getIntent();
        mBusiness = (CIBusiness)intent.getSerializableExtra(KEY_BUSINESS);

    }

    @Override
    public boolean supportOffline() {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cibusiness_detail, menu);
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

    private void startChatWithBusiness() {

        ArrayList<String> accounts = new ArrayList<String>(AccountManager.getInstance().getAccounts());
        String account = accounts.get(0);

        ArrayList<String> groups = new ArrayList<>();

        try {
            RosterManager.getInstance().createContact(account, mBusiness.username,
                    mBusiness.name, groups);
            PresenceManager.getInstance().requestSubscription(account, mBusiness.username);
        } catch (NetworkException e) {
            CIApp.getInstance().onError(e);
            return;
        }

        MessageManager.getInstance().openChat(account, mBusiness.username);

        Intent intent = new Intent(this, CIChatActivity.class);
        intent.putExtra(CIChatActivity.KEY_USER, mBusiness.username);
        intent.putExtra(CIChatActivity.KEY_ACCOUNT, account);
        startActivity(intent);
    }

    private View.OnClickListener mLLChatClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startChatWithBusiness();
        }
    };
}
