package edu.sdsmt.group4.Control;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.sdsmt.group4.Model.MonitorCloud;
import edu.sdsmt.group4.R;

public class NewUserActivity extends AppCompatActivity {
    private TextView userName;
    private TextView email;
    private TextView newPassword;
    private TextView confirmPassword;
    private Button create;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        userName = findViewById(R.id.userNameText);
        email = findViewById(R.id.emailText);
        newPassword = findViewById(R.id.newPassword);
        confirmPassword = findViewById(R.id.confirmPassword);
        create = findViewById(R.id.createButton);

    }

    public void logIn(boolean authenticated){
        if(!authenticated){
            //TODO: Create error message if creation fails
        }else{
            finish();
        }
    }

    public void onCreateClick(View view) {
        final MonitorCloud monitor = MonitorCloud.INSTANCE;
        /*
        if(userName.getText().equals("") || email.getText().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Invalid user name or email",Toast.LENGTH_SHORT).show();
        }
        else if(!newPassword.getText().toString().trim().equals(confirmPassword.getText().toString().trim()))
        {
            Log.d("pass", newPassword.getText().toString().trim());
            Log.d("pass", confirmPassword.getText().toString().trim());
            Toast.makeText(getApplicationContext(),"passwords don't match",Toast.LENGTH_SHORT).show();
        }
        else
        {*/
            monitor.setNewUser(this);
            monitor.setUserDetails(userName.getText().toString(),
                    email.getText().toString(),
                    confirmPassword.getText().toString(),
                    "p");
            monitor.createUser();
            monitor.startAuthListening();
            finish();
        //}

    }
}