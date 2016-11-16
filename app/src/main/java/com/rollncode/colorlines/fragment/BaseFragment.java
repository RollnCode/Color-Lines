package com.rollncode.colorlines.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.rollncode.colorlines.R;
import com.rollncode.colorlines.activity.BaseActivity;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 20.06.16
 */
public class BaseFragment extends Fragment {

    private int mFragmentContainerId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Activity activity = getActivity();
        if (activity instanceof BaseActivity) {
            mFragmentContainerId = ((BaseActivity) activity).getFragmentContainerId();
        }
    }

    public boolean startFragment(@NonNull BaseFragment fragment, boolean addToBackStack, @NonNull Object... objects) {
        return startFragmentSimple(fragment, addToBackStack);
    }

    protected final boolean startFragmentSimple(@NonNull BaseFragment fragment, boolean addToBackStack) {
        final String tag = fragment.getClass().getSimpleName();

        final FragmentManager manager = getFragmentManager();
        if (!addToBackStack) {
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.enter_pop, R.anim.exit_pop);
        transaction.replace(mFragmentContainerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commitAllowingStateLoss();

        return true;
    }

    protected final void popBackStack() {
        final FragmentActivity activity = getActivity();
        if (activity != null && activity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            activity.getSupportFragmentManager().popBackStack();
        }
    }

}
