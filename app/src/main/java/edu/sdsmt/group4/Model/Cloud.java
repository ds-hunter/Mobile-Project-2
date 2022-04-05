package edu.sdsmt.group4.Model;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.sdsmt.group4.Control.GameBoardActivity;
import edu.sdsmt.group4.R;
import edu.sdsmt.group4.View.GameBoardView;

public class Cloud {
    private final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference matches = database.getReference("matches");

    public boolean checkForEndGame() {
        DatabaseReference roundsRef = matches.child("testMatchUID").child("game").child("currRound");

        roundsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return false;
    }
    public void loadFromCloud(
            final GameBoardView view,
            TextView player1Name,
            TextView player2Name,
            TextView p1Score,
            TextView p2Score,
            TextView rounds,
            Button captureOptions,
            Button capture,
            String thisPlayer
    )
    {
        DatabaseReference matchRef = matches.child("testmatchUID");

        // Read from the database
        matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                view.loadJSON(dataSnapshot,player1Name,player2Name,p1Score, p2Score,rounds, captureOptions,capture,thisPlayer);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                view.post(() -> {
                    Toast.makeText(view.getContext(), R.string.loading_fail, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void saveToCloud(GameBoardView view) {
        DatabaseReference myRef = matches.child("testmatchUID");

        view.saveJSON(myRef);

    }

    public void reset(){
        matches.child("testmatchUID/player1").removeValue();
        matches.child("testmatchUID/player2").removeValue();

        matches.child("testmatchUID/game/currRound").setValue("5");
    }
}
