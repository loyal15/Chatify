package im.chatify.service.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.gpit.android.util.Utils;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import im.chatify.common.memorize.MemorizingTrustManager;
import im.chatify.model.CIChatContact;
import im.chatify.model.CIConst;


/**
 * Created by Furuha on 27.12.2014.
 */
public class CISmackConnection implements ConnectionListener {

    public static enum ConnectionState {
        CONNECTED, CONNECTING, RECONNECTING, DISCONNECTED;
    }

    private static final String TAG = "SMACK";
    private final Context mApplicationContext;
    private final String mPassword;
    private final String mUsername;
    private final String mServiceName;

    private XMPPTCPConnection mConnection;
    private ArrayList<String> mRoster;
    private BroadcastReceiver mReceiver;
    private Context mContext;
    private ProviderManager mProviderManager = new ProviderManager();

    private static CISmackConnection instance;

    public static CISmackConnection getInstance(Context context) {
        if (instance == null) {
            instance = new CISmackConnection(context);
        }

        return instance;
    }

    public CISmackConnection(Context pContext) {
        Log.i(TAG, "ChatConnection()");

        mContext = pContext;

        mApplicationContext = pContext.getApplicationContext();
        mPassword = PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString("xmpp_password", null);
        String jid = PreferenceManager.getDefaultSharedPreferences(mApplicationContext).getString("xmpp_jid", null);
        mServiceName = jid.split("@")[1];
        mUsername = jid.split("@")[0];

    }

    public void connect() throws IOException, XMPPException, SmackException {
        Log.i(TAG, "connect()");

        SmackConfiguration.setDefaultPacketReplyTimeout(CIConst.TIMEOUT_CONNECTION_SMACK);

        SSLContext sc = null;
        MemorizingTrustManager mtm = new MemorizingTrustManager(mContext, null);

        try {
            sc = SSLContext.getInstance("TLS");

            try {
                sc.init(null, new X509TrustManager[]{mtm}, new java.security.SecureRandom());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        String resourceName = "Android" + "-" + Utils.getOSVersion() + "-" + Utils.getDeviceName(mContext) + "-" + Utils.getAppVersionCode(mContext);

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .allowEmptyOrNullUsernames()
                .setServiceName(mServiceName)
                .setResource(resourceName)
                .setHost("52.28.128.116")
                .setPort(443)
                .setConnectTimeout(CIConst.TIMEOUT_CONNECTION_SMACK)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setLegacySessionDisabled(true)
                .setDebuggerEnabled(true)
                .build();




        mConnection = new XMPPTCPConnection(config);
        mConnection.setUseStreamManagement(true);

        mConnection.addPacketInterceptor(new PacketListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

            }
        }, new PacketFilter() {
            @Override
            public boolean accept(Stanza packet) {
                return true;
            }
        });

        try {

            mConnection.connect();

            registerChatifyUser();

//            mConnection.login("4915735983515", "D7269585C5758650B519D75C317848B1");

//            mConnection.sendPacket();

//            Presence response = new Presence(Presence.Type.subscribe);
//            response.setTo("4915735983514@chatify.im");
//            mConnection.sendPacket(response);

//            roster.setRosterLoadedAtLogin(true);
            // jid: String, user: String, groups: String[]
//            roster.createEntry("4915735983513", "Anna", null);
//

//            Roster roster = Roster.getInstanceFor(mConnection);
//            Presence presence = new Presence(Presence.Type.subscribe);
//            presence.setTo("4915735983513@chatify.im");
//            mConnection.sendPacket(presence);

            Roster roster = Roster.getInstanceFor(mConnection);

            roster.setSubscriptionMode(Roster.SubscriptionMode.accept_all);
            roster.createEntry("358414755120@chatify.im", "358414755120", null);
//
//            if (!roster.isLoaded())
//                roster.reloadAndWait();

            Collection<RosterEntry> entries = roster.getEntries();

            if (entries != null) {

            }

            /*
            ChatManager chatmanager = ChatManager.getInstanceFor(mConnection);
            Chat newChat = chatmanager.createChat("alice@chatify.im");

            try {
                newChat.sendMessage("Howdy!");
            }
            catch (SmackException.NotConnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            */


        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    public void login() {

        try {
            mConnection.login(mUsername, mPassword);
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        Log.i(TAG, "disconnect()");
        if(mConnection != null){
            mConnection.disconnect();
        }

        mConnection = null;
        if(mReceiver != null){
            mApplicationContext.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    public void registerChatifyUser() {

        Map<String,String> attributes = new HashMap<String,String>();
        attributes.put("code", "1234");

        AccountManager accManager = AccountManager.getInstance(mConnection);

        try {
            accManager.createAccount(CIIQStanzaManager.USERNAME, CIIQStanzaManager.PASSWORD, attributes);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

        Stanza getStanza = CIIQStanzaManager.createIQStanzaForRegister(mConnection.getServiceName(), mUsername, mPassword, IQ.Type.get);

        try {

            PacketCollector collector = mConnection.createPacketCollectorAndSend(null, getStanza);

            IQ response= collector.nextResult(CIConst.TIMEOUT_CONNECTION_SMACK);

            collector.cancel();

            if (response == null) {

            }

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<CIChatContact> getContactList() {

        ArrayList<CIChatContact> usersList=new ArrayList<CIChatContact>();

//        Presence presence = new Presence(Presence.Type.subscribe);

        //
//            mConnection.sendPacket(presence);
        Roster roster = Roster.getInstanceFor(mConnection);
        Collection<RosterEntry> entries = roster.getEntries();

        for (RosterEntry entry : entries) {

            CIChatContact contact = new CIChatContact();

            Presence entryPresence = roster.getPresence(entry.getUser());
            Presence.Type type = entryPresence.getType();

            contact.name = entry.getName().toString();
            contact.status = type.toString();

            usersList.add(contact);
        }

        return usersList;
    }

    @Override
    public void connected(XMPPConnection connection) {
        CISmackService.sConnectionState = ConnectionState.CONNECTED;
        Log.i(TAG, "connected()");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed) {
        CISmackService.sConnectionState = ConnectionState.CONNECTED;
        Log.i(TAG, "authenticated()");
    }

    @Override
    public void connectionClosed() {
        CISmackService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.i(TAG, "connectionClosed()");
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        CISmackService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.i(TAG, "connectionClosedOnError()");
    }

    @Override
    public void reconnectingIn(int seconds) {
        CISmackService.sConnectionState = ConnectionState.RECONNECTING;
        Log.i(TAG, "reconnectingIn()");
    }

    @Override
    public void reconnectionSuccessful() {
        CISmackService.sConnectionState = ConnectionState.CONNECTED;
        Log.i(TAG, "reconnectionSuccessful()");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        CISmackService.sConnectionState = ConnectionState.DISCONNECTED;
        Log.i(TAG, "reconnectionFailed()");
    }
}
