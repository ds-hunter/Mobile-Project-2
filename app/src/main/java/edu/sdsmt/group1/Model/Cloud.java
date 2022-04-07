package edu.sdsmt.group1.Model;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.sdsmt.group1.Control.WaitingDlg;
import edu.sdsmt.group1.R;
import edu.sdsmt.group1.View.GameBoardView;

public class Cloud {
    private final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference matches = database.getReference("matches");


    public void loadFromCloud(
            final GameBoardView view,
            TextView player1Name,
            TextView player2Name,
            TextView p1Score,
            TextView p2Score,
            TextView rounds,
            Button captureOptions,
            Button capture,
            String thisPlayer,
            WaitingDlg dlg
    )
    {
        DatabaseReference matchRef = matches.child("testmatchUID");

        // Read from the database
        matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                view.loadJSON(dataSnapshot,player1Name,player2Name,p1Score, p2Score,rounds, captureOptions,capture,thisPlayer);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                view.post(() -> {
                    Toast.makeText(view.getContext(), R.string.loading_fail, Toast.LENGTH_SHORT).show();
                    dlg.unAuthorized();
                });
            }
        });
    }

    public void saveToCloud(GameBoardView view) {
        DatabaseReference myRef = matches.child("testmatchUID");

        view.saveJSON(myRef);

    }

    public void setEndGame()
    {
        matches.child("testmatchUID/game/endGame").setValue(true);
    }

    public void reset(){
        matches.child("testmatchUID/player1").removeValue();
        matches.child("testmatchUID/player2").removeValue();
        matches.child("testmatchUID/game/currPlayer").setValue(0);
        matches.child("testmatchUID/game/currRound").setValue("5");
        matches.child("testmatchUID/game/endGame").setValue(false);
        matches.child("testmatchUID/game/collectableAmt").setValue(0);

        for(int i = 0; i <= 17 ; i++){
            matches.child("testmatchUID/game/collectables/c"+i).removeValue();
        }
    }


    public DatabaseReference getReference() {
        return matches;
    }
}
