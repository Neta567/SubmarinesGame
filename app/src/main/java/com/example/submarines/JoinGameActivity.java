package com.example.submarines;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Map;
import java.util.Random;

public class JoinGameActivity extends AppCompatActivity {

    private Context context; // קונטקסט של הקלאס הזה לשלוח לגיים אקטיביטי לאחר מכן
    private final FireBaseStore fireBaseStore = new FireBaseStore();
    private EditText editTextUsername;
    private Button btnJoin;
    private TextView textGameStarting,textGameStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game); // מראה את הנתונים על המסך
        this.context = this;

        int random = new Random().nextInt(100);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextUsername.setText("Piccachu" + random);

        btnJoin = findViewById(R.id.btnJoin);
        textGameStarting = findViewById(R.id.textGameStarting);
        textGameStatus = findViewById(R.id.textGameStatus);

        btnJoin.setOnClickListener(v -> { // אם לחצו על הכפתור של גויין
            btnJoin.setEnabled(false); // אי אפשר ללחוץ יותר עליו
            textGameStarting.setText("Joining...\n"); // מריץ על המסך את הסטרינג הזה

            Animation anim = AnimationUtils.loadAnimation(context, R.anim.blink); // יוצר אנימציה לסטרינג הקודם
            anim.setDuration(2000); // אורך של 2 דקות
            textGameStarting.startAnimation(anim); // מפעיל את האנימציה

            String player = editTextUsername.getText().toString(); // לוקח את השם של השחקן שהכנסנו
            fireBaseStore.getOpenGameId(player, new FireBaseStore.Callback<Map<String, Object>>() { // פנייה לפיירבייס שמחפש לאיזה משחק להוסיף את השחקן שהתחבר ומביא האש מאפ של שם של השחקן השני ושם משחק שפתוח
                @Override
                public void onSuccess(Map<String, Object> result) {

                    String gameId = String.valueOf(new Random().nextInt(10000)); // מייצר סתם קוד למשחק חדש במידת הצורך
                    boolean isExistingGame = (result != null && !result.isEmpty()); // אם קיים משחק פתוח?
                    if (isExistingGame) { // אם קיים משחק
                        // Open game found, use existing game ID
                        gameId = result.get("gameId").toString(); // הקוד יהפוך למה שהגיע אליו
                    }
                    // No open game found, create a new game ID
                    GameModel.getInstance().setGameId(gameId); // מעדכנים את הגיים מודל עם הקוד משחק
                    GameModel.getInstance().setCurrentPlayer(player); // מעדכנים את השם שחקן שלי במודל גם כן

                    StringBuilder gameStatusBuilder = new StringBuilder(); // מאפשר לחבר שתי מחרוזות ביחד
                    gameStatusBuilder.append("Game ID: :").append(gameId).append("\n"); // משרשר את ההתחלה לקוד עצמו
                    gameStatusBuilder.append("Player: ").append(player).append(" Joined.\n"); // גם כן השרשור

                    if(isExistingGame) { // שוב בודק אם קיים - אם כן -
                        String otherPlayer = result.get("otherPlayer").toString(); // ניקח ממה שהגיע את השם של השחקן השני

                        GameModel.getInstance().setOtherPlayer(otherPlayer); // נעדכן גם אותו במודל

                        gameStatusBuilder.append("Player: ").append(otherPlayer).append(" also joined\n"); // נשרשר גם אותו
                        textGameStarting.setText("Starting..."); // נתחיל טקסט של התחלה כי הצלחנו לחבר 2 שחקנים יחד
                        textGameStarting.startAnimation(anim); // הפעלת אנימציה
                        GameModel.getInstance().setGameState(GameModel.GameState.TWO_PLAYERS_JOINED); // מעביר את מצב המשחק ל2 שהתחברו

                    } else { // אם לא קיים משחק פתוח
                        gameStatusBuilder.append("No other player joined."); // מוסיף למה שכתוב על המסך את זה
                        textGameStarting.setText("Waiting..."); // רושם ממתין
                        textGameStarting.startAnimation(anim); // מפעיל על זה אנימציה
                        GameModel.getInstance().setGameState(GameModel.GameState.ONE_PLAYER_JOINED); // מעדכן את מצב המשחק לשחקן אחד שהתחבר

                        fireBaseStore.subscribeForGameStateChange(
                                GameModel.getInstance().getGameId(), new FireBaseStore.Callback<Map<String, Object>>() { // במקרה שקרה שינוי חיצוני כלשהו אנחנו מקבלים עם הקול באק סנאפשוט של השינוי (מישהו חדש רוצה להיכנס למשחק)
                                    @Override
                                    public void onSuccess(Map<String, Object> result) { // השינוי שחוזר כאש מאפ
                                        if (result != null && !result.isEmpty()) {
                                            if(result.containsKey(GameModel.GameModelFields.game_state.toString())) { // אם יש בשינוי שדה של מצב משחק אז -
                                                GameModel.getInstance().setGameState(
                                                        GameModel.GameState.valueOf((String) result.get(GameModel.GameModelFields.game_state.toString()))); // תשנה במודל את המצב משחק למצב משחק שקרה השינוי

                                                if(GameModel.getInstance().getGameState() == GameModel.GameState.TWO_PLAYERS_JOINED) { // אם המצב נהפך ל2 משתתפים נכנסו למשחק אז -
                                                    fireBaseStore.getOtherPlayerName(GameModel.getInstance().getGameId(),
                                                            GameModel.getInstance().getCurrentPlayerName(), new FireBaseStore.Callback<Map<String, Object>>() { // מביא אי די וש םשל הנוכחי ורוצה שיחזיר לו את השם של השחקן השני
                                                                @Override
                                                                public void onSuccess(Map<String, Object> result) { // השם מגיע לכאן
                                                                    GameModel.getInstance().setOtherPlayer(result.get("otherPlayer").toString()); // מעדכנים את השם במודל
                                                                    startGame(); // מתחילים את המשחק
                                                                }

                                                                @Override
                                                                public void onFailure(Exception e) {
                                                                    e.printStackTrace();
                                                                }
                                                            });
                                                }
                                            };

                                        }
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        Log.d("Join Game", "Failed to handle update in game state: ", e);
                                    }
                                });
                    }

                    textGameStatus.setText(gameStatusBuilder.toString()); // אם היה פתוח אז תשים את הטקסט
                    fireBaseStore.saveGame(GameModel.getInstance()); // שומר את כל הנתונים של המשחק בפייר סטור

                    startGame(); // מתחיל את המשחק
                }
                @Override
                public void onFailure(Exception e) {
                    Log.d("Join Game", "Error getting game ids: ", e);
                }
            });
        });
    }

    private void startGame() {
        if(GameModel.getInstance().getGameState() == GameModel.GameState.TWO_PLAYERS_JOINED) { // אם המצב ששני שחקנים התחברו -
            Handler handler = new Handler(Looper.getMainLooper()); // מפעילים הנדלר שזה מתזמן מעבר בין אקטיביטים
            // Create a Runnable to start the new Activity
            Runnable startNewActivityRunnable = () -> { // מבצעים את הפעולה הזאת שזה מעכב את המסך ל2 שניות ואז מבצע את הפעולה הבאה
                try {
                    Intent intent = new Intent(context, GameActivity.class); // מעבר לגיים אקטיביטי
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            // Delay the start of the new Activity by 2 seconds
            handler.postDelayed(startNewActivityRunnable, 2000);
        }
    }
}