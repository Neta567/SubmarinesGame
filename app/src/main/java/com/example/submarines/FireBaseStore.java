package com.example.submarines;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FireBaseStore {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveGame(int gameId, float gameScore, String name)
    {
        HashMap<String, Object> game = new HashMap<>();
        game.put("gameId", gameId);
        game.put("gameScore", gameScore);
        game.put("name", name);


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
    public float returnBestScore() {
        ArrayList<Float> bestScore = new ArrayList<>();;
        try {
            db.collection("Games")
                    //.whereGreaterThanOrEqualTo("gameScore", 0)
                    //.orderBy("gameScore")
                    //.limit(1)
                    //.count()
                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                Log.d("error", "e.getMessage()");
//                            } else {
//                                Log.d("error", "e.getMessage()");
//                            }
//                        }
//                    });
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        bestScore.add(queryDocumentSnapshots.getDocuments().get(0).getDouble("gameScore").floatValue());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("error", e.getMessage());
                    }
                });
        } catch (Exception e) {
            Log.d("error", e.getMessage());
        }
        while(bestScore.isEmpty()) {}

        return bestScore.get(0);
    }
}
