package com.example.match4game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;

public class Match4Board extends View {
    private final int boardColor, lineBorderColor, lineFillColor;
    private int p1Color, p2Color;
    private float volume;
    private int cellSize;
    private final Paint paint;
    private final GameLogic game;
    private TextView p1Scoreboard, p2Scoreboard;
    private int p1Score, p2Score;
    private ImageView turnIndicator;
    private final SoundPool soundPool;
    private final int sound1, sound2;

    public Match4Board(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Match4Board, 0, 0);
        try {
            boardColor = a.getInteger(R.styleable.Match4Board_boardColor, 0);
            lineBorderColor = a.getInteger(R.styleable.Match4Board_lineBorderColor, 0);
            lineFillColor = a.getInteger(R.styleable.Match4Board_lineFillColor, 0);
            p1Color = a.getInteger(R.styleable.Match4Board_p1Color, 0);
            p2Color = a.getInteger(R.styleable.Match4Board_p2Color, 0);
        }finally {
            a.recycle();
        }
        volume = 1.0f;
        cellSize = getWidth() / 7;
        paint = new Paint();
        paint.setAntiAlias(true);
        game = new GameLogic();
        p1Score = 0;
        p2Score = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else { soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0); }
        sound1 = soundPool.load(getContext(), R.raw.place_piece, 1);
        sound2 = soundPool.load(getContext(), R.raw.player_win, 1);
    }

    public void setPlayerPrefs(int color1, int color2, float volume){
        p1Color = getResources().getColor(color1);
        p2Color = getResources().getColor(color2);
        this.volume = volume;
    }

    public void setGameVariables(int score1, int score2, boolean p1Turn, boolean gameOver, int[] coords) {
        p1Score = score1;
        p2Score = score2;
        game.setP1Turn(p1Turn);
        game.setGameOver(gameOver);
        game.setWinningLineCoords(coords);
    }

    public boolean isP1Turn() { return game.isP1Turn(); }
    public boolean isGameOver(){ return game.isGameOver(); }
    public int[] getWinningLineCoords() { return game.getWinningLineCoords(); }

    public void linkInterface(TextView p1Scoreboard, TextView p2Scoreboard, ImageView turnIndicator) {
        this.p1Scoreboard = p1Scoreboard;
        this.p2Scoreboard = p2Scoreboard;
        this.turnIndicator = turnIndicator;
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        int dimensions;
        if (getMeasuredWidth() / 7 < getMeasuredHeight() / 6) {
            dimensions = getMeasuredWidth();
            cellSize = dimensions / 7;
            setMeasuredDimension(dimensions, dimensions * 6 / 7);
        }else {
            dimensions = getMeasuredHeight();
            cellSize = dimensions / 6;
            setMeasuredDimension(dimensions * 7 / 6, dimensions);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int columnSelected = (int) Math.floor(event.getX() / cellSize);
            if(game.playerMove(columnSelected)) {
                invalidate();
                if(game.isGameOver()) {
                    if(game.isP1Turn()) {
                        p1Score++;
                        p1Scoreboard.setText(String.valueOf(p1Score));
                    }else {
                        p2Score++;
                        p2Scoreboard.setText(String.valueOf(p2Score));
                    }
                    soundPool.play(sound2, volume, volume, 0, 0, 1);
                }else{
                    turnIndicator.setColorFilter(game.isP1Turn() ? p1Color : p2Color);
                    soundPool.play(sound1, volume, volume, 0, 0, 1);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawGameBoard(canvas);
        for(int x = 0; x < game.getBoardColumns(); x++) {
            for(int y = 0; y < game.getBoardRows(); y++) {
                if(game.getGameBoard()[x][y] == 1) { drawPiece(canvas, true, x, y); }
                else if(game.getGameBoard()[x][y] == 2) { drawPiece(canvas, false, x, y); }
            }
        }
        if(game.isGameOver()) { drawLine(canvas); }
    }

    private void drawGameBoard(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeWidth(cellSize * 0.18f);
        paint.setColor(boardColor);
        for (int x = 0; x <= 7; x++) {
            canvas.drawLine(x * cellSize, 0, x * cellSize, canvas.getHeight(), paint);
        }
        for (int y = 0; y <= 6; y++) {
            canvas.drawLine(0, y * cellSize, canvas.getWidth(), y * cellSize, paint);
        }
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 6; y++) {
                canvas.drawCircle((x + 0.5f) * cellSize, (y + 0.5f) * cellSize, cellSize / 2.0f, paint);
            }
        }
    }
    
    private void drawPiece(Canvas canvas, boolean p1, int x, int y) {
        paint.setStyle(Paint.Style.FILL);
        if(p1) { paint.setColor(p1Color); }
        else { paint.setColor(p2Color); }
        canvas.drawCircle((x + 0.5f) * cellSize, (y + 0.5f) * cellSize, cellSize * 0.41f, paint);
    }

    private void drawLine(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(20);
        paint.setColor(lineBorderColor);
        canvas.drawLine((game.getWinningLineCoords()[0] + 0.5f) * cellSize,
                        (game.getWinningLineCoords()[1] + 0.5f) * cellSize,
                        (game.getWinningLineCoords()[2] + 0.5f) * cellSize,
                        (game.getWinningLineCoords()[3] + 0.5f) * cellSize, paint);
        paint.setStrokeWidth(15);
        paint.setColor(lineFillColor);
        canvas.drawLine((game.getWinningLineCoords()[0] + 0.5f) * cellSize,
                        (game.getWinningLineCoords()[1] + 0.5f) * cellSize,
                        (game.getWinningLineCoords()[2] + 0.5f) * cellSize,
                        (game.getWinningLineCoords()[3] + 0.5f) * cellSize, paint);
    }

    public void resetGame() {
        game.reset();
        turnIndicator.setColorFilter(p1Color);
        invalidate();
    }

    public char[] saveGameState() { return game.saveGameState(); }
    public void restoreGameState(char[] state) { game.restoreGameState(state); }
}
