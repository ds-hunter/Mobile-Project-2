package edu.sdsmt.group4.Control;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import edu.sdsmt.group4.Model.Cloud;
import edu.sdsmt.group4.R;

public class EndGameActivity extends AppCompatActivity {
    public final static String PLAYER1_MESSAGE = "edu.sdsmt.group1.PLAYER1_MESSAGE";
    public final static String PLAYER2_MESSAGE  = "edu.sdsmt.group1.PLAYER2_MESSAGE";
    public final static String WINNER_MESSAGE  = "edu.sdsmt.group1.WINNER_MESSAGE";
    TextView player1;
    TextView player2;
    TextView winner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        player1 = findViewById(R.id.player1Score);
        player2 = findViewById(R.id.player2Score);
        winner = findViewById(R.id.winnerTextView);

        // Get the message from the intent
        Intent intent = getIntent();

        player1.setText(intent.getStringExtra(PLAYER1_MESSAGE));
        player2.setText(intent.getStringExtra(PLAYER2_MESSAGE));
        winner.setText(intent.getStringExtra(WINNER_MESSAGE));
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(PLAYER1_MESSAGE,player1.getText().toString() );
        bundle.putString(PLAYER2_MESSAGE,player2.getText().toString());
        bundle.putString(WINNER_MESSAGE,winner.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        player1.setText(bundle.getString(PLAYER1_MESSAGE));
        player2.setText(bundle.getString(PLAYER2_MESSAGE));
        winner.setText(bundle.getString(WINNER_MESSAGE));
    }

    @Override
    public void onBackPressed() {
        Cloud cloud = new Cloud();

        cloud.reset();
        finish();
    }

    public void onReturnClick(View view)
    {
        Cloud cloud = new Cloud();
        cloud.reset();
        finish();
    }
}
