package com.example.submarines.helpers;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import com.example.submarines.R;

public class DialogService {

    private final static DialogService instance = new DialogService(); // מופע של הדיאלוג
    private Dialog turnDialog; // הפעלת דיאלוג עבור התורות
    private Dialog winLooseDialog; // דיאלוג עבור המנצח

    public static DialogService getInstance() { return instance; }

    public Dialog getOpponentTurnDialog(Context context) { // שם דיאלוג לפי התורות
        if(turnDialog == null) {
            turnDialog = new Dialog(context); // מגדיר דיאלוג
            turnDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // מביא לו את המסך שעליו הוא צריך להפעיל את הדיאלוג
            turnDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT)); // הולך למסך והופך את המסגרת לשקופה
            turnDialog.setContentView(R.layout.game_turn); // מפעיל את הדיאלוג שהוגדר באקסמל
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
