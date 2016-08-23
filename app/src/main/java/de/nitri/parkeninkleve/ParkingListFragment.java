package de.nitri.parkeninkleve;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A placeholder fragment containing a simple view.
 */
public class ParkingListFragment extends Fragment implements AdapterView.OnItemClickListener{

    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    private List<ParkingModel> mParkings = new ArrayList<>();
    private ParkingAdapter adapter;
    private Callback mCallback;
    private ParkingModel mSelectedParking;

    public ParkingListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_parking_list, container, false);
        ListView lvParkings = (ListView) rootView.findViewById(R.id.listViewParkings);

        mDatabaseHelper = DatabaseHelper.getInstance(mContext);

        adapter = new ParkingAdapter(mContext, R.layout.parking_list_item, mParkings);

        if (mCallback.getLastLocation() != null) {
            updateDistances(mCallback.getLastLocation());
        }

        lvParkings.setAdapter(adapter);
        lvParkings.setOnItemClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateParkings();
    }

    protected void updateParkings() {
        mParkings.clear();
        mParkings.addAll(mDatabaseHelper.getParkings());
        if (mCallback.getLastLocation() != null) {
            updateDistances(mCallback.getLastLocation());
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (Callback) context;
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        mContext = null;
    }

    public void updateDistances(Location lastLocation) {
        for (ParkingModel parking : mParkings) {
            parking.setDistance((float) distance(lastLocation.getLatitude(), lastLocation.getLongitude(), parking.getLat(), parking.getLon()));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ParkingModel selectedParking = mParkings.get(i);
        mCallback.showParkingDialog(selectedParking);
    }

    private class ParkingAdapter extends ArrayAdapter<ParkingModel> {

        private final List<ParkingModel> parkings;
        private final int resource;
        private final Context context;

        public ParkingAdapter(Context context, int resource, List<ParkingModel> parkings) {
            super(context, resource, parkings);
            this.parkings = parkings;
            this.resource = resource;
            this.context = context;
        }

        @SuppressLint({"InflateParams", "SetTextI18n"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(resource, null);
            }

            TextView tvParking = (TextView) v.findViewById(R.id.textViewParking);
            TextView tvFree = (TextView) v.findViewById(R.id.textViewFree);
            TextView tvTotal = (TextView) v.findViewById(R.id.textViewTotal);
            TextView tvDistance = (TextView) v.findViewById(R.id.textViewDistance);

            ParkingModel parking = parkings.get(position);

            tvParking.setText(parking.getParkplatz());

            tvFree.setTextColor(ContextCompat.getColor(context, android.R.color.tertiary_text_light));
            String free = getString(R.string.unk);
            if (parking.getFrei() >= 0)
                free = Integer.toString(parking.getFrei());
            if (parking.getFrei() >= 10)
                tvFree.setTextColor(ContextCompat.getColor(context, R.color.green));
            if (parking.getFrei() < 10 && parking.getFrei() > 0)
                tvFree.setTextColor(ContextCompat.getColor(context, R.color.orange));
            if (parking.getFrei() == 0)
                tvFree.setTextColor(ContextCompat.getColor(context, R.color.red));
            tvFree.setText(free);
            tvTotal.setText("(" + parking.getGesamt() + ")");
            // Log.d("DISTANCE", Float.toString(parking.getDistance()));
            if (parking.getDistance() > 0)
                tvDistance.setText( String.format(Locale.ROOT, "%.1f km", parking.getDistance()));
            return v;
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        if (lat1 == lat2 && lon1 == lon2)
            return 0;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    interface Callback {
        Location getLastLocation();
        void showParkingDialog(ParkingModel parking);
    }

}
