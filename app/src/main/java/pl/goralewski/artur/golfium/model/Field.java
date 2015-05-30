package pl.goralewski.artur.golfium.model;

/**
 * Created by Artur Goralewski on 14/05/2015.
 */
public class Field implements TableRow{

    private Integer id;
    private String name;

    public Field(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
