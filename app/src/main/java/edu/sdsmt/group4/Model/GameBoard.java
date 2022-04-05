package edu.sdsmt.group4.Model;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import edu.sdsmt.group4.View.GameBoardView;

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
    private int totalRound;
    private final Context context;

    public GameBoard(Context context, GameBoardView view) {
        this.context = context;
        for (int i = 0; i < 21; i++) {
            Collectable collectable = new Collectable(context, i, 0.2f);
            collectables.add(collectable);
        }
    }

    public ArrayList<Collectable> getCollectables () {
        return collectables;
    }

    public void capture (CaptureObject capture){
        ArrayList<Collectable> collected = capture.getContainedCollectables(collectables);
        for (Collectable c : collected)
            collectables.remove(c);
        switch (currentPlayer.getId()) {
            case 0:
                players.get(0).incScore(collected.size());
                currentPlayer = players.get(1);
                break;
            case 1:
                players.get(1).incScore(collected.size());
                currentPlayer = players.get(0);
                rounds--;
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
        bundle.putInt(CURRENT_PLAYER_ID, currentPlayer.getId());
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

            currentPlayer = new Player(players.get(id).getName(), id);
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
            return currentPlayer.getId();
        }

        public String getPlayer1Score () {
            return String.valueOf(players.get(0).getScore());
        }

        public String getPlayer2Score () {
            return String.valueOf(players.get(1).getScore());
        }

        public String getPlayer1Name () {
            return players.get(0).getName();
        }

        public String getPlayer2Name () {
            return players.get(1).getName();
        }

        public void addPlayer ( String screenName ) {
            players.add(new Player(screenName, players.size()));
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
            snapshot.child("game/currPlayer").setValue(currentPlayer.getId());
            snapshot.child("game/numRounds").setValue(rounds);
            snapshot.child("player1/score").setValue(players.get(0).getScore());
            snapshot.child("player1/screenName").setValue(players.get(0).getName());
            snapshot.child("player1/score").setValue(players.get(1).getScore());
            snapshot.child("player2/screenName").setValue(players.get(1).getName());
        }

    public String getPlayer2Email() {
        return "testPlaye2r@gmail.com";
        // return players.get(1).getEmail();
    }

    public String getPlayer1Email() {
        return "testPlayer@gmail.com";
        //return players.get(0).getEmail();
    }
}
