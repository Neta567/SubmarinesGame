package com.example.submarines;


import android.widget.TextView;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class FireBaseStore {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveGame(String gameId, float gameScore, String name)
    {
        HashMap<String, Object> game = new HashMap<>();
        game.put("gameId", gameId);
        game.put("gameScore", gameScore);
        game.put("name", name);

        db.collection("Games").document(String.valueOf(gameId)).set(game);

    }

    public void updateScoresInDailog(TextView textView)
    {

        db.collection("Games").orderBy("gameScore", Query.Direction.DESCENDING).limit(1).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>()
                {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots)
                    {
                        textView.setText(queryDocumentSnapshots.getDocuments().get(0).getDouble("gameScore").toString());
                    }
                }
                );

    }
}
