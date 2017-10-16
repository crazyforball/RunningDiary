package sydney.edu.au.runningdiary.model;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

import java.util.List;

/**
 * Created by yang on 9/29/17.
 */

public class TrackPoint extends SugarRecord<TrackPoint> {
    private long recordId;
    private double latitude;
    private double longitude;



    public TrackPoint() {
    }

    public TrackPoint(long recordId, double latitude, double longitude) {
        this.recordId = recordId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}