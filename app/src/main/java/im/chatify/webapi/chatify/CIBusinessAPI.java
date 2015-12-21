package im.chatify.webapi.chatify;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import im.chatify.R;
import im.chatify.model.CICategory;
import im.chatify.webapi.CIBaseWebAPI;
import im.chatify.webapi.OnAPICompletedListener;

/**
 * Created by administrator on 8/23/15.
 */
public class CIBusinessAPI extends CIBaseWebAPI {

    public CIBusinessAPI(Context context, boolean synchronous) {
        super(context, synchronous);
    }

    public void getCategoryList(final OnAPICompletedListener listener, final boolean needPlaceHolder) {

        AsyncHttpClient httpClient = new AsyncHttpClient();

        httpClient.get("http://ec2-54-148-128-104.us-west-2.compute.amazonaws.com/settings/categories/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String response = new String(bytes);

                try {

                    JSONArray responseArray = new JSONArray(response);
                    ArrayList<CICategory> categoryList = new ArrayList<CICategory>();

                    if (needPlaceHolder == true) {
                        CICategory placeholderCategory = new CICategory(mContext);
                        placeholderCategory.categoryName = mContext.getResources().getString(R.string.tag_business_category);
                        categoryList.add(placeholderCategory);
                    }

                    for (int j = 0 ; j < responseArray.length(); j ++) {
                        JSONObject categoryObject = responseArray.getJSONObject(j);
                        CICategory category = new CICategory(categoryObject);
                        categoryList.add(category);
                    }

                    if (listener != null)
                        listener.onCompleted(categoryList);
                    else
                        listener.onFailed(null);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                listener.onFailed(throwable);
            }
        });
    }
}
