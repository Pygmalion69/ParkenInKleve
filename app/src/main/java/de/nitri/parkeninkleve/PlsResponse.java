package de.nitri.parkeninkleve;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helfrich on 8/28/16.
 */
public class PlsResponse {

    private long stand;
    private List<ParkingModel> daten = new ArrayList<>();

    long getStand() {
        return stand;
    }

    public void setStand(long stand) {
        this.stand = stand;
    }

    public List<ParkingModel> getDaten() {
        return daten;
    }

    public void setDaten(List<ParkingModel> daten) {
        this.daten = daten;
    }


}
