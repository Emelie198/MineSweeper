package at.forman.minesweeper.model;

import java.util.*;

public class Game {
    public enum FrontState {HIDDEN, VISIBLE, FLAGGED}
    public enum Result {WIN, HIT, VALID, INVALID}
    public enum BackState {BOMB(-1), ZERO(0), ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8);
        private final int num;
        BackState(int n)
        {
            num = n;
        }
        public int getNum(){return num;}
        public BackState inc()
        {
            if (num >= 0 && num < 8)
                return BackState.values()[num+2];
            else throw new ArrayIndexOutOfBoundsException("Wrong value in enum BackState");
        }

        public FieldState toFieldState() {
            System.out.println("toFieldState() called with " + this);
            switch (this) {
                case BOMB:
                    return FieldState.BOMB;
                case ZERO:
                    return FieldState.ZERO;
                case ONE:
                    return FieldState.ONE;
                case TWO:
                    return FieldState.TWO;
                case THREE:
                    return FieldState.THREE;
                case FOUR:
                    return FieldState.FOUR;
                case FIVE:
                    return FieldState.FIVE;
                case SIX:
                    return FieldState.SIX;
                case SEVEN:
                    return FieldState.SEVEN;
                case EIGHT:
                    return FieldState.EIGHT;
                default:
                    throw new IllegalStateException("Unexpected value: " + this);
            }
        }
    }

    private BackState[][] backState;
    private FrontState[][] frontState;
    private int numVisible;
    private int numFlagged;
    private int numBombs;
    public int cols, rows;

    public Game(int cols, int rows, int numBombs)
    {
        setup(cols, rows, numBombs);
    }
    public BackState getGameState(int col, int row)
    {
        return backState[col][row];
    }
    public FrontState getShowState(int col, int row)
    {
        return frontState[col][row];
    }

    public FrontState[][] getFrontState() {
        return frontState;
    }
    public void setup(int cols, int rows, int numBombs)
    {
        this.cols = cols;
        this.rows = rows;
        this.numBombs = numBombs;
        backState = new BackState[cols][rows];
        frontState = new FrontState[cols][rows];
        numVisible = 0;
        numFlagged = 0;
        for (int i = 0; i < cols; i++)
            for (int j = 0; j < rows; j++)
            {
                backState[i][j] = BackState.ZERO;
                frontState[i][j] = FrontState.HIDDEN;
            }
        Random rand = new Random();
        for (int i = 0; i < numBombs; i++)
        {
            int bx = rand.nextInt(cols);
            int by = rand.nextInt(rows);
            if (backState[bx][by] == BackState.ZERO) backState[bx][by] = BackState.BOMB;
            else i--;
        }
        for (int i = 0; i < cols; i++)
            for (int j = 0; j < rows; j++)
            {
                if (backState[i][j] == BackState.BOMB)
                {
                    if(i > 0 && j > 0 && backState[i-1][j-1] != BackState.BOMB) backState[i-1][j-1] = backState[i-1][j-1].inc();
                    if(j > 0 && backState[i][j-1] != BackState.BOMB) backState[i][j-1] = backState[i][j-1].inc();
                    if(i < cols-1 && j > 0 && backState[i+1][j-1] != BackState.BOMB) backState[i+1][j-1] = backState[i+1][j-1].inc();
                    if(i > 0 && backState[i-1][j] != BackState.BOMB) backState[i-1][j] = backState[i-1][j].inc();
                    if(i < cols-1 && backState[i+1][j] != BackState.BOMB) backState[i+1][j] = backState[i+1][j].inc();
                    if(i > 0 && j < rows-1 && backState[i-1][j+1] != BackState.BOMB) backState[i-1][j+1] = backState[i-1][j+1].inc();
                    if(j < rows-1 && backState[i][j+1] != BackState.BOMB) backState[i][j+1] = backState[i][j+1].inc();
                    if(i < cols-1 && j < rows-1 && backState[i+1][j+1] != BackState.BOMB) backState[i+1][j+1] = backState[i+1][j+1].inc();
                }
            }
    }
    public Result uncover(int col, int row)
    {
        if (frontState[col][row] == FrontState.HIDDEN)
        {
            frontState[col][row] = FrontState.VISIBLE;
            numVisible++;
            if(backState[col][row] == BackState.BOMB)
            {
                uncoverAll();
                return Result.HIT;
            }
            if (backState[col][row] == BackState.ZERO) recursiveUncover(col,row);
            if (numFlagged == numBombs && cols * rows - numVisible - numFlagged == 0)
                return Result.WIN;
            return Result.VALID;
        }
        return Result.INVALID;
    }
    public void uncoverAll()
    {
        for (int i = 0; i < cols; i++)
            for(int j = 0; j < rows; j++)
                frontState[i][j] = FrontState.VISIBLE;
    }
    public void recursiveUncover(int col, int row)
    {
        if(col > 0 && row > 0) recUncover(col-1,row-1);
        if(row > 0) recUncover(col,row-1);
        if(col < cols-1 && row > 0) recUncover(col+1,row-1);
        if(col > 0) recUncover(col-1,row);
        if(col < cols-1) recUncover(col+1,row);
        if(col > 0 && row < rows-1) recUncover(col-1,row+1);
        if(row < rows-1) recUncover(col,row+1);
        if(col < cols-1 && row < rows-1) recUncover(col+1,row+1);

    }
    private void recUncover(int col, int row)
    {
        if (frontState[col][row] == FrontState.HIDDEN)
        {
            numVisible++;
            frontState[col][row] = FrontState.VISIBLE;
            if (backState[col][row] == BackState.ZERO) recursiveUncover(col,row);
        }
    }
    public Result flag(int col, int row)
    {
        if (frontState[col][row] == FrontState.HIDDEN)
        {
            numFlagged++;
            frontState[col][row] = FrontState.FLAGGED;
            if (numFlagged == numBombs && cols * rows - numVisible - numFlagged == 0)
                return Result.WIN;
            return Result.VALID;
        }
        else if (frontState[col][row] == FrontState.FLAGGED)
        {
            numFlagged--;
            frontState[col][row] = FrontState.HIDDEN;
            return Result.VALID;
        }
        return Result.INVALID;
    }
    public void reset() {
        numVisible = 0;
        numFlagged = 0;

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                backState[i][j] = BackState.ZERO;
                frontState[i][j] = FrontState.HIDDEN;
            }
        }

        Random rand = new Random();
        for (int i = 0; i < numBombs; i++) {
            int bx = rand.nextInt(cols);
            int by = rand.nextInt(rows);
            if (backState[bx][by] == BackState.ZERO) backState[bx][by] = BackState.BOMB;
            else i--;
        }

        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {
                if (backState[i][j] == BackState.BOMB) {
                    if (i > 0 && j > 0 && backState[i - 1][j - 1] != BackState.BOMB)
                        backState[i - 1][j - 1] = backState[i - 1][j - 1].inc();
                    if (j > 0 && backState[i][j - 1] != BackState.BOMB)
                        backState[i][j - 1] = backState[i][j - 1].inc();
                    if (i < cols - 1 && j > 0 && backState[i + 1][j - 1] != BackState.BOMB)
                        backState[i + 1][j - 1] = backState[i + 1][j - 1].inc();
                    if (i > 0 && backState[i - 1][j] != BackState.BOMB)
                        backState[i - 1][j] = backState[i - 1][j].inc();
                    if (i < cols - 1 && backState[i + 1][j] != BackState.BOMB)
                        backState[i + 1][j] = backState[i + 1][j].inc();
                    if (i > 0 && j < rows - 1 && backState[i - 1][j + 1] != BackState.BOMB)
                        backState[i - 1][j + 1] = backState[i - 1][j + 1].inc();
                    if (j < rows - 1 && backState[i][j + 1] != BackState.BOMB)
                        backState[i][j + 1] = backState[i][j + 1].inc();
                    if (i < cols - 1 && j < rows - 1 && backState[i + 1][j + 1] != BackState.BOMB)
                        backState[i + 1][j + 1] = backState[i + 1][j + 1].inc();
                }
            }
        }
    }

}
