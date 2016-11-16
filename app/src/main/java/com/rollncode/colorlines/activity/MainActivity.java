package com.rollncode.colorlines.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.rollncode.colorlines.R;
import com.rollncode.colorlines.fragment.MenuFragment;
import com.rollncode.colorlines.util.GooglePlayHelper;

/**
 * @author Maxim Ambroskin kkxmshu@gmail.com
 * @since 15.06.16
 */
public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //noinspection ConstantConditions
        GooglePlayHelper.init(this, findViewById(android.R.id.content));

        if (savedInstanceState == null) {
            startFragment(MenuFragment.newInstance(), false);
        }
    }

    @Override
    public int getFragmentContainerId() {
        return R.id.fragment_container;
    }

    private boolean popBackStack() {
        final int backStackEntryCount = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackEntryCount > 0) {
            final Fragment fragment = getFragmentFromContainer();
            if (fragment != null && fragment instanceof NavigationFragment) {
                final NavigationFragment navigationFragment = (NavigationFragment) fragment;
                if (navigationFragment.onBackPressed()) {
                    return true;
                }
            }
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        GooglePlayHelper.getInstance().connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GooglePlayHelper.getInstance().disconnect();
    }

    public void onDialogDismissed() {
        GooglePlayHelper.getInstance().setResolvingError(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GooglePlayHelper.REQUEST_RESOLVE_ERROR) {
            GooglePlayHelper.getInstance().setResolvingError(false);
            if (resultCode == RESULT_OK) {
                if (!GooglePlayHelper.getInstance().isConnected()) {
                    GooglePlayHelper.getInstance().connect();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!popBackStack()) {
            super.onBackPressed();
        }
    }

    public interface NavigationFragment {
        boolean onBackPressed();
    }

}
