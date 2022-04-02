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
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import edu.sdsmt.group4.Model.GameBoard;
import edu.sdsmt.group4.R;

public class WelcomeActivity extends AppCompatActivity {
    int TYPE_TEXT_VARIATION_VISIBLE_PASSWORD = 145;
    int TYPE_TEXT_VARIATION_PASSWORD = 129;
    public final static String PLAYER1NAME_MESSAGE = "edu.sdsmt.group4.PLAYER1NAME_MESSAGE";
    public final static String PLAYER2NAME_MESSAGE  = "edu.sdsmt.group4.PLAYER2NAME_MESSAGE";
    public final static String ROUNDS_MESSAGE  = "edu.sdsmt.group4.ROUNDS_MESSAGE";
    TextView email;
    TextView password;
    TextView rounds;
    TextView passwordBox;
    Switch passwordSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        email = findViewById(R.id.emailInput);
        password = findViewById(R.id.passwordInput);
        rounds = findViewById(R.id.roundsInput);
        passwordBox = findViewById(R.id.passwordInput);
        passwordSwitch = findViewById(R.id.passwordSwitch);
    }

    public void onStart(View view) {
        WaitingDlg dlg = new WaitingDlg();
        dlg.show(getSupportFragmentManager(), "Loading");
        /*
        Intent intent = new Intent(this, GameBoardActivity.class);

        // This is old stuff but we will leave it for now
        intent.putExtra(PLAYER1NAME_MESSAGE, "TODO");
        intent.putExtra(PLAYER2NAME_MESSAGE, "TODO");
        intent.putExtra(ROUNDS_MESSAGE, rounds.getText().toString());

        // We will still need to switch activities, but instead of putExtra we will
        // be pushing to the cloud.
        startActivity(intent);
        email.setText("");
        password.setText("");
        rounds.setText("");*/
    }

    public void onHowToPlay(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeActivity.this);
        builder.setTitle(R.string.HowToPlayTitle);
        builder.setMessage(R.string.HowToPlayMessage);
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    public void onPasswordSwitch(View view) {
        int type = passwordSwitch.isChecked() ? 145 : 129;
        passwordBox.setInputType(type);
    }
}