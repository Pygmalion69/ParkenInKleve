package de.nitri.parkeninkleve;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.nitri.parkeninkleve.TableColumns.STATUS;
import static de.nitri.parkeninkleve.TableColumns._ID;
import static de.nitri.parkeninkleve.TableColumns.PARKING;
import static de.nitri.parkeninkleve.TableColumns.TOTAL;
import static de.nitri.parkeninkleve.TableColumns.FREE;
import static de.nitri.parkeninkleve.TableColumns.STATUS_DATE_TIME;
import static de.nitri.parkeninkleve.TableColumns.LAT;
import static de.nitri.parkeninkleve.TableColumns.LON;

/**
 * Created by helfrich on 06/07/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    private static final String DATABASE_NAME = "parking.db";
    protected static final String TABLE_NAME = "data";

    private static final int DATABASE_VERSION = 1;

    public static synchronized DatabaseHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * make call to static method "getInstance()" instead.
     */
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PARKING + " TEXT, " + STATUS + " TEXT, " + TOTAL + " INTEGER, " + FREE + " INTEGER, " + STATUS_DATE_TIME + " INTEGER, " +
                LAT + " REAL, " + LON + " REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public synchronized void clearData() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }

    public synchronized void updateData(long timestamp, List<ParkingModel> parkings) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        for (ParkingModel parking : parkings) {
            ContentValues row = new ContentValues();
            row.put(PARKING, parking.getParkplatz());
            row.put(STATUS, parking.getStatus());
            row.put(TOTAL, parking.getGesamt());
            row.put(FREE, parking.getFrei());
            row.put(STATUS_DATE_TIME, timestamp);
            row.put(LAT, parking.getLat());
            row.put(LON, parking.getLon());
            db.insert(TABLE_NAME, null, row);

        }
        db.close();
    }

    public synchronized List<ParkingModel> getParkings() {
        // Do not bind a cursor as we need to update our local data set with distance.
        SQLiteDatabase db = getWritableDatabase();
        List<ParkingModel> parkings = new ArrayList<>();
        Cursor cursor = db.query(true, TABLE_NAME, new String[]{"*"}, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            ParkingModel parking = new ParkingModel();
            parking.setParkplatz(cursor.getString(cursor.getColumnIndex(PARKING)));
            parking.setStatus(cursor.getString(cursor.getColumnIndex(STATUS)));
            parking.setGesamt(cursor.getInt(cursor.getColumnIndex(TOTAL)));
            parking.setFrei(cursor.getInt(cursor.getColumnIndex(FREE)));
            //TODO:
            long dateTime = cursor.getLong(cursor.getColumnIndex(STATUS_DATE_TIME));
            parking.setStand(new Date(cursor.getLong(cursor.getColumnIndex(STATUS_DATE_TIME))));
            parking.setLat(cursor.getDouble(cursor.getColumnIndex(LAT)));
            parking.setLon(cursor.getDouble(cursor.getColumnIndex(LON)));
            parkings.add(parking);
        }
        cursor.close();
        return  parkings;

    }
}