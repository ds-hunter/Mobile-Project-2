package edu.sdsmt.group4.Model;

import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.sdsmt.group4.R;
import edu.sdsmt.group4.View.GameBoardView;

public class Cloud {
    private final static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final DatabaseReference matches = database.getReference("matches");

    public void loadFromCloud(final GameBoardView view) {
        DatabaseReference matchRef = matches.child("testmatchUID");

        // Read from the database
        matchRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                view.loadJSON(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                view.post(() -> {
                    Toast.makeText(view.getContext(), R.string.loading_fail, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void saveToCloud() {
        // TO DO
    }
}
