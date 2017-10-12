package sydney.edu.au.runningdiary.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.model.Record;
import sydney.edu.au.runningdiary.model.User;
import sydney.edu.au.runningdiary.utils.FileUtil;
import sydney.edu.au.runningdiary.utils.HttpCallbackListener;
import sydney.edu.au.runningdiary.utils.HttpUtil;

/**
 * Created by yang on 9/25/17.
 */

public class RunActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_distance;
    private TextView tv_duration;
    private TextView tv_pace;

    private Button btn_run_start;
    private Button btn_run_stop;
    private Button btn_run_pause;
    private Button btn_music_play;
    private Button btn_music_pause;
    private Button btn_music_next;
    private Button btn_music_previous;
    private Button btn_map;
    private Button btn_weather;
    private Button btn_history;

    private int time = 0;
    private double totalDistance = 0;
    private double deltaDistance = 0;
    private double pace;

    private LocationManager locationManager;
    private MyLocationListener locationListener = new MyLocationListener();
    private String locationProvider;

    private Location starting_location;
    public static ArrayList<LatLng> trackPoints = new ArrayList<LatLng>();
    public static ArrayList<LatLng> mapPoints;

    private boolean isStart = false;

    private static final int START_RECORD_TIME = 1;
    private static final int SAVE_RECORD = 2;

    private ProgressDialog progressDialog;
    private String result;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_RECORD_TIME:
                    time++;
                    tv_duration.setText(FileUtil.toHMS(time));
                    break;
                case SAVE_RECORD:
                    progressDialog.dismiss();
                    onActionResult(result);
                    break;
                default:
                    break;
            }
        }
    };

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private ArrayList<String> audioPaths = new ArrayList<String>();
    private static int music_index = 0;
    private static int numOfAudios;

    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run);

        user = (User) getIntent().getSerializableExtra("user");

        locationManager = (LocationManager) RunActivity.this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
            return;
        }

        initiateMediaPlayer();

        btn_run_start = (Button) findViewById(R.id.btn_run_start);
        btn_run_pause = (Button) findViewById(R.id.btn_run_pause);
        btn_run_stop = (Button) findViewById(R.id.btn_run_stop);
        btn_music_play = (Button) findViewById(R.id.btn_music_play);
        btn_music_pause = (Button) findViewById(R.id.btn_music_pause);
        btn_music_next = (Button) findViewById(R.id.btn_music_next);
        btn_music_previous = (Button) findViewById(R.id.btn_music_previous);
        btn_map = (Button) findViewById(R.id.btn_map);
        btn_weather = (Button) findViewById(R.id.btn_weather);
        btn_history = (Button) findViewById(R.id.btn_history);

        btn_run_start.setOnClickListener(this);
        btn_run_pause.setOnClickListener(this);
        btn_run_stop.setOnClickListener(this);
        btn_music_play.setOnClickListener(this);
        btn_music_pause.setOnClickListener(this);
        btn_music_next.setOnClickListener(this);
        btn_music_previous.setOnClickListener(this);
        btn_map.setOnClickListener(this);
        btn_weather.setOnClickListener(this);
        btn_history.setOnClickListener(this);

        tv_distance = (TextView) findViewById(R.id.tv_km);
        tv_duration = (TextView) findViewById(R.id.tv_hms);
        tv_pace= (TextView) findViewById(R.id.tv_km_h);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_run_start:
                try {
                    isStart = true;
                    new Thread(new TimeThread()).start();

                    starting_location = locationManager.getLastKnownLocation(locationProvider);
                    System.out.println("initial:" + starting_location);
                    if (starting_location != null) {
                        trackPoints.add(new LatLng(starting_location.getLatitude(), starting_location.getLongitude()));
                    }
                    locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);

                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_run_pause:
                isStart = false;
                break;
            case R.id.btn_run_stop:
                isStart = false;
                locationManager.removeUpdates(locationListener);
                AlertDialog.Builder builder = new AlertDialog.Builder(RunActivity.this);
                builder.setTitle(R.string.save_record_title)
                        .setMessage(R.string.save_record_msg)
                        .setPositiveButton(R.string.btn_save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgressDialog();

                                Record record = new Record(new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(new Date()),
                                        Math.round((totalDistance / 1000) * 100) / 100.0, time, Math.round((pace * 100)) / 100.0);
                                user.addHistory(record);

                                String address = "http://" + MainActivity.IP + "/sydney/SaveServlet";
                                address = address + "?username=" + user.getUsername() + "&createDate=" + record.getCreateDate()
                                        + "&distance=" + record.getDistance() + "&duration=" + record.getDuration() + "&pace=" + record.getPace();
                                HttpUtil.sendGetRequest(address, new HttpCallbackListener() {
                                    @Override
                                    public void onFinish(String response) {
                                        result = response;
                                        handler.sendEmptyMessage(SAVE_RECORD);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });

                                reset();

                            }
                        })
                        .setNegativeButton(R.string.btn_discard, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reset();
                            }
                        });
                builder.create().show();
                break;
            case R.id.btn_music_play:
                if (audioPaths.size() <= 0) {
                    Toast.makeText(RunActivity.this, "No music found.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                        btn_music_play.setVisibility(View.GONE);
                        btn_music_pause.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.btn_music_pause:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    btn_music_pause.setVisibility(View.GONE);
                    btn_music_play.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_music_next:
                try {
                    if (music_index < numOfAudios - 1) {
                        music_index++;
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(audioPaths.get(music_index));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_music_previous:
                try {
                    if (music_index >= 1 ) {
                        music_index--;
                        mediaPlayer.reset();
                        mediaPlayer.setDataSource(audioPaths.get(music_index));
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_map:
                Intent intent_map = new Intent(RunActivity.this, MapsActivity.class);
                startActivity(intent_map);
                break;
            case R.id.btn_weather:
                Intent intent_weather = new Intent(RunActivity.this, WeatherActivity.class);
                startActivity(intent_weather);
                break;
            case R.id.btn_history:
                Intent intent_history = new Intent(RunActivity.this, HistoryActivity.class);
                intent_history.putExtra("username", user.getUsername());
                startActivity(intent_history);
                break;
            default:
                break;
        }
    }

    private class TimeThread implements Runnable {
        @Override
        public void run() {
            try {
                while (isStart) {
                    handler.sendEmptyMessage(START_RECORD_TIME);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (starting_location == null) {
                return;
            }
            double distance = location.distanceTo(starting_location);
            deltaDistance += distance;

            if (deltaDistance >= 10) {
                starting_location = location;
                trackPoints.add(new LatLng(starting_location.getLatitude(), starting_location.getLongitude()));

                totalDistance += deltaDistance;
                deltaDistance = 0;
                tv_distance.setText(Math.round((totalDistance/1000)*100)/100.0 + " km");
                pace = (totalDistance / (time * 1.0)) * 3.6;
                tv_pace.setText(Math.round((pace*100))/100.0 + " km/h");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    private void initiateMediaPlayer() {
        try {
            audioPaths.addAll(FileUtil.getAudioPaths(RunActivity.this));
            numOfAudios = audioPaths.size();
            if (numOfAudios > 0) {
                mediaPlayer.setDataSource(audioPaths.get(music_index));
                mediaPlayer.prepare();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                            try {
                                if (music_index < numOfAudios - 1) {
                                    music_index++;
                                    mediaPlayer.reset();
                                    mediaPlayer.setDataSource(audioPaths.get(music_index));
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Toast.makeText(RunActivity.this, "Save Failed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RunActivity.this, "Save Successfully", Toast.LENGTH_SHORT).show();
        }

    }

    private void reset() {
        tv_distance.setText("0.00 km");
        tv_duration.setText("00:00:00");
        tv_pace.setText("0.00 km/h");
        time = 0;
        mapPoints = new ArrayList<LatLng>();
        mapPoints.addAll(trackPoints);
        trackPoints.clear();
    }

}
