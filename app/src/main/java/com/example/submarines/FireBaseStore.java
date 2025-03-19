package com.example.submarines;


import com.example.submarines.model.GameModel;
import com.example.submarines.model.Player;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FireBaseStore {

    public static final String GAMES = "Games";
    public static final String PLAYERS = "Players";
    public static FireBaseStore INSTANCE = new FireBaseStore();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void saveGame(GameModel model) {
        try {
            DocumentReference gameDocumentRef = db.collection(GAMES)
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
            gameDocumentRef.collection(PLAYERS)
                    .document(model.getCurrentPlayerName())
                    .set(model.getPlayer())
                    .addOnSuccessListener(v -> System.out.println("Player saved"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getOtherPlayerName(String gameId, String currentPlayerName, Callback<Map<String, Object>> callback) {
        try {
            //TODO: fix bug in case two players join with the same name
            db.collection(GAMES).document(gameId)
                    .collection(PLAYERS)
                    .whereNotEqualTo(Player.PlayerFields.name.toString(), currentPlayerName)
                    //.whereNotEqualTo("currentPlayerName", model.getCurrentPlayerName())
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        if (!querySnapshot.isEmpty()) {
                            String otherPlayerName =
                                    querySnapshot.getDocuments().get(0)
                                            .get(Player.PlayerFields.name.toString(), String.class);

                            Map<String, Object> playerData = new HashMap<>();
                            playerData.put("otherPlayer", otherPlayerName);

                            callback.onSuccess(playerData);
                        } else {
                            callback.onFailure(null);
                        }

                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getOpenGameId(String playerName, final Callback<Map<String, Object>> callback) {
        db.collection(GAMES)
                .whereEqualTo(GameModel.GameModelFields.game_state.toString(),
                        GameModel.GameState.ONE_PLAYER_JOINED.toString())
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Open game found
                        String gameId = querySnapshot.getDocuments().get(0).getId();
                        Map<String, Object> gameData = new HashMap<>();
                        gameData.put("gameId", gameId);

                        getOtherPlayerName(gameId, playerName, new Callback<Map<String, Object>>() {
                            @Override
                            public void onSuccess(Map<String, Object> result) {
                                gameData.putAll(result);
                                callback.onSuccess(gameData);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                callback.onFailure(e);
                            }
                        });

                        //String otherPlayerName = "Player2";
                        //gameData.put("otherPlayer", otherPlayerName);


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

    public void subscribeForGameStateChange(String gameId, Callback<Map<String, Object>> callback) {
        DocumentReference gameDocumentRef = db.collection(GAMES)
                .document(gameId);
        gameDocumentRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                callback.onFailure(e);
                return;
            }

           //handle only server events
           if(snapshot != null && !snapshot.getMetadata().hasPendingWrites()) {
               if (snapshot.exists()) {
                   callback.onSuccess(snapshot.getData());
               } else {
                   callback.onFailure(null);
               }
           }
        });
    }

    public void subscribeForPlayerStateChange(String gameId, String playerName, Callback<Map<String, Object>> callback) {

        DocumentReference playerDocumentRef = db.collection(GAMES)
                .document(gameId).collection(PLAYERS).document(playerName);
        playerDocumentRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                callback.onFailure(e);
                return;
            }

            if (snapshot != null && !snapshot.getMetadata().hasPendingWrites()) {
                if (snapshot.exists()) {
                    callback.onSuccess(snapshot.getData());
                } else {
                    callback.onFailure(null);
                }
            }
        });
    }



    public interface Callback<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }
}
