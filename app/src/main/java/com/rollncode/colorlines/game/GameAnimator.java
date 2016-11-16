package com.rollncode.colorlines.game;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rollncode.colorlines.R;
import com.rollncode.colorlines.game.model.Position;
import com.rollncode.colorlines.util.Utils;
import com.rollncode.colorlines.view.CellView;

import java.util.ArrayList;
import java.util.Random;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 23.06.16
 */
public class GameAnimator {

    private static final String ALPHA = "alpha";

    public static AnimatorSet move(final CellView[][] cells, ArrayList<Position> path, CellView view) {
        final AnimatorSet set = new AnimatorSet();
        final ArrayList<Animator> collection = new ArrayList<>();
        float[] coords = Utils.getCoordinates(cells, path.get(0));
        view.setX(coords[0]);
        view.setY(coords[1]);
        for (int i = 1; i < path.size(); i++) {
            coords = Utils.getCoordinates(cells, path.get(i));
            ObjectAnimator animatorX = ObjectAnimator.ofFloat(view, View.X, coords[0]);
            ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, View.Y, coords[1]);
            AnimatorSet s = new AnimatorSet();
            s.playTogether(animatorX, animatorY);
            collection.add(s);
        }
        set.playSequentially(collection);
        set.setInterpolator(new BounceInterpolator());
        return set;
    }

    static AnimatorSet disappear(final CellView[][] cells, ArrayList<Position> toRemove) {
        final AnimatorSet set = new AnimatorSet();
        final ArrayList<Animator> collection = new ArrayList<>();
        for (int i = 0; i < toRemove.size(); i++) {
            Position p = toRemove.get(i);
            Drawable drawable = cells[p.y][p.x].getDrawable();
            ObjectAnimator animatorAlpha = ObjectAnimator.ofPropertyValuesHolder(drawable,
                    PropertyValuesHolder.ofInt(ALPHA, 255, 0));
            collection.add(animatorAlpha);
        }
        set.playTogether(collection);
        return set;
    }

    public static AnimatorSet appear(final CellView[][] cells, ArrayList<Position> appeared) {
        final AnimatorSet set = new AnimatorSet();
        final ArrayList<Animator> collection = new ArrayList<>();
        for (int i = 0; i < appeared.size(); i++) {
            Position p = appeared.get(i);
            Drawable drawable = cells[p.y][p.x].getDrawable();
            ObjectAnimator animatorAlpha = ObjectAnimator.ofPropertyValuesHolder(drawable,
                    PropertyValuesHolder.ofInt(ALPHA, 0, 255));
            collection.add(animatorAlpha);
        }
        set.playTogether(collection);
        return set;
    }

    public static Animator rotate(final TextView view) {
        Random random = new Random();
        ObjectAnimator animator = ObjectAnimator
                .ofFloat(view,View.ROTATION, view.getRotation() + random.nextInt((640 - 150) + 1) + 150);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        return animator;
    }

    public static Animator flip(final RelativeLayout relativeLayout, boolean isError) {
        final AnimatorSet set = new AnimatorSet();
        final TextView textView;
        if (isError) {
            textView = (TextView) relativeLayout.findViewById(R.id.tv_error);
        } else {
            textView = (TextView) relativeLayout.findViewById(R.id.tv_info);
        }
        ObjectAnimator close1 = ObjectAnimator.ofFloat(relativeLayout, View.SCALE_X, 1, 0);
        close1.addListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                relativeLayout.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setVisibility(View.VISIBLE);
            }

            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        ObjectAnimator open1 = ObjectAnimator.ofFloat(relativeLayout, View.SCALE_X, 0, 1);
        ObjectAnimator close2 = ObjectAnimator.ofFloat(relativeLayout, View.SCALE_X, 1, 0);
        close2.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                textView.setVisibility(View.GONE);
            }

            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        close2.setStartDelay(1000);
        ObjectAnimator open2 = ObjectAnimator.ofFloat(relativeLayout, View.SCALE_X, 0, 1);
        open2.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                relativeLayout.setEnabled(true);
            }

            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        set.playSequentially(close1, open1, close2, open2);
        return set;
    }

}
