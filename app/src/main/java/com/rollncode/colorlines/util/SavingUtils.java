package com.rollncode.colorlines.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import static com.rollncode.colorlines.util.Utils.DEFAULT_SELECTED;
import static com.rollncode.colorlines.util.Utils.SIZE;

/**
 * Class for saving game state
 *
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 01.07.16
 */
public class SavingUtils {

    private static final String KEY_0 = "KEY_0"; // field
    private static final String KEY_1 = "KEY_1"; // score
    private static final String KEY_2 = "KEY_2"; // selected index

    private static SavingUtils sInstance;

    private final SharedPreferences mPreferences;

    public static void init(@NonNull Context context) {
        sInstance = new SavingUtils(context);
    }

    public static SavingUtils getInstance() {
        return sInstance;
    }

    private SavingUtils(@NonNull Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void save(@NonNull final int[][] field, int score) {
        mPreferences.edit().putString(KEY_0, Utils.convertToJson(field)).putInt(KEY_1, score).apply();
    }

    public void setSelected(int index) {
        mPreferences.edit().putInt(KEY_2, index).apply();
    }

    public int getSelected() {
        return mPreferences.getInt(KEY_2, DEFAULT_SELECTED);
    }

    public void clear() {
        mPreferences.edit().clear().apply();
    }

    public int[][] getField() {
        final String json = mPreferences.getString(KEY_0, null);
        if (!TextUtils.isEmpty(json)) {
            return Utils.convertFromJson(json);
        }
        return null;
    }

    public int getSize() {
        final int[][] field = getField();
        if (field != null) {
            return field.length;
        }
        return SIZE[DEFAULT_SELECTED];
    }

    public int getScore() {
        return mPreferences.getInt(KEY_1, 0);
    }
}
