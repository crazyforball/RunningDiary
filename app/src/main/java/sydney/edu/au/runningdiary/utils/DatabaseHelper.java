package sydney.edu.au.runningdiary.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yang on 8/21/17.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private final static int VERSION = 1;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, int version) {
        this(context, name, null, version);
    }

    public DatabaseHelper(Context context, String name) {
        this(context, name, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("create a database");
        db.execSQL("create table RECORD (createDate timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, distance decimal(10,2) NOT NULL, duration int NOT NULL, pace decimal(10,2) NOT NULL, PRIMARY KEY (createDate))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("update the database");
    }
}
