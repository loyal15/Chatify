package vsppsgv.chatify.im.webapi.chatify;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import vsppsgv.chatify.im.webapi.CIBaseWebAPI;
import vsppsgv.chatify.im.webapi.OnAPICompletedListener;

/**
 * Created by administrator on 8/23/15.
 */
public class CIBusinessAPI extends CIBaseWebAPI {

    private final static String BASE_URL = "https://maps.googleapis.com/maps/api/place/search/json?location=%f,%f&radius=5000&sensor=false&key=AIzaSyD2KjmW5Hd3kxDV_RHBasPasrDWsKZxe5o";

    public CIBusinessAPI(Context context, boolean synchronous) {
        super(context, synchronous);
    }

    public void getNearbyPublicBusiness(double latitude, double longitude, OnAPICompletedListener listener) {

        AsyncHttpClient httpClient = new AsyncHttpClient();

        httpClient.get(BASE_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String response = new String(bytes);

                try {

                    JSONObject responseObject = new JSONObject(response);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
}
