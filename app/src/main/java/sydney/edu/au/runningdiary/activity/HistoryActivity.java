package sydney.edu.au.runningdiary.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.adapter.RecordAdapter;
import sydney.edu.au.runningdiary.model.Record;
import sydney.edu.au.runningdiary.utils.FileUtil;
import sydney.edu.au.runningdiary.utils.HttpCallbackListener;
import sydney.edu.au.runningdiary.utils.HttpUtil;

public class HistoryActivity extends AppCompatActivity {
    private ListView lv_history;
    private TextView tv_monthly_distance;
    private TextView tv_monthly_duration;
    private TextView tv_monthly_pace;

    private ArrayList<Record> records = new ArrayList<Record>();
    private RecordAdapter recordAdapter;

    private static final long MON_IN_MILLISEC = 259200000;
    private static final int ACTION_QUERY = 1;
    private String result;
    private ProgressDialog progressDialog;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ACTION_QUERY:
                    progressDialog.dismiss();
                    onActionResult(result);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lv_history = (ListView) findViewById(R.id.lv_history);
        tv_monthly_distance = (TextView) findViewById(R.id.tv_monthly_km);
        tv_monthly_duration = (TextView) findViewById(R.id.tv_monthly_hms);
        tv_monthly_pace = (TextView) findViewById(R.id.tv_monthly_km_h);

        Date queryDate = new Date(new Date().getTime() - MON_IN_MILLISEC);
        String address = "http://" + MainActivity.IP + "/sydney/QueryServlet";
        address = address + "?username=" + getIntent().getStringExtra("username") + "&createDate=" + new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(queryDate);

        showProgressDialog();

        HttpUtil.sendGetRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                System.out.println(response);
                result = response;
                handler.sendEmptyMessage(ACTION_QUERY);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void onActionResult(String result) {
        if (result.isEmpty()) {
        } else {
            double monthly_distance = 0.00;
            int monthly_duration = 0;
            double monthly_pace = 0.00;


            String[] data = result.split(";");
            for (String entry : data) {
                if(!entry.isEmpty()) {
                    String[] fields = entry.split(",");

                    String createDate = null;
                    double distance = 0.00;
                    int duration = 0;
                    double pace = 0.00;

                    createDate = fields[0];
                    distance = Double.parseDouble(fields[1]);
                    duration = Integer.parseInt(fields[2]);
                    pace = Double.parseDouble(fields[3]);

                    monthly_distance += distance;
                    monthly_duration += duration;

                    records.add(new Record(createDate, distance, duration, pace));
                }
            }
            recordAdapter = new RecordAdapter(this, R.layout.activity_record, records);
            lv_history.setAdapter(recordAdapter);

            tv_monthly_distance.setText(monthly_distance + " km");
            tv_monthly_duration.setText(FileUtil.toHMS(monthly_duration));

            monthly_pace = (monthly_distance * 1000/ (monthly_duration * 1.0)) * 3.6;
            tv_monthly_pace.setText(Math.round((monthly_pace*100))/100.0 + " km/h");
        }

    }
}
