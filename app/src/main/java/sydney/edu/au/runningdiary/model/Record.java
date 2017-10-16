package sydney.edu.au.runningdiary.model;

import com.orm.SugarRecord;

/**
 * Created by yang on 9/29/17.
 */

public class Record extends SugarRecord<Record> {
    private String username;
    private String createDate;
    private double distance;
    private int duration;
    private double pace;

    public Record() {
    }

    public Record(String username, String createDate, double distance, int duration, double pace) {
        this.username = username;
        this.createDate = createDate;
        this.distance = distance;
        this.duration = duration;
        this.pace = pace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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