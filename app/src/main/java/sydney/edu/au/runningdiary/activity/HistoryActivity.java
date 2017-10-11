package sydney.edu.au.runningdiary.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.adapter.RecordAdapter;
import sydney.edu.au.runningdiary.model.Record;
import sydney.edu.au.runningdiary.utils.DBUtil;
import sydney.edu.au.runningdiary.utils.FileUtil;

public class HistoryActivity extends AppCompatActivity {
    private ListView lv_history;
    private TextView tv_monthly_distance;
    private TextView tv_monthly_duration;
    private TextView tv_monthly_pace;

    private ArrayList<Record> records = new ArrayList<Record>();
    private RecordAdapter recordAdapter;

    private static final long MON_IN_MILLISEC = 259200000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lv_history = (ListView) findViewById(R.id.lv_history);
        tv_monthly_distance = (TextView) findViewById(R.id.tv_monthly_km);
        tv_monthly_duration = (TextView) findViewById(R.id.tv_monthly_hms);
        tv_monthly_pace = (TextView) findViewById(R.id.tv_monthly_km_h);

        Date queryDate = new Date(new Date().getTime() - MON_IN_MILLISEC);
        records.addAll(DBUtil.readRecordsFromDatabase(HistoryActivity.this, queryDate));

        onActionResult();

    }

    private void onActionResult() {
        if (records.isEmpty()) {
        } else {
            double monthly_distance = 0.00;
            int monthly_duration = 0;
            double monthly_pace = 0.00;

            for (Iterator<Record> iterator = records.iterator(); iterator.hasNext(); ) {
                Record record = iterator.next();
                monthly_distance += record.getDistance();
                monthly_duration += record.getDuration();
            }

            recordAdapter = new RecordAdapter(this, R.layout.activity_record, records);
            lv_history.setAdapter(recordAdapter);

            tv_monthly_distance.setText(monthly_distance + " km");
            tv_monthly_duration.setText(FileUtil.toHMS(monthly_duration));

            monthly_pace = (monthly_distance * 1000 / (monthly_duration * 1.0)) * 3.6;
            tv_monthly_pace.setText(Math.round((monthly_pace * 100)) / 100.0 + " km/h");
        }

    }
}
