package com.rollncode.colorlines.game.model;

import android.annotation.SuppressLint;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 16.06.16
 */
public class Position {
    public int x;
    public int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("(%d,%d) ", x, y);
    }
}
