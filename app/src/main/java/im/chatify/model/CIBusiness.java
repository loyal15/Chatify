package im.chatify.model;

import android.location.Location;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import im.chatify.R;

/**
 * Created by administrator on 9/23/15.
 */
public class CIBusiness implements Serializable {

    public final static int TYPE_BUSINESS_AUTOMOTIVE = 1;
    public final static int TYPE_BUSINESS_BANK = 2;
    public final static int TYPE_BUSINESS_BED = 3;
    public final static int TYPE_BUSINESS_BUSINESS = 26;
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
    public String       location;
    public String       businessId;
    public String       name;
    public String       placeId;
    public String       reference;
    public String       scope;
    public int          type;
    public String       vicinity;
    public String       username;

    public CIBusiness() {

    }

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

    public static int getBusinessDrawableIdFromType(int type) {

        int resourceId = R.mipmap.ic_business_other;

        switch (type) {
            case TYPE_BUSINESS_AUTOMOTIVE:
                resourceId = R.mipmap.ic_business_automotive;
                break;
            case TYPE_BUSINESS_BANK:
                resourceId = R.mipmap.ic_business_bank;
                break;
            case TYPE_BUSINESS_BED:
                resourceId = R.mipmap.ic_business_bed;
                break;
            case TYPE_BUSINESS_BUSINESS:
                resourceId = R.mipmap.ic_business_business;
                break;
            case TYPE_BUSINESS_CUPCAKE:
                resourceId = R.mipmap.ic_business_cupcake;
                break;
            case TYPE_BUSINESS_DINNER:
                resourceId = R.mipmap.ic_business_dinner;
                break;
            case TYPE_BUSINESS_ENTERTAINMENT:
                resourceId = R.mipmap.ic_business_entertainment;
                break;
            case TYPE_BUSINESS_EVENT:
                resourceId = R.mipmap.ic_business_event;
                break;
            case TYPE_BUSINESS_GOVERNMENT:
                resourceId = R.mipmap.ic_business_government;
                break;
            case TYPE_BUSINESS_HEALTH:
                resourceId = R.mipmap.ic_business_health;
                break;
            case TYPE_BUSINESS_HOME_SERVICE:
                resourceId = R.mipmap.ic_business_home_services;
                break;
            case TYPE_BUSINESS_HOTEL:
                resourceId = R.mipmap.ic_business_hotel;
                break;
            case TYPE_BUSINESS_INTERNET:
                resourceId = R.mipmap.ic_business_internet;
                break;
            case TYPE_BUSINESS_LEGAL:
                resourceId = R.mipmap.ic_business_legal;
                break;
            case TYPE_BUSINESS_LOCAL_SERVICE:
                resourceId = R.mipmap.ic_business_localservices;
                break;
            case TYPE_BUSINESS_MEDICAL:
                resourceId = R.mipmap.ic_business_medical;
                break;
            case TYPE_BUSINESS_OTHER:
                resourceId = R.mipmap.ic_business_other;
                break;
            case TYPE_BUSINESS_PUBLIC_SERVICE:
                resourceId = R.mipmap.ic_business_public_services;
                break;
            case TYPE_BUSINESS_REAL_ESTATE:
                resourceId = R.mipmap.ic_business_real_estate;
                break;
            case TYPE_BUSINESS_RESTAURANT:
                resourceId = R.mipmap.ic_business_restaurant;
                break;
            case TYPE_BUSINESS_SHOP:
                resourceId = R.mipmap.ic_business_shop;
                break;
            case TYPE_BUSINESS_TOURISM:
                resourceId = R.mipmap.ic_business_tourism;
                break;
            default:
                break;
        }

        return resourceId;
    }


    public int getBusinessDrawableIdFromType() {

        int resourceId = R.mipmap.ic_business_other;

        switch (type) {
            case TYPE_BUSINESS_AUTOMOTIVE:
                resourceId = R.mipmap.ic_business_automotive;
                break;
            case TYPE_BUSINESS_BANK:
                resourceId = R.mipmap.ic_business_bank;
                break;
            case TYPE_BUSINESS_BED:
                resourceId = R.mipmap.ic_business_bed;
                break;
            case TYPE_BUSINESS_BUSINESS:
                resourceId = R.mipmap.ic_business_business;
                break;
            case TYPE_BUSINESS_CUPCAKE:
                resourceId = R.mipmap.ic_business_cupcake;
                break;
            case TYPE_BUSINESS_DINNER:
                resourceId = R.mipmap.ic_business_dinner;
                break;
            case TYPE_BUSINESS_ENTERTAINMENT:
                resourceId = R.mipmap.ic_business_entertainment;
                break;
            case TYPE_BUSINESS_EVENT:
                resourceId = R.mipmap.ic_business_event;
                break;
            case TYPE_BUSINESS_GOVERNMENT:
                resourceId = R.mipmap.ic_business_government;
                break;
            case TYPE_BUSINESS_HEALTH:
                resourceId = R.mipmap.ic_business_health;
                break;
            case TYPE_BUSINESS_HOME_SERVICE:
                resourceId = R.mipmap.ic_business_home_services;
                break;
            case TYPE_BUSINESS_HOTEL:
                resourceId = R.mipmap.ic_business_hotel;
                break;
            case TYPE_BUSINESS_INTERNET:
                resourceId = R.mipmap.ic_business_internet;
                break;
            case TYPE_BUSINESS_LEGAL:
                resourceId = R.mipmap.ic_business_legal;
                break;
            case TYPE_BUSINESS_LOCAL_SERVICE:
                resourceId = R.mipmap.ic_business_localservices;
                break;
            case TYPE_BUSINESS_MEDICAL:
                resourceId = R.mipmap.ic_business_medical;
                break;
            case TYPE_BUSINESS_OTHER:
                resourceId = R.mipmap.ic_business_other;
                break;
            case TYPE_BUSINESS_PUBLIC_SERVICE:
                resourceId = R.mipmap.ic_business_public_services;
                break;
            case TYPE_BUSINESS_REAL_ESTATE:
                resourceId = R.mipmap.ic_business_real_estate;
                break;
            case TYPE_BUSINESS_RESTAURANT:
                resourceId = R.mipmap.ic_business_restaurant;
                break;
            case TYPE_BUSINESS_SHOP:
                resourceId = R.mipmap.ic_business_shop;
                break;
            case TYPE_BUSINESS_TOURISM:
                resourceId = R.mipmap.ic_business_tourism;
                break;
            default:
                break;
        }

        return resourceId;
    }



    public float getDistanceFromLocation(double latA, double longA) {

        Location locationA = new Location("point A");
        locationA.setLatitude(latA);
        locationA.setLongitude(longA);

        Location locationB = new Location("point B");

        locationB.setLatitude(latitude);
        locationB.setLongitude(longitude);

        float distance = locationA.distanceTo(locationB);

        if (distance > 0)
            distance = distance / 1000;

        return distance;
    }
}
