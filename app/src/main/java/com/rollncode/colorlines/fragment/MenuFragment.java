package com.rollncode.colorlines.fragment;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.rollncode.colorlines.R;
import com.rollncode.colorlines.dialog.GameDialog;
import com.rollncode.colorlines.game.GameAnimator;
import com.rollncode.colorlines.util.GooglePlayHelper;
import com.rollncode.colorlines.util.ObjectsReceiver;
import com.rollncode.colorlines.util.SavingUtils;

import static com.rollncode.colorlines.util.GooglePlayHelper.REQUEST_GOOGLE_PLAY_SERVICES;
import static com.rollncode.colorlines.util.Utils.SIZE;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 20.06.16
 */
public class MenuFragment extends BaseFragment
        implements ObjectsReceiver {

    private RelativeLayout mRlBtn3;
    private RelativeLayout mRlBtn4;
    private TextView mTvMain;
    private TextView mTvLines;

    private int mSizeSelected;

    public static MenuFragment newInstance() {
        return new MenuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSizeSelected = SavingUtils.getInstance().getSelected();
        final GameDialog dialog = new GameDialog(getContext(), this);
        if (SavingUtils.getInstance().getField() != null) {
            dialog.show(R.id.code_unfinished_game);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRlBtn3 = (RelativeLayout) view.findViewById(R.id.rl_btn3);
        mRlBtn4 = (RelativeLayout) view.findViewById(R.id.rl_btn4);
        mTvMain = (TextView) view.findViewById(R.id.tv_main);
        mTvLines = (TextView) view.findViewById(R.id.tv_lines);

        view.findViewById(R.id.rl_btn1).setOnClickListener(mOnClickListener);
        view.findViewById(R.id.rl_btn2).setOnClickListener(mOnClickListener);
        mRlBtn3.setOnClickListener(mOnClickListener);
        mRlBtn4.setOnClickListener(mOnClickListener);
        mRlBtn4.setOnLongClickListener(mOnLongClickListener);
    }

    private void setSelectedText() {
        final String selected = getResources().getStringArray(R.array.field_size)[mSizeSelected];
        mTvMain.setText(selected);
    }

    @Override
    public void onResume() {
        super.onResume();
        setSelectedText();
    }

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_btn1:
                    mSizeSelected++;
                    if (mSizeSelected > 2) {
                        mSizeSelected = 0;
                    }
                    setSelectedText();
                    break;

                case R.id.rl_btn2:
                    SavingUtils.getInstance().setSelected(mSizeSelected);
                    startFragment(PlayFragment.newInstance(SIZE[mSizeSelected]), true);
                    break;

                case R.id.rl_btn3:
                    if (GooglePlayHelper.getInstance().isConnected()) {
                        startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(
                                GooglePlayHelper.getInstance().getClient()),
                                REQUEST_GOOGLE_PLAY_SERVICES);
                    } else {
                        GameAnimator.flip(mRlBtn3, true).start();
                        GooglePlayHelper.getInstance().getClient().reconnect();
                    }
                    break;

                case R.id.rl_btn4:
                    Animator rotation = GameAnimator.rotate(mTvLines);
                    rotation.start();
                    break;

                default:
                    break;
            }
        }
    };

    private final OnLongClickListener mOnLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            GameAnimator.flip(mRlBtn4, false).start();
            return true;
        }
    };

    @Override
    public void onObjectsReceive(@IdRes int code, @NonNull Object... objects) {
        final boolean response = (boolean) objects[0];
        if (response) {
            startFragment(PlayFragment.newInstance(), true);

        } else {
            SavingUtils.getInstance().clear();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED
                && requestCode == REQUEST_GOOGLE_PLAY_SERVICES)  {
            GooglePlayHelper.getInstance().getClient().disconnect();
        }
    }
}
