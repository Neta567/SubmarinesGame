package com.example.submarines;


import com.example.submarines.model.GameModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseStore {

    public static FireBaseStore INSTANCE = new FireBaseStore();

    public void saveGame(GameModel model) {

        try {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Games")
                    .document(model.getGameId()).set(model);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
