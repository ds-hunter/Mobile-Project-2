package edu.sdsmt.group1.Control;

/**Project 2 Grading

        firebase login: Added Dr.Rebenitsch as owner of firebase project
        firebase password: Added Dr.Rebenitsch as owner of firebase project
        Time out period: 30 seconds
        How to reset database (file or button): file, there is a reset button in the game, but it is
                      for resetting only part of the firebase(more info provided below)
        Reminder: Mark where the timeout period is set with GRADING: TIMEOUT


        Group:

        Done 6pt Game still works and Database setup
        Done 8pt Database setup\reset
        Done 8pt New user activity
        Done 18pt Opening\login activity
        Done 5pt rotation


        Individual:

        Sequencing
        Done 4pt Registration sequence
        Done 9pt Login Sequence
        Done 18pt Play Sequence
        Done 9pt Exiting menu, and handlers
        Done 5pt rotation


        Upload

        Done 6pt intial setup
        Done 6pt waiting
        Done 17pt store game state
        Done 11pt notify end/early exits
        Done 5pt rotation


        Download

        Done 6pt intial setup
        Done 6pt waiting
        Done 17pt store game state
        Done 11pt grab and forward end/early exits
        Done 5pt rotation


        Monitor Waiting
        NA 10pt inital setup
        NA 12pt Uploading the 3 state
        NA 12pt Downloading the 3 state
        NA 6pt UI update
        NA 5pt rotation

        Please list any additional rules that may be needed to properly grade your project:
        If you exit the game without clicking any of the below options after logging in, you will
        have to click the reset gamestate button to restore the game state
        1)the play again or back button of endgame activity
        2) The cancel button from the waiting player dialog
        3) The endgame or back button from the GameBoard activity

**/

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.sdsmt.group1.Model.Cloud;
import edu.sdsmt.group1.Model.MonitorCloud;
import edu.sdsmt.group1.R;

public class WelcomeActivity extends AppCompatActivity {

    public final static String THIS_PLAYER  = "edu.sdsmt.group1.THIS_PLAYER";
    public final static String THIS_PASSWORD  = "edu.sdsmt.group1.THIS_PASSWORD";
    public final static String ROUNDS_MESSAGE  = "edu.sdsmt.group1.ROUNDS_MESSAGE";
    private SharedPreferences preferences;
    EditText user;
    EditText rounds;
    EditText passwordBox;
    CheckBox rememberBox;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        bundle = new Bundle();

        user = findViewById(R.id.userNameInput);
        rounds = findViewById(R.id.roundsInput);
        passwordBox = findViewById(R.id.passwordInput);
        rememberBox = findViewById(R.id.rememberBox);
        preferences = this.getSharedPreferences("login", 0);
        String userName=preferences.getString("userName", "");
        String pass=preferences.getString("password", "");
        if(!userName.equals("") && !pass.equals(""))
        {
            user.setText(userName);
            passwordBox.setText(pass);
        }
    }

    public void onStart(View view) {
        final MonitorCloud monitor = MonitorCloud.INSTANCE;
        monitor.logOut();
        Intent intent = new Intent(this, GameBoardActivity.class);
         //This is old stuff but we will leave it for no

       intent.putExtra(THIS_PLAYER, user.getText().toString());


        if(rememberBox.isChecked())
        {
            SharedPreferences.Editor editor =preferences.edit();
            String username=user.getText().toString().trim();
            String password=passwordBox.getText().toString().trim();
            editor.putString("userName",username);
            editor.putString("password",password);
            editor.apply();
        }
        monitor.setWelcome(this);
        String roundStr = rounds.getText().toString();
        if(roundStr.trim().equals(""))
            roundStr = "5";
        int roundTotal = Integer.parseInt(roundStr);
        monitor.setUserDetails(" ",
                user.getText().toString(),
                passwordBox.getText().toString(),
                roundTotal);
        monitor.signIn();
        monitor.startAuthListening();
        if(monitor.isAuthenticated()) {

            startActivity(intent);
        }
    }

    public void resetFirebaseClicked(View v)
    {
        Cloud cloud = new Cloud();
        cloud.reset();
    }

    public void logIn(boolean authenticated){
        if(!authenticated){
            Toast.makeText(getApplicationContext(), R.string.loading_fail,Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, GameBoardActivity.class);
            intent.putExtra(ROUNDS_MESSAGE, rounds.getText().toString());
            intent.putExtra(THIS_PLAYER, user.getText().toString());
            intent.putExtra(THIS_PASSWORD, passwordBox.getText().toString());
            startActivity(intent);
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("userName", user.getText().toString());
        bundle.putString("password", passwordBox.getText().toString());
        bundle.putString("rounds", rounds.getText().toString());
        bundle.putBoolean("checked", rememberBox.isChecked());
    }
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        user.setText(bundle.getString("userName"));
        passwordBox.setText(bundle.getString("password"));
        rounds.setText(bundle.getString("rounds"));
        rememberBox.setChecked(bundle.getBoolean("checked"));
    }
    public void onAccountClick(View view){
        Intent intent = new Intent(this, NewUserActivity.class);
        startActivity(intent);
        //logIn();
    }

    public void onHowToPlay(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
        builder.setTitle(R.string.HowToPlayTitle);
        builder.setMessage(R.string.HowToPlayMessage);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }
}
