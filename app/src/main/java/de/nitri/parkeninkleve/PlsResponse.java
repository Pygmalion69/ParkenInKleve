package de.nitri.parkeninkleve;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helfrich on 8/28/16.
 */
public class PlsResponse {

    private Date stand;
    private List<ParkingModel> daten = new ArrayList<>();

    public List<ParkingModel> getDaten() {
        return daten;
    }

    public void setDaten(List<ParkingModel> daten) {
        this.daten = daten;
    }

    public Date getStand() {
        return stand;
    }

    public void setStand(Date stand) { this.stand = stand; }

}
