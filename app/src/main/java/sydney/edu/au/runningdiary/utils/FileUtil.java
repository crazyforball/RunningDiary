package sydney.edu.au.runningdiary.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yang on 9/6/17.
 */

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static List<String> getAudioPaths(Context context) {
        List<String> audioPaths = new ArrayList<String>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, MediaStore.Audio.Media.MIME_TYPE + "=? or "
                + MediaStore.Audio.Media.MIME_TYPE + "=?", new String[]{"audio/mp3", "audio/mpeg"}, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        if (cursor == null || cursor.getCount() <= 0) return null;
        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            String path = cursor.getString(index);
            File file = new File(path);
            if (file.exists()) {
                audioPaths.add(path);
                Log.i(TAG, "audio path: " + path);
            }
        }
        cursor.close();

        return audioPaths;
    }

    public static String toHMS(int duration) {
        int hour = duration / 3600;
        int min = (duration % 3600) / 60;
        int sec = (duration % 3600) % 60;
        String HH = "";
        String MM = "";
        String SS = "";
        if (hour < 10) {
            HH = "0" + hour;
        } else {
            HH += hour;
        }
        if(min < 10) {
            MM = "0" + min;
        } else {
            MM += min;
        }
        if(sec < 10) {
            SS = "0" + sec;
        } else {
            SS += sec;
        }
        return HH + ":" + MM + ":" + SS;
    }
}
