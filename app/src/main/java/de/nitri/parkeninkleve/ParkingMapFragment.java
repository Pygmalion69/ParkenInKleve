package de.nitri.parkeninkleve;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ParkingMapFragment extends Fragment {


    private MapView mMapView;
    private GoogleMap googleMap;
    private Context mContext;

    private DatabaseHelper mDatabaseHelper;
    private List<ParkingModel> mParkings = new ArrayList<>();

    LatLng kleve = new LatLng(51.787, 6.1355);
    CameraPosition cameraPosition = new CameraPosition.Builder().target(kleve).zoom(14).build();
    private BitmapDescriptor parkingMapIcon;


    public ParkingMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_parking_map, container, false);
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        parkingMapIcon = BitmapDescriptorFactory.fromResource(R.drawable.transport_parking);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                boolean coarseGranted = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                boolean fineGranted = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
                if (coarseGranted || fineGranted) {
                    googleMap.setMyLocationEnabled(true);
                }

                // For dropping a marker at a point on the Map

                // googleMap.addMarker(new MarkerOptions().position(kleve).title("Marker Title").snippet("Marker Description"));

                // For zooming automatically to the location of the marker
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                updateParkings();
            }
        });

        mDatabaseHelper = DatabaseHelper.getInstance(mContext);

        return rootView;

    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_recenter:
                if (googleMap != null)
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void updateParkings() {
        mParkings.clear();
        mParkings.addAll(mDatabaseHelper.getParkings());
        for (ParkingModel parking : mParkings) {
            LatLng parkingPosition = new LatLng(parking.getLat(), parking.getLon());
            String free;
            if (parking.getFrei() < 0) free = getString(R.string.unk);
            else free = Integer.toString(parking.getFrei());
            googleMap.addMarker(new MarkerOptions().position(parkingPosition).icon(parkingMapIcon).anchor(0.5f, 0.5f).title(parking.getParkplatz()).
                    snippet(getString(R.string.free) + ": " + free));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (googleMap != null) {
            updateParkings();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
}
