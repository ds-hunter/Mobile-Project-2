package edu.sdsmt.group1.Model;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.Random;

import edu.sdsmt.group1.View.GameBoardView;

public class GameBoard {

    private static final String PLAYER_NAMES ="GameBoard.playerNames" ;
    private static final String PLAYER_SCORES ="GameBoard.playerScores" ;
    private static final String PLAYER_EMAILS ="GameBoard.playerEmails" ;
    private static final String CURRENT_PLAYER_ID = "GameBoard.currentPlayerScore";
    private final ArrayList<Collectable> collectables = new ArrayList<>();
    private Player currentPlayer;
    private final ArrayList<Player> players = new ArrayList<>();
    private final static String REL_LOCATIONS = "GameBoard.relLocations";
    private final static String LOCATIONS = "GameBoard.locations";
    private final static String IDS = "GameBoard.ids";
    private int rounds;
    private final Context context;
    GameBoardView view;
    private float canvasWidth;
    private float canvasHeight;
    private float viewWidth;
    private long player1Update = System.currentTimeMillis() / 1000L;
    private long player2Update = System.currentTimeMillis() / 1000L;
    private boolean isForceEndGame = false;

    public GameBoard(Context context, GameBoardView view) {
        this.context = context;
        this.view = view;
    }

    public void setCanvasParam(float cWidth, float cHeight, float vWidth){
        canvasWidth = cWidth;
        canvasHeight = cHeight;
        viewWidth = vWidth;
    }

    public void populateGameBoard(){
        Random rand = new Random();

        float canvasx = (viewWidth - canvasWidth)/2;
        float canvasy = 0;

        for (int i = 0; i < 21; i++) {
            Collectable collectable = new Collectable(context, i, 0.2f);
            collectable.setShuffle(true);
            collectable.shuffle(canvasWidth, canvasHeight, canvasx, canvasy, rand);
            collectable.setShuffle(false);
            collectables.add(collectable);
        }
    }

    public int getCollectableAmt(){
        return collectables.size();
    }

    public ArrayList<Collectable> getCollectables () {
        return collectables;
    }

    public void capture (CaptureObject capture){
        ArrayList<Collectable> collected = capture.getContainedCollectables(collectables);
        Cloud cloud = new Cloud();
        for (Collectable c : collected)
            collectables.remove(c);

        Log.d("GETID", String.valueOf(currentPlayer.getId()));
        switch (currentPlayer.getId()) {
            case 0:
                players.get(0).incScore(collected.size());
                currentPlayer = players.get(1);
                cloud.saveToCloud(view);
                break;
            case 1:
                players.get(1).incScore(collected.size());
                currentPlayer = players.get(0);
                rounds--;
                cloud.saveToCloud(view);
                break;
        }
    }

        public void saveInstanceState (Bundle bundle){
            float[] relLocations = new float[collectables.size() * 2];
            float[] locations = new float[collectables.size() * 2];
            int[] ids = new int[collectables.size()];

        int [] playerScores = new int[players.size()];
        String [] playerNames = new String[players.size()];
        String []playerEmails = new String[players.size()];
        for (int i = 0; i < collectables.size(); i++) {
            Collectable collectable = collectables.get(i);
            relLocations[i * 2] = collectable.getRelX();
            relLocations[i * 2 + 1] = collectable.getRelY();
            locations[i * 2] = collectable.getX();
            locations[i * 2 + 1] = collectable.getY();
            ids[i] = collectable.getId();
        }
        for(int i = 0; i < players.size(); i ++) {
            playerNames[i] = players.get(i).getName();
            playerScores[i] = players.get(i).getScore();
            playerEmails[i] = players.get(i).getEmail();
        }
        bundle.putFloatArray(REL_LOCATIONS, relLocations);
        bundle.putFloatArray(LOCATIONS, locations);
        bundle.putIntArray(IDS,  ids);
        bundle.putIntArray(PLAYER_SCORES, playerScores);
        bundle.putStringArray(PLAYER_NAMES, playerNames);
        bundle.putStringArray(PLAYER_EMAILS, playerEmails);
        //bundle.putInt(CURRENT_PLAYER_ID, currentPlayer.getId());
    }

