package sydney.edu.au.runningdiary.service;

import java.util.List;

import sydney.edu.au.runningdiary.model.TrackPoint;

/**
 * Created by yang on 10/13/17.
 */

public class TrackPointManager {
    public static void saveTrackPoint(TrackPoint trackPoint) {
        trackPoint.save();
    }

    public static List<TrackPoint> getTrackPoints(long recordId) {
        return TrackPoint.findWithQuery(TrackPoint.class, "Select * from Track_Point where record_id = ?", String.valueOf(recordId));
    }

    public static void deleteTrackPoint(TrackPoint trackPoint) {
        trackPoint.delete();
    }
}
