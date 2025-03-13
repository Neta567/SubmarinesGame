package com.example.submarines;


import com.example.submarines.model.GameModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseStore {

    public static FireBaseStore INSTANCE = new FireBaseStore();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveGame(GameModel model) {
        try {
            db.collection("Games")
                    .document(model.getGameId())
                    .collection("Players")
                    .document(model.getCurrentPlayerName()).set(model);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getOtherPlayerName(GameModel model) {
        try {
            db.collection("Games").document(model.getGameId())
                    .collection("Players")
                    .whereEqualTo("currentPlayerName", model.getCurrentPlayerName())
                    //.whereNotEqualTo("currentPlayerName", model.getCurrentPlayerName())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String otherPlayerName = "";
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
    public void getOpenGameId(final Callback<String> callback) {
        db.collection("/Games")
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Open game found
                        String gameId = querySnapshot.getDocuments().get(0).getId();
                        callback.onSuccess(gameId);
                    } else {
                        // No open game found
                        callback.onFailure(null);
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
