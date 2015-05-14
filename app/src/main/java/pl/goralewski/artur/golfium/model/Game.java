package pl.goralewski.artur.golfium.model;

import java.util.Date;

/**
 * Created by Artur Góralewski on 11/05/2015.
 */
public class Game implements TableRow {

    private Integer id;
    private Date start;
    private Date end;
    private Integer score;
    private Integer userId;
    private Integer fieldId;

    public Game(Integer id, Date start, Date end, Integer score, Integer userId, Integer fieldId) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.score = score;
        this.userId = userId;
        this.fieldId = fieldId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }
}
