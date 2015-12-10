package im.chatify.page.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import im.chatify.CIApp;
import im.chatify.R;
import im.chatify.common.CIGPSClient;
import im.chatify.model.CIBusiness;
import im.chatify.page.chat.CIChatActivity;
import im.chatify.xabber.android.data.NetworkException;
import im.chatify.xabber.android.data.account.AccountManager;
import im.chatify.xabber.android.data.message.MessageManager;
import im.chatify.xabber.android.data.roster.PresenceManager;
import im.chatify.xabber.android.data.roster.RosterManager;

/**
 * Created by administrator on 8/23/15.
 */
public class CIBusinessListFragment extends Fragment {

    private MapView                     mMVBusiness;
    private ListView                    mLVBusiness;
    private CIBusinessListAdapter       mBusinessAdapter;
    private ArrayList<CIBusiness>       mBusinessList = new ArrayList<CIBusiness>();

    private GoogleMap                   mMap;
    private BroadcastReceiver           mBroadcastReceiver;
    private boolean                     mMapZoomed;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }

        initData();
    }

    public void onActivityCreated(Bundle savedBundle) {

        super.onActivityCreated(savedBundle);

        MapsInitializer.initialize(getActivity());
        createMapView(savedBundle);
        initMapView(savedBundle);
        addBusinessPinInMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_cimaplist, container, false);
        initUI(rootView, savedInstanceState);

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        CIGPSClient gpsClient = CIGPSClient.getInstance(getContext());
        gpsClient.disconnect();
    }

    @Override
    public void onStop() {
        super.onStop();

        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(getContext());
        mgr.unregisterReceiver(mBroadcastReceiver);
    }

    private void initData() {

        CIBusiness business1 = new CIBusiness();
        business1.name = "Coffee Day";
        business1.type = CIBusiness.TYPE_BUSINESS_SHOP;
        business1.latitude = 40.1;
        business1.longitude = -86.7;
        business1.username = "4915735983515@chatify.im";
        business1.location = "Upton Park, London";

        mBusinessList.add(business1);

        CIBusiness business2 = new CIBusiness();
        business2.name = "Outlet Mall";
        business2.type = CIBusiness.TYPE_BUSINESS_SHOP;
        business2.latitude = 41.1;
        business2.longitude = -84.4;
        business2.username = "alice@chatify.im";
        business2.location = "Upton Park, London";

        mBusinessList.add(business2);

        CIBusiness business3 = new CIBusiness();
        business3.name = "Llford Computers";
        business3.type = CIBusiness.TYPE_BUSINESS_BUSINESS;
        business3.latitude = 40.1;
        business3.longitude = -82.3;
        business3.username = "4915735983514@chatify.im";
        business3.location = "Upton Park, London";
        mBusinessList.add(business3);

        mBroadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };

        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(getContext());
        mgr.registerReceiver(mBroadcastReceiver, new IntentFilter("KEY_UPDATE_BUSINESSLIST"));
    }

    private void initUI(ViewGroup view, Bundle savedInstanceState) {

        mMVBusiness = (MapView) view.findViewById(R.id.mvBusiness);

        mLVBusiness = (ListView)view.findViewById(R.id.lvBusiness);
        mBusinessAdapter = new CIBusinessListAdapter(getActivity(), R.layout.listitem_business, mBusinessList);
        mLVBusiness.setAdapter(mBusinessAdapter);
        mLVBusiness.setOnItemClickListener(mBusinessListClickListener);

    }

    private void initMapView(Bundle savedInstanceState) {

        mMVBusiness.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mMVBusiness.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        Location location = mMap.getMyLocation();
        LatLng myLocation = null;

        CIGPSClient.getInstance(getContext()).setLocationListener(mLocationChangedListener);

        if (location != null) {
            myLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,
                    7));
        } else {

            double latitude = CIGPSClient.getInstance(getContext()).getLatitude();
            double longitude = CIGPSClient.getInstance(getContext()).getLongitude();

            if (latitude != 0 && longitude != 0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 20));
            }
        }
    }

    public void createMapView(Bundle savedBundle) {
        if (mMVBusiness != null) {
            mMVBusiness.onCreate(savedBundle);
        }
    }

    private void addBusinessPinInMap() {

        mMap.clear();
        for (int i = 0; i < mBusinessList.size(); i ++) {

            CIBusiness business = mBusinessList.get(i);

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(business.latitude, business.longitude))
                    .title(business.name)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_businessdetail_location)));

        }
    }

    @Override
    public void onResume() {
        mMVBusiness.onResume();

        CIGPSClient gpsClient = CIGPSClient.getInstance(getContext());
        gpsClient.connect();

        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMVBusiness.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMVBusiness.onLowMemory();
    }

    private AdapterView.OnItemClickListener mBusinessListClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            CIBusiness business = mBusinessList.get(i);

            ArrayList<String> accounts = new ArrayList<String>(AccountManager.getInstance().getAccounts());
            String account = accounts.get(0);

            ArrayList<String> groups = new ArrayList<>();

            try {
                RosterManager.getInstance().createContact(account, business.username,
                        business.name, groups);
                PresenceManager.getInstance().requestSubscription(account, business.username);
            } catch (NetworkException e) {
                CIApp.getInstance().onError(e);
            }

            MessageManager.getInstance().openChat(account, business.username);

            Intent intent = new Intent(getActivity(), CIChatActivity.class);
            intent.putExtra(CIChatActivity.KEY_USER, business.username);
            intent.putExtra(CIChatActivity.KEY_ACCOUNT, account);
            getActivity().startActivity(intent);
            /*
            CIBusiness business = mBusinessList.get(i);
            Intent intent = new Intent(getActivity(), CIBusinessDetailActivity.class);
            intent.putExtra(CIBusinessDetailActivity.KEY_BUSINESS, business);
            getActivity().startActivity(intent);
            */

        }
    };

    private CIGPSClient.LocationChangedListener mLocationChangedListener = new CIGPSClient.LocationChangedListener() {
        @Override
        public void onLocationChanged(Location location) {

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            if (latitude != 0 && longitude != 0 && mMapZoomed == false) {
                mMapZoomed = true;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 20));
            }
        }
    };
}
