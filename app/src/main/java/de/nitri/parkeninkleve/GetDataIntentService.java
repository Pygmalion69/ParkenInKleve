package de.nitri.parkeninkleve;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GetDataIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_DOWNLOAD = "de.nitri.parkeninkleve.action.DOWNLOAD";

    public static final String DOWNLOAD_READY = "de.nitri.parkeninkleve.DOWNLOAD_READY";

    // TODO: Rename parameters
    private static final String EXTRA_URL = "de.nitri.parkeninkleve.extra.URL";
    private static Context mContext;

    public GetDataIntentService() {
        super("GetDataIntentService");
    }

    private final String TAG = getClass().getSimpleName();

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionDownload(Context context, String url) {
        Intent intent = new Intent(context, GetDataIntentService.class);
        intent.setAction(ACTION_DOWNLOAD);
        intent.putExtra(EXTRA_URL, url);
        mContext = context;
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DOWNLOAD.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionDownload(url);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDownload(String url) {

        Gson gson = new GsonBuilder()
                .setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ApiEndpointInterface apiService =
                retrofit.create(ApiEndpointInterface.class);

        Call<List<ParkingModel>> call = apiService.getData();
        call.enqueue(new Callback<List<ParkingModel>>() {
            @Override
            public void onResponse(Call<List<ParkingModel>> call, Response<List<ParkingModel>> response) {
                if (response.body() != null) {
                    DatabaseHelper databaseHelper = DatabaseHelper.getInstance(mContext);
                    databaseHelper.updateData(response.body());
                    Intent intent = new Intent(DOWNLOAD_READY);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onFailure(Call<List<ParkingModel>> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }

    public interface ApiEndpointInterface {

        @GET("parkleitsystem")
        Call<List<ParkingModel>> getData();
    }

}
