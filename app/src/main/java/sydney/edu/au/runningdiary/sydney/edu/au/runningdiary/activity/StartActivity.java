package sydney.edu.au.runningdiary.sydney.edu.au.runningdiary.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import sydney.edu.au.runningdiary.R;

public class StartActivity extends AppCompatActivity {

    public static String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private static int index = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        checkAndRequestPermissions(this);
    }

    public void checkAndRequestPermissions(Activity activity) {
        if (index < PERMISSIONS.length) {
            if (ContextCompat.checkSelfPermission(activity, PERMISSIONS[index]) != PackageManager.PERMISSION_GRANTED) {
                System.out.println("permission denied");
                requestPermission(activity, PERMISSIONS[index], index);
            } else {
                index++;
                checkAndRequestPermissions(activity);
            }
        }
        if (index == PERMISSIONS.length) {
            toMainActivity();
        }
    }

    public void requestPermission(Activity activity, String permission, int permission_request_code){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            Toast.makeText(activity, "permission needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity,new String[]{permission},permission_request_code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == index) {
            if (permissions[0].equals(PERMISSIONS[index])) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    System.out.println("result denied");
                    checkAndRequestPermissions(this);
                } else {
                    index++;
                    checkAndRequestPermissions(this);
                }
            }
        }
    }

    private void toMainActivity() {
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
