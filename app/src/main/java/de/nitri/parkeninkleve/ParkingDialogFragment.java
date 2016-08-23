package de.nitri.parkeninkleve;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ParkingDialogFragment extends DialogFragment {

    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    private String title;
    private String message;
    private double lat;
    private double lon;

    public ParkingDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        title = bundle.getString(TITLE);
        message = bundle.getString(MESSAGE);
        lat = bundle.getDouble(LAT, 0);
        lon = bundle.getDouble(LON, 0);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message)
                .setPositiveButton(R.string.navigate, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + lat + "," + lon);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                        dismiss();
                    }
                })
        ;
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
