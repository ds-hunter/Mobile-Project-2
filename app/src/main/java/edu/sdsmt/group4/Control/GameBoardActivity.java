package edu.sdsmt.group4.Control;

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

import com.google.firebase.database.DatabaseReference;

import java.util.Timer;
import java.util.TimerTask;

import edu.sdsmt.group4.Model.Cloud;
import edu.sdsmt.group4.R;
import edu.sdsmt.group4.View.GameBoardView;

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
    Timer timer;
    Timer loadTimer;

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
        view.updateGUI(player1Name,player2Name,rounds, captureOptions,capture,thisPlayer );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        // Set the main load timer
        loadTimer = new Timer();
        loadTimer.schedule(new LoadTask(), 3000, 3000);

        // Set the waiting for player timer
        view = this.findViewById(R.id.gameBoardView);
        if (view.getNumPlayers() != 2) {
            dlg = new WaitingDlg(view);
            dlg.show(getSupportFragmentManager(), "Loading");
            timer = new Timer();
            timer.schedule(new WaitForPlayerTask(), 1000, 1000);
        }
        //get player names and no of rounds from prev
        Intent intent = getIntent();
        String name1 ="";
        String name2 = "";
        thisPlayer = intent.getStringExtra(WelcomeActivity.THIS_PLAYER);

        //view.addPlayer(name1,0);
        //view.addPlayer(name2,1);
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
        rounds = findViewById(R.id.rounds);
        player1Name.setText(R.string.Name1);
        player2Score.setText("0");
        player2Name.setText(R.string.Name2);
        player1Score.setText("0");
        rounds.setText("1");
        player1Name.setTextColor(Color.parseColor("#FF0000"));
        //updateGUI();

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
            //send is endGame to cloud?
             endGame();
        }
    }


    public void endGame()
    {            String winner = "WINNER\n";
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
        startActivity(intent);
        finish();}


    public void onCaptureClick(View v) {
        view.captureClicked();
        view.updateGUI(player1Name,player2Name,rounds, captureOptions,capture,thisPlayer );
        Cloud cloud = new Cloud();
        cloud.saveToCloud(view);
        isEndGame();
    }

    //GRADING: BACK
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameBoardActivity.this);
        builder.setTitle(R.string.QUIT_GAME);
        builder.setMessage(R.string.QUIT_GAME_MESSAGE);
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> finish());
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
                timer.cancel();
            }
        }
    }

    class LoadTask extends TimerTask {
        @Override
        public void run() {
            Cloud cloud = new Cloud();
            cloud.loadFromCloud(view);
            // updateGUI(); This can't be called because its on a different thread
        }
    }
}
