package sydney.edu.au.runningdiary.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sydney.edu.au.runningdiary.model.Record;


/**
 * Created by yang on 10/11/17.
 */

public class DBUtil {

    public static void insertRecordToDatabase(Context context, Record record) {
        String createDate = record.getCreateDate();
        double distance = record.getDistance();
        int duration = record.getDuration();
        double pace = record.getPace();

        ContentValues values = new ContentValues();
        values.put("createDate", createDate);
        values.put("distance", distance);
        values.put("duration", duration);
        values.put("pace", pace);

        DatabaseHelper dbHelper = new DatabaseHelper(context, "RunningDiary_DB");
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert("RECORD", null, values);
        db.close();
        dbHelper.close();
    }

    public static List<Record> readRecordsFromDatabase(Context context, Date queryDate) {
        List<Record> records = new ArrayList<Record>();
        DatabaseHelper dbHelper = new DatabaseHelper(context, "RunningDiary_DB", null, 1);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("RECORD", new String[]{"createDate", "distance", "duration", "pace"}, "createDate > ?", new String[]{new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(queryDate)}, null, null, "createDate");
        while (cursor.moveToNext()) {
            records.add(new Record(cursor.getString(cursor.getColumnIndex("createDate")), Double.parseDouble(cursor.getString(cursor.getColumnIndex("distance"))), Integer.parseInt(cursor.getString(cursor.getColumnIndex("duration"))), Double.parseDouble(cursor.getString(cursor.getColumnIndex("pace")))));
        }
        cursor.close();
        db.close();
        return records;
    }

}
