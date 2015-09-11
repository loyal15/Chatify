package vsppsgv.chatify.im.service.xmpp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import vsppsgv.chatify.im.common.memorize.MemorizingTrustManager;


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

        SmackConfiguration.setDefaultPacketReplyTimeout(10000);

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

        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(mUsername, mPassword)
                .setServiceName(mServiceName)
                .setHost("52.28.128.116")
                .setPort(443)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                .setLegacySessionDisabled(true)
                .build();

        mConnection = new XMPPTCPConnection(config);

        try {

            mConnection.connect();
            mConnection.login();

            Presence presence = new Presence(Presence.Type.unavailable);
            presence.setStatus("GOGOGO");
            mConnection.sendPacket(presence);

            ChatManager chatmanager = ChatManager.getInstanceFor(mConnection);


            Chat newChat = chatmanager.createChat("alice@chatify.im");

            try {
                newChat.sendMessage("Howdy!");
            }
            catch (SmackException.NotConnectedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
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
