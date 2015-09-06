package vsppsgv.chatify.im.webapi.chatify;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import vsppsgv.chatify.im.webapi.CIBaseWebAPI;
import vsppsgv.chatify.im.webapi.OnAPICompletedListener;

/**
 * Created by administrator on 8/23/15.
 */
public class CILoginSignupAPI extends CIBaseWebAPI {

    public CILoginSignupAPI(Context context, boolean synchronous) {
        super(context, synchronous);
    }

    public void registerUser(String fullName, String phone, OnAPICompletedListener listener) {

        showProgress();

        AsyncHttpClient httpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("name", fullName);
        params.put("phone", phone);
        params.put("latitude", 0);
        params.put("longitude", 0);

//        httpClient.post("http://ec2-54-148-128-104.us-west-2.compute.amazonaws.com/users/register/", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, Header[] headers, byte[] bytes) {
//
//            }
//
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//
//            }
//        }
    }
}
