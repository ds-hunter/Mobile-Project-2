package edu.sdsmt.group4.Control;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.sdsmt.group4.Model.MonitorCloud;
import edu.sdsmt.group4.R;

public class NewUserActivity extends AppCompatActivity {
    private TextView userName;
    private TextView email;
    private TextView newPassword;
    private TextView confirmPassword;
    private Button create;
    private MonitorCloud monitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        userName = findViewById(R.id.userNameText);
        email = findViewById(R.id.emailText);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        create = findViewById(R.id.createButton);

        monitor = new MonitorCloud();

    }

    public void onCreate(View view) {
        monitor.setUserDetails(userName.getText().toString(),
                email.getText().toString(),
                confirmPassword.getText().toString(),
                "p");

        if(!monitor.createUser()){
            //TODO: Create error message if creation fails
        }else{
            finish();
        }
    }
}