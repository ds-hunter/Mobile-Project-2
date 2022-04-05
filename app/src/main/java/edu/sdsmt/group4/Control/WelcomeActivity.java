package edu.sdsmt.group4.Control;

/* *
 * Project 1 Grading
 *
 * Group:
 * Done 6pt No redundant activities
 * Done 6pt How to play dialog
 * Done 6pt Icons
 * Done 6pt End activity
 * Done 6pt Back button handled
 * How to open the "how to play dialog": Click on the how to play button
 *
 * Individual:
 *
 * 	Play activity and custom view
 *
 * 		Done 9pt Activity appearance
 * 		Done 16pt Static Custom View
 * 		Done 20pt Dynamic part of the Custom View
 * 		Done 15pt Rotation
 *
 * 	Welcome activity and Game Class
 *
 * 		Done 13pt Welcome activity appearance
 * 		Done 20pt Applying capture rules
 * 		Done 12pt Game state
 * 		Done 15pt Rotation
 * 		What is the probability of the rectangle capture: starts with 50% and changes proportional
 *       to the scaling. So if 2 times larger, probability is 25%
 *
 * 	Capture activity and activity sequencing
 *
 * 		Done 9pt Capture activity appearance
 * 		Done 16pt Player round sequencing
 * 		Done 20pt Move to next activity
 * 		Done 15pt Rotation
 *
 * 	Timer
 *
 * 		NA 9pt Timer activity
 * 		NA 24pt Graphic
 * 		NA 12pt Player turn end
 * 		NA 15pt Rotation
 *
 *
 * Please list any additional rules that may be needed to properly grade your project:
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.sdsmt.group4.Model.GameBoard;
import edu.sdsmt.group4.Model.MonitorCloud;
import edu.sdsmt.group4.R;

public class WelcomeActivity extends AppCompatActivity {
    int TYPE_TEXT_VARIATION_VISIBLE_PASSWORD = 145;
    int TYPE_TEXT_VARIATION_PASSWORD = 129;
    public final static String PLAYER1NAME_MESSAGE = "edu.sdsmt.group1.PLAYER1NAME_MESSAGE";
    public final static String PLAYER2NAME_MESSAGE  = "edu.sdsmt.group1.PLAYER2NAME_MESSAGE";
    public final static String THIS_PLAYER  = "edu.sdsmt.group1.THIS_PLAYER";
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
        if(monitor.isAuthenticated())
            startActivity(intent);
    }

    public void logIn(boolean authenticated){
        if(!authenticated){
            //TODO: create an error message explaining why sign-in failed
        }else {
            Intent intent = new Intent(this, GameBoardActivity.class);
            intent.putExtra(ROUNDS_MESSAGE, rounds.getText().toString());
            intent.putExtra(THIS_PLAYER, user.getText().toString());
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
