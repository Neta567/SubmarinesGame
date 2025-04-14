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
import com.google.firebase.firestore.Query;
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
    public void returnBestScore(Callback<Float> callback) {
        ArrayList<Float> bestScore = new ArrayList<>();;
        try {
            db.collection("Games")
                    //.whereGreaterThanOrEqualTo("gameScore", 0)
                    .orderBy("gameScore", Query.Direction.DESCENDING)
                    .limit(1)
                    //.count()
                    .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        bestScore.add(queryDocumentSnapshots.getDocuments().get(0).getDouble("gameScore").floatValue());
                        callback.onSuccess(bestScore.get(0));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.d("error", e.getMessage());
                        callback.onFailure(null);
                    }
                });
        } catch (Exception e) {
            Log.d("error", e.getMessage());
        }
//        while(bestScore.isEmpty()) {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

    }

    public interface Callback<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }
}
