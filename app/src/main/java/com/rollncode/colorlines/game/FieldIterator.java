package com.rollncode.colorlines.game;

import java.util.Iterator;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 16.06.16
 */
class FieldIterator implements Iterator<Integer> {

    private int mPosition;
    private boolean mReversed = false;
    private int mLength;

    FieldIterator(int target, int start, int length) {
        mLength = length;

        if (target >= start) {
            mPosition = mLength - 1; // reverse then
            mReversed = true;
        } else {
            mPosition = 0;
        }
    }

    @Override
    public boolean hasNext() {
        if (mReversed) {
            return mPosition >= 0;
        }
        return mPosition < mLength;
    }

    @Override
    public Integer next() {
        if (mReversed) {
            return mPosition--;
        }
        return mPosition++;
    }

    @Override
    public void remove() {}

    public Integer value() {
        return mPosition;
    }
}
