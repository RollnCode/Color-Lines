package com.rollncode.colorlines.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.rollncode.colorlines.R;
import com.rollncode.colorlines.game.interfaces.State;
import com.rollncode.colorlines.game.model.Position;
import com.rollncode.colorlines.util.Utils;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 20.06.16
 */
public class CellView extends ImageView {

    public interface OnToggledListener {
        void OnToggled(CellView v, boolean touchOn);
    }

    private boolean mTouchOn;
    private boolean mDownTouch = false;
    private OnToggledListener mToggledListener;

    private int mState = State.PASSABLE;
    private int mIdX = 0;
    private int mIdY = 0;

    public CellView(Context context, int x, int y) {
        this(context);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
        mIdX = x;
        mIdY = y;
        int padding = (int) (getResources().getDimension(R.dimen.cell_padding) /
                getResources().getDisplayMetrics().density);
        setPadding(padding, padding, padding, padding);
        init();
    }

    public CellView(Context context) {
        this(context, null);
    }

    public CellView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CellView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTouchOn = false;
    }

    public void animated() {
        int padding = (int) (getResources().getDimension(R.dimen.cell_padding) /
                getResources().getDisplayMetrics().density);
        setPadding(padding, padding, padding, padding);
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.transparent));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(widthMeasureSpec));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                mTouchOn = !mTouchOn;
                invalidate();

                if (mToggledListener != null) {
                    mToggledListener.OnToggled(this, mTouchOn);
                }

                mDownTouch = true;
                return true;

            case MotionEvent.ACTION_UP:
                if (mDownTouch) {
                    mDownTouch = false;
                    performClick();
                    return true;
                }
        }
        return false;
    }

    public void setState(@State int state) {
        mState = state;
        if (mState >= State.ORANGE) {
            if (getDrawable() == null) {
                setImageResource(R.drawable.shape_circle);
            }
            Utils.setColor(mState, this);

        } else {
            setImageDrawable(null);
        }
    }

    public @State int getState() {
        return mState;
    }

    public Position getPosition() {
        return new Position(mIdX, mIdY);
    }

    public void setSelected(boolean flag) {
        if (flag) {
            setBackgroundColor(ContextCompat.getColor(getContext(), R.color.selected));
            return;
        }
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray));
    }

    public void setOnToggledListener(OnToggledListener listener) {
        mToggledListener = listener;
    }

}
