package com.rollncode.colorlines.activity;

import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.rollncode.colorlines.fragment.BaseFragment;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 20.06.16
 */
public abstract class BaseActivity extends AppCompatActivity {

    public boolean startFragment(@NonNull BaseFragment fragment, boolean addToBackStack) {
        final String tag = fragment.getClass().getSimpleName();
        final FragmentManager manager = getSupportFragmentManager();
        final int containerId = getFragmentContainerId();

        if (!addToBackStack && containerId == getFragmentContainerId()) {
            manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        final FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerId, fragment, tag);
        if (addToBackStack) {
            transaction.addToBackStack(tag);
        }
        transaction.commit();
        return true;
    }

    @IdRes
    public abstract int getFragmentContainerId();

    @Nullable
    public final Fragment getFragmentFromContainer() {
        return getFragmentFromContainer(getFragmentContainerId());
    }

    @Nullable
    public final Fragment getFragmentFromContainer(@IdRes int containerId) {
        return getSupportFragmentManager().findFragmentById(containerId);
    }

}
