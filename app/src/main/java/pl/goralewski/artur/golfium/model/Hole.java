package pl.goralewski.artur.golfium.model;

/**
 * Created by Artur Goralewski on 14/05/2015.
 */
public class Hole implements TableRow{

    private Integer id;
    private Integer par;
    private Double latitude;
    private Double longitude;
    private Integer fieldId;

    public Hole(Integer id, Integer par, Double latitude, Double longitude, Integer fieldId) {
        this.id = id;
        this.par = par;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fieldId = fieldId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPar() {
        return par;
    }

    public void setPar(Integer par) {
        this.par = par;
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

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }
}
