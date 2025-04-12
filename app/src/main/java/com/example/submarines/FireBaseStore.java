package com.example.submarines;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FireBaseStore {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveGame(int gameId, int gameScore)
    {
        HashMap<String, Integer> game = new HashMap<>();
        game.put("gameId", gameId);
        game.put("gameScore", gameScore);

        try
        {
            db.collection("Games")
                    .document(String.valueOf(gameId))
                    .set(game);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
