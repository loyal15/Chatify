package vsppsgv.chatify.im.webapi.chatify;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;

import vsppsgv.chatify.im.webapi.CIBaseWebAPI;
import vsppsgv.chatify.im.webapi.OnAPICompletedListener;

/**
 * Created by administrator on 8/23/15.
 */
public class CILoginSignupAPI extends CIBaseWebAPI {

    AsyncHttpClient httpClient = new AsyncHttpClient();

    public CILoginSignupAPI(Context context, boolean synchronous) {
        super(context, synchronous);
    }

    public void registerUser(String fullName, String phone, OnAPICompletedListener listener) {

        showProgress();

        RequestParams params = new RequestParams();
        params.put("name", fullName);
        params.put("phone", phone);
        params.put("latitude", "0.0");
        params.put("longitude", "0.0");

        httpClient.post(mContext, "http://ec2-54-148-128-104.us-west-2.compute.amazonaws.com/users/register/", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String response = new String(bytes);

                if (response != null) {

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                String error = new String(bytes);

                if (error != null) {

                }
            }
        });

    }
}
