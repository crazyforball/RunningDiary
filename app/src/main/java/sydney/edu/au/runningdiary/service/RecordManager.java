package sydney.edu.au.runningdiary.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import sydney.edu.au.runningdiary.model.Record;

/**
 * Created by yang on 10/13/17.
 */

public class RecordManager {
    public static void saveRecord(Record record) {
        record.save();
    }

    public static List<Record> getRecords(String username, Date queryDate) {
        return Record.findWithQuery(Record.class, "Select * from Record where username = ? and create_date > ? Order By create_date DESC", username, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(queryDate));
    }

    public static void deleteRecord(Record record) {
        record.delete();
    }
}
