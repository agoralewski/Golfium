package pl.goralewski.artur.golfium.model;

import java.util.Date;

/**
 * Created by Artur Góralewski on 12/05/2015.
 */
public class Coordinates {

    private Integer id;
    private Double latitude;
    private Double longitude;
    private Date time;
    private Integer holePlayId;

    public Coordinates(Integer id, Double latitude, Double longitude, Date time, Integer holePlayId) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.holePlayId = holePlayId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Integer getHolePlayId() {
        return holePlayId;
    }

    public void setHolePlayId(Integer holePlayId) {
        this.holePlayId = holePlayId;
    }
}
