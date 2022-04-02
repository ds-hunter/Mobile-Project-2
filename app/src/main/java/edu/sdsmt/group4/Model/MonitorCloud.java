package edu.sdsmt.group4.Model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MonitorCloud {
    public final static MonitorCloud INSTANCE = new MonitorCloud();

    private String USER;
    private String EMAIL;
    private String PASSWORD;
    private String TAG;
    // Firebase instance variables
    private final FirebaseAuth userAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser;
    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
    private boolean authenticated = false;

    public boolean isAuthenticated(){
        return authenticated;
    }
    /**
     *  private to defeat instantiation.
     */
    private MonitorCloud() {

    }

    public void login(String user, String email, String passwd, String player){
        USER = user;
        EMAIL = email;
        PASSWORD = passwd;
        TAG = player;
        createUser();
        startAuthListening();
    }

    private void createUser() {
        Task<AuthResult> result = userAuth.createUserWithEmailAndPassword(EMAIL, PASSWORD);
        result.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                    firebaseUser = userAuth.getCurrentUser();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("/screenName/"+USER, true);
                    result.put("/"+firebaseUser.getUid()+"/name", USER);
                    result.put("/"+firebaseUser.getUid()+"/password", PASSWORD);
                    result.put("/"+firebaseUser.getUid()+"/email", EMAIL);
                    userRef.updateChildren(result);
                }else if(task.getException().getMessage().equals("The email address is already in use by another account.")){
                    signIn();
                } else {
                    Log.d(TAG, "Problem: " + task.getException().getMessage());
                    authenticated = false;
                    signIn();
                    firebaseUser = userAuth.getCurrentUser();
                }
            }
        });
    }


    private void signIn() {
        Task<AuthResult> result = userAuth.signInWithEmailAndPassword(EMAIL, PASSWORD);
        result.addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                    authenticated = true;

                } else {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    authenticated = false;
                }
            }
        });
    }

    private void startAuthListening() {
        userAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if ( firebaseUser != null) {

                    // User is signed in
                    authenticated = true;
                    Log.d(TAG, "onAuthStateChanged:signed_in:" +  firebaseUser.getUid());
                } else {

                    // User is signed out
                    authenticated = false;
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        });

    }

    public String getUserUid(){
        //stop people from getting the Uid if not logged in
        if(firebaseUser == null)
            return "";
        else
            return firebaseUser.getUid();
    }

}