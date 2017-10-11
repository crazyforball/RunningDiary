package sydney.edu.au.runningdiary.model;

import java.io.Serializable;

/**
 * Created by yang on 9/29/17.
 */

public class Record implements Serializable {
    private String createDate;
    private double distance;
    private int duration;
    private double pace;

    public Record(String createDate, double distance, int duration, double pace) {
        this.createDate = createDate;
        this.distance = distance;
        this.duration = duration;
        this.pace = pace;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPace() {
        return pace;
    }

    public void setPace(double pace) {
        this.pace = pace;
    }
}