package sydney.edu.au.runningdiary.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.activity.LoginActivity;
import sydney.edu.au.runningdiary.activity.MainActivity;
import sydney.edu.au.runningdiary.model.Record;
import sydney.edu.au.runningdiary.model.TrackPoint;
import sydney.edu.au.runningdiary.service.RecordManager;
import sydney.edu.au.runningdiary.service.TrackPointManager;
import sydney.edu.au.runningdiary.utils.FileUtil;


/**
 * Created by yang on 10/14/17.
 */

public class RunFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private TextView tv_distance;
    private TextView tv_duration;
    private TextView tv_pace;

    private Button btn_run_start;
    private Button btn_run_pause_stop;
    private ImageButton btn_music_play;
    private ImageButton btn_music_pause;
    private ImageButton btn_music_next;
    private ImageButton btn_music_previous;
    private ImageButton btn_music_logo;

    private int time = 0;
    private double totalDistance = 0;
    private double deltaDistance = 0;
    private double pace;

    private MyLocationListener locationListener;

    private LocationManager locationManager;
    private String locationProvider;
    private Location starting_location;

    private boolean isStart = false;
    private boolean isPlayingMusic = false;

    private static final int START_RECORD_TIME = 1;

    Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START_RECORD_TIME:
                    time++;
                    tv_duration.setText(FileUtil.toHMS(time));
                    pace = (totalDistance / (time * 1.0)) * 3.6;
                    tv_pace.setText(Math.round((pace*100))/100.0 + "");
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_run, container, false);

        setupLocation();
        setupMediaPlayer();

        btn_run_start = (Button) view.findViewById(R.id.btn_run_start);
        btn_run_pause_stop = (Button) view.findViewById(R.id.btn_run_pause_stop);
        btn_music_logo = (ImageButton) view.findViewById(R.id.btn_music_logo);
        btn_music_play = (ImageButton) view.findViewById(R.id.btn_music_play);
        btn_music_pause = (ImageButton) view.findViewById(R.id.btn_music_pause);
        btn_music_next = (ImageButton) view.findViewById(R.id.btn_music_next);
        btn_music_previous = (ImageButton) view.findViewById(R.id.btn_music_previous);

        btn_run_start.setOnClickListener(this);
        btn_run_pause_stop.setOnClickListener(this);
        btn_run_pause_stop.setOnLongClickListener(this);
        btn_music_play.setOnClickListener(this);
        btn_music_pause.setOnClickListener(this);
        btn_music_next.setOnClickListener(this);
        btn_music_previous.setOnClickListener(this);

        tv_distance = (TextView) view.findViewById(R.id.tv_km);
        tv_duration = (TextView) view.findViewById(R.id.tv_hms);
        tv_pace= (TextView) view.findViewById(R.id.tv_km_h);

        setupViews();

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_run_start:
                MainActivity.trackPoints = new ArrayList<LatLng>();
                MapsFragment.resetMap();

                btn_run_start.setVisibility(View.GONE);
                btn_run_pause_stop.setVisibility(View.VISIBLE);

                isStart = true;
                new Thread(new TimeThread()).start();

                MainActivity.trackPoints.add(MainActivity.current_position);
                break;
            case R.id.btn_run_pause_stop:
                btn_run_pause_stop.setVisibility(View.GONE);
                btn_run_start.setVisibility(View.VISIBLE);

                isStart = false;
                break;
            case R.id.btn_music_play:
                if (audioPaths.size() <= 0) {
                    Toast.makeText(getActivity(), "No music found.", Toast.LENGTH_SHORT).show();
                } else {
                    if (!mediaPlayer.isPlaying()) {
                        isPlayingMusic = true;
                        mediaPlayer.start();
                        btn_music_logo.setVisibility(View.VISIBLE);
                        btn_music_play.setVisibility(View.GONE);
                        btn_music_pause.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.btn_music_pause:
                if (mediaPlayer.isPlaying()) {
                    isPlayingMusic = false;
                    mediaPlayer.pause();
                    btn_music_logo.setVisibility(View.GONE);
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
                    if (music_index >= 1) {
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
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.btn_run_pause_stop:
                isStart = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.save_record_title)
                        .setMessage(R.string.save_record_msg)
                        .setPositiveButton(R.string.btn_save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String username = LoginActivity.user.getUsername();
                                String createDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                                Record recordToSave = new Record(username, createDate,
                                        Math.round((totalDistance / 1000) * 100) / 100.0, time, Math.round((pace * 100)) / 100.0);

                                RecordManager.saveRecord(recordToSave);

                                for (Iterator<LatLng> iterator = MainActivity.trackPoints.iterator(); iterator.hasNext();) {
                                    LatLng location_point = iterator.next();
                                    TrackPoint trackPointToSave = new TrackPoint(recordToSave.getId(), location_point.latitude, location_point.longitude);
                                    TrackPointManager.saveTrackPoint(trackPointToSave);
                                }

                                reset();
                            }
                        })
                        .setNegativeButton(R.string.btn_discard, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reset();
                                MapsFragment.resetMap();
                                MapsFragment.setupMap();
                            }
                        });
                builder.create().show();
                break;
            default:
                break;
        }
        return false;
    }

    private class TimeThread implements Runnable {
        @Override
        public void run() {
            try {
                while (isStart) {
                    timeHandler.sendEmptyMessage(START_RECORD_TIME);
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
                MainActivity.current_position = new LatLng(starting_location.getLatitude(), starting_location.getLongitude());
                System.out.println(MainActivity.current_position);

                if (isStart) {
                    MainActivity.trackPoints.add(MainActivity.current_position);
                    //update google map
                    MapsFragment.updateMap();

                    totalDistance += deltaDistance;
                    tv_distance.setText(Math.round((totalDistance / 1000) * 100) / 100.0 + "");
                }

                deltaDistance = 0;
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

    private void setupLocation() {
        locationListener = new MyLocationListener();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(getActivity(), "No location provider to use", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            starting_location = locationManager.getLastKnownLocation(locationProvider);
            locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (starting_location != null) {
            MainActivity.current_position = new LatLng(starting_location.getLatitude(), starting_location.getLongitude());
        }
    }

    private void setupMediaPlayer() {
        try {
            audioPaths.addAll(FileUtil.getAudioPaths(getActivity()));
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

    private void reset() {
        time = 0;
        totalDistance = 0;
        pace=0;
        setupViews();
    }

    private void setupViews() {
        tv_distance.setText(Math.round((totalDistance/1000)*100)/100.0 + "");
        tv_duration.setText(FileUtil.toHMS(time));
        tv_pace.setText(Math.round((pace*100))/100.0 + "");
        if (isPlayingMusic) {
            btn_music_logo.setVisibility(View.VISIBLE);
            btn_music_play.setVisibility(View.GONE);
            btn_music_pause.setVisibility(View.VISIBLE);
        } else {
            btn_music_logo.setVisibility(View.GONE);
            btn_music_play.setVisibility(View.VISIBLE);
            btn_music_pause.setVisibility(View.GONE);
        }
        if (isStart) {
            btn_run_start.setVisibility(View.GONE);
            btn_run_pause_stop.setVisibility(View.VISIBLE);
        } else {
            btn_run_start.setVisibility(View.VISIBLE);
            btn_run_pause_stop.setVisibility(View.GONE);
        }
    }

}
