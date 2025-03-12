package com.example.submarines;


import com.example.submarines.model.GameModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBaseStore {

    public static FireBaseStore INSTANCE = new FireBaseStore();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                        if(documentSnapshot.size() == 1) {
                            otherPlayerName =
                                    documentSnapshot.getDocuments().get(0)
                                            .get("currentPlayerName", String.class);
                            GameModel.getInstance().setOtherPlayer(otherPlayerName);
                        }

                    });
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return GameModel.getInstance().getOtherPlayer();
    }

    public void setReference(GameModel model) {
//        db.collection("Games").document(model.getGameId()).addSnapshotListener(
//                (documentSnapshot, e) -> {
//        )
    }

}
