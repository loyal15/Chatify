package im.chatify.xabber.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import im.chatify.CIApp;

public class ShutDownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        CIApp.getInstance().requestToClose();
    }
}
