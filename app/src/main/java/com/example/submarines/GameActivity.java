package com.example.submarines;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.submarines.boards.BoardGame;
import com.example.submarines.databinding.ActivityGameBinding;
import com.example.submarines.databinding.GameOverBinding;
import com.example.submarines.helpers.DialogService;
import com.example.submarines.helpers.FireBaseStore;
import com.example.submarines.helpers.MusicService;
import com.example.submarines.model.GameModel;
import com.example.submarines.model.Player;
import com.google.gson.Gson;

import java.util.Map;
import java.util.Objects;

public class GameActivity extends AppCompatActivity {

    private ActivityGameBinding binding; // מייצר ביינדינג חדש
    private GameOverBinding gameOverBinding; // ?
    private MusicService musicService;
    private final FireBaseStore fireBaseStore = new FireBaseStore();

    private MainActivity mainActivity;

    private DialogService dialogService = new DialogService();
    private boolean isMusicPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater()); // מקשר את ....
        setContentView(binding.rootLayout); // מפעיל את האקסמל על המסך
        binding.setViewModel(GameModel.getInstance()); // מתחיל קישור נתונים בין המודל לאקטיביטי

        gameOverBinding = GameOverBinding.inflate(getLayoutInflater()); // ??

        BoardGame myBoard = new BoardGame(this); // מייצר רכיב חדש המכיל את שתי הלוחות
        myBoard.subscribeOnFireEvent(() -> { // שמים מאזין על שתי הלוחות האלה ובמידה שקרה שינוי ונלחץ משהו אז נפעיל את הדיאלוג שיחסום את המסך
            dialogService.getOpponentTurnDialog(this).show();
            return null;
        });
        binding.ll.addView(myBoard); // מפעילים את הרכיב על המסך ומראים את 2 הלוחות
        startMusicService(); // מפעילים את המוזיקה

        binding.startGameButton.setOnClickListener(v -> { // במידה ולוחצים על הכפתור של התחילת משחק (וי) -
            if (myBoard.validateCanStartTheGame()) { // בודק אם ברכיב הזה הלוח של הצוללות מסודר תקין וכל הצוללות על המסך
                Toast.makeText(myBoard.getContext(), "Game Started", Toast.LENGTH_SHORT).show(); // טוסט של תחילת המסך
                GameModel.getInstance().setCurrentPlayerGameStatus(Player.PlayerGameStatus.STARTED); // בגלל שהוא לחץ על וי אז הוא מוגדר כשחקן שהתחיל את המשחק

                binding.rotationButton.setEnabled(false); // אי אפשר כבר ללחוץ על זה
                binding.rotationButton.setVisibility(View.INVISIBLE); // לא רואים את הכפתור יותר
                binding.erasureButton.setEnabled(false); // כנל על המחיקה
                binding.erasureButton.setVisibility(View.INVISIBLE);

                if (GameModel.getInstance().getOtherPlayerGameStatus() == Player.PlayerGameStatus.STARTED) { // אם גם השחקן השני התחיל את המשחק אז -
                    GameModel.getInstance().setGameState(GameModel.GameState.STARTED); // מעדכנים את מצב המשחק להתחיל
                }

                fireBaseStore.saveGame(GameModel.getInstance()); // שומרים את הנתונים החדשים בפייק סטור
                dialogService.getOpponentTurnDialog(this).show(); // מופעלת חסימת מסך

            } else {
                Toast.makeText(myBoard.getContext(), "Not all submarines are placed", Toast.LENGTH_SHORT).show(); // אם מצב הצוללות לא תקין אז תופעל הודעה
            }
        });
        binding.rotationButton.setOnClickListener(v -> myBoard.rotateSubmarine()); // אם לחצו על סיבוב צוללת אז היא תסתובב
        binding.erasureButton.setOnClickListener(v -> myBoard.resetSubmarineBoard()); // כנל על מחיקה
        binding.setup.setOnClickListener(v -> { // אם לוחצים על סידור קבוע אז זה יסדר
                    myBoard.setupBoard();
                    myBoard.invalidate();
                }
        );
        binding.startStopMusicButton.setOnClickListener(v -> { // אם לוחצים על כפתורים המוזיקה
                    if(isMusicPlaying) { // אם מופעל
                        stopMusicService(); // יכבה
                    } else { // ולהפך
                        startMusicService();
                    }
                }
        );

        fireBaseStore.subscribeForGameStateChange(
                GameModel.getInstance().getGameId(), new FireBaseStore.Callback<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        if (result != null && !result.isEmpty()) {
                            if (result.containsKey(GameModel.GameModelFields.game_state.toString())) { // אם הסנאפשוט של השינוי מכיל מצב משחק
                                GameModel.getInstance().setGameState(
                                        GameModel.GameState.valueOf((String) result.get(GameModel.GameModelFields.game_state.toString()))); // תעדכן את המצב משחק למה שהוא השתנה

                                if (GameModel.getInstance().getGameState() == GameModel.GameState.STARTED) { // אם זה שונה להתחל אז -
                                    myBoard.invalidate(); // תצייר את המסך מחדש ואז בעצם הוא ילך לאון דרו של הבורד גיים
                                } else if (GameModel.getInstance().getGameState() == GameModel.GameState.GAME_OVER) { // אם קרה שינוי והמשחק הסתיים
                                    String msg = result.get(GameModel.GameModelFields.game_result.toString()).toString(); // שם של מי ניצח
                                    endGame(msg); // הולך לפונקציה של סוף משחק
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.d("Join Game", "Failed to handle update in game state: ", e);
                    }
                });

        fireBaseStore.subscribeForPlayerStateChange(GameModel.getInstance().getGameId(),
                GameModel.getInstance().getOtherPlayer(), new FireBaseStore.Callback<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) { // מקבל סנפשוט על שינוי במצב השחקן
                        if (result != null && !result.isEmpty()) {
                            if (result.containsKey(Player.PlayerFields.game_status.toString())) { // אם קיים בו מצב משחק של השחקן אז -
                                Player.PlayerGameStatus gameStatus =
                                        Player.PlayerGameStatus.valueOf((String) result.get(Player.PlayerFields.game_status.toString())); // שם במשתנה את מצב השחקן השני

                                GameModel.getInstance().setOtherPlayerGameStatus(
                                        Player.PlayerGameStatus.valueOf((String) result.get(Player.PlayerFields.game_status.toString()))); // מעדכן במודל את מצב השחקן השני

                                if (gameStatus == Player.PlayerGameStatus.STARTED &&
                                        GameModel.getInstance().getCurrentPlayerGameStatus() == Player.PlayerGameStatus.STARTED) { // אם שני השחקנים התחילו את המשחק
                                    dialogService.getOpponentTurnDialog(GameActivity.this).dismiss(); // מורידים את החסימה של המסך

                                    Gson Gs = new Gson();
                                    if (result.containsKey(Player.PlayerFields.submarines_board.toString())) { // אם קיים בסנפשוט לוח של צוללות של השחקן השני אז -
                                        Object subBoardJson = result.get(Player.PlayerFields.submarines_board.toString()); // מקבל אובייקט של הלוח צוללות של השני
                                        int[][] subBoard = Gs.fromJson((subBoardJson).toString(), int[][].class); // מעביר את האובייקט מסטרינג למערך של צוללות
                                        GameModel.getInstance().updatePlayer2SubmarinesBoard(subBoard);

                                        Object fireBoardJson = result.get(Player.PlayerFields.fire_board.toString()); // מקבל אובייקט של הלוח יריות של השני
                                        int[][] fireBoard = Gs.fromJson((fireBoardJson).toString(), int[][].class); // מעביר את האובייקט מסטרינג למערך
                                        GameModel.getInstance().updatePlayer1SubmarinesBoardAfterFire(fireBoard); // אחרי שקרה שינוי והלוח יריות של השני השתנה אז נעדכן את הלוח שלי
                                        myBoard.invalidate(); // נצייר את הלוחות מחדש עם בומים ואיקסים

                                        if (GameModel.getInstance().isGameOver()) { // אם המשחק הסתיים וכל הצוללות הרוסות אז -
                                            GameModel.getInstance().setGameState(GameModel.GameState.GAME_OVER); // נשנה את מצב במשחק לזהו
                                            GameModel.getInstance().setCurrentPlayerGameStatus(Player.PlayerGameStatus.LOOSE); // כל הצוללות שלי הרוסות אז אני המפסיד ונשנה בהתאם
                                            fireBaseStore.saveGame(GameModel.getInstance()); // נשמור את השינויים בפייר סטור

                                            endGame(GameModel.getInstance().getOtherPlayer() + " WON"); // נקרא לסיום משחק עם השם של מי שניצח
                                        }
                                    }
                                } else if (gameStatus == Player.PlayerGameStatus.LOOSE) { // אם המשתנה של השחקן השני השתנה למפסיד
                                    GameModel.getInstance().setGameState(GameModel.GameState.GAME_OVER); // אז נעשה שהמשחק נגמר
                                    GameModel.getInstance().setCurrentPlayerGameStatus(Player.PlayerGameStatus.WON); // אני ניצחתי
                                    fireBaseStore.saveGame(GameModel.getInstance()); // נשמור את השינוים בפייר בייס
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
    }

    private void endGame(String gameResult) { // מקבל את השם של מי שניצח
        gameOverBinding.gameOverTxt.setText(gameResult); //
        dialogService.getWinLooseDialog(this, gameOverBinding.getRoot()).show(); // יכניס לדיאלוג ויראה את מי שניצח

        Handler handler = new Handler(Looper.getMainLooper()); // יוצרים הנדלר שמתזמן אירועים במעבר בין מסכים
        // Create a Runnable to start the new Activity
        Runnable startNewActivityRunnable = () -> { // אחרי יצירת הדיליי של ה3 שניות מגדירים אינטנט למעבר חזרה למאיין אקטיביטי
            try {
                Intent intent = new Intent(binding.getRoot().getContext(), MainActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // Delay the start of the new Activity by 2 seconds
        handler.postDelayed(startNewActivityRunnable, 3000); // מייצרים דיליי של 3 שניות עם ההנדלר
    }

    private void startMusicService() { // מפעילים את המוזיקה כשאר עוברים לאקטיביטי הזה
        isMusicPlaying = true; // הופכים את המצב של המוזיקה לאמת
        Intent serviceIntent = new Intent(this, MusicService.class); // עוברים לשרת של מוזיקה - בעקרון הוא אקטיביטי גם כן
        serviceIntent.setAction("PLAY");  //
        startService(serviceIntent); //
    }

    private void stopMusicService() {
        isMusicPlaying = false;
        Intent serviceIntent = new Intent(this, MusicService.class);
        serviceIntent.setAction("STOP");
        startService(serviceIntent);
    }
}