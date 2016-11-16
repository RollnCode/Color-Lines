package com.rollncode.colorlines.fragment;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.rollncode.colorlines.R;
import com.rollncode.colorlines.activity.MainActivity.NavigationFragment;
import com.rollncode.colorlines.dialog.GameDialog;
import com.rollncode.colorlines.game.Game;
import com.rollncode.colorlines.game.GameAnimator;
import com.rollncode.colorlines.game.PreviousHolder;
import com.rollncode.colorlines.game.interfaces.State;
import com.rollncode.colorlines.game.model.Position;
import com.rollncode.colorlines.util.GooglePlayHelper;
import com.rollncode.colorlines.util.IntWrapper;
import com.rollncode.colorlines.util.ObjectsReceiver;
import com.rollncode.colorlines.util.SavingUtils;
import com.rollncode.colorlines.util.Utils;
import com.rollncode.colorlines.view.CellView;
import com.rollncode.colorlines.view.CellView.OnToggledListener;
import com.rollncode.colorlines.view.NextHolderView;

import java.util.ArrayList;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 20.06.16
 */
public class PlayFragment extends BaseFragment
        implements OnToggledListener, AnimatorListener, ObjectsReceiver, NavigationFragment {

    private static final String KEY_0 = "KEY_0"; // size

    private TextView mTvScore;
    private TextView mBtnUndo;
    private GridLayout mGrid;
    private CellView[][] mCells;
    private CellView mCvAnim;
    private NextHolderView mNextHolder;

    private int mSize;

    private PreviousHolder mPrevious;
    private Position mStart;
    private Position mEnd;
    private IntWrapper mScore;

    private boolean mAvailable;
    private boolean mNeedSave;
    private boolean mSavedGame;

    private GameDialog mDialog;

    public static PlayFragment newInstance() {
        return new PlayFragment();
    }

    public static PlayFragment newInstance(final int size) {
        final Bundle args = new Bundle();
        args.putInt(KEY_0, size);
        final PlayFragment fragment = new PlayFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_play, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedGame = false;
        if (savedInstanceState != null) {
            mSize = savedInstanceState.getInt(KEY_0);
        } else if (getArguments() != null && getArguments().containsKey(KEY_0)) {
            mSize = getArguments().getInt(KEY_0);
        } else {
            mSize = SavingUtils.getInstance().getSize();
            mSavedGame = true;
        }
        mDialog = new GameDialog(getContext(), this);
        mPrevious = new PreviousHolder(mSize);
        mScore = new IntWrapper(0);
        mAvailable = true;
        mNeedSave = true;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        {
            mTvScore = (TextView) view.findViewById(R.id.tv_score);
            mBtnUndo = (Button) view.findViewById(R.id.btn_undo);
            mGrid = (GridLayout) view.findViewById(R.id.field);
            mCvAnim = (CellView) view.findViewById(R.id.cv_anim);
            mNextHolder = (NextHolderView) view.findViewById(R.id.next_holder);
        }
        setCanUndo(false);
        mCvAnim.animated();

        mGrid.setRowCount(mSize);
        mGrid.setColumnCount(mSize);
        final int cols = mGrid.getColumnCount();
        final int rows = mGrid.getRowCount();

        mCells = new CellView[cols][rows];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                CellView v = new CellView(getContext(), x, y);
                v.setOnToggledListener(this);
                mCells[y][x] = v;
                mGrid.addView(v);
            }
        }

        if (mSavedGame) {
            Utils.setStates(mCells, SavingUtils.getInstance().getField());
            mScore.value = SavingUtils.getInstance().getScore();
            SavingUtils.getInstance().clear();

        } else {
            Game.generate(mCells, Game.nextColors(), mScore);
        }

        mTvScore.setText(String.valueOf(mScore.value));
        mNextHolder.store(Game.nextColors());
        mBtnUndo.setOnClickListener(mOnClickListener);
        mGrid.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);

    }

    public void move(ArrayList<Position> path, @State int state) {
        mCvAnim.setState(state);
        AnimatorSet set = GameAnimator.move(mCells, Utils.reducePath(path), mCvAnim);
        set.addListener(this);
        set.start();
    }

    @Override
    public void OnToggled(CellView v, boolean touchOn) {
        if (mAvailable) {
            if (v.getState() != State.PASSABLE) {
                setSelected(false);
                mStart = v.getPosition();
                setSelected(true);

            } else if (mStart != null) {
                setSelected(false);
                mEnd = v.getPosition();
                final @State int state = mCells[mStart.y][mStart.x].getState();
                final ArrayList<Position> path = Game.path(mCells, mEnd, mStart);
                if (path != null && path.size() > 1) {
                    move(path, state);
                }
            }
        }
    }

    private void setCanUndo(boolean flag) {
        mPrevious.setCanUndo(flag);
        mBtnUndo.setEnabled(flag);
        final int color;
        if (!flag) {
            color = ContextCompat.getColor(getContext(), R.color.disabled);
        } else {
            color = ContextCompat.getColor(getContext(), R.color.white);
        }
        mBtnUndo.setTextColor(color);
        final Drawable drawable = mBtnUndo.getCompoundDrawables()[0].mutate();
        drawable.setColorFilter(color, Mode.MULTIPLY);
        mBtnUndo.setCompoundDrawables(drawable, null, null, null);
    }

    private void setSelected(boolean flag) {
        if (mStart != null) {
            mCells[mStart.y][mStart.x].setSelected(flag);
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        setCanUndo(false);
        mAvailable = false;
        mPrevious.save(mCells, mScore.value);
        mCvAnim.setVisibility(View.VISIBLE);
        mCells[mStart.y][mStart.x].setState(State.PASSABLE);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mCvAnim.setVisibility(View.GONE);
        mCells[mEnd.y][mEnd.x].setState(mCvAnim.getState());
        final int burned = Game.check(mCells, mEnd, mScore);
        if (burned == 0) {
            final ArrayList<Position> generated = Game.generate(mCells, mNextHolder.collect(), mScore);
            if (generated.size() == 0 || Game.getEmptyCells(mCells).size() == 0) {
                mNeedSave = false;
                GooglePlayHelper.getInstance().enterScore(getContext(), mScore.value, mSize);
                mDialog.show(R.id.code_game_over, mScore.value);

            } else {
                GameAnimator.appear(mCells, generated).start();
            }
            mNextHolder.store(Game.nextColors());
            setCanUndo(true);

        } else {
            mScore.value += burned;
            setCanUndo(false);
        }
        mTvScore.setText(String.valueOf(mScore.value));
        mStart = null;
        mAvailable = true;
    }

    @Override
    public void onAnimationCancel(Animator animation) {}

    @Override
    public void onAnimationRepeat(Animator animation) {}

    @Override
    public void onPause() {
        super.onPause();
        if (mNeedSave) {
            SavingUtils.getInstance().save(Utils.makeArray(mCells), mScore.value);
        }
    }

    @Override
    public void onObjectsReceive(@IdRes int code, @NonNull Object... objects) {
        final boolean response;
        switch (code) {
            case R.id.code_game_over:
                response = (boolean) objects[0];
                if (response) {
                    mScore.value = 0;
                    Utils.clear(mCells);
                    Game.generate(mCells, mNextHolder.collect(), mScore);
                    mTvScore.setText(String.valueOf(mScore.value));
                    setCanUndo(false);
                    mNeedSave = true;

                } else {
                    popBackStack();
                }
                break;

            case R.id.code_exit_game:
                response = (boolean) objects[0];
                if (!response) {
                    GooglePlayHelper.getInstance().enterScore(getContext(), mScore.value, mSize);
                    mNeedSave = false;
                    popBackStack();
                }
                break;

            default:
                break;
        }
    }

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_undo:
                    if (mPrevious != null && mPrevious.isCanUndo()) {
                        Utils.setStates(mCells, mPrevious.getField());
                        mScore.value = mPrevious.getScore();
                        mTvScore.setText(String.valueOf(mScore.value));
                        mNextHolder.store(Game.nextColors());
                        setSelected(false);
                        mStart = null;
                        setSelected(false);
                        setCanUndo(false);
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private final OnGlobalLayoutListener mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            final int margin = 1;

            final int cols = mGrid.getColumnCount();
            final int rows = mGrid.getRowCount();
            final int w = mGrid.getWidth() / cols;
            final int h = mGrid.getHeight() / rows;

            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    GridLayout.LayoutParams params = (GridLayout.LayoutParams)
                            mCells[y][x].getLayoutParams();
                    params.width = w - 2 * margin;
                    params.height = h - 2 * margin;
                    params.setMargins(margin, margin, margin, margin);
                    mCells[y][x].setLayoutParams(params);
                }
            }

            LayoutParams params = mCvAnim.getLayoutParams();
            params.width = w - 2 * margin;
            params.height = h - 2 * margin;
            mCvAnim.setLayoutParams(params);
        }
    };

    @Override
    public boolean onBackPressed() {
        mDialog.show(R.id.code_exit_game);
        return true;
    }
}
