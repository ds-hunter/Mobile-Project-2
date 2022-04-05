package edu.sdsmt.group4.Control;

import androidx.annotation.NonNull;
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
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("userName", userName.getText().toString());
        bundle.putString("newPassword", newPassword.getText().toString());
        bundle.putString("confirmPassword", confirmPassword.getText().toString());
        bundle.putString("email", email.getText().toString());
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        userName.setText(bundle.getString("userName"));
        newPassword.setText(bundle.getString("newPassword"));
        confirmPassword.setText(bundle.getString("confirmPassword"));
        email.setText(bundle.getString("email"));
    }
    public void onCreateClick(View view) {
        final MonitorCloud monitor = MonitorCloud.INSTANCE;

        if(userName.getText().toString().trim().equals("") || email.getText().toString().trim().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Invalid user name or email",Toast.LENGTH_SHORT).show();
        }
        else if(!newPassword.getText().toString().trim().equals(confirmPassword.getText().toString().trim()))
        {
            Toast.makeText(getApplicationContext(),"passwords don't match",Toast.LENGTH_SHORT).show();
        }
        else
        {
            monitor.setNewUser(this);
            monitor.setUserDetails(userName.getText().toString(),
                    email.getText().toString(),
                    confirmPassword.getText().toString(),
                    "p", "0");
            monitor.createUser();
            monitor.startAuthListening();
            finish();
        }

    }
}