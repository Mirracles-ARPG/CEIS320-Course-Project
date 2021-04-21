package com.example.match4game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayGame extends AppCompatActivity {
    private Match4Board m4b;
    private TextView p1Score;
    private TextView p2Score;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String PLAYER_1_COLOR = "player1Color";
    private static final String PLAYER_2_COLOR = "player2Color";
    private static final String VOLUME = "volume";
    private static final String GAME_STATE = "gameState";
    private static final String PLAYER_1_SCORE = "player1Score";
    private static final String PLAYER_2_SCORE = "player2Score";
    private static final String IS_PLAYER_1_TURN = "isP1Turn";
    private static final String IS_GAME_OVER = "isGameOver";
    private static final String WINNING_LINE_COORDINATES = "winningLineCoords";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        ImageView turnIndicator = findViewById(R.id.turnIndicator);
        m4b = findViewById(R.id.match4Board);
        p1Score = findViewById(R.id.p1Score);
        p2Score = findViewById(R.id.p2Score);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        m4b.setPlayerPrefs(sharedPreferences.getInt(PLAYER_1_COLOR, R.color.playerColor1),
                           sharedPreferences.getInt(PLAYER_2_COLOR, R.color.playerColor2),
                           sharedPreferences.getInt(VOLUME, 100) / 100.0f);
        m4b.linkInterface(p1Score, p2Score, turnIndicator);
        if (savedInstanceState != null) {
            turnIndicator.setColorFilter(getResources().getColor(savedInstanceState.getBoolean(IS_PLAYER_1_TURN) ?
                                         sharedPreferences.getInt(PLAYER_1_COLOR, R.color.playerColor1) :
                                         sharedPreferences.getInt(PLAYER_2_COLOR, R.color.playerColor2)));
            m4b.restoreGameState(savedInstanceState.getCharArray(GAME_STATE));
            m4b.setGameVariables(Integer.parseInt(savedInstanceState.getCharSequence(PLAYER_1_SCORE).toString()),
                                 Integer.parseInt(savedInstanceState.getCharSequence(PLAYER_2_SCORE).toString()),
                                 savedInstanceState.getBoolean(IS_PLAYER_1_TURN), savedInstanceState.getBoolean(IS_GAME_OVER),
                                 savedInstanceState.getIntArray(WINNING_LINE_COORDINATES));
            p1Score.setText(savedInstanceState.getCharSequence(PLAYER_1_SCORE));
            p2Score.setText(savedInstanceState.getCharSequence(PLAYER_2_SCORE));
        }else { turnIndicator.setColorFilter(getResources().getColor(sharedPreferences.getInt(PLAYER_1_COLOR, R.color.playerColor1))); }

        Button btnQuitGame = findViewById(R.id.btnQuitGame);
        btnQuitGame.setOnClickListener(v -> goMainMenu());

        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(v -> resetMatch4Board());
    }

    private void goMainMenu() { finish(); }
    private void resetMatch4Board() { m4b.resetGame(); }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray(GAME_STATE, m4b.saveGameState());
        outState.putCharSequence(PLAYER_1_SCORE, p1Score.getText());
        outState.putCharSequence(PLAYER_2_SCORE, p2Score.getText());
        outState.putBoolean(IS_PLAYER_1_TURN, m4b.isP1Turn());
        outState.putBoolean(IS_GAME_OVER, m4b.isGameOver());
        outState.putIntArray(WINNING_LINE_COORDINATES, m4b.getWinningLineCoords());
    }
}