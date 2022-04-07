package edu.sdsmt.group1.Control;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import edu.sdsmt.group1.Model.Cloud;
import edu.sdsmt.group1.R;
import edu.sdsmt.group1.View.GameBoardView;

public class GameBoardActivity extends AppCompatActivity {
    private GameBoardView view;
    public static final String CAPTURED_INT = "edu.sdsmt.group1.RETURN_MESSAGE";
    private TextView player1Name;
    private TextView player2Name;
    private TextView player1Score;
    private TextView player2Score;
    private TextView rounds;
    private Button capture;
    private Button captureOptions;
    private ActivityResultLauncher<Intent> captureResultLauncher;
    private String thisPlayer;
    private WaitingDlg dlg;
    private DatabaseReference ref;
    private boolean loadBool = true;
    Timer timer;
    Timer loadTimer;
    Cloud cloud;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("THIS_PLAYER",thisPlayer );
        view.saveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle bundle) {
        super.onRestoreInstanceState(bundle);
        thisPlayer = bundle.getString("THIS_PLAYER");
        Log.d("GameBoard: ", " LOADED INSTANCE STATE ");
        view.loadInstanceState(bundle);
        view.updateGUI(player1Name,player2Name,player1Score,player2Score,rounds,captureOptions,capture,thisPlayer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        // Set the main load timer
        loadTimer = new Timer();
        loadTimer.schedule(new LoadTask(), 1000, 1000);


        cloud = new Cloud();
        ref = cloud.getReference();
        // Set the waiting for player timer
        view = this.findViewById(R.id.gameBoardView);
        if (view.getNumPlayers() != 2 ) {
            dlg = new WaitingDlg();
            dlg.setActivity(this);
            dlg.setCancelable(false);
            dlg.show(getSupportFragmentManager(), "Loading");
            timer = new Timer();
            timer.schedule(new WaitForPlayerTask(), 1000, 1000);
        }
        //get player names and no of rounds from prev
        Intent intent = getIntent();
        thisPlayer = intent.getStringExtra(WelcomeActivity.THIS_PLAYER);

        view.setRounds(1);
        view.setDefaultPlayer();

        player1Name = findViewById(R.id.player1Name);
        player2Name = findViewById(R.id.player2Name);
        player1Score = findViewById(R.id.player1Score);
        player2Score = findViewById(R.id.player2Score);
        capture = findViewById(R.id.captureButton);
        captureOptions = findViewById(R.id.optionsButton);
        capture.setEnabled(false);

        //load from cloud here?
        player1Score.setText(view.getPlayer1Score());
        player2Score.setText(view.getPlayer2Score());
        rounds = findViewById(R.id.rounds);
        rounds.setText("N/A");
        player1Name.setTextColor(Color.parseColor("#FF0000"));


        //any target
        ActivityResultContracts.StartActivityForResult contract =
                new ActivityResultContracts.StartActivityForResult();
        captureResultLauncher = registerForActivityResult(contract, (result) -> {
            int resultCode = result.getResultCode();
            if (resultCode == Activity.RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                //if no capture option is selected
                capture.setEnabled(true);
                view.setCapture(data.getIntExtra(CAPTURED_INT, 0));
            }
        });
    }

    private void isEndGame() {
        if(view.isEndGame()) {
             endGame();
        }
    }


    public void endGame()
    {

        cloud.setEndGame();
        String winner = "WINNER\n";
        int player1Score = Integer.parseInt(view.getPlayer1Score());
        int player2Score = Integer.parseInt(view.getPlayer2Score());

        Intent intent = new Intent(this, EndGameActivity.class);

        intent.putExtra(EndGameActivity.PLAYER1_MESSAGE, view.getPlayer1Name()
                + "'s Score\n" + view.getPlayer1Score());
        intent.putExtra(EndGameActivity.PLAYER2_MESSAGE, view.getPlayer2Name()
                + "'s Score\n" + view.getPlayer2Score());

        //get the winner
        if (player1Score > player2Score)
            winner += view.getPlayer1Name();
        else if (player1Score < player2Score)
            winner += view.getPlayer2Name();
        else
            winner = "TIE!";
        intent.putExtra(EndGameActivity.WINNER_MESSAGE, winner);
        loadTimer.cancel();
        startActivity(intent);
        finish();
    }


    public void onCaptureClick(View v) {
        loadBool = false;
        view.captureClicked();
        view.updateGUI(player1Name,player2Name,player1Score,player2Score,rounds,captureOptions,capture,thisPlayer);

        cloud.saveToCloud(view);
        loadBool = true;
        isEndGame();
    }

    public void stopLoadTimer() {
        loadTimer.cancel();
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameBoardActivity.this);
        builder.setTitle(R.string.QUIT_GAME);
        builder.setMessage(R.string.QUIT_GAME_MESSAGE);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            endGame();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public void onCaptureOptionsClick(View v){
        Intent switchActivityIntent = new Intent(this, CaptureSelectionActivity.class);
        captureResultLauncher.launch(switchActivityIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.gameboard_menu, menu);
        return true;
    }

    /**
     * Handle options menu selections
     *
     * @param item Menu item selected
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exit_game) {
               endGame();
        }
        return super.onOptionsItemSelected(item);
    }

    class WaitForPlayerTask extends TimerTask {
        @Override
        public void run() {
            if (view.getNumPlayers() == 2) {
                if (dlg != null) {
                    dlg.dismiss();
                }
                if(view.getCollectableAmt() == 0 && thisPlayer.equals(view.getPlayer1Email())) {
                    loadBool = false;
                    view.generateBoard();
                    cloud.saveToCloud(view);
                    loadBool = true;
                }
                timer.cancel();
                view.player1Update();
                view.player2Update();
            }
        }
    }

    class LoadTask extends TimerTask {
        @Override
        public void run() {

                DatabaseReference matchRef = ref.child("testmatchUID").child("game").child("endGame");

                // Read from the database
                matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((boolean) dataSnapshot.getValue()) {
                            endGame();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            if(loadBool) {
                cloud.loadFromCloud(view, player1Name, player2Name, player1Score, player2Score, rounds, captureOptions, capture, thisPlayer, dlg);
                // Check if board currently has 2 players to detect if a match is going and check for timeouts
                //GRADING: TIMEOUT
                if (view.getNumPlayers() == 2) {
                    if (view.getPlayer1Time() > 30) {
                        Log.d("Player Timeout", "Player 1");
                        endGame();
                    }
                    if (view.getPlayer2Time() > 30) {
                        Log.d("Player Timeout", "Player 2");
                        endGame();
                    }
                }
            }
        }
    }
}
