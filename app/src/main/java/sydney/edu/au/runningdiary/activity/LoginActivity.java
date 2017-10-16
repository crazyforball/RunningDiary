package sydney.edu.au.runningdiary.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import sydney.edu.au.runningdiary.R;
import sydney.edu.au.runningdiary.model.User;
import sydney.edu.au.runningdiary.service.UserManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_email;
    private EditText et_password;
    private Button btn_login;
    private Button btn_register;
    public static User user;

    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
            Toast toast = Toast.makeText(LoginActivity.this, "Failed to connect to the network.", Toast.LENGTH_SHORT);
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
            if (v.getId() == R.id.btn_login) {
                if (UserManager.validateUser(email, password)) {
                    user = new User(email, password);
                    Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    toRunActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }

            }
            if (v.getId() == R.id.btn_register) {
                if (UserManager.saveUser(email, password)) {
                    user = UserManager.getUser(email);
                    Toast.makeText(LoginActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();
                    toRunActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetworkInfo() != null) {
            return connectivityManager.getActiveNetworkInfo().isAvailable();
        }
        return false;
    }

    private void toRunActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
