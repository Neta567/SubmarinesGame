package com.example.submarines;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameModel extends BaseObservable {

    // מכיל את כל המידע הדרוש על המשחק ועל השחקנים שמשתתפים בו

    public enum GameState {
        NOT_STARTED,
        ONE_PLAYER_JOINED,
        TWO_PLAYERS_JOINED,
        STARTED,
        GAME_OVER
    }

    public enum GameModelFields {
        game_id,
        game_state,
        game_result
    }

    private static final GameModel INSTANCE = new GameModel();

    private String gameId = "-1";
    private final Player[] players = new Player[2];
    private GameState gameState = GameState.NOT_STARTED;
    private Submarine currentSubmarine;

    private GameModel() {} // פעולה בונה פרטית שיוצרת מודל אחד ויחיד

    public static GameModel getInstance() {
        return INSTANCE;
    }

    public String getGameId() {
        return gameId;
    }
    public GameState getGameState() {
        return gameState;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        if (gameState == GameState.STARTED) {
            currentSubmarine = null;
        }
    }

    public String getGameResult() {

        String gameResult = "In Progress";
        if(gameState == GameState.GAME_OVER) {
            if (players[0].isGameOver()) // עבור השחקן שלי בודק אם כל הצוללות הרוסות כלומר כל האריי ליסט של הריבועים התפוסים הם גם פגועים
                gameResult = players[1].getName() + " WON"; // ואז אם הכל אצלי הרוס אז השני ניצח
            else
                gameResult = players[0].getName() + " WON"; // ואם לא הכל הרוס אצלי ונגמר המשחק אז אני ניצחתי
        }
        return gameResult;
    }

    public boolean isGameStarted() {
        return gameState == GameState.STARTED;
    }

    @Bindable
    public String getCurrentPlayerName() {
        return players[0].getName();
    } // השם של השחקן שלי

    @Bindable
    public String getCurrentPlayerStatus() {
        return getCurrentPlayerGameStatus().toString();
    }

    public Player.PlayerGameStatus getCurrentPlayerGameStatus() { // מחזיר אינמ של מצב השחקן שלי
        return players[0].getGameStatus();
    }

    @Bindable
    public String getOtherPlayer() {
        return players[1].getName();
    } // השם של השחקן השני

    public Player.PlayerGameStatus getOtherPlayerGameStatus() { // מחזיר אינמ של מצב השחקן השני
        return players[1].getGameStatus();
    }

    public void setCurrentPlayer(String player) {
        players[0] = new Player(player);
        notifyPropertyChanged(BR.currentPlayerName); // דרך הדטא ביינדינג נשים את השם של השחקן שלי בשורה למעלה
    }

    public void setCurrentPlayerGameStatus(Player.PlayerGameStatus playerGameStatus) { // לא בשימוש
        players[0].setGameStatus(playerGameStatus); // משנה את מצב השחקן שלי
        notifyPropertyChanged(BR.currentPlayerStatus);
    }

    public void setOtherPlayer(String otherPlayer) {
        players[1] = new Player(otherPlayer);
        notifyPropertyChanged(BR.otherPlayer);
    }

    public void setOtherPlayerGameStatus(Player.PlayerGameStatus playerGameStatus) { // לא בשימוש
        players[1].setGameStatus(playerGameStatus);
    }

    public void setCurrentSubmarine(Submarine submarine) { // מחליף את הצוללת הנוכחית
        currentSubmarine = submarine;
    }
    public Submarine getCurrentSubmarine() {
        return currentSubmarine;
    } // מחזיר את הצוללת הנוכחית

    public void setSubmarineBoardSquareState(int i, int j, Square.SquareState state) { // מעדכן בלוח של השחקן שלי הצוללות את מצב הריבועים
        players[0].setSubmarineBoardSquareState(i, j, state);
    }

    public void setPlayer2SubmarineBoardSquareState(int i, int j, Square.SquareState state) { // מעדכן בלוח של הצוללות של השחקן השני את מצב הריבועים
        players[1].setSubmarineBoardSquareState(i, j, state);
    }

    public void updatePlayer2SubmarinesBoard(int[][] subBoard) { // מעדכן את מצב לוח הצוללות של היריב
        players[1].updateSubmarinesBoard(subBoard);
    }

    public void updatePlayer1SubmarinesBoardAfterFire(int[][] fireBoard) {  // אחרי שקיבלתי את הלוח יריות של השני אני מעדכנת את הלוח צוללות שלי
        players[0].updateSubmarinesBoardAfterFire(fireBoard);
    }

    public void setFireBoardSquareState(int i, int j, Square.SquareState state) { // מעדכן את מצב הריבוע בלוח יריות שלי
        players[0].setFireBoardSquareState(i, j, state);
    }

    public ArrayList<Submarine> initPlayer1Submarines() { // מחזיר אריי ליסט של 4 צוללות עבור השחקן שלי
        return players[0].initSubmarines();
    }

    public Square[][] initPlayer1SubmarinesBoard() { // מחזיר לוח חדש עבור לוח הצוללות שלי
        return players[0].initSubmarinesBoard();
    }

    public Square[][] initPlayer2SubmarinesBoard() { // מחזיר לוח חדש עבור לוח הצוללות של השני
        return players[1].initSubmarinesBoard();
    }

    public Square[][] initPlayer1FireBoard() { // מחזיר את לוח היריות החדש של השחקן שלי
        return players[0].initFireBoard();
    }

    public boolean isGameOver() { // מחזיר אם עבור השחקן שלי כל הצוללות הרוסות כלומר כל האריי של הריבועים התפוסים הם גם פגועים
        return players[0].isGameOver();
                //|| players[1].isGameOver();
    }

    public Map<String, Object> getGame() { // מחזיר משחק לפי האס מאפ
        HashMap<String, Object> game = new HashMap<>();
        game.put(GameModelFields.game_id.toString(), gameId);
        game.put(GameModelFields.game_state.toString(), gameState.toString());
        game.put(GameModelFields.game_result.toString(), getGameResult());

        return game;
    }

    public Map<String, Object> getPlayer() { // מחזיר שחקן לפי האש מאפ של שם מצב ולוח צוללות ויריות
        HashMap<String, Object> player = new HashMap<>();
        player.put(Player.PlayerFields.name.toString(), players[0].getName());
        player.put(Player.PlayerFields.game_status.toString(), players[0].getGameStatus().toString());

        Gson gson = new Gson(); // סיפרייה שיודעת להעביר ממערך לסטרינג או סטרינג למערך
        player.put(Player.PlayerFields.fire_board.toString(),
                gson.toJson(players[0].getFireBoardModel())); // טו גייסון אז ממערך לסטרינג
        player.put(Player.PlayerFields.submarines_board.toString(),
                gson.toJson(players[0].getSubmarinesBoardModel()));

        return player;

        // פרומ גייסון אז מסטרינג למערך
    }
}
