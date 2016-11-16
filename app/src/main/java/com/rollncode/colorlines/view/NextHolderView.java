package com.rollncode.colorlines.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.rollncode.colorlines.R;
import com.rollncode.colorlines.util.Utils;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 24.06.16
 */
public class NextHolderView extends LinearLayout {

    private ImageView mIvFirst;
    private ImageView mIvSecond;
    private ImageView mIvThird;

    private int[] mStates;

    public NextHolderView(@NonNull Context context) {
        this(context, null);
    }

    public NextHolderView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NextHolderView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_next_holder, this);
        {
            mIvFirst = (ImageView) findViewById(R.id.iv_first);
            mIvSecond = (ImageView) findViewById(R.id.iv_second);
            mIvThird = (ImageView) findViewById(R.id.iv_third);
        }
        mStates = new int[3];
    }

    public void store(int[] states) {
        mStates = states;
        Utils.setColor(mStates[0], mIvFirst);
        Utils.setColor(mStates[1], mIvSecond);
        Utils.setColor(mStates[2], mIvThird);
    }

    public int[] collect() {
        return mStates;
    }

}
