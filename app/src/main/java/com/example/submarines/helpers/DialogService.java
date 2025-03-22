package com.example.submarines.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import com.example.submarines.R;

public class DialogService {

    private final static DialogService instance = new DialogService();
    private Dialog turnDialog;
    private Dialog winLooseDialog;

    public static DialogService getInstance() { return instance; }

    public Dialog getOpponentTurnDialog(Context context) {
        if(turnDialog == null) {
            turnDialog = new Dialog(context);
            turnDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            turnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            turnDialog.setContentView(R.layout.game_turn);
        }
        return turnDialog;
    }

    public Dialog getWinLooseDialog(Context context, View view) {
        if(winLooseDialog == null) {
            winLooseDialog = new Dialog(context);
            winLooseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            winLooseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            winLooseDialog.setContentView(view);
        }
        return winLooseDialog;
    }
}
