package com.rollncode.colorlines.game.interfaces;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 21.06.16
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({State.FINAL, State.START, State.PASSABLE, State.ORANGE, State.YELLOW,
        State.TEAL, State.GREEN, State.PURPLE, State.PINK, State.BLUE})
public @interface State {
    int FINAL = 0;
    int START = 253;
    int PASSABLE = 254;
    // IMPASSABLE COLORS
    int ORANGE = 255;
    int YELLOW = 256;
    int TEAL = 257;
    int GREEN = 258;
    int PURPLE = 259;
    int PINK = 260;
    int BLUE = 261;
}
