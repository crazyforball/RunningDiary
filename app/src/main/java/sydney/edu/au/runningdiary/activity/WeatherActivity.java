package sydney.edu.au.runningdiary.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.net.URL;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.utils.HttpCallbackListener;
import sydney.edu.au.runningdiary.utils.HttpUtil;

public class WeatherActivity extends AppCompatActivity {

    private TextView tv_location;
    private TextView tv_weather;
    private TextView tv_temperature;
    private TextView tv_precipitation;
    private TextView tv_humidity;
    private TextView tv_wind;
    private ImageView iv_weather;
    private ProgressDialog progressDialog;

    private String locationName;
    private String weather;
    private String temperature;
    private String precipitation;
    private String humidity;
    private String wind;
    private Bitmap weather_img;


    private static final int UPDATE_UI = 100;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_UI:
                    closeProgressDialog();
                    tv_location.setText(locationName);
                    tv_weather.setText(weather);
                    tv_temperature.setText(temperature);
                    tv_precipitation.setText(precipitation);
                    tv_humidity.setText(humidity);
                    tv_wind.setText(wind);
                    iv_weather.setImageBitmap(weather_img);
                    break;
                default:
                    break;
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        tv_location = (TextView) findViewById(R.id.tv_location);
        tv_weather = (TextView) findViewById(R.id.tv_weather);
        tv_temperature = (TextView) findViewById(R.id.tv_temperature);
        tv_precipitation = (TextView) findViewById(R.id.tv_precipitation);
        tv_humidity = (TextView) findViewById(R.id.tv_humidity);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        iv_weather = (ImageView) findViewById(R.id.iv_weather);

        if (RunActivity.mapPoints.size() > 0) {
            LatLng current_latLng = RunActivity.mapPoints.get(0);
            queryWeather(Math.round(current_latLng.latitude * 1000000)/ 1000000.0, Math.round(current_latLng.longitude * 1000000)/ 1000000.0);
        }
    }

    private void queryWeather(double latitude, double altitude) {
        String key = "b74ff92fbc622155";
        String address = "http://api.wunderground.com/api/" + key +
                "/conditions/q/" + latitude + "," + altitude + ".json";

        showProgressDialog();

        HttpUtil.sendGetRequest(address, new HttpCallbackListener(){
            @Override
            public void onFinish(String response) {
//                System.out.println(response);
                parseJSONWithJSONObject(response);
                handler.sendEmptyMessage(UPDATE_UI);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                closeProgressDialog();
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

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void parseJSONWithJSONObject(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject jsonObject_current_observation = jsonObject.getJSONObject("current_observation");

            JSONObject jsonObject_display_location = jsonObject_current_observation.getJSONObject("display_location");
            locationName = jsonObject_display_location.getString("city") + ", " + jsonObject_display_location.getString("state") + ", " + jsonObject_display_location.getString("country");
            weather = jsonObject_current_observation.getString("weather");
            temperature = jsonObject_current_observation.getString("temperature_string");
            precipitation = jsonObject_current_observation.getString("precip_today_string");
            humidity = jsonObject_current_observation.getString("relative_humidity");
            wind = jsonObject_current_observation.getDouble("wind_kph") + " km/h";
            weather_img = BitmapFactory.decodeStream((new URL(jsonObject_current_observation.getString("icon_url")).openStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