        public void loadInstanceState (Bundle bundle){
            float[] relLocations = bundle.getFloatArray(REL_LOCATIONS);
            float[] locations = bundle.getFloatArray(LOCATIONS);
            int[] ids = bundle.getIntArray(IDS);
            String[] playerNames = bundle.getStringArray(PLAYER_NAMES);
            int[] playerScores = bundle.getIntArray(PLAYER_SCORES);
            int id = bundle.getInt(CURRENT_PLAYER_ID);
            String [] playerEmails = bundle.getStringArray(PLAYER_EMAILS);
            collectables.clear();

            for (int i = 0; i < ids.length; i++) {
                Collectable collectable = new Collectable(context, ids[i], 0.2f);
                collectable.setRelX(relLocations[i * 2]);
                collectable.setRelY(relLocations[i * 2 + 1]);
                collectable.setX(locations[i * 2]);
                collectable.setY(locations[i * 2 + 1]);
                collectable.setShuffle(false);
                collectables.add(collectable);
            }
            for (int i = 0; i < playerNames.length; i++) {
                players.add(new Player(playerNames[i], i));
                players.get(i).incScore(playerScores[i]);
                players.get(i).setEmail(playerEmails[i]);
            }

             //currentPlayer = new Player(players.get(id).getName(), id);
        }

        public boolean isEndGame () {
            return rounds <= 0 || collectables.isEmpty();
        }

        public void setDefaultPlayer () {
            if (!players.isEmpty())
                currentPlayer = players.get(0);
        }

        public void setRounds ( int r){
            rounds = r;
        }

        public String getRounds () {
            return String.valueOf(rounds);
        }

        public int getNumPlayers () {
            return players.size();
        }

        public int getCurrentPlayerId () {
            if (currentPlayer != null) {
                return currentPlayer.getId();
            }
            return 0;
        }

        public Player getCurrentPlayer() {
            return currentPlayer;
        }

        public String getPlayer1Score () {
            if (getNumPlayers() < 1)
                return "0";
            return String.valueOf(players.get(0).getScore());
        }

        public String getPlayer2Score () {
            if (getNumPlayers() < 2)
                return "0";
            return String.valueOf(players.get(1).getScore());
        }

        public long getPlayer1Time() { return System.currentTimeMillis() / 1000L - player1Update; }

        public long getPlayer2Time() { return System.currentTimeMillis() / 1000L - player2Update; }

        public void player1Update() {
            player1Update = System.currentTimeMillis() / 1000L;
        }

        public void player2Update() {
            player2Update = System.currentTimeMillis() / 1000L;
        }

        public void setPlayer1Score (int score) {
            if (getNumPlayers() < 1)
                return;
            players.get(0).setScore(score);
        }

        public void setPlayer2Score (int score) {
            if (getNumPlayers() < 2)
                return;
            players.get(1).setScore(score);
        }

        public String getPlayer1Name () {
            if (players.size() >= 1)
                return players.get(0).getName();
            return "Player 1";
        }

        public String getPlayer2Name () {
            if (players.size() >= 2)
                return players.get(1).getName();
            return "Player 2";
        }

        public void addPlayer ( String screenName, String email ) {
            players.add(new Player(screenName,  players.size()));
            players.get(players.size() - 1 ).setEmail(email);
        }

        public void setPlayer ( int id){
            currentPlayer = players.get(id);
        }

        public boolean hasPlayer ( int index){
            if (players.size() >= index) return false;
            return players.get(index) != null;
        }

        public void saveJSON (DatabaseReference snapshot){
            for (int i = 0; i < collectables.size(); i++) {
                snapshot.child("game/collectables/c" + i + "/relx").setValue(collectables.get(i).getRelX());
                snapshot.child("game/collectables/c" + i + "/rely").setValue(collectables.get(i).getRelY());
            }
            snapshot.child("game/collectableAmt").setValue(collectables.size());
            snapshot.child("game/currPlayer").setValue(currentPlayer.getId());
            snapshot.child("game/currRound").setValue(getRounds());
            snapshot.child("player1/score").setValue(getPlayer1Score());
            snapshot.child("player1/screenName").setValue(getPlayer1Name());
            snapshot.child("player2/score").setValue(getPlayer2Score());
            snapshot.child("player2/screenName").setValue(getPlayer2Name());
            snapshot.child("player1/email").setValue(getPlayer1Email());
            snapshot.child("player2/email").setValue(getPlayer2Email());
        }

    public String getPlayer2Email() {
       // return "testPlaye2r@gmail.com";
         return players.get(1).getEmail();
    }

    public String getPlayer1Email() {
        //return "testPlayer@gmail.com";
        return players.get(0).getEmail();
    }

    public void addCollectable(int id, float relX, float relY, boolean shuffle) {
        Collectable c = new Collectable(context, id, 0.2f);
        c.setRelX(relX);
        c.setRelY(relY);
        c.setShuffle(shuffle);
        collectables.add(c);
    }
    public void clearCollectables() {
        collectables.clear();
    }

    public boolean isForceEndGame() {
        return isForceEndGame;
    }

    public void setForceEndGame(boolean forceEndGame) {
        isForceEndGame = forceEndGame;
    }
}
