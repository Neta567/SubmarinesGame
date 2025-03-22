package com.example.submarines.helpers;


import com.example.submarines.model.GameModel;
import com.example.submarines.model.Player;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FireBaseStore {

    public static final String GAMES = "Games";
    public static final String PLAYERS = "Players";
    private static final FireBaseStore INSTANCE = new FireBaseStore();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FireBaseStore() {}

    public static FireBaseStore getInstance() { return INSTANCE; }

    public void saveGame(GameModel model) {
        try {
            DocumentReference gameDocumentRef = db.collection(GAMES)
                    .document(model.getGameId());
            gameDocumentRef.set(model.getGame());
                    //.addOnSuccessListener(v -> System.out.println("Game saved"));
            savePlayer(gameDocumentRef, model);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void savePlayer(DocumentReference gameDocumentRef, GameModel model) {
        try {
            gameDocumentRef.collection(PLAYERS)
                    .document(model.getCurrentPlayerName())
                    .set(model.getPlayer());
                   // .addOnSuccessListener(v -> System.out.println("Player saved"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getOtherPlayerName(String gameId, String currentPlayerName, Callback<Map<String, Object>> callback) { // לפי האידי הוא מחזיר את המידע על השחקן
        try {
            //TODO: fix bug in case two players join with the same name
            db.collection(GAMES).document(gameId) // הולך לקולקשיין משחקים ואז למסמך משחק
                    .collection(PLAYERS)// ואז לקולקשיין שחקנים
                    .whereNotEqualTo(Player.PlayerFields.name.toString(), currentPlayerName) // מחפש איזה שחקן מתאים לשם השחקן השני
                    .get()
                    .addOnSuccessListener(querySnapshot -> { // מחזיר תמונת מצב
                        if (!querySnapshot.isEmpty()) {
                            String otherPlayerName =
                                    querySnapshot.getDocuments().get(0)
                                            .get(Player.PlayerFields.name.toString(), String.class); // מכניס למשתנה את השם שמצא

                            Map<String, Object> playerData = new HashMap<>(); // יוצר האש מאפ
                            playerData.put("otherPlayer", otherPlayerName); // מכניס לשם את השם

                            callback.onSuccess(playerData); // שולח חזרה את השם לאיפה שקראו לפעולה הזו
                        } else {
                            callback.onFailure(null);
                        }
                    });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getOpenGameId(String playerName, final Callback<Map<String, Object>> callback) { // מקבל שם של שחקן
        db.collection(GAMES) //ניגש לקולקשיין של המשחקים
                .whereEqualTo(GameModel.GameModelFields.game_state.toString(), // הולך למצב משחק
                        GameModel.GameState.ONE_PLAYER_JOINED.toString()) // ובודק לאיזה משחקים המצב הוא של שחקן אחד שהתחבר
                .limit(1) // לוקח רק משחק אחד משם
                .get()
                .addOnSuccessListener(querySnapshot -> { // מחזיר סנאפשוט - תמונת מצב שמכילה מסמך של המשחק שהוא מצא

                    //  כל מסמך שמור עם האש מאפים
                    if (!querySnapshot.isEmpty()) { // אם קיים משחק כזה - בדרך כלל תמיד יהיה
                        // Open game found
                        String gameId = querySnapshot.getDocuments().get(0).getId(); // לוקח את הסנאפשוט הולך למסמך ולמיקום 0 שזה האידי
                        Map<String, Object> gameData = new HashMap<>(); // יוצר האש מאפ חדש
                        gameData.put("gameId", gameId); // מכניס אליו את הגיים אידי שהוא מצא שפתוח

                        getOtherPlayerName(gameId, playerName, new Callback<Map<String, Object>>() { // מקבלים את השם של השחקן השני שנמצא במשחק הפתוח
                            @Override
                            public void onSuccess(Map<String, Object> result) {
                                gameData.putAll(result); // נהיה האש מאפ עם אי די ושם של השחקן השני
                                callback.onSuccess(gameData); // מחזיר את האש מאפ לאן שקראו לו
                            }

                            @Override
                            public void onFailure(Exception e) {
                                callback.onFailure(e);
                            }
                        });

                    } else {
                        // No open game found
                        callback.onSuccess(new HashMap<>());
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle errors
                    callback.onFailure(null);
                });
    }

    public void subscribeForGameStateChange(String gameId, Callback<Map<String, Object>> callback) { // מקבל אי די של משחק
        DocumentReference gameDocumentRef = db.collection(GAMES) // הולך לקולקשיין משחק
                .document(gameId); // והולך למסמך של הגיים איי די שנשלח אליו
        gameDocumentRef.addSnapshotListener((snapshot, e) -> { // מוסיף למסמך זה מאזין ומבצע פעולה ברגע שמתבצע שינוי
            if (e != null) {
                callback.onFailure(e);
                return;
            }

           //handle only server events
           if(snapshot != null && !snapshot.getMetadata().hasPendingWrites()) { // אם התמונת מצב של השינוי לא ריקה והתמונת מצב זה שינוי חיצוני ולא שלי אז -
               if (snapshot.exists()) {
                   callback.onSuccess(snapshot.getData()); // מחזיר את המסמך עם השינוי
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
