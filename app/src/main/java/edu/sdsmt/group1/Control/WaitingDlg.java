package edu.sdsmt.group1.Control;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import edu.sdsmt.group1.Model.Cloud;
import edu.sdsmt.group1.R;

public class WaitingDlg extends DialogFragment {
    private GameBoardActivity activity;

    public WaitingDlg() {
    }

    public void setActivity(GameBoardActivity a) {
        activity = a;
    }

    public void unAuthorized(){
        Cloud cloud = new Cloud();
        cloud.reset();
        activity.finish();
        activity.stopLoadTimer();}
    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.loading_dlg, null);
        builder.setView(view);

        // Add a cancel button
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {
            // UPLOAD TASK, DELETE USER FROM FIREBASE
            Cloud cloud = new Cloud();
            cloud.reset();
            activity.finish();
            activity.stopLoadTimer();
        });


        return builder.create();
    }
}
