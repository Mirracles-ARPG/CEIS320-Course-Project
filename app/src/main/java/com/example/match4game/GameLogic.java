package com.example.match4game;

import java.util.Arrays;

public class GameLogic {
    private final int boardRows, boardColumns;
    private boolean p1Turn, gameOver;
    private final int[] winningLineCoords = {0,0,0,0};
    private final int[][] gameBoard;

    GameLogic() {
        boardRows = 6;
        boardColumns = 7;
        gameBoard = new int[boardColumns][boardRows];
        reset();
    }

    public void reset() {
        p1Turn = true;
        gameOver = false;
        for(int x = 0; x < boardColumns; x++) {
            for(int y = 0; y < boardRows; y++) {
                gameBoard[x][y] = 0;
            }
        }
    }

    public boolean playerMove(int column) {
        if(!gameOver && !isColumnFull(column)) {
            int row = findLowestRow(column);
            int player = (p1Turn ? 1 : 2);
            gameBoard[column][row] = player;
            if (checkWin(column, row, player)) { gameOver = !gameOver; }
            else { p1Turn = !p1Turn; }
            return true;
        }
        return false;
    }

    private boolean isColumnFull(int column) { return gameBoard[column][0] != 0; }

    private int findLowestRow(int column) {
        for(int row = boardRows - 1; row >= 0; row--) {
            if(gameBoard[column][row] == 0) { return row; }
        }
        return 0;
    }

    private boolean checkWin(int c, int r, int p) {
        int inARow = 0;
        for(int y = 0; y < boardRows; y++) {
            if (gameBoard[c][y] == p) { inARow++; }
            else { inARow = 0; }
            if (inARow == 1) {
                winningLineCoords[0] = c;
                winningLineCoords[1] = y;
            }else if(inARow == 4) {
                winningLineCoords[2] = c;
                winningLineCoords[3] = y;
                return true;
            }
        }

        inARow = 0;
        for(int x = 0; x < boardColumns; x++) {
            if (gameBoard[x][r] == p) { inARow++; }
            else { inARow = 0; }
            if (inARow == 1) {
                winningLineCoords[0] = x;
                winningLineCoords[1] = r;
            }else if(inARow == 4) {
                winningLineCoords[2] = x;
                winningLineCoords[3] = r;
                return true;
            }
        }

        inARow = 0;
        int diag1X = (c >= r ? (c - r) : 0);
        int diag1Y = (c >= r ? 0 : (r - c));
        while(diag1X < boardColumns && diag1Y < boardRows) {
            if (gameBoard[diag1X][diag1Y] == p) { inARow++; }
            else { inARow = 0; }
            if (inARow == 1) {
                winningLineCoords[0] = diag1X;
                winningLineCoords[1] = diag1Y;
            }else if(inARow == 4) {
                winningLineCoords[2] = diag1X;
                winningLineCoords[3] = diag1Y;
                return true;
            }
            diag1X++;
            diag1Y++;
        }

        inARow = 0;
        int diag2X = ((c >= boardRows - 1 - r) ? (c - (boardRows - 1 - r)) : 0);
        int diag2Y = ((c >= boardRows - 1 - r) ? (boardRows - 1) : (r + c));
        while(diag2X < boardColumns && diag2Y > 0) {
            if (gameBoard[diag2X][diag2Y] == p) { inARow++; }
            else { inARow = 0; }
            if (inARow == 1) {
                winningLineCoords[0] = diag2X;
                winningLineCoords[1] = diag2Y;
            }else if(inARow == 4) {
                winningLineCoords[2] = diag2X;
                winningLineCoords[3] = diag2Y;
                return true;
            }
            diag2X++;
            diag2Y--;
        }

        return false;
    }

    public int getBoardRows() { return boardRows; }
    public int getBoardColumns() { return boardColumns; }
    public boolean isP1Turn() { return p1Turn; }
    public boolean isGameOver() { return gameOver; }
    public int[] getWinningLineCoords() { return winningLineCoords; }
    public int[][] getGameBoard() { return gameBoard; }

    public void setP1Turn(boolean bool) { p1Turn = bool; }
    public void setGameOver(boolean bool) { gameOver = bool; }
    public void setWinningLineCoords(int[] coords) { System.arraycopy(coords, 0, winningLineCoords, 0, 4); }

    public char[] saveGameState() {
        String state = Arrays.deepToString(gameBoard);
        state = state.replace("[", "");
        state = state.replace(", ", "");
        state = state.replace("]", "");
        return state.toCharArray();
    }

    public void restoreGameState(char[] state) {
        for(int x = 0; x < boardColumns; x++) {
            for(int y = 0; y < boardRows; y++) {
                gameBoard[x][y] = Character.getNumericValue(state[(x * boardRows) + y]);
            }
        }
    }
}
