package com.rollncode.colorlines.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rollncode.colorlines.R;
import com.rollncode.colorlines.game.interfaces.State;
import com.rollncode.colorlines.game.model.Position;
import com.rollncode.colorlines.view.CellView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 21.06.16
 */
public class Utils {

    public static final int DEFAULT_SELECTED = 2;
    public static final int[] SIZE = {5, 7, 9};

    public static void clear(CellView[][] field) {
        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                field[i][j].setState(State.PASSABLE);
            }
        }
    }

    public static int[][] copy(int[][] source) {
        int[][] t = new int[source.length][source[0].length];
        for (int i = 0; i < source.length; i++) {
            t[i] = Arrays.copyOf(source[i], source[i].length);
        }
        return t;
    }

    public static int[][] makeArray(CellView[][] cells) {
        int[][] t = new int[cells.length][cells[0].length];
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                t[i][j] = cells[i][j].getState();
            }
        }
        return t;
    }

    public static void setStates(final CellView[][] cells, int[][] array) {
        if (cells.length == array.length) {
            for (int i = 0; i < cells.length; i++) {
                for (int j = 0; j < cells[i].length; j++) {
                    //noinspection WrongConstant
                    cells[i][j].setState(array[i][j]);
                }
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    public static void setColor(@State int state, ImageView view) {
        final Context context = view.getContext();
        final int color;
        switch (state) {
            case State.ORANGE:
                color = ContextCompat.getColor(context, R.color.orange);
                break;

            case State.YELLOW:
                color = ContextCompat.getColor(context, R.color.yellow);
                break;

            case State.TEAL:
                color = ContextCompat.getColor(context, R.color.teal);
                break;

            case State.GREEN:
                color = ContextCompat.getColor(context, R.color.green);
                break;

            case State.PURPLE:
                color = ContextCompat.getColor(context, R.color.purple);
                break;

            case State.PINK:
                color = ContextCompat.getColor(context, R.color.pink);
                break;

            case State.BLUE:
                color = ContextCompat.getColor(context, R.color.blue);
                break;

            default:
                color = ContextCompat.getColor(context, android.R.color.black);
                break;
        }
        view.setColorFilter(color);
    }

    public static float[] getCoordinates(final CellView[][] cells, Position p) {
        final CellView cell = cells[p.y][p.x];
        int statusBarHeight = getStatusBarHeight(cell.getResources());
        Rect rect = new Rect();
        cell.getGlobalVisibleRect(rect);
        final float x = rect.left;
        final float y = rect.top - statusBarHeight;
        return new float[] {x, y};
    }

    public static int getStatusBarHeight(Resources resources) {
        int height = 0;
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = resources.getDimensionPixelSize(resourceId);
        }
        return height;
    }

    public static ArrayList<Position> reducePath(final ArrayList<Position> path) {
        final ArrayList<Position> reduced = new ArrayList<>();
        reduced.add(path.get(0));
        for (int i = 1; i < path.size() - 1; i++) {
            Position prev = path.get(i - 1);
            Position cur = path.get(i);
            Position next = path.get(i + 1);

            if (prev.x == cur.x && cur.x == next.x) {
                continue;
            }
            if (prev.y == cur.y && cur.y == next.y) {
                continue;
            }
            reduced.add(cur);
        }
        if (!reduced.contains(path.get(path.size() - 1))) {
            reduced.add(path.get(path.size() - 1));
        }

        return reduced;
    }

    static String getLeaderboard(Context context, int size) {
        switch (size) {
            case 5:
                return context.getString(R.string.leaderboard_5x5);

            case 7:
                return context.getString(R.string.leaderboard_7x7);

            default:
                return context.getString(R.string.leaderboard_9x9);
        }
    }

    public static boolean receiveObjects(@Nullable WeakReference<ObjectsReceiver> weakReceiver, @IdRes int code, @NonNull Object... objects) {
        final ObjectsReceiver receiver = weakReceiver == null ? null : weakReceiver.get();
        final boolean received = receiver != null;
        if (received) {
            receiver.onObjectsReceive(code, objects);
        }
        return received;
    }

    static String convertToJson(final int[][] array) {
        final Gson gson = new GsonBuilder().create();
        return gson.toJson(array);
    }

    static int[][] convertFromJson(String json) {
        final Gson gson = new GsonBuilder().create();
        return gson.fromJson(json, int[][].class);
    }

}
