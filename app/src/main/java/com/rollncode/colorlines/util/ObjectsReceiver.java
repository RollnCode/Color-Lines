package com.rollncode.colorlines.util;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 01.07.16
 */
public interface ObjectsReceiver {
    void onObjectsReceive(@IdRes int code, @NonNull Object... objects);
}
