package com.example.submarines;


import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class FireBaseStore {

    public void saveGame(AppCompatActivity activity) {

        HashMap<String, Object> map = new HashMap<>();
        map.put("gameName", "game1");
        map.put("gameResult", "P1 Wins");
        try {
            // games/game_###/player1Board
            // games/game_###/player2Board
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("games").document("5").set(
                    map
            ).addOnSuccessListener(activity,
                    aVoid -> Toast.makeText(activity, "Saved", Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(activity,
                    aVoid -> Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
