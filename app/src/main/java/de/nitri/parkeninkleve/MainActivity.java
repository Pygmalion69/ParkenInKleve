package de.nitri.parkeninkleve;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ParkingListFragment.Callback {

    private Intent mServiceIntent;

    protected static final String REST_URL = "http://services.nitri.de/parkleitsystem/api/";
    protected static long DOWNLOAD_INTERVAL = 180000;
    private DownloadReadyReceiver downloadReadyReceiver;
    private ParkingCollectionPagerAdapter mParkingCollectionPagerAdapter;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mParkingCollectionPagerAdapter =
                new ParkingCollectionPagerAdapter(
                        getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mParkingCollectionPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        downloadReadyReceiver = new DownloadReadyReceiver();
        //mServiceIntent = new Intent(this, GetDataIntentService.class);
        GetDataIntentService.startActionDownload(this, REST_URL);
        scheduleAlarm();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(downloadReadyReceiver, new IntentFilter(GetDataIntentService.DOWNLOAD_READY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(downloadReadyReceiver);
    }



    @Override
    public void onConnected(Bundle connectionHint) {
        boolean coarseGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean fineGranted = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (!coarseGranted && !fineGranted) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
           /* ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);*/
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            updateDistances();
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                mLastLocation = location;
                updateDistances();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, locationListener);

    }

    private void updateDistances() {
        if (mParkingCollectionPagerAdapter.getCurrentFragment() != null && mParkingCollectionPagerAdapter.getCurrentFragment() instanceof ParkingListFragment) {
            ParkingListFragment parkingListFragment = (ParkingListFragment) mParkingCollectionPagerAdapter.getCurrentFragment();
            parkingListFragment.updateDistances(mLastLocation);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            // other 'case' lines to check for other
            // permissions this app might request
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mGoogleApiClient.reconnect();

                } else {

                    //finish();
                }
                return;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), GetDataAlarmReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, GetDataAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis() + DOWNLOAD_INTERVAL; // first alarm
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                DOWNLOAD_INTERVAL, pIntent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public Location getLastLocation() {
        return mLastLocation;
    }

    @Override
    public void showParkingDialog(ParkingModel parking) {
        ParkingDialogFragment parkingDialogFragment = new ParkingDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ParkingDialogFragment.TITLE, parking.getParkplatz());
        //TODO:
        String free;
        if (parking.getFrei() < 0) free = getString(R.string.unk);
        else free = Integer.toString(parking.getFrei());
        DateFormat dateFormat;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dateFormat = new SimpleDateFormat(android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "MM/dd/yyyy hh:mm"), Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:", Locale.getDefault());
        }
        //dateFormat.setTimeZone(TimeZone.getTimeZone("Germany/Berlin"));
        String dateTimeString = dateFormat.format(parking.getStand());
        bundle.putString(ParkingDialogFragment.MESSAGE, getString(R.string.free) + ": " + free + "   (" + dateTimeString + ")");
        bundle.putDouble(ParkingDialogFragment.LON, parking.getLon());
        bundle.putDouble(ParkingDialogFragment.LAT, parking.getLat());
        parkingDialogFragment.setArguments(bundle);
        parkingDialogFragment.show(getSupportFragmentManager(), "PARKING_DIALOG");
    }

    public static class GetDataAlarmReceiver extends BroadcastReceiver {
        public static final int REQUEST_CODE = 12345;
        //public static final String ACTION = "de.nitri.parkeninkleve.GetData";

        // Triggered by the Alarm periodically (starts the service to run task)
        @Override
        public void onReceive(Context context, Intent intent) {
            /*Intent i = new Intent(context, GetDataIntentService.class);
            i.putExtra("foo", "bar");
            context.startService(i);*/
            GetDataIntentService.startActionDownload(context, REST_URL);
        }
    }

    public class DownloadReadyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // instantiateItem simply returns a reference if the fragment already exists
            ParkingListFragment parkingListFragment = (ParkingListFragment) mParkingCollectionPagerAdapter.instantiateItem(viewPager, 0);
            if (parkingListFragment != null) parkingListFragment.updateParkings();
            ParkingMapFragment parkingMapFragment = (ParkingMapFragment) mParkingCollectionPagerAdapter.instantiateItem(viewPager, 1);
            if (parkingMapFragment != null) parkingMapFragment.updateParkings();
        }

    }

    // Since this is an object collection, use a FragmentStatePagerAdapter,
// and NOT a FragmentPagerAdapter.
    public class ParkingCollectionPagerAdapter extends FragmentStatePagerAdapter {
        ParkingCollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private Fragment mCurrentFragment;

        Fragment getCurrentFragment() {
            return mCurrentFragment;
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment;
            switch (i) {
                case 0:
                    fragment = new ParkingListFragment();
                    return fragment;
                case 1:
                    return new ParkingMapFragment();
                default:
                    return new Fragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.list);
                case 1:
                    return getString(R.string.map);
                default:
                    return "";
            }

        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            if (getCurrentFragment() != object) {
                mCurrentFragment = ((Fragment) object);
            }
            super.setPrimaryItem(container, position, object);
        }

    }
}
