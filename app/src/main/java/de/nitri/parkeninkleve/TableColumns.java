package de.nitri.parkeninkleve;

import android.provider.BaseColumns;

/**
 * Created by helfrich on 06/07/16.
 */
public interface TableColumns extends BaseColumns {

    static final String PARKING = "parking";
    static final String STATUS = "status";
    static final String STATUS_DATE_TIME = "status_date_time";
    static final String TOTAL = "total";
    static final String FREE = "free";
    static final String LAT = "lat";
    static final String LON = "lon";
}
