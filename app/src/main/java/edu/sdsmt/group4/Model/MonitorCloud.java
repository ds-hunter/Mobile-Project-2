package edu.sdsmt.group4.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import edu.sdsmt.group4.Control.NewUserActivity;
import edu.sdsmt.group4.Control.WelcomeActivity;

public class MonitorCloud {
    //public final static MonitorCloud INSTANCE = new MonitorCloud();

    private String USER;
    private String TEMPUSER1;
    private String TEMPUSER2;
    private String EMAIL;
    private String PASSWORD;
    private String TAG;
    private int ROUNDS;
    // Firebase instance variables
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
    private final DatabaseReference matchRef = FirebaseDatabase.getInstance().getReference().child("matches");

    private boolean authenticated = false;
    public final static MonitorCloud INSTANCE = new MonitorCloud();

    private WelcomeActivity wAct;
    private NewUserActivity nuAct;

    public boolean isAuthenticated() {

        return authenticated;
    }

    public void setWelcome(WelcomeActivity welcome) {
        wAct = welcome;
    }

    public void setNewUser(NewUserActivity newUser) {
        nuAct = newUser;
    }

    private MonitorCloud() {
    }

    public void setUserDetails(String user, String email, String passwd, int rounds) {
        USER = user;
        EMAIL = email;
        PASSWORD = passwd;
        ROUNDS = rounds;
        TAG = "";
    }

    public void createUser() {
        Task<AuthResult> result = userAuth.createUserWithEmailAndPassword(EMAIL, PASSWORD);
        result.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("/screenName/" + USER, true);
                    result.put("/" + firebaseUser.getUid() + "/name", USER);
                    result.put("/" + firebaseUser.getUid() + "/password", PASSWORD);
                    result.put("/" + firebaseUser.getUid() + "/email", EMAIL);
                    userRef.updateChildren(result);
                } else if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                    //signIn();
                    authenticated = false;
                } else {
                    Log.d(TAG, "Problem: " + task.getException().getMessage());
                    authenticated = false;
                    signIn();
                    firebaseUser = userAuth.getCurrentUser();
                }

                if (wAct != null) {
                    wAct.logIn(isAuthenticated());
                } else {
                    nuAct.logIn(isAuthenticated());
                }
            }
        });
    }


    public void signIn() {
        Task<AuthResult> result = userAuth.signInWithEmailAndPassword(EMAIL, PASSWORD);
        result.addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    firebaseUser = userAuth.getCurrentUser();

                    setTag();
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                    authenticated = true;
                } else {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    authenticated = false;
                }
            }
        });
    }

    public void signOut()
    {
       userAuth.signOut();
    }

    public void startAuthListening() {
        userAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {

                    // User is signed in
                    authenticated = true;
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                } else {

                    // User is signed out
                    authenticated = false;
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        });

    }

    public void getUserUid() {
        //stop people from getting the Uid if not logged in
        if (firebaseUser == null)
            return;
        else
            matchRef.child("testmatchUID/"+TAG+"/uid").setValue(firebaseUser.getUid());
    }

    public void setPlayerName() {
        DatabaseReference myRef = userRef.child(FirebaseAuth.getInstance().getUid());
        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!(TAG.equals(""))) {
                    String UID = firebaseUser.getUid();
                    USER = dataSnapshot.child("/name").getValue().toString();
                    matchRef.child("testmatchUID/" + TAG + "/screenName").setValue(USER);
                    EMAIL = dataSnapshot.child("/email").getValue().toString();
                    matchRef.child("testmatchUID/" + TAG + "/email").setValue(EMAIL);
                    if (wAct != null) {
                    matchRef.child("testmatchUID/" + TAG + "/email").setValue(EMAIL);}
                    if(wAct != null){
                        wAct.logIn(isAuthenticated());
                    } else {
                        nuAct.logIn(isAuthenticated());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("setName", error.toString());
            }
        });
    }

    private void setTag() {
        DatabaseReference tempRef = matchRef.child("testmatchUID");
        tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!snapshot.child("player1").exists()) {
                    TAG = "player1";
                } else if (!snapshot.child("player2").exists()) {
                    TAG = "player2";
                } else {
                    TEMPUSER1 = snapshot.child("player1/screenName").getValue().toString();
                    TEMPUSER2 = snapshot.child("player2/screenName").getValue().toString();
                    checkIfIsPlayer();
                }
                if (!(TAG.equals(""))) {
                    matchRef.child("testmatchUID/" + TAG + "/score").setValue(0);
                    if (TAG.equals("player1")) {
                        matchRef.child("testmatchUID/game/currRound").setValue(ROUNDS);
                        matchRef.child("testmatchUID/game/endGame").setValue(false);
                    }
                    setPlayerName();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d("setName", error.toString());
            }
        });
    }

    public void checkIfIsPlayer(){
        DatabaseReference myRef = userRef;

        // Read from the database
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((TAG.equals(""))) {
                    String UID = firebaseUser.getUid();
                    USER = dataSnapshot.child(UID + "/screenName").getValue().toString();
                    if(USER.equals(TEMPUSER1) || USER.equals(TEMPUSER2)) {
                        if (wAct != null) {
                            wAct.logIn(isAuthenticated());
                        } else {
                            nuAct.logIn(isAuthenticated());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    public void logOut(){
        FirebaseAuth.getInstance().signOut();
    }



    public void setAuthenticated(boolean b) {
        authenticated = b;
    }
}
