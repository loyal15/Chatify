package vsppsgv.chatify.im.webapi.chatify;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

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

    public void registerUser(String fullName, String phone, final OnAPICompletedListener listener) {

        showProgress();

        RequestParams requestParams = new RequestParams();

        requestParams.put("name", fullName);
        requestParams.put("phone", phone);
        requestParams.put("latitude", "12");
        requestParams.put("longitude", "12");

        httpClient.post("http://ec2-54-148-128-104.us-west-2.compute.amazonaws.com/users/register/", requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                dismissProgress();

                String response = new String(bytes);

                if (response != null) {

                    try {
                        JSONObject resObject = new JSONObject(response);
                        if (resObject.getString("result").equals("success")) {
                            listener.onCompleted(resObject);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    listener.onFailed(null);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                dismissProgress();

                listener.onFailed(throwable);
            }
        });
    }
}
