package com.rollncode.colorlines.game;

import com.rollncode.colorlines.util.Utils;
import com.rollncode.colorlines.view.CellView;

/**
 * Holds previous game state
 *
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 27.06.16
 */
public class PreviousHolder {

    private int[][] mField;
    private int mScore;
    private boolean mCanUndo;

    public PreviousHolder(int size) {
        mField = new int[size][size];
    }

    public void save(final CellView[][] cells, int score) {
        mField = Utils.makeArray(cells);
        mScore = score;
    }

    public int[][] getField() {
        return mField;
    }

    public int getScore() {
        return mScore;
    }

    public void setCanUndo(boolean canUndo) {
        mCanUndo = canUndo;
    }

    public boolean isCanUndo() {
        return mCanUndo;
    }
}
