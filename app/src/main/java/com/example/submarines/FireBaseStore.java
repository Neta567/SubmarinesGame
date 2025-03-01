package com.example.submarines;


import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.submarines.model.GameModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class FireBaseStore {

    public static FireBaseStore INSTANCE = new FireBaseStore();

    public void saveGame(GameModel model) {

        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Games")
                    .document(model.gameId).set(model);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
