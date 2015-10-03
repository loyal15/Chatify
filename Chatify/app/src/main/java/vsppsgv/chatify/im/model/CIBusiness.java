package vsppsgv.chatify.im.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by administrator on 9/23/15.
 */
public class CIBusiness {

    public final static int TYPE_BUSINESS_AUTOMOTIVE = 1;
    public final static int TYPE_BUSINESS_BANK = 2;
    public final static int TYPE_BUSINESS_BED = 3;
    public final static int TYPE_BUSINESS_BUSINESS = 4;
    public final static int TYPE_BUSINESS_CUPCAKE = 5;
    public final static int TYPE_BUSINESS_DINNER = 6;
    public final static int TYPE_BUSINESS_ENTERTAINMENT = 7;
    public final static int TYPE_BUSINESS_EVENT = 8;
    public final static int TYPE_BUSINESS_GOVERNMENT = 9;
    public final static int TYPE_BUSINESS_HEALTH = 10;
    public final static int TYPE_BUSINESS_HOME_SERVICE = 11;
    public final static int TYPE_BUSINESS_HOTEL = 12;
    public final static int TYPE_BUSINESS_INTERNET = 13;
    public final static int TYPE_BUSINESS_LEGAL = 14;
    public final static int TYPE_BUSINESS_LOCAL_SERVICE = 15;
    public final static int TYPE_BUSINESS_MEDICAL = 16;
    public final static int TYPE_BUSINESS_OTHER = 17;
    public final static int TYPE_BUSINESS_PUBLIC_SERVICE = 18;
    public final static int TYPE_BUSINESS_REAL_ESTATE = 19;
    public final static int TYPE_BUSINESS_RESTAURANT = 20;
    public final static int TYPE_BUSINESS_SHOP = 21;
    public final static int TYPE_BUSINESS_TOURISM = 22;

    public double       latitude;
    public double       longitude;
    public String       businessId;
    public String       name;
    public String       placeId;
    public String       reference;
    public String       scope;
    public int          type;
    public String       vicinity;

    public CIBusiness(JSONObject object) {

        try {
            latitude = object.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            longitude = object.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            businessId = object.getString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            placeId = object.getString("place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            reference = object.getString("scope");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            scope = object.getString("scope");
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        type =

        try {
            vicinity = object.getString("vicinity");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
