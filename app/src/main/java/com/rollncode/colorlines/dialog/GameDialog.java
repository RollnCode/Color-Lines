package com.rollncode.colorlines.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.rollncode.colorlines.R;
import com.rollncode.colorlines.util.ObjectsReceiver;
import com.rollncode.colorlines.util.Utils;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 01.07.16
 */
public class GameDialog extends BaseDialog
        implements OnClickListener, OnDismissListener {

    private final AlertDialog mDialog;

    private ImageView mIvImage;
    private TextView mTvText;
    private TextView mBtnPositive;
    private TextView mBtnNegative;

    private int mCode;
    private boolean mDismissed;

    public GameDialog(Context context, @Nullable ObjectsReceiver receiver) {
        super(context, receiver);

        @SuppressLint("InflateParams")
        final View view = LayoutInflater.from(context).inflate(R.layout.dialog_game, null);
        {
            mIvImage = (ImageView) view.findViewById(R.id.image);
            mTvText = (TextView) view.findViewById(R.id.text);
            mBtnPositive = (TextView) view.findViewById(R.id.btn_positive);
            mBtnNegative = (TextView) view.findViewById(R.id.btn_negative);
        }

        mDialog = new Builder(context)
                .setView(view)
                .create();

        mDialog.setOnDismissListener(this);
        mBtnPositive.setOnClickListener(this);
        mBtnNegative.setOnClickListener(this);
    }

    @Override
    public void show(@NonNull Object... objects) {
        mDialog.show();
        mDismissed = true;

        mCode = (int) objects[0];
        switch (mCode) {
            case R.id.code_game_over:
                final int score = (int) objects[1];
                mBtnPositive.setText(R.string.replay);
                mIvImage.setImageResource(R.drawable.vec_medal);
                mTvText.setText(mContext.getString(R.string.format_your_score_is_d, score));
                break;

            case R.id.code_exit_game:
                mBtnPositive.setText(R.string.stay);
                mIvImage.setImageResource(R.drawable.vec_door);
                mTvText.setText(R.string.are_you_leaving);
                break;

            case R.id.code_unfinished_game:
                mBtnPositive.setText(R.string.continue_game);
                mBtnNegative.setText(R.string.cancel);
                mIvImage.setImageResource(R.drawable.vec_light_bulb);
                mTvText.setText(R.string.unfinished_game);

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_positive:
                Utils.receiveObjects(mReceiver, mCode, true);
                mDismissed = false;
                mDialog.cancel();
                break;

            case R.id.btn_negative:
                mDialog.cancel();
                break;

            default:
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mDismissed) {
            Utils.receiveObjects(mReceiver, mCode, false);
        }
    }
}
