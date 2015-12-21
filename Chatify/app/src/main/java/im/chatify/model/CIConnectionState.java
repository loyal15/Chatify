package im.chatify.model;

import android.content.Context;

/**
 * Created by administrator on 11/17/15.
 */
public class CIConnectionState {


    public static final int CONNECTION_NOT_AUTHORIZED = 0;
    public static final int CONNECTION_AUTHORIZED = 1;

    private static CIConnectionState mInstance;


    public static CIConnectionState getInstance(Context context) {

        if (mInstance == null)
            mInstance = new CIConnectionState();

        return mInstance;
    }

    public int authorizedState;

}
