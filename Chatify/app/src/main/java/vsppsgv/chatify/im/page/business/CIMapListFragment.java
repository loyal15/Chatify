package vsppsgv.chatify.im.page.business;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;

import vsppsgv.chatify.im.R;

/**
 * Created by administrator on 8/23/15.
 */
public class CIMapListFragment extends Fragment {

    private MapView         mMVBusiness;
    private ListView        mLVBusiness;

    private GoogleMap       mMap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_cimaplist, container, false);
        initUI(rootView, savedInstanceState);

        return rootView;
    }

    private void initUI(ViewGroup view, Bundle savedInstanceState) {

        mMVBusiness = (MapView) view.findViewById(R.id.mvBusiness);
        mMVBusiness.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        mMap = mMVBusiness.getMap();
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setMyLocationEnabled(true);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        mMap.animateCamera(cameraUpdate);

        mLVBusiness = (ListView)view.findViewById(R.id.lvBusiness);
    }

    @Override
    public void onResume() {
        mMVBusiness.onResume();
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
}
