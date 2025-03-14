package com.example.submarines;


import com.example.submarines.model.GameModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FireBaseStore {

    public static FireBaseStore INSTANCE = new FireBaseStore();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveGame(GameModel model) {
        try {
            DocumentReference gameDocumentRef = db.collection("Games")
                    .document(model.getGameId());
            gameDocumentRef.set(model.getGame())
                    .addOnSuccessListener(v -> System.out.println("Game saved"));
            savePlayer(gameDocumentRef, model);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void savePlayer(DocumentReference gameDocumentRef, GameModel model) {
        try {
            gameDocumentRef.collection("Players")
                    .document(model.getCurrentPlayerName())
                    .set(model.getPlayer())
                    .addOnSuccessListener(v -> System.out.println("Player saved"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getOtherPlayerName(GameModel model) {
        try {
            db.collection("Games").document(model.getGameId())
                    .collection("Players")
                    .whereNotEqualTo("currentPlayerName", model.getCurrentPlayerName())
                    //.whereNotEqualTo("currentPlayerName", model.getCurrentPlayerName())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String otherPlayerName;
                        if (documentSnapshot.size() == 1) {
                            otherPlayerName =
                                    documentSnapshot.getDocuments().get(0)
                                            .get("currentPlayerName", String.class);
                            GameModel.getInstance().setOtherPlayer(otherPlayerName);
                        }

                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return GameModel.getInstance().getOtherPlayer();
    }
    public void getOpenGameId(final Callback<Map<String, Object>> callback) {
        db.collection("Games")
                .whereEqualTo("gameState", GameModel.GameState.ONE_PLAYER_JOINED.toString())
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Open game found
                        String gameId = querySnapshot.getDocuments().get(0).getId();
                        Map<String, Object> gameData = new HashMap<>();
                        gameData.put("gameId", gameId);

                        String otherPlayerName = "Player2";
                        gameData.put("otherPlayer", otherPlayerName);

                        callback.onSuccess(gameData);
                    } else {
                        // No open game found
                        callback.onSuccess(new HashMap<>());
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    e.printStackTrace();
                    callback.onFailure(null);
                });
    }

    public interface Callback<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}
