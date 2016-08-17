package de.nitri.parkeninkleve;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class ParkingModel {

    private Integer frei;
    private Integer gesamt;
    private Double lat;
    private Double lon;
    private String parkplatz;
    private Date stand;
    private String status;

    private float distance;

    /**
     * @return The frei
     */
    public Integer getFrei() {
        return frei;
    }

    /**
     * @param frei The Frei
     */
    public void setFrei(Integer frei) {
        this.frei = frei;
    }

    /**
     * @return The gesamt
     */
    public Integer getGesamt() {
        return gesamt;
    }

    /**
     * @param gesamt The Gesamt
     */
    public void setGesamt(Integer gesamt) {
        this.gesamt = gesamt;
    }

    /**
     * @return The lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     * @param lat The Lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * @return The lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     * @param lon The Lon
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * @return The parkplatz
     */
    public String getParkplatz() {
        return parkplatz;
    }

    /**
     * @param parkplatz The Parkplatz
     */
    public void setParkplatz(String parkplatz) {
        this.parkplatz = parkplatz;
    }

    /**
     * @return The stand
     */
    public Date getStand() {
        return stand;
    }

    /**
     * @param stand The Stand
     */
    public void setStand(Date stand) {
        this.stand = stand;
    }

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The Status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

}

