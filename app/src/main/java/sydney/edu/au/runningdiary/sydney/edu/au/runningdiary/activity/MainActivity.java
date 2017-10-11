package sydney.edu.au.runningdiary.sydney.edu.au.runningdiary.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.sydney.edu.au.runningdiary.model.User;
import sydney.edu.au.runningdiary.sydney.edu.au.runningdiary.utils.HttpCallbackListener;
import sydney.edu.au.runningdiary.sydney.edu.au.runningdiary.utils.HttpUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private Button btn_register;
    private ProgressDialog progressDialog;
    private String result;
    private User user;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            onActionResult(result);
        }
    };

    private static final int ACTION_LOGIN = 1;
    private static final int ACTION_REGISTER = 2;
    private static int action;

    private String email;
    private String password;

    public static String IP = "10.0.2.2:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_email = (EditText) findViewById(R.id.email);
        et_password = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);

        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!checkNetwork()) {
            Toast toast = Toast.makeText(MainActivity.this, "Failed to connect to the network.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        email = et_email.getText().toString();
        password = et_password.getText().toString();

        if (email.isEmpty()) {
            et_email.setError("email required");
            et_email.requestFocus();
        } else if (password.isEmpty()) {
            et_password.setError("password required");
            et_password.requestFocus();
        } else {
            String address = null;
            if (v.getId() == R.id.btn_login) {
                action = ACTION_LOGIN;
                address = "http://" + IP + "/sydney/LoginServlet";
            }
            if (v.getId() == R.id.btn_register) {
                action = ACTION_REGISTER;
                address = "http://" + IP + "/sydney/RegisterServlet";
            }
            address = address + "?username=" + email + "&password=" + password;
            showProgressDialog(action);
            HttpUtil.sendGetRequest(address, new HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    result = response;
                    handler.sendEmptyMessage(action);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    private void showProgressDialog(int action) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Notification");
        if (action == ACTION_LOGIN) {
            progressDialog.setMessage("Logging in...");
        } else if (action == ACTION_REGISTER) {
            progressDialog.setMessage("Registering...");
        }
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void onActionResult(String result) {
        if (action == ACTION_LOGIN) {
            if (result.isEmpty()) {
                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                toRunActivity();
            }
        }
        if (action == ACTION_REGISTER) {
            if (result.isEmpty()) {
                Toast.makeText(MainActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                toRunActivity();
            }
        }
    }

    private void toRunActivity() {
        Intent intent = new Intent(MainActivity.this, RunActivity.class);
        user = new User(email, password);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
